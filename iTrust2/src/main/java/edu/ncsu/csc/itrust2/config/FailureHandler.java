package edu.ncsu.csc.iTrust2.config;

import java.io.IOException;
import java.time.ZonedDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.models.security.LoginAttempt;
import edu.ncsu.csc.iTrust2.models.security.LoginBan;
import edu.ncsu.csc.iTrust2.models.security.LoginLockout;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.services.security.LoginAttemptService;
import edu.ncsu.csc.iTrust2.services.security.LoginBanService;
import edu.ncsu.csc.iTrust2.services.security.LoginLockoutService;
import edu.ncsu.csc.iTrust2.utils.EmailUtil;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Custom AuthenticationFailureHandler to record Failed attempts, and lockout or
 * ban a user or IP if necessary.
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
public class FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private LoggerUtil          loggerUtil;

    @Autowired
    private EmailUtil           emailUtil;

    @Autowired
    private LoginBanService     loginBanService;

    @Autowired
    private LoginLockoutService loginLockoutService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private UserService         userService;

    @Override
    public void onAuthenticationFailure ( final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException ae ) throws IOException, ServletException {
        /*
         * Credit for username to
         * https://stackoverflow.com/questions/8676206/how-can-i-get-the
         * -username-from-a-failed-login-using-spring-security
         */
        final String username = request
                .getParameter( UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY );
        User user = null;
        final String addr = request.getRemoteAddr();

        if ( username != null ) {
            user = userService.findByName( username );
        }

        if ( ae instanceof BadCredentialsException ) {
            // need to lockout IP
            if ( loginAttemptService.countByIP( addr ) >= 5 ) {
                loginAttemptService.clearIP( addr );
                // Check if need to ban IP
                if ( loginLockoutService.getRecentIPLockouts( addr ) >= 2 ) {
                    // BAN
                    final LoginBan ban = new LoginBan();
                    ban.setIp( addr );
                    ban.setTime( ZonedDateTime.now() );
                    loginBanService.save( ban );

                    loginLockoutService.clearIP( addr );
                    loggerUtil.log( TransactionType.IP_BANNED, addr, null, addr + " has been banned." );
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?ipbanned" );
                }
                else {
                    // lockout IP.
                    final LoginLockout lockout = new LoginLockout();
                    lockout.setIp( addr );
                    lockout.setTime( ZonedDateTime.now() );
                    loginLockoutService.save( lockout );
                    loggerUtil.log( TransactionType.IP_LOCKOUT, addr, null, addr + " has been locked out for 1 hour." );
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?iplocked" );

                    sendEmail( username );
                }
                return;
            }
            else {
                // fail for IP
                final LoginAttempt attempt = new LoginAttempt();
                attempt.setTime( ZonedDateTime.now() );
                attempt.setIp( addr );
                loginAttemptService.save( attempt );
            }

            // check username
            if ( username != null ) {
                user = userService.findByName( username );
            }

            if ( user != null ) {
                // check if need to lockout username
                if ( loginAttemptService.countByUser( user ) >= 2 ) {
                    loginAttemptService.clearUser( user );
                    // check if need to ban user
                    if ( loginLockoutService.getRecentUserLockouts( user ) >= 2 ) {
                        loginLockoutService.clearUser( user );
                        final LoginBan ban = new LoginBan();
                        ban.setTime( ZonedDateTime.now() );
                        ban.setUser( user );
                        loginBanService.save( ban );
                        loggerUtil.log( TransactionType.USER_BANNED, username, null, username + " has been banned." );
                        this.getRedirectStrategy().sendRedirect( request, response, "/login?banned" );

                        sendEmail( username );

                    }
                    else {
                        // lockout user
                        final LoginLockout lock = new LoginLockout();
                        lock.setTime( ZonedDateTime.now() );
                        lock.setUser( user );
                        loginLockoutService.save( lock );
                        loggerUtil.log( TransactionType.USER_LOCKOUT, username, null,
                                username + " has been locked out for 1 hour." );
                        this.getRedirectStrategy().sendRedirect( request, response, "/login?locked" );

                        sendEmail( username );
                    }
                    return;
                }
                else {
                    // fail for username
                    final LoginAttempt attempt = new LoginAttempt();
                    attempt.setTime( ZonedDateTime.now() );
                    attempt.setUser( user );
                    loginAttemptService.save( attempt );
                }
            }

        }
        else if ( ae instanceof DisabledException ) {
            if ( username != null ) {
                user = userService.findByName( username );
            }
            if ( user != null ) {
                // redirect to user lockout or user ban
                if ( loginBanService.isUserBanned( user ) ) {
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?banned" );
                    return;
                }
                else if ( loginLockoutService.isUserLocked( user ) ) {
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?locked" );
                    return;
                }
                // else, otherwise disabled
            }

            this.getRedirectStrategy().sendRedirect( request, response, "/login?locked" );
            return;
        }
        this.getRedirectStrategy().sendRedirect( request, response, "/login?error" );
    }

    private void sendEmail ( final String username ) {
        final User user = userService.findByName( username );
        if ( null != user ) {
            emailUtil.sendEmail( user, "iTrust2: Your account has beeen locked out",
                    "Your iTrust2 account has been locked out due to too many failed log in attempts." );
            loggerUtil.log( TransactionType.CREATE_LOCKOUT_EMAIL, username );

        }
        else {
            loggerUtil.log( TransactionType.CREATE_MISSING_EMAIL_LOG, username );
        }
    }

}
