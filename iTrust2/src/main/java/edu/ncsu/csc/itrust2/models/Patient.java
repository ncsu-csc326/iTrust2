package edu.ncsu.csc.iTrust2.models;

import java.time.LocalDate;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateConverter;

import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.iTrust2.adapters.LocalDateAdapter;
import edu.ncsu.csc.iTrust2.forms.PatientForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.enums.BloodType;
import edu.ncsu.csc.iTrust2.models.enums.Ethnicity;
import edu.ncsu.csc.iTrust2.models.enums.Gender;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;

@Entity
public class Patient extends User {

    /**
     * The first name of this patient
     */
    @Length ( min = 1 )
    private String    firstName;

    /**
     * The preferred name of this patient
     */
    @Length ( max = 20 )
    private String    preferredName;

    /**
     * The last name of this patient
     */
    @Length ( min = 1 )
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
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = LocalDateConverter.class )
    @JsonAdapter ( LocalDateAdapter.class )
    private LocalDate dateOfBirth;

    /**
     * The date of death of this patient
     */
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = LocalDateConverter.class )
    @JsonAdapter ( LocalDateAdapter.class )
    private LocalDate dateOfDeath;

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
     * For Hibernate
     */
    public Patient () {

    }

    public Patient ( final UserForm uf ) {
        super( uf );
        if ( !getRoles().contains( Role.ROLE_PATIENT ) ) {
            throw new IllegalArgumentException( "Attempted to create a Patient record for a non-Patient user!" );
        }
    }

    public Patient update ( final PatientForm form ) {
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

        setDateOfBirth( LocalDate.parse( form.getDateOfBirth() ) );

        // Patient can't set their date of death
        if ( form.getDateOfDeath() != null ) {
            setDateOfDeath( LocalDate.parse( form.getDateOfDeath() ) );
            setCauseOfDeath( form.getCauseOfDeath() );
        }

        setBloodType( BloodType.parse( form.getBloodType() ) );

        setEthnicity( Ethnicity.parse( form.getEthnicity() ) );

        setGender( Gender.parse( form.getGender() ) );

        return this;
    }

    public String getFirstName () {
        return firstName;
    }

    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    public String getPreferredName () {
        return preferredName;
    }

    public void setPreferredName ( final String preferredName ) {
        this.preferredName = preferredName;
    }

    public String getLastName () {
        return lastName;
    }

    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    public String getEmail () {
        return email;
    }

    public void setEmail ( final String email ) {
        this.email = email;
    }

    public String getAddress1 () {
        return address1;
    }

    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    public String getAddress2 () {
        return address2;
    }

    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    public String getCity () {
        return city;
    }

    public void setCity ( final String city ) {
        this.city = city;
    }

    public State getState () {
        return state;
    }

    public void setState ( final State state ) {
        this.state = state;
    }

    public String getZip () {
        return zip;
    }

    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    public String getPhone () {
        return phone;
    }

    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth () {
        return dateOfBirth;
    }

    public void setDateOfBirth ( final LocalDate dateOfBirth ) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath () {
        return dateOfDeath;
    }

    public void setDateOfDeath ( final LocalDate dateOfDeath ) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getCauseOfDeath () {
        return causeOfDeath;
    }

    public void setCauseOfDeath ( final String causeOfDeath ) {
        this.causeOfDeath = causeOfDeath;
    }

    public BloodType getBloodType () {
        return bloodType;
    }

    public void setBloodType ( final BloodType bloodType ) {
        this.bloodType = bloodType;
    }

    public Ethnicity getEthnicity () {
        return ethnicity;
    }

    public void setEthnicity ( final Ethnicity ethnicity ) {
        this.ethnicity = ethnicity;
    }

    public Gender getGender () {
        return gender;
    }

    public void setGender ( final Gender gender ) {
        this.gender = gender;
    }

}
