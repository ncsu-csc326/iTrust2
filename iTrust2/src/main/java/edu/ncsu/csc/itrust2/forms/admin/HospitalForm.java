package edu.ncsu.csc.itrust2.forms.admin;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.persistent.Hospital;

/**
 * Form used for creating a new Hospital. Will be parsed into an actual Hospital
 * object to be saved.
 *
 * @author Kai Presler-Marshall
 *
 */
public class HospitalForm {

    /**
     * Name of the Hospital
     */
    @NotEmpty
    @Length ( max = 255 )
    private String name;

    /**
     * Address of the Hospital
     */
    @NotEmpty
    @Length ( max = 255 )
    private String address;

    /**
     * ZIP Code of the Hospital
     */
    @NotEmpty
    @Length ( min = 5, max = 5 )
    private String zip;

    /**
     * State of the Hospital
     */
    @NotEmpty
    @Length ( max = 255 )
    private String state;

    /**
     * Creates an empty HospitalForm object. Used by the controllers for filling
     * out a new Hospital.
     */
    public HospitalForm () {
    }

    /**
     * Creates a HospitalForm from the Hospital provided. Used to convert a
     * Hospital to a form that can be edited.
     *
     * @param h
     *            Hospital to convert to its Form.
     */
    public HospitalForm ( final Hospital h ) {
        setName( h.getName() );
        setAddress( h.getAddress() );
        setZip( h.getZip() );
        setState( h.getState().getName() );
    }

    /**
     * Gets the name of the Hospital from this HospitalForm
     *
     * @return Name of the Hospital
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name of the Hospital in this HospitalForm
     *
     * @param name
     *            New Name of the Hospital
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the Address of the Hospital in this HospitalForm.
     *
     * @return The address of the Hospital
     */
    public String getAddress () {
        return address;
    }

    /**
     * Sets the Address of this Hospital.
     *
     * @param address
     *            New Address to set.
     */
    public void setAddress ( final String address ) {
        this.address = address;
    }

    /**
     * Gets the ZIP code of the Hospital in this form
     *
     * @return ZIP code of the Hospital
     */
    public String getZip () {
        return zip;
    }

    /**
     * Sets the ZIP code of this Hospital
     *
     * @param zip
     *            ZIP code to set for the hospital
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Retrieves the State of this Hospital
     *
     * @return State of the Hospital
     */
    public String getState () {
        return state;
    }

    /**
     * Sets the State of the Hospital
     *
     * @param state
     *            New State to set
     */
    public void setState ( final String state ) {
        this.state = state;
    }

}
