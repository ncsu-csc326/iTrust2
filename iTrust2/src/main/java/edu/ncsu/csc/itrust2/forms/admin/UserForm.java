package edu.ncsu.csc.itrust2.forms.admin;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Form used for creating a new User. Will be parsed into an actual User object
 * to be saved.
 *
 * @author Kai Presler-Marshall
 *
 */
public class UserForm {

    /**
     * Empty constructor used to generate a blank form for the user to fill out.
     */
    public UserForm () {
    }

    /**
     * Create a UserForm from all of its fields.
     *
     * @param username
     *            Username of the new user.
     * @param password
     *            Password of the new user
     * @param role
     *            Role of the new User
     * @param enabled
     *            Whether the new User is enabled or not
     *
     */
    public UserForm ( final String username, final String password, final String role, final String enabled ) {
        setUsername( username );
        setPassword( password );
        setPassword2( password );
        setRole( role );
        setEnabled( enabled );
    }

    /**
     * Create a new UserForm from all of its fields.
     *
     * @param username
     *            Username of the new user
     * @param password
     *            Password of the new user
     * @param role
     *            Role (Role Enum) of the new user
     * @param enabled
     *            Whether the user is enabled; 1 for enabled, 0 for disabled.
     */
    public UserForm ( final String username, final String password, final Role role, final Integer enabled ) {
        this( username, password, role.toString(), enabled != 0 ? "true" : null );
    }

    /**
     * Create a UserForm from the User object provided. This unfortunately
     * cannot fill out the password as the password cannot be un-hashed.
     *
     * @param u
     *            User object to convert to a UserForm.
     */
    public UserForm ( final User u ) {
        setUsername( u.getUsername() );
        setRole( u.getRole().toString() );

        setEnabled( u.getEnabled().toString() );
    }

    /**
     * Get the Username of the User for the form
     *
     * @return The Username of the User
     */
    public String getUsername () {
        return username;
    }

    /**
     * Sets a new Username for the User on the form
     *
     * @param username
     *            New Username to set
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * Gets the Password provided in the form
     *
     * @return Password provided
     */
    public String getPassword () {
        return password;
    }

    /**
     * Sets the Password for the User on the form.
     *
     * @param password
     *            Password of the user
     */
    public void setPassword ( final String password ) {
        this.password = password;
    }

    /**
     * Gets the Password confirmation provided in the form
     *
     * @return Password confirmation provided
     */
    public String getPassword2 () {
        return password2;
    }

    /**
     * Sets the Password confirmation in the Form
     *
     * @param password2
     *            The password confirmation
     */
    public void setPassword2 ( final String password2 ) {
        this.password2 = password2;
    }

    /**
     * Role of the new User
     *
     * @return The User's role
     */
    public String getRole () {
        return role;
    }

    /**
     * Sets the Role of the new User
     *
     * @param role
     *            Role of the user
     */
    public void setRole ( final String role ) {
        this.role = role;
    }

    /**
     * Gets whether the new User created is to be enabled or not
     *
     * @return Whether the User is enabled
     */
    public String getEnabled () {
        return enabled;
    }

    /**
     * Retrieves whether the user is enabled or not
     *
     * @param enabled
     *            New Enabled setting
     */
    public void setEnabled ( final String enabled ) {
        this.enabled = enabled;
    }

    /**
     * Username of the user
     */
    @NotEmpty
    @Length ( max = 20 )
    private String username;

    /**
     * Password of the user
     */
    @NotEmpty
    @Length ( min = 6, max = 20 )
    private String password;

    /***
     * Confirmation password of the user
     */
    @NotEmpty
    @Length ( min = 6, max = 20 )
    private String password2;

    /**
     * Role of the user
     */
    @NotEmpty
    private String role;

    /**
     * Whether the User is enabled or not
     */
    private String enabled;

}
