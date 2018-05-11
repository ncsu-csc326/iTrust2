package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.LoginBan;
import edu.ncsu.csc.itrust2.models.persistent.LoginLockout;
import edu.ncsu.csc.itrust2.models.persistent.LoginAttempt;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class LockoutTest {

    @Test
    public void testUserLockouts () {
        final String userName = "lockoutUser";

        User user = User.getByName( userName );
        if ( user == null ) {
            user = new User();
            user.setUsername( userName );
            user.setPassword( "pw" );
            user.setEnabled( 1 );
            user.setRole( Role.ROLE_HCP );
            user.save();
        }
        // Clear any existing entries
        LoginAttempt.clearUser( user );
        LoginLockout.clearUser( user );
        LoginBan.clearUser( user );

        assertEquals( 0, LoginAttempt.getUserFailures( user ) );
        assertFalse( LoginLockout.isUserLocked( user ) );
        assertFalse( LoginBan.isUserBanned( user ) );

        LoginAttempt attempt = new LoginAttempt();
        attempt.setTime( Calendar.getInstance() );
        attempt.setUser( user );
        attempt.save();
        assertEquals( 1, LoginAttempt.getUserFailures( user ) );
        assertFalse( LoginLockout.isUserLocked( user ) );
        attempt = new LoginAttempt();
        attempt.setTime( Calendar.getInstance() );
        attempt.setUser( user );
        attempt.save();
        assertEquals( 2, LoginAttempt.getUserFailures( user ) );
        LoginAttempt.clearUser( user );

        assertNull( attempt.getIp() );

        LoginLockout lockout = new LoginLockout();
        lockout.setUser( user );
        Calendar d = Calendar.getInstance();
        // Test expiration
        d.setTimeInMillis( d.getTimeInMillis() - ( 1000 * 60 * 61 ) );
        lockout.setTime( d );
        lockout.save();
        assertEquals( 0, LoginAttempt.getUserFailures( user ) );
        assertFalse( LoginLockout.isUserLocked( user ) );
        lockout = new LoginLockout();
        lockout.setTime( Calendar.getInstance() );
        lockout.setUser( user );
        lockout.save();
        assertTrue( LoginLockout.isUserLocked( user ) );
        LoginLockout.clearUser( user );
        assertFalse( LoginLockout.isUserLocked( user ) );

        assertNull( lockout.getIp() );

        final LoginBan ban = new LoginBan();
        ban.setUser( user );
        d = Calendar.getInstance();
        d.setTimeInMillis( 0 ); // Even really old still in effect
        ban.setTime( d );
        ban.save();
        assertTrue( LoginBan.isUserBanned( user ) );
        LoginBan.clearUser( User.getByName( userName ) );
        assertFalse( LoginBan.isUserBanned( user ) );

        assertNull( ban.getIp() );
    }

    @Test
    public void testIPLockouts () {

        final String ip = "111.111.111.111";

        // Clear any existing entries
        LoginAttempt.clearIP( ip );
        LoginLockout.clearIP( ip );
        LoginBan.clearIP( ip );

        assertEquals( 0, LoginAttempt.getIPFailures( ip ) );
        assertFalse( LoginLockout.isIPLocked( ip ) );
        assertFalse( LoginBan.isIPBanned( ip ) );

        LoginAttempt attempt = new LoginAttempt();
        attempt.setTime( Calendar.getInstance() );
        attempt.setIp( ip );
        attempt.save();
        assertEquals( 1, LoginAttempt.getIPFailures( ip ) );
        assertFalse( LoginLockout.isIPLocked( ip ) );
        attempt = new LoginAttempt();
        attempt.setTime( Calendar.getInstance() );
        attempt.setIp( ip );
        attempt.save();
        assertEquals( 2, LoginAttempt.getIPFailures( ip ) );
        LoginAttempt.clearIP( ip );

        assertNull( attempt.getUser() );

        LoginLockout lockout = new LoginLockout();
        lockout.setIp( ip );
        Calendar d = Calendar.getInstance();
        // Test expiration
        d.setTimeInMillis( d.getTimeInMillis() - ( 1000 * 60 * 61 ) );
        lockout.setTime( d );
        lockout.save();
        assertEquals( 0, LoginAttempt.getIPFailures( ip ) );
        assertFalse( LoginLockout.isIPLocked( ip ) );
        lockout = new LoginLockout();
        lockout.setTime( Calendar.getInstance() );
        lockout.setIp( ip );
        lockout.save();
        assertTrue( LoginLockout.isIPLocked( ip ) );
        LoginLockout.clearIP( ip );
        assertFalse( LoginLockout.isIPLocked( ip ) );

        assertNull( lockout.getUser() );

        final LoginBan ban = new LoginBan();
        ban.setIp( ip );
        d = Calendar.getInstance();
        d.setTimeInMillis( 0 ); // Even really old still in effect
        ban.setTime( d );
        ban.save();
        assertTrue( LoginBan.isIPBanned( ip ) );
        LoginBan.clearIP( ip );
        assertFalse( LoginBan.isIPBanned( ip ) );

        assertNull( ban.getUser() );
    }
}
