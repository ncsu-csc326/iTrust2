package edu.ncsu.csc.itrust2.models.persistent;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.State;

/**
 * Database-persisted representation of all non-Patient types of iTrust2 users.
 * This includes HCPs, Admins, and other future users.
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "Personnel" )
public class Personnel extends DomainObject<Personnel> {

    /**
     * Get the personnel by username
     *
     * @param username
     *            the username of the personnel to get
     * @return the personnel result with the queried username
     */
    public static Personnel getByName ( final String username ) {
        return getByName( User.getByName( username ) );
    }

    /**
     * Get the personnel by username, done by passing the User representation of
     * the personnel
     *
     * @param self
     *            the self the user representation of the personnel with their
     *            username
     * @return the personnel result with the queried username
     */
    public static Personnel getByName ( final User self ) {
        try {
            return getWhere( createCriterionAsList( "self", self ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get all personnel in the DB
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<Personnel> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @return all personnel in the DB
     */
    @SuppressWarnings ( "unchecked" )
    static public List<Personnel> getPersonnel () {
        return (List<Personnel>) getAll( Personnel.class );
    }

    /**
     * Get all Personnel in the database where the passed query is true
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<Personnel> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return all Personnel in the database where the passed query is true
     */
    @SuppressWarnings ( "unchecked" )
    private static List<Personnel> getWhere ( final List<Criterion> where ) {
        return (List<Personnel>) getWhere( Personnel.class, where );
    }

    /**
     * This stores a reference to the User object that this personnel is.
     * Mandatory.
     */
    @JoinColumn ( name = "self_id", columnDefinition = "varchar(100)" )
    @OneToOne
    private User    self;

    /**
     * Whether or not the personnel is enabled
     */
    private Integer enabled;

    /**
     * The first name of the personnel
     */
    @Length ( max = 20 )
    private String  firstName;

    /**
     * The last name of the personnel
     */
    @Length ( max = 30 )
    private String  lastName;

    /**
     * The address line 1 of the personnel
     */
    @Length ( max = 50 )
    private String  address1;

    /**
     * The address line 2 of the personnel
     */
    @Length ( max = 50 )
    private String  address2;

    /**
     * The city of residence of the personnel
     */
    @Length ( max = 15 )
    private String  city;

    /**
     * The state of residence of the personnel
     */
    @Enumerated ( EnumType.STRING )
    private State   state;

    /**
     * The zipcode of the personnel
     */
    @Length ( min = 5, max = 10 )
    private String  zip;

    /**
     * The phone number of the personnel
     */
    @Length ( min = 12, max = 12 )
    private String  phone;

    /**
     * The specialty of the personnel
     */
    private String  specialty; /*
                                * Possibly consider making this an enum in the
                                * future
                                */

    /**
     * The email of the personnel
     */
    @Length ( max = 30 )
    private String  email;

    /**
     * The id of the personnel
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long    id;

    /**
     * Create a new personnel based off of the PersonnelForm
     *
     * @param form
     *            the filled-in personnel form with personnel information
     */
    public Personnel ( final PersonnelForm form ) {
        setSelf( User.getByName( form.getSelf() ) );
        setEnabled( form.getEnabled() != null ? 1 : 0 );
        setFirstName( form.getFirstName() );
        setLastName( form.getLastName() );
        setAddress1( form.getAddress1() );
        setAddress2( form.getAddress2() );
        setCity( form.getCity() );
        setState( State.valueOf( form.getState() ) );
        setZip( form.getZip() );
        setPhone( form.getPhone() );
        setSpecialty( form.getSpecialty() );
        setEmail( form.getEmail() );
        try {
            setId( Long.valueOf( form.getId() ) );
        }
        catch ( NullPointerException | NumberFormatException npe ) {
            /* Will not have ID set if fresh form */
        }
    }

    /**
     * Empty constructor necessary for Hibernate.
     */
    public Personnel () {

    }

    /**
     * Get the id of this personnel
     *
     * @return the id of this personnel
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the id of this personnel
     *
     * @param id
     *            the id to set this personnel to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the user representation of this personnel
     *
     * @return the user representation of this personnel
     */
    public User getSelf () {
        return self;
    }

    /**
     * Set the user representation of this personnel
     *
     * @param self
     *            the user representation to set this personnel to
     */
    public void setSelf ( final User self ) {
        this.self = self;
    }

    /**
     * Get whether or not this personnel is enabled
     *
     * @return whether or not this personnel is enabled
     */
    public Integer getEnabled () {
        return enabled;
    }

    /**
     * Set whether or not this personnel is enabled
     *
     * @param enabled
     *            whether or not this personnel is enabled
     */
    public void setEnabled ( final Integer enabled ) {
        this.enabled = enabled;
    }

    /**
     * Get the first name of this personnel
     *
     * @return the first name of this personnel
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Set the first name of this personnel
     *
     * @param firstName
     *            the first name to set this personnel to
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Get the last name of this personnel
     *
     * @return the last name of this personnel
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Set the last name of this personnel
     *
     * @param lastName
     *            the last name to set this personnel to
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Get the address line 1 of this personnel
     *
     * @return the address line 1 of this personnel
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Set the address line 1 of this personnel
     *
     * @param address1
     *            the address line 1 to set this personnel to
     */
    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    /**
     * Get the address line 2 of this personnel
     *
     * @return the address line 2 of this personnel
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Set the address line 2 of this personnel
     *
     * @param address2
     *            the address line 2 to set this personnel to
     */
    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    /**
     * Get the city of residence of this personnel
     *
     * @return the city of residence of this personnel
     */
    public String getCity () {
        return city;
    }

    /**
     * Set the city of residence of this personnel
     *
     * @param city
     *            the city of residence to set this personnel to
     */
    public void setCity ( final String city ) {
        this.city = city;
    }

    /**
     * Get the state of residence of this personnel
     *
     * @return the state of residence of this personnel
     */
    public State getState () {
        return state;
    }

    /**
     * Set the state of residence of this personnel
     *
     * @param state
     *            the state of residence to set this personnel to
     */
    public void setState ( final State state ) {
        this.state = state;
    }

    /**
     * Get the zipcode of this personnel
     *
     * @return the zipcode of this personnel
     */
    public String getZip () {
        return zip;
    }

    /**
     * Set the zipcode of this personnel
     *
     * @param zip
     *            the zipcode to set this personnel to
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Get the phone number of this personnel
     *
     * @return the phone number of this personnel
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Set the phone number of this personnel
     *
     * @param phone
     *            the phone number to set this personnel to
     */
    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    /**
     * Get the specialty of this personnel
     *
     * @return the specialty of this personnel
     */
    public String getSpecialty () {
        return specialty;
    }

    /**
     * Set the specialty of this personnel
     *
     * @param specialty
     *            the specialty to set this personnel to
     */
    public void setSpecialty ( final String specialty ) {
        this.specialty = specialty;
    }

    /**
     * Get the email of this personnel
     *
     * @return the email of this personnel
     */
    public String getEmail () {
        return email;
    }

    /**
     * Set the email of this personnel
     *
     * @param email
     *            the email to set this personnel to
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }

}
