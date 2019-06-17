package edu.ncsu.csc.itrust2.config;

import java.io.IOException;
import java.time.ZonedDateTime;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LoginAttempt;
import edu.ncsu.csc.itrust2.models.persistent.LoginBan;
import edu.ncsu.csc.itrust2.models.persistent.LoginLockout;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.EmailUtil;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Custom AuthenticationFailureHandler to record Failed attempts, and lockout or
 * ban a user or IP if necessary.
 *
 * @author Thomas
 *
 */
public class FailureHandler extends SimpleUrlAuthenticationFailureHandler {
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

        if ( ae instanceof BadCredentialsException ) {
            // need to lockout IP
            if ( LoginAttempt.getIPFailures( addr ) >= 5 ) {
                LoginAttempt.clearIP( addr );
                // Check if need to ban IP
                if ( LoginLockout.getRecentIPLockouts( addr ) >= 2 ) {
                    // BAN
                    final LoginBan ban = new LoginBan();
                    ban.setIp( addr );
                    ban.setTime( ZonedDateTime.now() );
                    ban.save();
                    LoginLockout.clearIP( addr );
                    LoggerUtil.log( TransactionType.IP_BANNED, addr, null, addr + " has been banned." );
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?ipbanned" );
                }
                else {
                    // lockout IP.
                    final LoginLockout lockout = new LoginLockout();
                    lockout.setIp( addr );
                    lockout.setTime( ZonedDateTime.now() );
                    lockout.save();
                    LoggerUtil.log( TransactionType.IP_LOCKOUT, addr, null, addr + " has been locked out for 1 hour." );
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?iplocked" );

                    final String name = username;
                    final String email = EmailUtil.getEmailByUsername( name );
                    if ( email != null ) {
                        try {
                            EmailUtil.sendEmail( email, "iTrust2: Your account has beeen locked out",
                                    "Your iTrust2 account has been locked out due to too many failed log in attemtps." );
                            LoggerUtil.log( TransactionType.CREATE_LOCKOUT_EMAIL, name );
                        }
                        catch ( final MessagingException e ) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        LoggerUtil.log( TransactionType.CREATE_MISSING_EMAIL_LOG, name );
                    }
                }
                return;
            }
            else {
                // fail for IP
                final LoginAttempt attempt = new LoginAttempt();
                attempt.setTime( ZonedDateTime.now() );
                attempt.setIp( addr );
                attempt.save();
            }

            // check username
            if ( username != null ) {
                user = User.getByName( username );
            }

            if ( user != null ) {
                // check if need to lockout username
                if ( LoginAttempt.getUserFailures( user ) >= 2 ) {
                    LoginAttempt.clearUser( user );
                    // check if need to ban user
                    if ( LoginLockout.getRecentUserLockouts( user ) >= 2 ) {
                        LoginLockout.clearUser( user );
                        final LoginBan ban = new LoginBan();
                        ban.setTime( ZonedDateTime.now() );
                        ban.setUser( user );
                        ban.save();
                        LoggerUtil.log( TransactionType.USER_BANNED, username, null, username + " has been banned." );
                        this.getRedirectStrategy().sendRedirect( request, response, "/login?banned" );

                        final String name = username;
                        final String email = EmailUtil.getEmailByUsername( name );
                        if ( email != null ) {
                            try {
                                EmailUtil.sendEmail( email, "iTrust2: Your account has beeen locked out",
                                        "Your iTrust2 account has been locked out due to too many failed log in attemtps." );
                                LoggerUtil.log( TransactionType.CREATE_LOCKOUT_EMAIL, name );
                            }
                            catch ( final MessagingException e ) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            LoggerUtil.log( TransactionType.CREATE_MISSING_EMAIL_LOG, name );
                        }
                    }
                    else {
                        // lockout user
                        final LoginLockout lock = new LoginLockout();
                        lock.setTime( ZonedDateTime.now() );
                        lock.setUser( user );
                        lock.save();
                        LoggerUtil.log( TransactionType.USER_LOCKOUT, username, null,
                                username + " has been locked out for 1 hour." );
                        this.getRedirectStrategy().sendRedirect( request, response, "/login?locked" );

                        final String name = username;
                        final String email = EmailUtil.getEmailByUsername( name );
                        if ( email != null ) {
                            try {
                                EmailUtil.sendEmail( email, "iTrust2: Your account has beeen locked out",
                                        "Your iTrust2 account has been locked out due to too many failed log in attemtps." );
                                LoggerUtil.log( TransactionType.CREATE_LOCKOUT_EMAIL, name );
                            }
                            catch ( final MessagingException e ) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            LoggerUtil.log( TransactionType.CREATE_MISSING_EMAIL_LOG, name );
                        }
                    }
                    return;
                }
                else {
                    // fail for username
                    final LoginAttempt attempt = new LoginAttempt();
                    attempt.setTime( ZonedDateTime.now() );
                    attempt.setUser( user );
                    attempt.save();
                }
            }

        }
        else if ( ae instanceof DisabledException ) {
            if ( username != null ) {
                user = User.getByName( username );
            }
            if ( user != null ) {
                // redirect to user lockout or user ban
                if ( LoginBan.isUserBanned( user ) ) {
                    this.getRedirectStrategy().sendRedirect( request, response, "/login?banned" );
                    return;
                }
                else if ( LoginLockout.isUserLocked( user ) ) {
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

}
