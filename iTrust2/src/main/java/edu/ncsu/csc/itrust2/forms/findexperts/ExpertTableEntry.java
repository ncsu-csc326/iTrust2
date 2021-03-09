package edu.ncsu.csc.itrust2.forms.findexperts;

import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;

/**
 * A single entry for the table of results on the Find Experts Page.
 *
 * @author nrshah4
 *
 */
public class ExpertTableEntry {

    /** hospital */
    private Hospital        hospital;

    /** personnel that meet the search requirements */
    private List<Personnel> personnel;

    /** distance from user in miles */
    private double          distance;

    /**
     * Empty Constructor for clean entry.
     */
    public ExpertTableEntry () {
        personnel = new ArrayList<Personnel>();

    }

    /**
     * Constructor.
     *
     * @param hospital
     *            the hospital of the entry
     * @param p
     *            the list of personnel at the hospital that meet the
     *            requirements.
     * @param dist
     *            the distance from the user's zip code.
     */
    public ExpertTableEntry ( final Hospital hospital, final List<Personnel> p, final double dist ) {
        this.hospital = hospital;
        this.personnel = p;
        this.distance = dist;
    }

    /**
     * Setter for hospital
     *
     * @param hospital
     *            the hospital to set
     */
    public void setHospital ( final Hospital hospital ) {
        this.hospital = hospital;
    }

    /**
     * Getter for hospital
     *
     * @return hospital
     */
    public Hospital getHospital () {
        return this.hospital;
    }

    /**
     * Setter for distance
     *
     * @param dist
     *            the distance to set
     */
    public void setDistance ( final double dist ) {
        this.distance = dist;
    }

    /**
     * Getter for distance
     *
     * @return distance
     */
    public double getDistance () {
        return this.distance;
    }

    /**
     * Add one personnel to list
     *
     * @param p
     *            personnel to add
     */
    public void addPersonnel ( final Personnel p ) {
        personnel.add( p );
    }

    /**
     * Set the personnel list
     * 
     * @param p
     *            the personnel list
     */
    public void setPersonnel ( final List<Personnel> p ) {
        this.personnel = p;
    }

    /**
     * Getter for personnel
     *
     * @return list of personnel
     */
    public List<Personnel> getPersonnel () {
        return this.personnel;
    }

    /**
     * To String method.
     *
     * @return String of hospital and personnel
     */
    @Override
    public String toString () {
        String s = hospital.toString();
        for ( int i = 0; i < personnel.size(); i++ ) {
            s += "\n" + personnel.get( i ).toString();
        }
        return s;
    }

}
