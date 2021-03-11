package edu.ncsu.csc.iTrust2.forms;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.iTrust2.models.Patient;

/**
 * Form for user to fill out to add a Patient to the system.
 *
 * @author Kai Presler-Marshall
 *
 */
public class PatientForm {

    /**
     * For Spring
     */
    public PatientForm () {

    }

    /**
     * Populate the patient form from a patient object
     *
     * @param patient
     *            the patient object to set the form with
     */
    public PatientForm ( final Patient patient ) {
        if ( null == patient ) {
            return; /* Nothing to do here */
        }
        setFirstName( patient.getFirstName() );
        setLastName( patient.getLastName() );
        setPreferredName( patient.getPreferredName() );
        setEmail( patient.getEmail() );
        setAddress1( patient.getAddress1() );
        setAddress2( patient.getAddress2() );
        setCity( patient.getCity() );
        if ( null != patient.getState() ) {
            setState( patient.getState().toString() );
        }
        setZip( patient.getZip() );
        setPhone( patient.getPhone() );

        if ( null != patient.getDateOfBirth() ) {
            setDateOfBirth( patient.getDateOfBirth().toString() );
        }
        if ( null != patient.getDateOfDeath() ) {
            setDateOfDeath( patient.getDateOfDeath().toString() );
        }

        setCauseOfDeath( patient.getCauseOfDeath() );

        if ( null != patient.getBloodType() ) {
            setBloodType( patient.getBloodType().toString() );
        }

        if ( null != patient.getEthnicity() ) {
            setEthnicity( patient.getEthnicity().toString() );
        }

        if ( null != patient.getGender() ) {
            setGender( patient.getGender().toString() );
        }

    }

    @Length ( max = 20 )
    private String  username;

    /** The first name of the patient **/
    @Length ( max = 20 )
    private String  firstName;

    /** The preferred name of the patient **/
    @Length ( max = 20 )
    private String  preferredName;

    /** The last name of the patient **/
    @Length ( max = 30 )
    private String  lastName;

    /** The email of the patient **/
    @Length ( max = 30 )
    private String  email;

    /** The address line 1 of the patient **/
    @Length ( max = 50 )
    private String  address1;

    /** The address line 2 of the patient **/
    @Length ( max = 50 )
    private String  address2;

    /** The city of residence of the patient **/
    @Length ( max = 15 )
    private String  city;

    /** The state of residence of the patient **/
    @Length ( min = 2, max = 2 )
    private String  state;

    /** The zipcode of the patient **/
    @Length ( min = 5, max = 10 )
    private String  zip;

    /** The phone number of the patient **/
    @Pattern ( regexp = "(^[0-9]{3}-[0-9]{3}-[0-9]{4}$)", message = "Phone number must be formatted as xxx-xxx-xxxx" )
    private String  phone;

    /** The date of birth of the patient **/
    @Length ( min = 10, max = 10 )
    private String  dateOfBirth;

    /** The date of death of the patient **/
    private String  dateOfDeath;

    /** The cause of death of the patient **/
    @Length ( max = 50 )
    private String  causeOfDeath;

    /** The blood type of the patient **/
    @NotEmpty
    private String  bloodType;

    /** The ethnicity of the patient **/
    @NotEmpty
    private String  ethnicity;

    /** The gender of the patient **/
    @NotEmpty
    private String  gender;

    /**
     * Whether the patient is diabetic/pre-diabetic or not
     */
    @NotNull
    private boolean isDiabetic;

    /**
     * Blood sugar limit for fasting
     */
    @Min ( 80 )
    @Max ( 130 )
    private int     fastingLimit;

    /**
     * Blood sugar limit for meals
     */
    @Min ( 120 )
    @Max ( 180 )
    private int     mealLimit;

    /**
     * Get the first name of the patient
     *
     * @return the first name of the patient
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Set the first name of the patient
     *
     * @param firstName
     *            the first name of the patient
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Get the preferred name of the patient
     *
     * @return the preferred name of the patient
     */
    public String getPreferredName () {
        return preferredName;
    }

    /**
     * Set the preferred name of the patient
     *
     * @param preferredName
     *            the preferred name of the patient
     */
    public void setPreferredName ( final String preferredName ) {
        this.preferredName = preferredName;
    }

