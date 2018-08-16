
package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.Role;

/**
 * Represents a prescription in the system. Each prescription is associated with
 * a patient and a drug.
 *
 * @author Connor
 * @author Kai Presler-Marshall
 */
@Entity
@Table ( name = "Prescriptions" )
public class Prescription extends DomainObject<Prescription> {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long     id;

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "drug_id" )
    private Drug     drug;

    @Min ( 1 )
    @JoinColumn ( name = "dosage_id" )
    private int      dosage;

    @NotNull
    @JoinColumn ( name = "start_id" )
    private Calendar startDate;

    @NotNull
    @JoinColumn ( name = "end_id" )
    private Calendar endDate;

    @Min ( 0 )
    @JoinColumn ( name = "renewal_id" )
    private int      renewals;

    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User     patient;

    /**
     * Empty constructor for Hibernate.
     */
    public Prescription () {
    }

    /**
     * Construct a new Prescription using the details in the given form.
     *
     * @param form
     *            the prescription form
     */
    public Prescription ( final PrescriptionForm form ) {
        setDrug( Drug.getByCode( form.getDrug() ) );
        setDosage( form.getDosage() );
        setRenewals( form.getRenewals() );
        setPatient( User.getByName( form.getPatient() ) );

        if ( form.getId() != null ) {
            setId( form.getId() );
        }

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        Date parsedDate = null;
        try {
            parsedDate = sdf.parse( form.getStartDate() );
        }
        catch ( final ParseException e ) {
            // Ignore, Hibernate will catch the null date
        }
        Calendar c = Calendar.getInstance();
        c.setTime( parsedDate );
        setStartDate( c );

        try {
            parsedDate = sdf.parse( form.getEndDate() );
        }
        catch ( final ParseException e ) {
            // Ignore, Hibernate will catch the null date
        }
        c = Calendar.getInstance();
        c.setTime( parsedDate );
        setEndDate( c );
    }

    /**
     * Sets the Prescription's unique id.
     *
     * @param id
     *            the prescription id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the id associated with the Prescription.
     *
     * @return the prescription id
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Returns the drug associated with this Prescription
     *
     * @return the prescription's drug
     */
    public Drug getDrug () {
        return drug;
    }

    /**
     * Associates this prescription with the given drug.
     *
     * @param drug
     *            the drug
     */
    public void setDrug ( final Drug drug ) {
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
    public void setDosage ( final int dosage ) {
        this.dosage = dosage;
    }

    /**
     * Returns the drug's first valid day.
     *
     * @return the start date
     */
    public Calendar getStartDate () {
        return startDate;
    }

    /**
     * Sets the prescription's first valid day to the given date.
     *
     * @param startDate
     *            the first valid day
     */
    public void setStartDate ( final Calendar startDate ) {
        this.startDate = startDate;
    }

    /**
     * Returns the prescription's final valid date.
     *
     * @return the prescription's end date
     */
    public Calendar getEndDate () {
        return endDate;
    }

    /**
     * Sets the prescription's final valid date.
     *
     * @param endDate
     *            the end date
     */
    public void setEndDate ( final Calendar endDate ) {
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
    public void setRenewals ( final int renewals ) {
        this.renewals = renewals;
    }

    /**
     * Returns the user associated with this prescription.
     *
     * @return the patient
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Sets the prescription's patient to the given user
     *
     * @param user
     *            the user
     */
    public void setPatient ( final User user ) {
        this.patient = user;
    }

    /**
     * Rerieve all Prescriptions for the patient provided
     * 
     * @param patient
     *            The Patient to find Prescriptions for
     * @return The List of records that was found
     */
    public static List<Prescription> getForPatient ( final String patient ) {
        return getWhere( eqList( "patient", User.getByNameAndRole( patient, Role.ROLE_PATIENT ) ) );
    }

    /**
     * Returns a collection of prescriptions that meet the "where" query
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return a collection of matching prescriptions
     */
    @SuppressWarnings ( "unchecked" )
    private static List<Prescription> getWhere ( final List<Criterion> where ) {
        return (List<Prescription>) getWhere( Prescription.class, where );
    }

    /**
     * Gets the Prescription with the given id, or null if none exists.
     *
     * @param id
     *            the id to query for
     * @return the matching prescription
     */
    public static Prescription getById ( final Long id ) {
        try {
            return getWhere( eqList( ID, id ) ).get( 0 );
        }
        catch ( final ObjectNotFoundException e ) {
            return null;
        }
    }

    /**
     * Gets a collection of all the prescriptions in the system.
     *
     * @return the system's prescription
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Prescription> getPrescriptions () {
        return (List<Prescription>) DomainObject.getAll( Prescription.class );
    }

}
