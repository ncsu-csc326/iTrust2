package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * This is the validated database-persisted office visit representation
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
@Table ( name = "OfficeVisits" )
public class OfficeVisit extends DomainObject<OfficeVisit> {

    /**
     * Get a specific office visit by the database ID
     *
     * @param id
     *            the database ID
     * @return the specific office visit with the desired ID
     */
    public static OfficeVisit getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }

    }

    /**
     * Get all office visits for a specific patient
     *
     * @param patientName
     *            the name of the patient
     * @return the office visits of the queried patient
     */
    public static List<OfficeVisit> getForPatient ( final String patientName ) {
        return getWhere( createCriterionAsList( "patient", User.getByNameAndRole( patientName, Role.ROLE_PATIENT ) ) );
    }

    /**
     * Get all office visits for a specific HCP
     *
     * @param hcpName
     *            the name of the HCP
     * @return the office visits of the queried HCP
     */
    public static List<OfficeVisit> getForHCP ( final String hcpName ) {
        return getWhere( createCriterionAsList( "hcp", User.getByNameAndRole( hcpName, Role.ROLE_HCP ) ) );
    }

    /**
     * Get all office visits done by a specific HCP for a specific patient
     *
     * @param hcpName
     *            the name of the HCP
     * @param patientName
     *            the name of the patient
     * @return the office visits of the queried HCP, patient combo
     */
    public static List<OfficeVisit> getForHCPAndPatient ( final String hcpName, final String patientName ) {
        final Vector<Criterion> criteria = new Vector<Criterion>();
        criteria.add( createCriterion( "hcp", User.getByNameAndRole( hcpName, Role.ROLE_HCP ) ) );
        criteria.add( createCriterion( "patient", User.getByNameAndRole( patientName, Role.ROLE_PATIENT ) ) );
        return getWhere( criteria );

    }

    /**
     * Get all office visits in the database
     *
     * @return all office visits in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<OfficeVisit> getOfficeVisits () {
        final List<OfficeVisit> visits = (List<OfficeVisit>) getAll( OfficeVisit.class );
        visits.sort( new Comparator<OfficeVisit>() {
            @Override
            public int compare ( final OfficeVisit o1, final OfficeVisit o2 ) {
                return o1.getDate().compareTo( o2.getDate() );
            }
        } );
        return visits;
    }

    /**
     * Helper method to pass to the DomainObject class that performs a specific
     * query on the database.
     *
     * @SuppressWarnings for Unchecked cast from List<capture#1-of ? extends
     *                   DomainObject> to List<OfficeVisit> Because get all just
     *                   returns a list of DomainObjects, the cast is okay.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return the result of the query
     */
    @SuppressWarnings ( "unchecked" )
    private static List<OfficeVisit> getWhere ( final List<Criterion> where ) {
        return (List<OfficeVisit>) getWhere( OfficeVisit.class, where );
    }

    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public OfficeVisit () {
    }

    /**
     * Creates an OfficeVisit from the OfficeVisitForm provided
     *
     * @param ovf
     *            OfficeVisitForm The OfficeVisitForm to create an OfficeVisit
     *            out of
     * @throws ParseException
     *             If the date & time in the OfficeVisitForm cannot be parsed
     *             properly
     * @throws NumberFormatException
     *             If the ID cannot be parsed to a Long.
     */
    public OfficeVisit ( final OfficeVisitForm ovf ) throws ParseException, NumberFormatException {
        setPatient( User.getByNameAndRole( ovf.getPatient(), Role.ROLE_PATIENT ) );
        setHcp( User.getByNameAndRole( ovf.getHcp(), Role.ROLE_HCP ) );
        setNotes( ovf.getNotes() );

        if ( ovf.getId() != null ) {
            setId( Long.parseLong( ovf.getId() ) );
        }

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy hh:mm aaa", Locale.ENGLISH );
        final Date parsedDate = sdf.parse( ovf.getDate() + " " + ovf.getTime() );
        final Calendar c = Calendar.getInstance();
        c.setTime( parsedDate );
        setDate( c );

        AppointmentType at = null;
        try {
            at = AppointmentType.valueOf( ovf.getType() );
        }
        catch ( final NullPointerException npe ) {
            at = AppointmentType.GENERAL_CHECKUP; /*
                                                   * If for some reason we don't
                                                   * have a type, default to
                                                   * general checkup
                                                   */
        }
        setType( at );

        if ( null != ovf.getPreScheduled() ) {
            final List<AppointmentRequest> requests = AppointmentRequest
                    .getAppointmentRequestsForHCPAndPatient( ovf.getHcp(), ovf.getPatient() );
            try {
                final AppointmentRequest match = requests.stream().filter( e -> e.getDate().equals( c ) )
                        .collect( Collectors.toList() )
                        .get( 0 ); /*
                                    * We should have one and only one
                                    * appointment for the provided HCP & patient
                                    * and the time specified
                                    */
                setAppointment( match );
            }
            catch ( final Exception e ) {
                throw new IllegalArgumentException( "Marked as preschedule but no match can be found" + e.toString() );
            }

        }
        setHospital( Hospital.getByName( ovf.getHospital() ) );
        setBasicHealthMetrics( new BasicHealthMetrics( ovf ) );

        // associate all diagnoses with this visit
        if ( ovf.getDiagnoses() != null ) {
            setDiagnoses( ovf.getDiagnoses() );
            for ( final Diagnosis d : diagnoses ) {
                d.setVisit( this );
            }
        }

        final Patient p = Patient.getPatient( patient );
        if ( p == null || p.getDateOfBirth() == null ) {
            return; // we're done, patient can't be tested against
        }
        final Calendar dob = p.getDateOfBirth();
        int age = date.get( Calendar.YEAR ) - dob.get( Calendar.YEAR );
        if ( date.get( Calendar.MONTH ) < dob.get( Calendar.MONTH ) ) {
            age -= 1;
        }
        else if ( date.get( Calendar.MONTH ) == dob.get( Calendar.MONTH ) ) {
            if ( date.get( Calendar.DATE ) < dob.get( Calendar.DATE ) ) {
                age -= 1;
            }
        }
        if ( age < 3 ) {
            validateUnder3();
        }
        else if ( age < 12 ) {
            validateUnder12();
        }
        else {
            validate12AndOver();
        }

        validateDiagnoses();

        final List<PrescriptionForm> ps = ovf.getPrescriptions();
        if ( ps != null ) {
            setPrescriptions( ps.stream().map( ( final PrescriptionForm pf ) -> new Prescription( pf ) )
                    .collect( Collectors.toList() ) );
        }
    }

    private void validateDiagnoses () {
        if ( diagnoses == null ) {
            return;
        }
        for ( final Diagnosis d : diagnoses ) {
            if ( d.getNote().length() > 500 ) {
                throw new IllegalArgumentException( "Dagnosis note too long (500 character max) : " + d.getNote() );
            }
            if ( d.getCode() == null ) {
                throw new IllegalArgumentException( "Diagnosis Code missing!" );
            }
        }
    }

    /**
     * Validates an office visit form for containing correct fields for patients
     * 12 and over. Expects the basic health metrics to already be set by the
     * OfficeVisit constructor.
     */
    private void validate12AndOver () {
        // should already be set in office visit constructor
        final BasicHealthMetrics bhm = getBasicHealthMetrics();
        if ( bhm.getDiastolic() == null || bhm.getHdl() == null || bhm.getHeight() == null
                || bhm.getHouseSmokingStatus() == null || bhm.getLdl() == null || bhm.getPatientSmokingStatus() == null
                || bhm.getSystolic() == null || bhm.getTri() == null || bhm.getWeight() == null ) {
            throw new IllegalArgumentException( "Not all necessary fields for basic health metrics were submitted." );
        }
    }

    /**
     * Validates an office visit form for containing correct fields for patients
     * 12 and under.
     */
    private void validateUnder12 () {
        // should already be set in office visit constructor
        final BasicHealthMetrics bhm = getBasicHealthMetrics();
        if ( bhm.getDiastolic() == null || bhm.getHeight() == null || bhm.getHouseSmokingStatus() == null
                || bhm.getSystolic() == null || bhm.getWeight() == null ) {
            throw new IllegalArgumentException( "Not all necessary fields for basic health metrics were submitted." );
        }
    }

    /**
     * Validates an office visit form for containing correct fields for patients
     * 3 and under.
     */
    private void validateUnder3 () {
        // should already be set in office visit constructor
        final BasicHealthMetrics bhm = getBasicHealthMetrics();
        if ( bhm.getHeight() == null || bhm.getHeadCircumference() == null || bhm.getHouseSmokingStatus() == null
                || bhm.getWeight() == null ) {
            throw new IllegalArgumentException( "Not all necessary fields for basic health metrics were submitted." );
        }
    }

    /**
     * Get the patient of this office visit
     *
     * @return the patient of this office visit
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Set the patient of this office visit
     *
     * @param patient
     *            the patient to set this office visit to
     */
    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    /**
     * Get the hcp of this office visit
     *
     * @return the hcp of this office visit
     */
    public User getHcp () {
        return hcp;
    }

    /**
     * Set the hcp of this office visit
     *
     * @param hcp
     *            the hcp to set this office visit to
     */
    public void setHcp ( final User hcp ) {
        this.hcp = hcp;
    }

    /**
     * Get the date of this office visit
     *
     * @return the date of this office visit
     */
    public Calendar getDate () {
        return date;
    }

    /**
     * Set the date of this office visit
     *
     * @param date
     *            the date to set this office visit to
     */
    public void setDate ( final Calendar date ) {
        this.date = date;
    }

    /**
     * Get the id of this office visit
     *
     * @return the id of this office visit
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the id of this office visit
     *
     * @param id
     *            the id to set this office visit to
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the type of this office visit
     *
     * @return the type of this office visit
     */
    public AppointmentType getType () {
        return type;
    }

    /**
     * Set the type of this office visit
     *
     * @param type
     *            the type to set this office visit to
     */
    public void setType ( final AppointmentType type ) {
        this.type = type;
    }

    /**
     * Get the hospital of this office visit
     *
     * @return the hospital of this office visit
     */
    public Hospital getHospital () {
        return hospital;
    }

    /**
     * Set the hospital of this office visit
     *
     * @param hospital
     *            the hospital to set this office visit to
     */
    public void setHospital ( final Hospital hospital ) {
        this.hospital = hospital;
    }

    /**
     * Get the notes of this office visit
     *
     * @return the notes of this office visit
     */
    public String getNotes () {
        return notes;
    }

    /**
     * Set the notes of this office visit
     *
     * @param notes
     *            the notes to set this office visit to
     */
    public void setNotes ( final String notes ) {
        this.notes = notes;
    }

    /**
     * Get the appointment of this office visit
     *
     * @return the appointment of this office visit
     */
    public AppointmentRequest getAppointment () {
        return appointment;
    }

    /**
     * Set the appointment of this office visit
     *
     * @param appointment
     *            the appointment to set this office visit to
     */
    public void setAppointment ( final AppointmentRequest appointment ) {
        this.appointment = appointment;
    }

    /**
     * Gets the basic health metrics for this office visit.
     *
     * @return the basicHealthMetrics
     */
    public BasicHealthMetrics getBasicHealthMetrics () {
        return basicHealthMetrics;
    }

    /**
     * Sets the basic health metrics for this office visit.
     *
     * @param basicHealthMetrics
     *            the basicHealthMetrics to set
     */
    public void setBasicHealthMetrics ( final BasicHealthMetrics basicHealthMetrics ) {
        this.basicHealthMetrics = basicHealthMetrics;
    }

    /**
     * Sets the list of Diagnoses associated with this visit
     *
     * @param list
     *            The List of Diagnoses
     */
    public void setDiagnoses ( final List<Diagnosis> list ) {
        diagnoses = list;
    }

    /**
     * Returns the list of diagnoses for this visit
     *
     * @return The list of diagnoses
     */
    public List<Diagnosis> getDiagnoses () {
        return diagnoses;
    }

    /**
     * Sets the list of prescriptions associated with this visit
     *
     * @param prescriptions
     *            The list of prescriptions
     */
    public void setPrescriptions ( final List<Prescription> prescriptions ) {
        this.prescriptions = prescriptions;
    }

    /**
     * Returns the list of prescriptions for this visit
     *
     * @return The list of prescriptions
     */
    public List<Prescription> getPrescriptions () {
        return prescriptions;
    }

    /**
     * The patient of this office visit
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User                     patient;

    /**
     * The hcp of this office visit
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "hcp_id", columnDefinition = "varchar(100)" )
    private User                     hcp;

    /**
     * The basic health metric data associated with this office visit.
     */
    @OneToOne
    @JoinColumn ( name = "basichealthmetrics_id" )
    private BasicHealthMetrics       basicHealthMetrics;

    /**
     * The date of this office visit
     */
    @NotNull
    private Calendar                 date;

    /**
     * The id of this office visit
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long                     id;

    /**
     * The type of this office visit
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private AppointmentType          type;

    /**
     * The hospital of this office visit
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "hospital_id", columnDefinition = "varchar(100)" )
    private Hospital                 hospital;

    /**
     * The set of diagnoses associated with this visits Marked transient so not
     * serialized or saved in DB If removed, serializer gets into an infinite
     * loop
     */
    @OneToMany ( mappedBy = "visit" )
    public transient List<Diagnosis> diagnoses;

    /**
     * The notes of this office visit
     */
    private String                   notes;

    /**
     * The appointment of this office visit
     */
    @OneToOne
    @JoinColumn ( name = "appointment_id" )
    private AppointmentRequest       appointment;

    @OneToMany ( fetch = FetchType.EAGER )
    @JoinColumn ( name = "prescriptions_id" )
    private List<Prescription>       prescriptions = Collections.emptyList();

    /**
     * Overrides the basic domain object save in order to save basic health
     * metrics and the office visit.
     */
    @Override
    public void save () {
        final BasicHealthMetrics oldBhm = BasicHealthMetrics.getById( basicHealthMetrics.getId() );
        this.basicHealthMetrics.save();

        //// SAVE PRESCRIPTIONS ////

        // Get saved visit
        final OfficeVisit oldVisit = OfficeVisit.getById( id );

        // Get prescription ids included in this office visit
        final Set<Long> currentIds = this.getPrescriptions().stream().map( Prescription::getId )
                .collect( Collectors.toSet() );

        // Get prescription ids saved previously
        final Set<Long> savedIds = oldVisit == null ? Collections.emptySet()
                : oldVisit.getPrescriptions().stream().map( Prescription::getId ).collect( Collectors.toSet() );

        // Save each of the prescriptions
        this.getPrescriptions().forEach( p -> {
            final boolean isSaved = savedIds.contains( p.getId() );
            if ( isSaved ) {
                LoggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(), getPatient().getUsername(),
                        "Editing prescription with id " + p.getId() );
            }
            else {
                LoggerUtil.log( TransactionType.PRESCRIPTION_CREATE, LoggerUtil.currentUser(),
                        getPatient().getUsername(), "Creating prescription with id " + p.getId() );
            }
            p.save();
        } );

        // Remove prescriptions no longer included
        if ( !savedIds.isEmpty() ) {
            savedIds.forEach( id -> {
                final boolean isMissing = currentIds.contains( id );
                if ( isMissing ) {
                    LoggerUtil.log( TransactionType.PRESCRIPTION_DELETE, LoggerUtil.currentUser(),
                            getPatient().getUsername(), "Deleting prescription with id " + id );
                    Prescription.getById( id ).delete();
                }
            } );
        }

        //// END PRESCRIPTIONS ////

        try {
            super.save();

            // get list of ids associated with this visit if this visit already
            // exists
            final Set<Long> previous = Diagnosis.getByVisit( id ).stream().map( Diagnosis::getId )
                    .collect( Collectors.toSet() );
            if ( getDiagnoses() != null ) {
                for ( final Diagnosis d : getDiagnoses() ) {
                    if ( d == null ) {
                        continue;
                    }

                    final boolean had = previous.remove( d.getId() );
                    try {
                        if ( !had ) {
                            // new Diagnosis
                            LoggerUtil.log( TransactionType.DIAGNOSIS_CREATE, getHcp().getUsername(),
                                    getPatient().getUsername(), getHcp() + " created a diagnosis for " + getPatient() );
                        }
                        else {
                            // already had - check if edited
                            final Diagnosis old = Diagnosis.getById( d.getId() );
                            if ( !old.getCode().getCode().equals( d.getCode().getCode() )
                                    || !old.getNote().equals( d.getNote() ) ) {
                                // was edited:
                                LoggerUtil.log( TransactionType.DIAGNOSIS_EDIT, getHcp().getUsername(),
                                        getPatient().getUsername(),
                                        getHcp() + " edit a diagnosis for " + getPatient() );

                            }
                        }
                    }
                    catch ( final Exception e ) {
                        e.printStackTrace();
                    }
                    d.save();

                }
            }
            // delete any previous associations - they were deleted by user.
            for ( final Long oldId : previous ) {
                final Diagnosis dDie = Diagnosis.getById( oldId );
                if ( dDie != null ) {
                    dDie.delete();
                    try {
                        LoggerUtil.log( TransactionType.DIAGNOSIS_DELETE, getHcp().getUsername(),
                                getPatient().getUsername(),
                                getHcp().getUsername() + " deleted a diagnosis for " + getPatient().getUsername() );
                    }
                    catch ( final Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch ( final Exception ex ) {
            // we don't want to save the bhm if an error occurs
            // but we also don't want to delete a valid copy if it was an
            // invalid edit
            this.basicHealthMetrics.delete();
            if ( oldBhm != null ) {
                oldBhm.save();
            }
            throw ex;
        }

    }

    /**
     * Deletes any diagnoses associated with this office visit, then deletes the
     * visit entry
     */
    @Override
    public void delete () {
        if ( diagnoses != null ) {
            for ( final Diagnosis d : diagnoses ) {
                d.delete();
                try {
                    LoggerUtil.log( TransactionType.DIAGNOSIS_DELETE, getHcp().getUsername(),
                            getPatient().getUsername(), getHcp() + " deleted a diagnosis for " + getPatient() );
                }
                catch ( final Exception e ) {
                    e.printStackTrace();
                }
            }
        }
        super.delete();
    }

    /**
     * Deletes all Office visits, and all Diagnoses (No diagnoses without an
     * office visit)
     */
    public static void deleteAll () {
        DomainObject.deleteAll( Diagnosis.class );
        DomainObject.deleteAll( OfficeVisit.class );
    }

}
