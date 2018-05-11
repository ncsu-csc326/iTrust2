package edu.ncsu.csc.itrust2.forms.hcp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import edu.ncsu.csc.itrust2.models.persistent.Prescription;

/**
 * A form for REST API communication. Contains fields for constructing
 * Prescription objects.
 *
 * @author Connor
 */
public class PrescriptionForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String            drug;
    private int               dosage;
    private String            startDate;
    private String            endDate;
    private int               renewals;
    private String            patient;
    private Long              id;

    /**
     * Empty constructor for filling in fields without a Prescription object.
     */
    public PrescriptionForm () {
    }

    /**
     * Constructs a new form with information from the given prescription.
     *
     * @param prescription
     *            the prescription object
     */
    public PrescriptionForm ( Prescription prescription ) {
        setId( prescription.getId() );
        setDrug( prescription.getDrug().getCode() );
        setDosage( prescription.getDosage() );
        final SimpleDateFormat tempDate = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        setStartDate( tempDate.format( prescription.getStartDate().getTime() ) );
        setEndDate( tempDate.format( prescription.getEndDate().getTime() ) );
        setRenewals( prescription.getRenewals() );
        setPatient( prescription.getPatient().getUsername() );
    }

    /**
     * Returns the drug associated with this Prescription
     *
     * @return the prescription's drug
     */
    public String getDrug () {
        return drug;
    }

    /**
     * Associates this prescription with the given drug.
     *
     * @param drug
     *            the drug
     */
    public void setDrug ( String drug ) {
        this.drug = drug;
    }

    /**
     * Returns the prescribed dosage of the drug.
     *
     * @return the drug dosage
     */
    public int getDosage () {
        return dosage;
    }

    /**
     * Sets the prescription's dosage in milligrams.
     *
     * @param dosage
     *            prescription dosage
     */
    public void setDosage ( int dosage ) {
        this.dosage = dosage;
    }

    /**
     * Returns the drug's first valid day.
     *
     * @return the start date
     */
    public String getStartDate () {
        return startDate;
    }

    /**
     * Sets the prescription's first valid day to the given date.
     *
     * @param startDate
     *            the first valid day
     */
    public void setStartDate ( String startDate ) {
        this.startDate = startDate;
    }

    /**
     * Returns the prescription's final valid date.
     *
     * @return the prescription's end date
     */
    public String getEndDate () {
        return endDate;
    }

    /**
     * Sets the prescription's final valid date.
     *
     * @param endDate
     *            the end date
     */
    public void setEndDate ( String endDate ) {
        this.endDate = endDate;
    }

    /**
     * Gets the prescription's number of renewals.
     *
     * @return the number of renewals
     */
    public int getRenewals () {
        return renewals;
    }

    /**
     * Sets the prescription's number of renewals to the given number.
     *
     * @param renewals
     *            the number of renewals
     */
    public void setRenewals ( int renewals ) {
        this.renewals = renewals;
    }

    /**
     * Returns the user associated with this prescription.
     *
     * @return the patient
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Sets the prescription's patient to the given user
     *
     * @param patient
     *            the patient
     */
    public void setPatient ( String patient ) {
        this.patient = patient;
    }

    /**
     * Returns the id associated with the Prescription.
     *
     * @return the prescription id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the Prescription's unique id.
     *
     * @param id
     *            the prescription id
     */
    public void setId ( Long id ) {
        this.id = id;
    }
}
