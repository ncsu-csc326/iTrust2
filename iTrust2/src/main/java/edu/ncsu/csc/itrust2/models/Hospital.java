package edu.ncsu.csc.iTrust2.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.iTrust2.forms.HospitalForm;
import edu.ncsu.csc.iTrust2.models.enums.State;

/**
 * Class representing a Hospital object, as stored in the DB
 *
 * @author Kai Presler-Marshall
 *
 */

@Entity
public class Hospital extends DomainObject implements Serializable {
    /**
     * Used for serializing the object.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a Hospital object from all of its individual fields.
     *
     * @param name
     *            Name of the Hospital
     * @param address
     *            Address of the Hospital
     * @param zip
     *            ZIP of the Hospital
     * @param state
     *            State of the Hospital
     */
    public Hospital ( final String name, final String address, final String zip, final String state ) {
        setName( name );
        setAddress( address );
        setZip( zip );
        setState( State.parse( state ) );
    }

    /**
     * Construct an empty Hospital record. Used for Hibernate.
     */
    public Hospital () {
    }

    /**
     * Construct a Hospital object from the HospitalForm object provided
     *
     * @param hf
     *            A HospitalForm to convert to a Hospital
     */
    public Hospital ( final HospitalForm hf ) {
        setName( hf.getName() );
        setAddress( hf.getAddress() );
        setZip( hf.getZip() );
        setState( State.parse( hf.getState() ) );
    }

    /**
     * Update this Hospital object from the HospitalForm object provided
     *
     * @param hf
     *            A HospitalForm to convert to a Hospital
     * @return `this` Hospital object, after updates
     */
    public Hospital update ( final HospitalForm hf ) {
        setName( hf.getName() );
        setAddress( hf.getAddress() );
        setZip( hf.getZip() );
        setState( State.parse( hf.getState() ) );
        return this;
    }

    /**
     * Name of the Hospital
     */
    @NotEmpty
    @Length ( max = 100 )
    @Id
    private String name;

    /**
     * Address of the Hospital
     */
    @NotEmpty
    @Length ( max = 100 )
    private String address;

    /**
     * State of the hospital
     */
    @Enumerated ( EnumType.STRING )
    private State  state;

    /**
     * ZIP code of the Hospital
     */
    @NotEmpty
    @Length ( min = 5, max = 10 )
    private String zip;

    /**
     * Retrieves the name of this Hospital
     *
     * @return The Name of the Hospital
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of this Hospital
     *
     * @param name
     *            New Name for the Hospital
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the Address of this Hospital
     *
     * @return Address of the Hospital
     */
    public String getAddress () {
        return address;
    }

    /**
     * Sets the Address of this Hospital
     *
     * @param address
     *            New Address of the Hospital
     */
    public void setAddress ( final String address ) {
        this.address = address;
    }

    /**
     * Gets the State of this Hospital
     *
     * @return The State of the Hospital
     */
    public State getState () {
        return state;
    }

    /**
     * Sets the State of this Hospital
     *
     * @param state
     *            New State of the Hospital
     */
    public void setState ( final State state ) {
        this.state = state;
    }

    /**
     * Gets the ZIP code of this Hospital
     *
     * @return The ZIP of the Hospital
     */
    public String getZip () {
        return zip;
    }

    /**
     * Sets the ZIP of this Hospital
     *
     * @param zip
     *            New ZIP code for the Hospital
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    @Override
    public String toString () {
        final String s = this.name + "  " + this.address;
        return s;
    }

    @Override
    public Serializable getId () {
        return getName();
    }

}
