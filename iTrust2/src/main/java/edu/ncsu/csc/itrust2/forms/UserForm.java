package edu.ncsu.csc.iTrust2.forms;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Form used for creating a new User. Will be parsed into an actual User object
 * to be saved.
 *
 * @author Kai Presler-Marshall
 *
 */
public class UserForm {

    /**
     * Username of the user
     */
    @NotEmpty
    @Length ( max = 20 )
    private String      username;

    /**
     * Password of the user
     */
    @NotEmpty
    @Length ( min = 6, max = 20 )
    private String      password;

    /***
     * Confirmation password of the user
     */
    @NotEmpty
    @Length ( min = 6, max = 20 )
    private String      password2;

    /**
     * Role of the user
     */
    @NotEmpty
    private Set<String> roles;

    /**
     * Whether the User is enabled or not
     */
    private String      enabled;

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
        addRole( role );
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
        setRoles( u.getRoles().stream().map( e -> e.toString() ).collect( Collectors.toSet() ) );
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
    public Set<String> getRoles () {
        return roles;
    }

    /**
     * Sets the Role of the new User
     *
     * @param roles
     *            Roles of the user
     */
    public void setRoles ( final Set<String> roles ) {
        this.roles = roles;
    }

    /**
     * Adds the provided Role to this User
     *
     * @param role
     *            The Role to add
     */
    public void addRole ( final String role ) {
        if ( null == this.roles ) {
            this.roles = new HashSet<String>();
        }
        this.roles.add( role );
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

}