    /**
     * Get the last name of the patient
     *
     * @return the last name of the patient
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Set the last name of the patient
     *
     * @param lastName
     *            the last name of the patient
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Get the email of the patient
     *
     * @return the email of the patient
     */
    public String getEmail () {
        return email;
    }

    /**
     * Set the email of the patient
     *
     * @param email
     *            the email of the patient
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }

    /**
     * Get the address line 1 of the patient
     *
     * @return the address line 1 of the patient
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Set the address line 1 of the patient
     *
     * @param address1
     *            the address line 1 of the patient
     */
    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    /**
     * Get the address line 2 of the patient
     *
     * @return the address line 2 of the patient
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Set the address line 2 of the patient
     *
     * @param address2
     *            the address line 2 of the patient
     */
    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    /**
     * Get the city of residence of the patient
     *
     * @return the city of residence of the patient
     */
    public String getCity () {
        return city;
    }

    /**
     * Set the city of residence of the patient
     *
     * @param city
     *            the city of residence of the patient
     */
    public void setCity ( final String city ) {
        this.city = city;
    }

    /**
     * Get the state of residence of the patient
     *
     * @return the state of residence of the patient
     */
    public String getState () {
        return state;
    }

    /**
     * Set the state of residence of the patient
     *
     * @param state
     *            the state of residence of the patient
     */
    public void setState ( final String state ) {
        this.state = state;
    }

    /**
     * Get the zipcode of the patient
     *
     * @return the zipcode of the patient
     */
    public String getZip () {
        return zip;
    }

    /**
     * Set the zipcode of the patient
     *
     * @param zip
     *            the zipcode of the patient
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Get the phone number of the patient
     *
     * @return the phone number of the patient
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Set the phone number of the patient
     *
     * @param phone
     *            the phone number of the patient
     */
    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    /**
     * Get the date of birth of the patient
     *
     * @return the date of birth of the patient
     */
    public String getDateOfBirth () {
        return dateOfBirth;
    }

    /**
     * Set the date of birth of the patient
     *
     * @param dateOfBirth
     *            the date of birth of the patient
     */
    public void setDateOfBirth ( final String dateOfBirth ) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Get the date of death of the patient
     *
     * @return the date of death of the patient
     */
    public String getDateOfDeath () {
        return dateOfDeath;
    }

    /**
     * Set the date of death of the patient
     *
     * @param dateOfDeath
     *            the date of death of the patient
     */
    public void setDateOfDeath ( final String dateOfDeath ) {
        this.dateOfDeath = dateOfDeath;
    }

    /**
     * Get the cause of death of the patient
     *
     * @return the cause of death of the patient
     */
    public String getCauseOfDeath () {
        return causeOfDeath;
    }

    /**
     * Set the cause of death of the patient
     *
     * @param causeOfDeath
     *            the cause of death of the patient
     */
    public void setCauseOfDeath ( final String causeOfDeath ) {
        this.causeOfDeath = causeOfDeath;
    }

    /**
     * Get the blood type of the patient
     *
     * @return the blood type of the patient
     */
    public String getBloodType () {
        return bloodType;
    }

    /**
     * Set the blood type of the patient
     *
     * @param bloodType
     *            the blood type of the patient
     */
    public void setBloodType ( final String bloodType ) {
        this.bloodType = bloodType;
    }

    /**
     * Get the ethnicity of the patient
     *
     * @return the ethnicity of the patient
     */
    public String getEthnicity () {
        return ethnicity;
    }

    /**
     * Set the ethnicity of the patient
     *
     * @param ethnicity
     *            the ethnicity of the patient
     */
    public void setEthnicity ( final String ethnicity ) {
        this.ethnicity = ethnicity;
    }

    /**
     * Get the gender of the patient
     *
     * @return the gender of the patient
     */
    public String getGender () {
        return gender;
    }

    /**
     * Set the gender of the patient
     *
     * @param gender
     *            the gender of the patient
     */
    public void setGender ( final String gender ) {
        this.gender = gender;
    }

    /**
     * Get the username of the patient
     *
     * @return the username of the patient
     */
    public String getUsername () {
        return username;
    }

    /**
     * Set the username of the patient
     *
     * @param username
     *            the username of the patient
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

}
