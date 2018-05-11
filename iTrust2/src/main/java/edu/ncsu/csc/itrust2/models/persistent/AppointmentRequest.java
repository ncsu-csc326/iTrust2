package edu.ncsu.csc.itrust2.models.persistent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

import edu.ncsu.csc.itrust2.forms.patient.AppointmentRequestForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Status;

/**
 * Backing object for the Appointment Request system. This is the object that is
 * actually stored in the database and reflects the persistent information we
 * have on the appointment request.
 *
 * @author Kai Presler-Marshall
 */

@Entity
@Table ( name = "AppointmentRequests" )
public class AppointmentRequest extends DomainObject<AppointmentRequest> {

    /**
     * Retrieve an AppointmentRequest by its numerical ID.
     *
     * @param id
     *            The ID (as assigned by the DB) of the AppointmentRequest
     * @return The AppointmentRequest, if found, or null if not found.
     */
    public static AppointmentRequest getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Retrieve a List of all AppointmentRequests from the database. Can be
     * filtered further once retrieved. Will return the AppointmentRequests
     * sorted by date.
     *
     * @return A List of all AppointmentRequests saved in the database
     */
    @SuppressWarnings ( "unchecked" )
    public static List<AppointmentRequest> getAppointmentRequests () {
        final List<AppointmentRequest> requests = (List<AppointmentRequest>) getAll( AppointmentRequest.class );
        requests.sort( new Comparator<AppointmentRequest>() {
            @Override
            public int compare ( final AppointmentRequest o1, final AppointmentRequest o2 ) {
                return o1.getDate().compareTo( o2.getDate() );
            }
        } );
        return requests;
    }

    /**
     * Used so that Hibernate can construct and load objects
     */
    public AppointmentRequest () {
    }

    /**
     * Retrieve a List of appointment requests that meets the given where
     * clause. Clause is expected to be valid SQL.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The matching list
     */
    @SuppressWarnings ( "unchecked" )
    private static List<AppointmentRequest> getWhere ( final List<Criterion> where ) {
        return (List<AppointmentRequest>) getWhere( AppointmentRequest.class, where );
    }

    /**
     * Retrieves all AppointmentRequests for the Patient provided.
     *
     * @param patientName
     *            Name of the patient
     * @return All of their AppointmentRequests
     */
    public static List<AppointmentRequest> getAppointmentRequestsForPatient ( final String patientName ) {
        return getWhere( createCriterionAsList( "patient", User.getByNameAndRole( patientName, Role.ROLE_PATIENT ) ) );
    }

    /**
     * Retrieves all AppointmentRequests for the HCP provided
     *
     * @param hcpName
     *            Name of the HCP
     * @return All AppointmentRequests involving this HCP
     */
    public static List<AppointmentRequest> getAppointmentRequestsForHCP ( final String hcpName ) {
        return getWhere( createCriterionAsList( "hcp", User.getByNameAndRole( hcpName, Role.ROLE_HCP ) ) );
    }

    /**
     * Retrieves all AppointmentRequests for the HCP _and_ Patient provided.
     * This is the intersection of the requests -- namely, only the ones where
     * both the HCP _and_ Patient are on the request.
     *
     * @param hcpName
     *            Name of the HCP
     * @param patientName
     *            Name of the Patient
     * @return The list of matching AppointmentRequests
     */
    public static List<AppointmentRequest> getAppointmentRequestsForHCPAndPatient ( final String hcpName,
            final String patientName ) {
        final Vector<Criterion> criteria = new Vector<Criterion>();
        criteria.add( createCriterion( "hcp", User.getByNameAndRole( hcpName, Role.ROLE_HCP ) ) );
        criteria.add( createCriterion( "patient", User.getByNameAndRole( patientName, Role.ROLE_PATIENT ) ) );
        return getWhere( criteria );
    }

