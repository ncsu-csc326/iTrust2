package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.Session;
import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.utils.DomainObjectCache;
import edu.ncsu.csc.itrust2.utils.HibernateUtil;

/**
 * Class representing a Patient object. This goes beyond the basic information
 * stored as part of a User and contains relevant information for a patient in
 * our medical records system.
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "Patients" )
public class Patient extends DomainObject<Patient> implements Serializable {

    /**
     * Randomly generated ID.
     */
    private static final long                         serialVersionUID = 4617248041239679701L;

    /**
     * The cache representation of the patients in the database
     */
    static private DomainObjectCache<String, Patient> cache            = new DomainObjectCache<String, Patient>(
            Patient.class );

    /**
     * Get all patients in the database
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<Patient> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @return all patients in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Patient> getPatients () {
        return (List<Patient>) getAll( Patient.class );
    }

    /**
     * Get a specific patient by username
     *
     * @param username
     *            the username of the patient to get
     * @return the patient with the queried username
     */
    public static Patient getPatient ( final String username ) {
        Patient patient = cache.get( username );
        if ( null == patient ) {
            try {
                patient = getWhere( "self_id = '" + username + "'" ).get( 0 );
                cache.put( username, patient );
            }
            catch ( final Exception e ) {
                // Exception ignored
            }
        }
        return patient;
    }

    /**
     * Helper method to pass to the DomainObject class that performs a specific
     * query on the database.
     *
     * @param where
     *            The specific query on the database
     * @return the result of the query
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Patient> getWhere ( final String where ) {
        return (List<Patient>) getWhere( Patient.class, where );
    }

    /**
     * Deletes the selected DomainObject from the database. This is operation
     * cannot be reversed.
     */
    @SuppressWarnings ( "unchecked" )
    @Override
    public void delete () {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete( this );
        session.getTransaction().commit();
        session.close();
        getCache( this.getClass() ).remove( this.getSelf().getUsername() );
    }

    /**
     * Get the patient representation of the specified user (not all users are
     * patients, aka an HCP is a user of the system, but not a patient)
     *
     * @param user
     *            the user to get the patient representation of
     * @return the corresponding patient record
     */
    public static Patient getPatient ( final User user ) {
        return getPatient( user.getUsername() );
    }

    /**
     * Empty constructor necessary for Hibernate.
     */
    public Patient () {
    }

    /**
     * Create a new patient based off of a user record
     *
     * @param self
     *            the user record
     */
    public Patient ( final User self ) {
        setSelf( self );
    }

    /**
     * Create a new patient based of a user record (found via the username)
     *
     * @param self
     *            the username
     */
    public Patient ( final String self ) {
        this( User.getByNameAndRole( self, Role.ROLE_PATIENT ) );
    }

    /**
     * Create a new patient based off of the PatientForm
     *
     * @param form
     *            the filled-in patient form with patient information
     * @throws ParseException
     *             if there is an issue in parsing the date
     */
    public Patient ( final PatientForm form ) throws ParseException {
        this( form.getSelf() );
        setMother( User.getByNameAndRole( form.getMother(), Role.ROLE_PATIENT ) );
        setFather( User.getByNameAndRole( form.getFather(), Role.ROLE_PATIENT ) );
        setFirstName( form.getFirstName() );
        setPreferredName( form.getPreferredName() );
        setLastName( form.getLastName() );
        setEmail( form.getEmail() );
        setAddress1( form.getAddress1() );
        setAddress2( form.getAddress2() );
        setCity( form.getCity() );
        setState( State.parse( form.getState() ) );
        setZip( form.getZip() );
        setPhone( form.getPhone() );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Date parsedDate = sdf.parse( form.getDateOfBirth() );
        final Calendar c = Calendar.getInstance();
        c.setTime( parsedDate );
        setDateOfBirth( c );

        if ( null != form
                .getDateOfDeath() ) { /*
                                       * Patient can't set their date of death
                                       */
            final Date parsedDeathDate = sdf.parse( form.getDateOfDeath() );
            final Calendar deathC = Calendar.getInstance();
            deathC.setTime( parsedDeathDate );
            setDateOfDeath( deathC );
            setCauseOfDeath( form.getCauseOfDeath() );
        }

        setBloodType( BloodType.parse( form.getBloodType() ) );

        setEthnicity( Ethnicity.parse( form.getEthnicity() ) );

        setGender( Gender.parse( form.getGender() ) );

        setId( form.getId() );
    }

    /**
     * This stores a reference to the User object that this patient is.
     * Mandatory.
     */
    @OneToOne
    @JoinColumn ( name = "self_id" )
    @Id
    private User      self;

    /**
     * For keeping track of the User who is the mother of this patient.
     * Optional.
     */
    @ManyToOne
    @JoinColumn ( name = "mother_id" )
    private User      mother;

    /**
     * For keeping track of the User who is the father of this patient.
     * Optional.
     */
    @ManyToOne
    @JoinColumn ( name = "father_id" )
    private User      father;

    /**
     * The first name of this patient
     */
    @Length ( max = 20 )
    private String    firstName;

    /**
     * The preferred name of this patient
     */
    @Length ( max = 20 )
    private String    preferredName;

    /**
     * The last name of this patient
     */
    @Length ( max = 30 )
    private String    lastName;

    /**
     * The email address of this patient
     */
    @Length ( max = 30 )
    private String    email;

    /**
     * The address line 1 of this patient
     */
    @Length ( max = 50 )
    private String    address1;

    /**
     * The address line 2 of this patient
     */
    @Length ( max = 50 )
    private String    address2;

    /**
     * The city of residence of this patient
     */
    @Length ( max = 15 )
    private String    city;

    /**
     * The state of residence of this patient
     */
    @Enumerated ( EnumType.STRING )
    private State     state;

    /**
     * The zip code of this patient
     */
    @Length ( min = 5, max = 10 )
    private String    zip;

    /**
     * The phone number of this patient
     */
    @Length ( min = 12, max = 12 )
    private String    phone;

    /**
     * The birthday of this patient
     */
    private Calendar  dateOfBirth;

    /**
     * The date of death of this patient
     */
    private Calendar  dateOfDeath;

    /**
     * The cause of death of this patient
     */
    private String    causeOfDeath;

    /**
     * The blood type of this patient
     */
    @Enumerated ( EnumType.STRING )
    private BloodType bloodType;

    /**
     * The ethnicity of this patient
     */
    @Enumerated ( EnumType.STRING )
    private Ethnicity ethnicity;

    /**
     * The gender of this patient
     */
    @Enumerated ( EnumType.STRING )
    private Gender    gender;

    /**
     * The id of this patient
     */
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long      id;

    /**
     * Set the id of this patient
     *
     * @param id
     *            the id to set this patient to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the id of this patient
     *
     * @return the id of this patient
     */
    @Override
    public Long getId () {
        return this.id;
    }

    /**
     * Get the user representation of this patient
     *
     * @return the user representation of this patient
     */
    public User getSelf () {
        return self;
    }

    /**
     * Set the user representation of this patient
     *
     * @param self
     *            representation the user representation to set this patient to
     */
    public void setSelf ( final User self ) {
        this.self = self;
    }

    /**
     * Get the mother of this patient
     *
     * @return the mother of this patient
     */
    public User getMother () {
        return mother;
    }

    /**
     * Set the mother of this patient
     *
     * @param mother
     *            the mother to set this patient to
     */
    public void setMother ( final User mother ) {
        this.mother = mother;
    }

    /**
     * Get the father of this patient
     *
     * @return the father of this patient
     */
    public User getFather () {
        return father;
    }

    /**
     * Set the father of this patient
     *
     * @param father
     *            the father to set this patient to
     */
    public void setFather ( final User father ) {
        this.father = father;
    }

    /**
     * Get the first name of this patient
     *
     * @return the first name of this patient
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Set the first name of this patient
     *
     * @param firstName
     *            the first name to set this patient to
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Get the preferred name of this patient
     *
     * @return the preferred name of this patient
     */
    public String getPreferredName () {
        return preferredName;
    }

    /**
     * Set the preferred name of this patient
     *
     * @param preferredName
     *            the preferred name to set this patient to
     */
    public void setPreferredName ( final String preferredName ) {
        this.preferredName = preferredName;
    }

    /**
     * Get the last name of this patient
     *
     * @return the last name of this patient
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Set the last name of this patient
     *
     * @param lastName
     *            the last name to set this patient to
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Get the email of this patient
     *
     * @return the email of this patient
     */
    public String getEmail () {
        return email;
    }

    /**
     * Set the email of this patient
     *
     * @param email
     *            the email to set this patient to
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }

    /**
     * Get the address line 1 of this patient
     *
     * @return the address line 1 of this patient
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Set the address line 1 of this patient
     *
     * @param address1
     *            the address line 1 to set this patient to
     */
    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    /**
     * Get the address line 2 of this patient
     *
     * @return the address line 2 of this patient
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Set the address line 2 of this patient
     *
     * @param address2
     *            the address line 2 to set this patient to
     */
    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    /**
     * Get the city of residence of this patient
     *
     * @return the city of residence of this patient
     */
    public String getCity () {
        return city;
    }

    /**
     * Set the city of residence of this patient
     *
     * @param city
     *            the city of residence to set this patient to
     */
    public void setCity ( final String city ) {
        this.city = city;
    }

    /**
     * Get the state of residence of this patient
     *
     * @return the state of residence of this patient
     */
    public State getState () {
        return state;
    }

    /**
     * Set the state of residence of this patient
     *
     * @param state
     *            the state of residence to set this patient to
     */
    public void setState ( final State state ) {
        this.state = state;
    }

    /**
     * Get the zipcode of this patient
     *
     * @return the zipcode of this patient
     */
    public String getZip () {
        return zip;
    }

    /**
     * Set the zipcode of this patient
     *
     * @param zip
     *            the zipcode to set this patient to
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Get the phone number of this patient
     *
     * @return the phone number of this patient
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Set the phone number of this patient
     *
     * @param phone
     *            the phone number to set this patient to
     */
    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    /**
     * Get the date of birth of this patient
     *
     * @return the date of birth of this patient
     */
    public Calendar getDateOfBirth () {
        return dateOfBirth;
    }

    /**
     * Set the date of birth of this patient
     *
     * @param dateOfBirth
     *            the date of birth to set this patient to
     */
    public void setDateOfBirth ( final Calendar dateOfBirth ) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Get the date of death of this patient
     *
     * @return the date of death of this patient
     */
    public Calendar getDateOfDeath () {
        return dateOfDeath;
    }

    /**
     * Set the date of death of this patient
     *
     * @param dateOfDeath
     *            the date of death to set this patient to
     */
    public void setDateOfDeath ( final Calendar dateOfDeath ) {
        this.dateOfDeath = dateOfDeath;
    }

    /**
     * Get the cause of death of this patient
     *
     * @return the cause of death of this patient
     */
    public String getCauseOfDeath () {
        return causeOfDeath;
    }

    /**
     * Set the cause of death of this patient
     *
     * @param causeOfDeath
     *            the cause of death to set this patient to
     */
    public void setCauseOfDeath ( final String causeOfDeath ) {
        this.causeOfDeath = causeOfDeath;
    }

    /**
     * Get the blood type of this patient
     *
     * @return the blood type of this patient
     */
    public BloodType getBloodType () {
        return bloodType;
    }

    /**
     * Set the blood type of this patient
     *
     * @param bloodType
     *            the blood type to set this patient to
     */
    public void setBloodType ( final BloodType bloodType ) {
        this.bloodType = bloodType;
    }

    /**
     * Get the ethnicity of this patient
     *
     * @return the ethnicity of this patient
     */
    public Ethnicity getEthnicity () {
        return ethnicity;
    }

    /**
     * Set the ethnicity of this patient
     *
     * @param ethnicity
     *            the ethnicity to set this patient to
     */
    public void setEthnicity ( final Ethnicity ethnicity ) {
        this.ethnicity = ethnicity;
    }

    /**
     * Get the gender of this patient
     *
     * @return the gender of this patient
     */
    public Gender getGender () {
        return gender;
    }

    /**
     * Set the gender of this patient
     *
     * @param gender
     *            the gender to set this patient to
     */
    public void setGender ( final Gender gender ) {
        this.gender = gender;
    }

}
