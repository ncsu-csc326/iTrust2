package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.personnel.LabProcedureForm;
import edu.ncsu.csc.itrust2.models.enums.LabStatus;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.enums.Role;

/**
 * This is the validated database-persisted lab procedure representation
 *
 * @author Alex Phelps
 *
 */
@Entity
@Table ( name = "LabProcedures" )
public class LabProcedure extends DomainObject<LabProcedure> {

    /**
     * The LOINC of this Lab Procedure
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "LOINC_code" )
    private LOINC       loinc;

    /**
     * The Priority of this LabProcedure
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private Priority    priority;

    /**
     * Comments on this LabProcedure
     */
    private String      comments;

    /**
     * The Status of this LabProcedure
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private LabStatus   status;

    /**
     * The id of this LabProcedure
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long        id;

    /**
     * The Lab Tech assigned to this LabProcedure
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "labtech", columnDefinition = "varchar(100)" )
    private User        labtech;

    /**
     * The Office Visit of the LabProcedure
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "visit", nullable = false )
    private OfficeVisit visit;

    /**
     * The User assigned to this LabProcedure
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient", columnDefinition = "varchar(100)" )
    private User        patient;

    /**
     * Get a specific lab procedure by the database ID
     *
     * @param id
     *            the database ID
     * @return the specific lab procedure with the desired ID
     */
    public static LabProcedure getById ( final Long id ) {
        try {
            return getWhere( eqList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get a specific lab procedure by the office visit ID
     *
     * @param id
     *            the database ID
     * @return the specific lab procedure with the desired visit ID
     */
    public static List<LabProcedure> getByVisit ( final Long id ) {
        try {
            return getWhere( eqList( "visit", OfficeVisit.getById( id ) ) );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Get all lab procedures for a specific patient
     *
     * @param patientName
     *            the name of the patient
     * @return the lab procedures associated with the queried patient
     */
    public static List<LabProcedure> getForPatient ( final String patientName ) {
        return getWhere( eqList( "patient", User.getByNameAndRole( patientName, Role.ROLE_PATIENT ) ) );
    }

    /**
     * Get all lab procedures for a specific patient
     *
     * @param id
     *            the id of the Office Visit
     * @return the lab procedures associated with the queried OfficeVisit
     */
    public static List<LabProcedure> getForVisit ( final Long id ) {
        return getWhere( eqList( "visit", OfficeVisit.getById( id ) ) );
    }

    /**
     * Get all lab procedures for a specific Lab Tech
     *
     * @param techName
     *            the name of the Lab Tech
     * @return the lab procedures of the queried Lab Tech
     */
    public static List<LabProcedure> getForLabtech ( final String techName ) {
        return getWhere( eqList( "labtech", User.getByNameAndRole( techName, Role.ROLE_LABTECH ) ) );
    }

    /**
     * Get all lab procedures done by a specific Lab Tech for a specific patient
     *
     * @param techName
     *            the name of the Lab Tech
     * @param patientName
     *            the name of the patient
     * @return the lab procedures of the queried Lab Tech, patient combo
     */
    public static List<LabProcedure> getForTechAndPatient ( final String techName, final String patientName ) {
        final Vector<Criterion> criteria = new Vector<Criterion>();
        criteria.add( eq( "labtech", User.getByNameAndRole( techName, Role.ROLE_LABTECH ) ) );
        criteria.add( eq( "patient", User.getByNameAndRole( patientName, Role.ROLE_PATIENT ) ) );
        return getWhere( criteria );

    }

    /**
     * Get all lab procedures in the database
     *
     * @return all lab procedures in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LabProcedure> getLabProcedures () {
        final List<LabProcedure> procedures = (List<LabProcedure>) getAll( LabProcedure.class );
        return procedures;
    }

    /**
     * Helper method to pass to the DomainObject class that performs a specific
     * query on the database.
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<LabProcedure> Because get all
     *                   just returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return the result of the query
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LabProcedure> getWhere ( final List<Criterion> where ) {
        return (List<LabProcedure>) getWhere( LabProcedure.class, where );
    }

    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public LabProcedure () {
    }

    /**
     * Creates a LabProcedure from the LabProcedureForm provided
     *
     * @param lpf
     *            LabProcedureForm The LabProcedureForm to create a LabProcedure
     *            out of
     * @throws ParseException
     *             If the date & time in the LabProcedureForm cannot be parsed
     *             properly
     * @throws NumberFormatException
     *             If the ID cannot be parsed to a Long.
     */
    public LabProcedure ( final LabProcedureForm lpf ) throws ParseException, NumberFormatException {
        setLoinc( LOINC.getById( lpf.getLoincId() ) );
        setPatient( User.getByName( lpf.getPatient() ) );
        try {
            setPriority( Priority.parseIntValue( ( Integer.parseInt( lpf.getPriority() ) ) ) );
        }
        catch ( final NumberFormatException nfe ) {
            setPriority( Priority.parseStrValue( lpf.getPriority() ) );
        }
        setComments( lpf.getComments() );
        try {
            setStatus( LabStatus.parseValue( Integer.parseInt( lpf.getStatus() ) ) );
        }
        catch ( final NumberFormatException nfe ) {
            setStatus( LabStatus.parseStrValue( lpf.getStatus() ) );
        }
        if ( lpf.getId() != null ) {
            setId( lpf.getId() );
        }
        setAssignedTech( User.getByName( lpf.getAssignedTech() ) );
        setVisit( OfficeVisit.getById( lpf.getVisitId() ) );
    }

    /**
     * Gets the LOINC object of this LabProcedure
     *
     * @return The LOINC object used for this LabProcedure
     */
    public LOINC getLoinc () {
        return loinc;
    }

    /**
     * Sets the LOINC object of this LabProcedure
     *
     * @param loinc
     *            The LOINC object to set for this LabProcedure
     */
    public void setLoinc ( final LOINC loinc ) {
        this.loinc = loinc;
    }

    /**
     * Get this LabProcedure's priority
     *
     * @return The priority of this LabProcedure
     */
    public Priority getPriority () {
        return priority;
    }

    /**
     * Sets the priority of this LabProcedure
     *
     * @param priority
     *            The priority of the LabProcedure
     */
    public void setPriority ( final Priority priority ) {
        this.priority = priority;
    }

    /**
     * Gets the comment(s) on the LabProcedure
     *
     * @return The comment(s) on the LabProcedure
     */
    public String getComments () {
        return comments;
    }

    /**
     * Sets the comment(s) on the LabProcedure
     *
     * @param comments
     *            The comment(s) to set for the LabProcedure
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Gets the status of the LabProcedure
     *
     * @return The status of this LabProcedure
     */
    public LabStatus getStatus () {
        return status;
    }

    /**
     * Sets/updates the status of the LabProcedure
     *
     * @param status
     *            The status of the LabProcedure
     */
    public void setStatus ( final LabStatus status ) {
        this.status = status;
    }

    /**
     * Get's the id of this LabProcedure
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the id of this LabProcedure
     *
     * @param id
     *            The id of this LabProcedure
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the assigned Lab Tech of this LabProcedure
     *
     * @return The assigned Lab Tech of this LabProcedure
     */
    public User getAssignedTech () {
        return labtech;
    }

    /**
     * Assigns a Lab Tech to this LabProcedure
     *
     * @param labtech
     *            The Lab Tech to assign to this LabProcedure
     */
    public void setAssignedTech ( final User labtech ) {
        this.labtech = labtech;
    }

    /**
     * Gets the OfficeVisit associated with this LabProcedure
     *
     * @return The OfficeVisit associated with this LabProcedure
     */
    public OfficeVisit getVisit () {
        return visit;
    }

    /**
     * Sets the OfficeVisit for this LabProcedure
     *
     * @param visit
     *            The OfficeVisit to set for this LabProcedure
     */
    public void setVisit ( final OfficeVisit visit ) {
        this.visit = visit;
    }

    /**
     * Get the patient of this LabProcedure
     *
     * @return the patient of this LabProcedure
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Set the patient of this LabProcedure
     *
     * @param patient
     *            the patient to set this LabProcedure for
     */
    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    /**
     * Deletes the LabProcedure
     */
    @Override
    public void delete () {
        super.delete();
    }

    /**
     * Deletes all LabProcedures
     */
    public static void deleteAll () {
        DomainObject.deleteAll( LabProcedure.class );
    }

    @Override
    public boolean equals ( final Object o ) {
        if ( o instanceof LabProcedure ) {
            final LabProcedure lp = (LabProcedure) o;
            return id.equals( lp.getId() );
        }
        return false;
    }

}
