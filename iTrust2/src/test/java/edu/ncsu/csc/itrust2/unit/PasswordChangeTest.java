package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.ncsu.csc.itrust2.forms.personnel.PasswordChangeForm;
import edu.ncsu.csc.itrust2.models.persistent.PasswordResetToken;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class PasswordChangeTest {

    PasswordEncoder pe = new BCryptPasswordEncoder();

    @Test
    public void testPasswordChangeForm () {

        final User user = new User();
        user.setPassword( pe.encode( "123456" ) );
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( "123456" );
        form.setNewPassword( "654321" );
        form.setNewPassword2( "654321" );
        assertTrue( form.validateChange( user ) );

        // dont match reentry
        form.setNewPassword( "different" );
        try {
            form.validateChange( user );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "New password and re-entry must match.", e.getMessage() );
        }

        // same as current
        form.setNewPassword( "123456" );
        form.setNewPassword2( "123456" );
        try {
            form.validateChange( user );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "New password must be different from current password.", e.getMessage() );
        }

        // wrong current pw
        form.setCurrentPassword( "wrong" );
        try {
            form.validateChange( user );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Incorrect password.", e.getMessage() );
        }

        // too short
        form.setNewPassword( "123" );
        form.setNewPassword2( "123" );
        form.setCurrentPassword( "123456" );
        try {
            form.validateChange( user );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "New password must be between 6 and 20 characters.", e.getMessage() );
        }

        // too long
        form.setNewPassword( "1234567890123456789012" );
        form.setNewPassword2( "1234567890123456789012" );
        try {
            form.validateChange( user );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "New password must be between 6 and 20 characters.", e.getMessage() );
        }
    }

    @Test
    public void testPasswordReset () {
        final User user = new User();
        user.setPassword( pe.encode( "123456" ) );
        final PasswordResetToken token = new PasswordResetToken( user );
        final PasswordChangeForm form = new PasswordChangeForm();
        form.setCurrentPassword( token.getTempPasswordPlaintext() );
        form.setNewPassword( "654321" );
        form.setNewPassword2( "654321" );
        assertTrue( form.validateReset( token ) );

        // wrong temp
        form.setCurrentPassword( "wrong" );
        try {
            form.validateReset( token );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "Incorrect temporary password.", e.getMessage() );
        }

        // re-entry doesnt match
        form.setCurrentPassword( token.getTempPasswordPlaintext() );
        form.setNewPassword2( "different" );
        try {
            form.validateReset( token );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "New password and re-entry must match.", e.getMessage() );
        }

        // same as old pw
        form.setNewPassword( "123456" );
        form.setNewPassword2( "123456" );
        try {
            form.validateReset( token );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "New password must be different from current password.", e.getMessage() );
        }

        // same as temp
        form.setNewPassword( token.getTempPasswordPlaintext() );
        form.setNewPassword2( token.getTempPasswordPlaintext() );
        try {
            form.validateReset( token );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "New password must be different from temporary password.", e.getMessage() );
        }

        // too short
        form.setNewPassword( "123" );
        form.setNewPassword2( "123" );
        try {
            form.validateReset( token );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "New password must be between 6 and 20 characters.", e.getMessage() );
        }

        // too long
        form.setNewPassword( "1234567890123456789012" );
        form.setNewPassword2( "1234567890123456789012" );
        try {
            form.validateReset( token );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "New password must be between 6 and 20 characters.", e.getMessage() );
        }

        // expired
        token.setCreationTime( token.getCreationTime() - PasswordResetToken.TOKEN_LIFETIME - 1 );
        assertTrue( token.isExpired() );
        form.setCurrentPassword( token.getTempPasswordPlaintext() );
        form.setNewPassword( "654321" );
        form.setNewPassword2( "654321" );
        try {
            form.validateReset( token );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "This temporary password has expired.", e.getMessage() );
        }
    }

}
