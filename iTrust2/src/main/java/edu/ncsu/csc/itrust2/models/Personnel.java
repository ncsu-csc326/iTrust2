package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.iTrust2.forms.PersonnelForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;

@Entity
public class Personnel extends User {

    /**
     * For Hibernate
     */
    public Personnel () {

    }

    public Personnel ( final UserForm uf ) {
        super( uf );
        if ( getRoles().contains( Role.ROLE_PATIENT ) ) {
            throw new IllegalArgumentException( "Attempted to create a Personnel record for a non-Patient user!" );
        }
    }

    /**
     * The first name of the personnel
     */
    @Length ( max = 20 )
    private String firstName;

    /**
     * The last name of the personnel
     */
    @Length ( max = 30 )
    private String lastName;

    /**
     * The address line 1 of the personnel
     */
    @Length ( max = 50 )
    private String address1;

    /**
     * The address line 2 of the personnel
     */
    @Length ( max = 50 )
    private String address2;

    /**
     * The city of residence of the personnel
     */
    @Length ( max = 15 )
    private String city;

    /**
     * The state of residence of the personnel
     */
    @Enumerated ( EnumType.STRING )
    private State  state;

    /**
     * The zipcode of the personnel
     */
    @Length ( min = 5, max = 10 )
    private String zip;

    /**
     * The phone number of the personnel
     */
    @Length ( min = 12, max = 12 )
    private String phone;

    /**
     * The email of the personnel
     */
    @Length ( max = 30 )
    private String email;

    /**
     * The id of the hospital the personnel works at
     */
    private String hospitalId;

    /**
     * Create a new personnel based off of the PersonnelForm
     *
     * @param form
     *            the filled-in personnel form with personnel information
     * @return `this` Personnel, after updating from form
     */
    public Personnel update ( final PersonnelForm form ) {

        setFirstName( form.getFirstName() );
        setLastName( form.getLastName() );
        setAddress1( form.getAddress1() );
        setAddress2( form.getAddress2() );
        setCity( form.getCity() );
        setState( State.valueOf( form.getState() ) );
        setZip( form.getZip() );
        setPhone( form.getPhone() );
        setEmail( form.getEmail() );

        return this;
    }

    /**
     * Retrieves the first name of this personnel
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

    /**
     * Set hospital id
     *
     * @param id
     *            the hospital's id
     */
    public void setHospitalId ( final String id ) {
        this.hospitalId = id;
    }

    /**
     * Get the hospital id
     *
     * @return hospitalId
     *
     */
    public String getHospitalId () {
        return this.hospitalId;
    }

    /**
     * To string method
     *
     * @return string rep. of Personnel.
     */
    @Override
    public String toString () {
        final String s = this.firstName + " " + this.lastName + " " + this.email;
        return s;
    }

}