    /**
     * Handles conversion between an AppointmentRequestForm (the form with
     * user-provided data) and an AppointmentRequest object that contains
     * validated information These two classes are closely intertwined to handle
     * validated persistent information and text-based information that is then
     * displayed back to the user.
     *
     * @param raf
     *            AppointmentRequestForm
     * @throws ParseException
     */
    public AppointmentRequest ( final AppointmentRequestForm raf ) throws ParseException {
        setPatient( User.getByNameAndRole( raf.getPatient(), Role.ROLE_PATIENT ) );
        setHcp( User.getByNameAndRole( raf.getHcp(), Role.ROLE_HCP ) );
        setComments( raf.getComments() );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy hh:mm aaa", Locale.ENGLISH );
        final Date parsedDate = sdf.parse( raf.getDate() + " " + raf.getTime() );
        final Calendar c = Calendar.getInstance();
        c.setTime( parsedDate );
        if ( c.before( Calendar.getInstance() ) ) {
            throw new IllegalArgumentException( "Cannot request an appointment before the current time" );
        }
        setDate( c );

        Status s = null;
        try {
            s = Status.valueOf( raf.getStatus() );
        }
        catch ( final NullPointerException npe ) {
            s = Status.PENDING; /*
                                 * Incoming AppointmentRequests will come in
                                 * from the form with no status. Set status to
                                 * Pending until it is adjusted further
                                 */
        }
        setStatus( s );
        AppointmentType at = null;
        try {
            at = AppointmentType.valueOf( raf.getType() );
        }
        catch ( final NullPointerException npe ) {
            at = AppointmentType.GENERAL_CHECKUP; /*
                                                   * If for some reason we don't
                                                   * have a type, default to
                                                   * general checkup
                                                   */
        }
        setType( at );

    }

    /**
     * ID of the AppointmentRequest
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long id;

    /**
     * Retrieves the ID of the AppointmentRequest
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID of the AppointmentRequest
     *
     * @param id
     *            The new ID of the AppointmentRequest. For Hibernate.
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * The Patient who is associated with this AppointmentRequest
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "patient_id", columnDefinition = "varchar(100)" )
    private User            patient;

    /**
     * The HCP who is associated with this AppointmentRequest
     */
    @NotNull
    @ManyToOne
    @JoinColumn ( name = "hcp_id", columnDefinition = "varchar(100)" )
    private User            hcp;

    /**
     * When this AppointmentRequest has been scheduled to take place
     */
    @NotNull
    private Calendar        date;

    /**
     * Store the Enum in the DB as a string as it then makes the DB info more
     * legible if it needs to be read manually.
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private AppointmentType type;

    /**
     * Retrieve the Status of this AppointmentRequest
     *
     * @return The Status of this AppointmentRequest
     */
    public Status getStatus () {
        return status;
    }

    /**
     * Set the Status of this AppointmentRequest
     *
     * @param status
     *            New Status
     */
    public void setStatus ( final Status status ) {
        this.status = status;
    }

    /**
     * Any (optional) comments on the AppointmentRequest
     */
    private String comments;

    /**
     * The Status of the AppointmentRequest
     */
    @NotNull
    @Enumerated ( EnumType.STRING )
    private Status status;

    /**
     * Retrieves the User object for the Patient for the AppointmentRequest
     *
     * @return The associated Patient
     */
    public User getPatient () {
        return patient;
    }

    /**
     * Sets the Patient for the AppointmentRequest
     *
     * @param patient
     *            The User object for the Patient on the Request
     */
    public void setPatient ( final User patient ) {
        this.patient = patient;
    }

    /**
     * Gets the User object for the HCP on the request
     *
     * @return The User object for the HCP on the request
     */
    public User getHcp () {
        return hcp;
    }

    /**
     * Sets the User object for the HCP on the AppointmentRequest
     *
     * @param hcp
     *            User object for the HCP on the Request
     */
    public void setHcp ( final User hcp ) {
        this.hcp = hcp;
    }

    /**
     * Retrieves the date & time of the AppointmentRequest
     *
     * @return Calendar for when the Request takes place
     */
    public Calendar getDate () {
        return date;
    }

    /**
     * Sets the date & time of the AppointmentRequest
     *
     * @param date
     *            Calendar object for the Date & Time of the request
     */
    public void setDate ( final Calendar date ) {
        this.date = date;
    }

    /**
     * Retrieves the comments on the request
     *
     * @return Comments on the AppointmentRequest
     */
    public String getComments () {
        return comments;
    }

    /**
     * Sets the Comments on the AppointmentRequest
     *
     * @param comments
     *            New comments for the AppointmentRequest
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Retrieves the Type of this AppointmentRequest.
     *
     * @return Type of this AppointmentRequest.
     */
    public AppointmentType getType () {
        return type;
    }

    /**
     * Sets the AppointmentType of this AppointmentRequest
     *
     * @param type
     *            The new type for the AppointmentRequest
     */
    public void setType ( final AppointmentType type ) {
        this.type = type;
    }

}
