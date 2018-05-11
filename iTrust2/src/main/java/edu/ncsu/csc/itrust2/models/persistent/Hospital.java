package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.forms.admin.HospitalForm;
import edu.ncsu.csc.itrust2.models.enums.State;

/**
 * Class representing a Hospital object, as stored in the DB
 *
 * @author Kai Presler-Marshall
 *
 */

@Entity
@Table ( name = "Hospitals" )
public class Hospital extends DomainObject<Hospital> implements Serializable {
    /**
     * Used for serializing the object.
     */
    private static final long serialVersionUID = 1L;

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
     * Retrieve a Hospital from the database or in-memory cache by name.
     *
     * @param name
     *            Name of the Hospital to retrieve
     * @return The Hospital found, or null if none was found.
     */
    public static Hospital getByName ( final String name ) {
        try {
            return getWhere( createCriterionAsList( "name", name ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Retrieve all matching Hospitals from the database that match a where
     * clause provided.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The matching Hospitals
     */
    @SuppressWarnings ( "unchecked" )
    private static List<Hospital> getWhere ( final List<Criterion> where ) {
        return (List<Hospital>) getWhere( Hospital.class, where );
    }

    /**
     * Retrieve all Hospitals from the database
     *
     * @return Hospitals found
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Hospital> getHospitals () {
        return (List<Hospital>) getAll( Hospital.class );
    }

    /**
     * Construct an empty Hospital record. Used for Hibernate.
     */
    public Hospital () {
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
    @Length ( min = 5, max = 5 )
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

    /**
     * Retrieves the ID (Name) of this Hospital
     */
    @Override
    public String getId () {
        return getName();
    }

}
