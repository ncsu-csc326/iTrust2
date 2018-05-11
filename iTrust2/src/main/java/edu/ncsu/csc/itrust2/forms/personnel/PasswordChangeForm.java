package edu.ncsu.csc.itrust2.forms.personnel;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.ncsu.csc.itrust2.models.persistent.PasswordResetToken;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Form used to change or reset a password. The same form is used for both by
 * changing the role of the current password to be temporary password for
 * resets.
 *
 * @author Thomas
 *
 */
public class PasswordChangeForm {

    static PasswordEncoder pe = new BCryptPasswordEncoder();
    private String         currentPassword;
    private String         newPassword;
    private String         newPassword2;

    /**
     * Returns the current password entered by the user. For resets, this is the
     * temporary password
     *
     * @return the currentPassword
     */
    public String getCurrentPassword () {
        return currentPassword;
    }

    /**
     * Sets the current password. For resets, this is the temp password
     *
     * @param currentPassword
     *            the currentPassword to set
     */
    public void setCurrentPassword ( final String currentPassword ) {
        this.currentPassword = currentPassword;
    }

    /**
     * Returns the ne wpassword enetered by the user
     *
     * @return the newPassword
     */
    public String getNewPassword () {
        return newPassword;
    }

    /**
     * Sets the new password
     *
     * @param newPassword
     *            the newPassword to set
     */
    public void setNewPassword ( final String newPassword ) {
        this.newPassword = newPassword;
    }

    /**
     * Returns the second entry of the new password
     *
     * @return the newPassword2
     */
    public String getNewPassword2 () {
        return newPassword2;
    }

    /**
     * Sets the second entry of the new password
     *
     * @param newPassword2
     *            the newPassword2 to set
     */
    public void setNewPassword2 ( final String newPassword2 ) {
        this.newPassword2 = newPassword2;
    }

    /**
     * Validates the form based on the passed Reset Token.
     *
     * @param token
     *            The Password Reset Token to verify against.
     * @return true is input is acceptable, false otherwise
     */
    public boolean validateReset ( final PasswordResetToken token ) {
        if ( token.isExpired() ) {
            token.delete();
            throw new IllegalArgumentException( "This temporary password has expired." );
        }
        if ( !pe.matches( getCurrentPassword(), token.getTempPassword() ) ) {
            throw new IllegalArgumentException( "Incorrect temporary password." );
        }
        // possibility of hash collision false positive.
        if ( pe.matches( getNewPassword(), token.getUser().getPassword() ) ) {
            throw new IllegalArgumentException( "New password must be different from current password." );
        }
        // cant match temp password
        if ( pe.matches( getNewPassword(), token.getTempPassword() ) ) {
            throw new IllegalArgumentException( "New password must be different from temporary password." );
        }

        if ( !getNewPassword().equals( getNewPassword2() ) ) {
            throw new IllegalArgumentException( "New password and re-entry must match." );
        }
        if ( getNewPassword().length() < 6 || getNewPassword().length() > 20 ) {
            throw new IllegalArgumentException( "New password must be between 6 and 20 characters." );
        }
        return true;
    }

    /**
     * Validates
     * 
     * @param user
     *            The user to validate against
     * @return true if the input is valid, false otherwise
     */
    public boolean validateChange ( final User user ) {
        if ( !pe.matches( getCurrentPassword(), user.getPassword() ) ) {
            throw new IllegalArgumentException( "Incorrect password." );
        }
        if ( !getNewPassword().equals( getNewPassword2() ) ) {
            throw new IllegalArgumentException( "New password and re-entry must match." );
        }
        if ( getNewPassword().length() < 6 || getNewPassword().length() > 20 ) {
            throw new IllegalArgumentException( "New password must be between 6 and 20 characters." );
        }
        if ( getNewPassword().equals( getCurrentPassword() ) ) {
            throw new IllegalArgumentException( "New password must be different from current password." );
        }
        return true;
    }

}
