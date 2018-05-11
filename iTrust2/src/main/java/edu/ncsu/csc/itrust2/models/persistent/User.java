package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.models.enums.Role;

/**
 * Basic class for a User in the system. This User class is a shared type that
 * is used for all users of the iTrust2 system and handles basic functionality
 * such as authenticating the user in the system. For specific rolls, an
 * additional record, such as a Patient or Personnel record, is created and
 * references the User object for that user. This allows the iTrust2 system to
 * keep only what information is needed for a particular type of user.
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "Users" )
public class User extends DomainObject<User> implements Serializable {

    /**
     * The UID of the user
     */
    private static final long serialVersionUID = 1L;

    /**
     * Get all users in the database
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<User> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @return all users in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<User> getUsers () {
        return (List<User>) getAll( User.class );
    }

    /**
     * Get the user by the username
     *
     * @param name
     *            the username of the user
     * @return the corresponding user with this username
     */
    public static User getByName ( final String name ) {
        try {
            return getWhere( createCriterionAsList( "username", name ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get the user by name and role
     *
     * @param name
     *            the username
     * @param type
     *            the role of the user
     * @return the user with this role and name
     */
    public static User getByNameAndRole ( final String name, final Role type ) {

        final Vector<Criterion> criteria = new Vector<Criterion>();
        criteria.add( createCriterion( "username", name ) );
        criteria.add( createCriterion( "role", type ) );
        try {
            return getWhere( criteria ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }

    }

    /**
     * Get all HCPs in the database
     *
     * @return all HCPs in the database
     */
    public static List<User> getHCPs () {
        return getByRole( Role.ROLE_HCP );
    }

    /**
     * Get all patients in the database
     *
     * @return all patients in the database
     */
    public static List<User> getPatients () {
        return getByRole( Role.ROLE_PATIENT );
    }

    /**
     * Get users where the passed query is true
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<User> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return users where the passed query is true
     */
    @SuppressWarnings ( "unchecked" )
    private static List<User> getWhere ( final List<Criterion> where ) {
        return (List<User>) getWhere( User.class, where );
    }

    /**
     * Get all users with the passed role
     *
     * @param role
     *            the role to get the users of
     * @return the users with the passed role
     */
    public static List<User> getByRole ( final Role role ) {
        return getWhere( createCriterionAsList( "role", role ) );
    }

    /** For Hibernate */
    public User () {
    }

    /**
     * All-argument constructor for User
     *
     * @param username
     *            Username
     * @param password
     *            The _already encoded_ password of the user.
     * @param role
     *            Role of the user
     * @param enabled
     *            1 if the user is enabled 0 if not
     */
    public User ( final String username, final String password, final Role role, final Integer enabled ) {
        setUsername( username );
        setPassword( password );
        setRole( role );
        setEnabled( enabled );
    }

    /**
     * Create a new user based off of the UserForm
     *
     * @param form
     *            the filled-in user form with user information
     */
    public User ( final UserForm form ) {
        setUsername( form.getUsername() );
        if ( !form.getPassword().equals( form.getPassword2() ) ) {
            throw new IllegalArgumentException( "Passwords do not match!" );
        }
        final PasswordEncoder pe = new BCryptPasswordEncoder();
        setPassword( pe.encode( form.getPassword() ) );
        setEnabled( null != form.getEnabled() ? 1 : 0 );
        setRole( Role.valueOf( form.getRole() ) );

    }

    /**
     * The username of the user
     */
    @Id
    @Length ( max = 20 )
    private String  username;

    /**
     * The password of the user
     */
    private String  password;

    /**
     * Whether or not the user is enabled
     */
    @Min ( 0 )
    @Max ( 1 )
    private Integer enabled;

    /**
     * The role of the user
     */
    @Enumerated ( EnumType.STRING )
    private Role    role;

    /**
     * Get the username of this user
     *
     * @return the username of this user
     */
    public String getUsername () {
        return username;
    }

    /**
     * Set the username of this user
     *
     * @param username
     *            the username to set this user to
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * Get the password of this user
     *
     * @return the password of this user
     */
    public String getPassword () {
        return password;
    }

    /**
     * Set the password of this user
     *
     * @param password
     *            the password to set this user to
     */
    public void setPassword ( final String password ) {
        this.password = password;
    }

    /**
     * Get whether or not the user is enabled
     *
     * @return Whether or not the user is enabled
     */
    public Integer getEnabled () {
        return enabled;
    }

    /**
     * Set whether or not the user is enabled
     *
     * @param enabled
     *            Whether or not the user is enabled
     */
    public void setEnabled ( final Integer enabled ) {
        this.enabled = enabled;
    }

    /**
     * Get the role of this user
     *
     * @return the role of this user
     */
    public Role getRole () {
        return role;
    }

    /**
     * Set the role of this user
     *
     * @param role
     *            the role to set this user to
     */
    public void setRole ( final Role role ) {
        this.role = role;
    }

    /**
     * Get the hashCode of this user
     *
     * @return the hashCode of this user
     */
    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( enabled == null ) ? 0 : enabled.hashCode() );
        result = prime * result + ( ( password == null ) ? 0 : password.hashCode() );
        result = prime * result + ( ( role == null ) ? 0 : role.hashCode() );
        result = prime * result + ( ( username == null ) ? 0 : username.hashCode() );
        return result;
    }

    /**
     * Whether or not this user is equal to the passed User
     *
     * @param obj
     *            the user to compate this user to
     */
    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final User other = (User) obj;
        if ( enabled == null ) {
            if ( other.enabled != null ) {
                return false;
            }
        }
        else if ( !enabled.equals( other.enabled ) ) {
            return false;
        }
        if ( password == null ) {
            if ( other.password != null ) {
                return false;
            }
        }
        else if ( !password.equals( other.password ) ) {
            return false;
        }
        if ( role != other.role ) {
            return false;
        }
        if ( username == null ) {
            if ( other.username != null ) {
                return false;
            }
        }
        else if ( !username.equals( other.username ) ) {
            return false;
        }
        return true;
    }

    /**
     * Get the id of this user (aka, the username)
     */
    @Override
    public String getId () {
        return getUsername();
    }

    @Override
    public void delete () {
        try {
            @SuppressWarnings ( "unchecked" )
            final List<PasswordResetToken> list = (List<PasswordResetToken>) DomainObject
                    .getAll( PasswordResetToken.class ).stream()
                    .filter( x -> ( (PasswordResetToken) x ).getUser().equals( this ) ).collect( Collectors.toList() );
            for ( final PasswordResetToken t : list ) {
                t.delete();
            }
        }
        catch ( final Exception e ) {
            // ignore to allow a second attempt at deleting this object
        }
        super.delete();
    }

}
