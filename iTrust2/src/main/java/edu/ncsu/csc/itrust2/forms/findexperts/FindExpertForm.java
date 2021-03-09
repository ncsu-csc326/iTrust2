package edu.ncsu.csc.itrust2.forms.findexperts;

import org.hibernate.validator.constraints.Length;

/**
 * Form for user to fill out to find experts of a specified specialty, zip code,
 * and radius.
 *
 * @author nrshah4
 *
 */
public class FindExpertForm {

    /** zip code. */
    private String zip;

    /** specialty. */
    private String specialty;

    /** radius. */
    @Length ( min = 0 )
    private int    radius;

    /**
     * Constructor for empty form.
     */
    public FindExpertForm () {

    }

    /**
     * Constructor
     *
     * @param specialty
     *            specialty entered in form
     * @param zip
     *            zip code entered in form
     * @param radius
     *            radius entered in form
     */
    public FindExpertForm ( final String specialty, final String zip, final int radius ) {
        this.specialty = specialty;
        this.zip = zip;
        this.radius = radius;
    }

    /**
     * Setter for specialty.
     *
     * @param specialty
     *            the specialty to set
     */
    public void setSpecialty ( final String specialty ) {
        this.specialty = specialty;
    }

    /**
     * Getter for specialty entered in form
     *
     * @return specialty
     */
    public String getSpecialty () {
        return this.specialty;
    }

    /**
     * Setter for zip code
     *
     * @param zip
     *            the zip to set
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Getter for zip code
     *
     * @return zip
     */
    public String getZip () {
        return this.zip;
    }

    /**
     * Getter for radius
     *
     * @return radius
     */
    public int getRadius () {
        return this.radius;
    }

    /**
     * Setter for radius
     *
     * @param radius
     *            the radius to set
     */
    public void setRadius ( final int radius ) {
        this.radius = radius;

    }
}
