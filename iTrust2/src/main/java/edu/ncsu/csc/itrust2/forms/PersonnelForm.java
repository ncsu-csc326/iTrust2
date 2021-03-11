package edu.ncsu.csc.iTrust2.forms;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

/**
 * Form for registering a user as an iTrust2 personnel or for editing their
 * existing information. Used for all non-patient types of users
 *
 * @author Kai Presler-Marshall
 *
 */
public class PersonnelForm {

    /**
     * Username of the iTrust2 personnel to make a Personnel object for
     */
    private String username;

    /**
     * First name of the Personnel
     */
    @Length ( max = 20 )
    private String firstName;

    /**
     * Last name of the Personnel
     */
    @Length ( max = 30 )
    private String lastName;

    /**
     * Address1 of the Personnel
     */
    @Length ( max = 50 )
    private String address1;

    /**
     * Address2 of the Personnel
     */
    @Length ( max = 50 )
    private String address2;

    /**
     * City of the Personnel
     */
    @Length ( max = 15 )
    private String city;

    /**
     * State of the Personnel
     */
    @Length ( min = 2, max = 2 )
    private String state;

    /**
     * Zip of the Personnel
     */
    @Length ( min = 5, max = 10 )
    private String zip;

    /**
     * Phone of the Personnel
     */
    @Pattern ( regexp = "(^[0-9]{3}-[0-9]{3}-[0-9]{4}$)", message = "Phone number must be formatted as xxx-xxx-xxxx" )
    private String phone;

    /**
     * Email of the Personnel
     */
    @Length ( max = 30 )
    private String email;

    /**
     * Creates a PersonnelForm object. For initializing a blank form
     */
    public PersonnelForm () {

    }

    /**
     * Get Username of the personnel
     *
     * @return The Personnel's username
     */
    public String getUsername () {
        return username;
    }

    /**
     * Set username of the Personenl
     *
     * @param username
     *            The personnel's username
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * Get the first name of the personnel
     *
     * @return Personnel's first name
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Set the First Name of the Personnel
     *
     * @param firstName
     *            New FirstName to set
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Get the last name of the Personnel
     *
     * @return The last name of the Personnel
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Set the last name of the Personnel
     *
     * @param lastName
     *            New last name to set
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Get Address1 of the Personnel
     *
     * @return Address1 of Personnel
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Set Address1 of Personnel
     *
     * @param address1
     *            New Address1 to set
     */
    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    /**
     * Get Address2 of Personnel
     *
     * @return Address2 of personnel
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Set Address2 of Personnel
     *
     * @param address2
     *            New Address2 to set
     */
    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    /**
     * Get the City of the Personnel
     *
     * @return Personnel's city of their address
     */
    public String getCity () {
        return city;
    }

    /**
     * Set the city of the Personnel
     *
     * @param city
     *            New city to set
     */
    public void setCity ( final String city ) {
        this.city = city;
    }

    /**
     * Get the Personnel's state
     *
     * @return State of the Personnel
     */
    public String getState () {
        return state;
    }

    /**
     * Set the State of the Personnel
     *
     * @param state
     *            New State to set
     */
    public void setState ( final String state ) {
        this.state = state;
    }

    /**
     * Get the ZIP of the Personnel
     *
     * @return The Personnel's ZIP
     */
    public String getZip () {
        return zip;
    }

    /**
     * Set the ZIP of the Personnel
     *
     * @param zip
     *            New ZIP to set
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Get the Phone number of the personnel
     *
     * @return The Personnel's phone number
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Set the Phone Number of the Personnel
     *
     * @param phone
     *            New phone number to set
     */
    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    /**
     * Get the Email of the Personnel
     *
     * @return Personnel's email
     */
    public String getEmail () {
        return email;
    }

    /**
     * Set the Email of the Personnel
     *
     * @param email
     *            The Personnel's new email
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }

}
