package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.models.security.LogEntry;
import edu.ncsu.csc.iTrust2.models.security.LoginLockout;
import edu.ncsu.csc.iTrust2.repositories.security.LoginLockoutRepository;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.services.security.LogEntryService;
import edu.ncsu.csc.iTrust2.services.security.LoginAttemptService;
import edu.ncsu.csc.iTrust2.services.security.LoginBanService;
import edu.ncsu.csc.iTrust2.services.security.LoginLockoutService;

/**
 * Tests for UC2 (Authenticate Users). Rather than testing the lockout /
 * lockout / ban mechanics in isolation, this class implements the four
 * Acceptance Scenarios written out explicitly in docs/UC2.md section 2.7, plus
 * the base successful-login flow, since those scenarios are the spec's own
 * definition of "done" for this Use Case.
 *
 * These tests exercise the real Spring Security filter chain (via
 * {@code springSecurity()}), which is what actually runs
 * {@link edu.ncsu.csc.iTrust2.config.FailureHandler},
 * {@link edu.ncsu.csc.iTrust2.config.LoginAuditingListener}, and
 * {@link edu.ncsu.csc.iTrust2.config.IPFilter} -- none of which had any test
 * coverage previously.
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class LoginSecurityTest {

    /** All MockMvc requests share this remote address, simulating "same machine" */
    private static final String    TEST_IP = "127.0.0.1";

    private static final String    PW      = "password1";

    private MockMvc                mvc;

    @Autowired
    private WebApplicationContext  context;

    @Autowired
    private UserService            userService;

    @Autowired
    private LoginAttemptService    loginAttemptService;

    @Autowired
    private LoginLockoutService    loginLockoutService;

    @Autowired
    private LoginBanService        loginBanService;

    @Autowired
    private LogEntryService        logEntryService;

    @Autowired
    private LoginLockoutRepository loginLockoutRepository;

    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).apply( springSecurity() ).build();
        userService.deleteAll();
        loginAttemptService.deleteAll();
        loginLockoutService.deleteAll();
        loginBanService.deleteAll();
    }

    private User createUser ( final String username ) {
        final UserForm uf = new UserForm( username, PW, Role.ROLE_HCP, 1 );
        final User user = new Personnel( uf );
        userService.save( user );
        return user;
    }

    private void failLogin ( final String username ) throws Exception {
        mvc.perform( formLogin().user( username ).password( "wrongPassword" ) );
    }

    /**
     * UC2 [Main Flow]: a User who authenticates with the correct username and
     * password is logged in (LOGIN_SUCCESS, code 2 is recorded), and any
     * previously recorded failed attempts for that User/IP are cleared.
     */
    @Test
    @Transactional
    public void testMainFlow_successfulLoginLogsAndClearsFailedAttempts () throws Exception {
        final String username = "mainFlowUser";
        final User user = createUser( username );

        // One failed attempt beforehand -- should be cleared on success
        failLogin( username );
        Assert.assertEquals( "One failed attempt should be recorded", 1, loginAttemptService.countByUser( user ) );

        mvc.perform( formLogin().user( username ).password( PW ) );

        Assert.assertEquals( "A successful login should clear prior failed attempts for the User", 0,
                loginAttemptService.countByUser( user ) );

        final List<LogEntry> successLogs = logEntryService.findAllForUser( username ).stream()
                .filter( e -> e.getLogCode() == TransactionType.LOGIN_SUCCESS ).collect( Collectors.toList() );
        Assert.assertEquals( "A LOGIN_SUCCESS (2) transaction should be logged", 1, successLogs.size() );
    }

    /**
     * UC2 [2.7 Scenario 1]: "User locked out after 3 attempts" -- HCP Shelly
     * Vang fails to authenticate 3 times in a row and is locked out of the
     * system as a result.
     */
    @Test
    @Transactional
    public void testScenario1_userLockedOutAfterThreeFailedAttempts () throws Exception {
        final String username = "shellyVang";
        final User user = createUser( username );

        failLogin( username );
        failLogin( username );
        Assert.assertFalse( "The User should not be locked out after only 2 failed attempts",
                loginLockoutService.isUserLocked( user ) );

        failLogin( username );
        Assert.assertTrue( "The User should be locked out after 3 failed attempts",
                loginLockoutService.isUserLocked( user ) );

        Assert.assertEquals( "A USER_LOCKOUT (4) transaction should be logged", 1,
                logEntryService.findAllForUser( username ).stream()
                        .filter( e -> e.getLogCode() == TransactionType.USER_LOCKOUT ).count() );
    }

    /**
     * UC2 [2.7 Scenario 2]: "User banned after 3 lockouts" -- if a User is
     * locked out 3 times within 24 hours, they are banned from the system
     * until an administrator re-authorizes them.
     *
     * Split from Scenario 1 because reaching this state requires driving the
     * lockout mechanism through three full lockout cycles (9 failed
     * attempts), which is a meaningfully more complex setup than a single
     * lockout.
     */
    @Test
    @Transactional
    public void testScenario2_userBannedAfterThreeLockoutsWithin24Hours () throws Exception {
        final String username = "shellyVangRepeatOffender";
        final User user = createUser( username );

        // Three lockout cycles of 3 failed attempts each. The per-IP failed
        // attempt counter is shared across all of these (FailureHandler
        // checks it before ever looking at the per-User counter), so without
        // clearing it between cycles it would reach its own threshold of 5
        // and divert one of these attempts into an IP lockout instead of the
        // intended 2nd/3rd User lockout. Clearing it here does not touch the
        // User-level LoginLockout history that the ban decision depends on.
        for ( int cycle = 0; cycle < 3; cycle++ ) {
            for ( int attempt = 0; attempt < 3; attempt++ ) {
                failLogin( username );
            }
            loginAttemptService.clearIP( TEST_IP );
        }

        Assert.assertTrue( "The User should be banned after their 3rd lockout within 24 hours",
                loginBanService.isUserBanned( user ) );

        Assert.assertEquals( "A USER_BANNED (6) transaction should be logged", 1,
                logEntryService.findAllForUser( username ).stream()
                        .filter( e -> e.getLogCode() == TransactionType.USER_BANNED ).count() );
    }

    /**
     * UC2 [2.7 Scenario 3]: "IP locked out after 6 attempts" -- if the last 6
     * login attempts from an IP address fail, across any number of users,
     * that IP is locked out, even if no single User individually reached the
     * lockout threshold on their own via that final attempt.
     */
    @Test
    @Transactional
    public void testScenario3_ipLockedOutAfterSixFailedAttemptsAcrossUsers () throws Exception {
        final String userA = "shellyVangIpTest";
        final String userB = "jimBean";
        createUser( userA );
        final User userBObj = createUser( userB );

        // 3 failures for User A (also locks User A individually, matching the
        // doc's own narrative)
        failLogin( userA );
        failLogin( userA );
        failLogin( userA );

        // 2 more failures for User B (5 total failed attempts from this IP)
        failLogin( userB );
        failLogin( userB );

        Assert.assertFalse( "The IP should not be locked after only 5 total failed attempts",
                loginLockoutService.isIPLocked( TEST_IP ) );

        // 6th failed attempt from this IP -- should trip the IP lockout
        // instead of User B's individual lockout
        failLogin( userB );

        Assert.assertTrue( "The IP should be locked out after 6 failed attempts", loginLockoutService.isIPLocked( TEST_IP ) );
        Assert.assertFalse( "User B's 3rd failure was consumed by the IP-lockout check, so User B "
                + "should not also be individually locked out", loginLockoutService.isUserLocked( userBObj ) );

        Assert.assertEquals( "An IP_LOCKOUT (5) transaction should be logged", 1,
                logEntryService.findAllForUser( TEST_IP ).stream()
                        .filter( e -> e.getLogCode() == TransactionType.IP_LOCKOUT ).count() );
    }

    /**
     * UC2 [2.7 Scenario 4]: "IP banned after 3 lockouts" -- an unregistered
     * (or any) user repeatedly failing login from the same IP address causes
     * that IP to be banned after 3 lockout cycles (18 total failed attempts).
     *
     * Split from Scenario 3 for the same reason Scenario 2 is split from
     * Scenario 1: this drives three full IP-lockout cycles rather than one.
     *
     * The doc's own narrative has each retry happen "an hour later" -- i.e.
     * after the 60-minute lockout window has elapsed. That matters here
     * because {@link edu.ncsu.csc.iTrust2.config.IPFilter} blocks *every*
     * request (including the next login POST) from a currently-locked IP, so
     * without simulating that elapsed time, attempts 7-18 would never reach
     * {@link edu.ncsu.csc.iTrust2.config.FailureHandler} at all. This test
     * backdates the LoginLockout timestamps between cycles (rather than
     * calling loginLockoutService.clearIP, which would also erase the
     * lockout history the 24-hour ban lookback depends on) to simulate that
     * elapsed hour.
     */
    @Test
    @Transactional
    public void testScenario4_ipBannedAfterThreeLockoutsWithin24Hours () throws Exception {
        // Per the doc, this scenario is driven by an unregistered user --
        // the User-specific branch in FailureHandler is skipped entirely
        // when no matching User is found, so only the IP counters advance.
        for ( int cycle = 0; cycle < 3; cycle++ ) {
            for ( int attempt = 0; attempt < 6; attempt++ ) {
                failLogin( "noSuchRegisteredUser" );
            }

            // Simulate the 60-minute lockout window elapsing so IPFilter
            // will let the next cycle's attempts through, while keeping the
            // lockout records (needed for the 24-hour ban lookback) intact.
            for ( final LoginLockout lockout : loginLockoutRepository.findByIp( TEST_IP ) ) {
                lockout.setTime( lockout.getTime().minusMinutes( 61 ) );
                loginLockoutRepository.save( lockout );
            }
        }

        Assert.assertTrue( "The IP should be banned after its 3rd lockout within 24 hours",
                loginBanService.isIPBanned( TEST_IP ) );

        Assert.assertEquals( "An IP_BANNED (7) transaction should be logged", 1,
                logEntryService.findAllForUser( TEST_IP ).stream()
                        .filter( e -> e.getLogCode() == TransactionType.IP_BANNED ).count() );
    }

}
