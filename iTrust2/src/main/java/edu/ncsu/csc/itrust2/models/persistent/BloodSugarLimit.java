package edu.ncsu.csc.itrust2.models.persistent;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;

/**
 * Class representing a Patient's blood sugar limits. These limits have default
 * values, but can be updated by a HCP.
 *
 * @author Sam Fields
 *
 */
@Entity
@Table ( name = "BloodSugarLimits" )
public class BloodSugarLimit extends DomainObject<Patient> {

    /** Default fasting limit for a patient */
    static private final int DEFAULT_FASTING_LIMIT = 100;

    /** Default meal limit for a patient */
    static private final int DEFAULT_MEAL_LIMIT    = 140;

    /**
     * The id of these limits
     */
    @GeneratedValue ( strategy = GenerationType.AUTO )
    @Id
    private Long      id;

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private Patient   patient;

    /**
     * Blood sugar limit for fasting
     */
    @Min ( 80 )
    @Max ( 130 )
    private int       fastingLimit;

    /**
     * Blood sugar limit for meals
     */
    @Min ( 120 )
    @Max ( 180 )
    private int       mealLimit;

    /**
     * Empty constructor necessary for Hibernate.
     */
    public BloodSugarLimit () {

    }

    /**
     * Create and initializes the blood sugar limits for a patient. The limits
     * are given the default values.
     *
     * @param patient
     *            the patient the limits are for
     */
    public BloodSugarLimit ( Patient patient ) {
        setPatient( patient );
        setFastingLimit( DEFAULT_FASTING_LIMIT );
        setMealLimit( DEFAULT_MEAL_LIMIT );
    }

    /**
     * Get the blood sugar limits for a specific patient
     *
     * @param patient
     *            the patient the limits are for
     * @return the limits of the queried patient
     */
    public static BloodSugarLimit getByPatient ( Patient patient ) {
        try {
            return getWhere( eqList( "patient", patient ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Helper method to pass to the DomainObject class that performs a specific
     * query on the database.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The list of all limit records found matching the Criterion
     *         provided
     */
    @SuppressWarnings ( "unchecked" )
    private static List<BloodSugarLimit> getWhere ( final List<Criterion> where ) {
        return (List<BloodSugarLimit>) getWhere( BloodSugarLimit.class, where );
    }

    /**
     * Returns the patient that the limits belong to
     *
     * @return the patient that the limits belong to
     */
    public Patient getPatient () {
        return patient;
    }

    /**
     * Sets the patient the limits are for
     *
     * @param patient
     *            the patient to set
     */
    public void setPatient ( Patient patient ) {
        this.patient = patient;
    }

    /**
     * Set the id of this limit
     *
     * @param id
     *            the id to set this patient to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the id of this limit
     *
     * @return the id of this patient
     */
    @Override
    public Long getId () {
        return this.id;
    }

    /**
     * Returns the fasting blood sugar limit imposed by the HCP
     *
     * @return the fasting blood sugar limit
     */
    public int getFastingLimit () {
        return fastingLimit;
    }

    /**
     * Sets the fasting blood sugar limit
     *
     * @param fastingLimit
     *            the fasting blood sugar limit
     */
    public void setFastingLimit ( final int fastingLimit ) {
        this.fastingLimit = fastingLimit;
    }

    /**
     * Returns the meal blood sugar limit imposed by the HCP
     *
     * @return the meal blood sugar limit
     */
    public int getMealLimit () {
        return mealLimit;
    }

    /**
     * Sets the meal blood sugar limit
     *
     * @param mealLimit
     *            the meal blood sugar limit
     */
    public void setMealLimit ( final int mealLimit ) {
        this.mealLimit = mealLimit;
    }

}
