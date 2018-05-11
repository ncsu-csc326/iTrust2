package edu.ncsu.csc.itrust2.forms.patient;

import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;

/**
 * This is the in-memory object that is used for requesting an appointment. It
 * is validated and converted into an AppointmentRequest object for persistence.
 *
 * @author Kai Presler-Marshall
 *
 */
public class AppointmentRequestForm {

    /**
     * Populate the appt request form from the Appointment request object
     *
     * @param ar
     *            the appointment request to populate the form from
     */
    public AppointmentRequestForm ( final AppointmentRequest ar ) {
        setPatient( ar.getPatient().getUsername() );
        setHcp( ar.getHcp().getUsername() );
        final SimpleDateFormat dateTemp = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        setDate( dateTemp.format( ar.getDate().getTime() ) );
        final SimpleDateFormat timeTemp = new SimpleDateFormat( "hh:mm aaa", Locale.ENGLISH );
        setTime( timeTemp.format( ar.getDate().getTime() ) );
        setType( ar.getType().toString() );
        setComments( ar.getComments() );
        if ( null != ar.getId() ) {
            setId( ar.getId().toString() );
        }
        setStatus( ar.getStatus().toString() );
    }

    /** The status of the appt request **/
    private String status;

    /** The patient of the appt request */
    private String patient;

    /** The hcp of the appt request */
    @NotNull
    private String hcp;

    /** The date of the appt request */
    @NotEmpty
    private String date;

    /** The id of the appt request */
    private String id;

    /** The time of the appt request */
    @NotEmpty
    private String time;

    /** The type of the appt request */
    private String type;

    /** The comments of the appt request */
    private String comments;

    /**
     * Don't use this one. For Hibernate/Thymeleaf
     */
    public AppointmentRequestForm () {
    }

    /**
     * Get the patient of the form
     *
     * @return the patient of the form
     */
    public String getPatient () {
        return patient;
    }

    /**
     * Set the patient of the form
     *
     * @param patient
     *            the patient of the form
     */
    public void setPatient ( final String patient ) {
        this.patient = patient;
    }

    /**
     * Get the hcp of the form
     *
     * @return the hcp of the form
     */
    public String getHcp () {
        return hcp;
    }

    /**
     * Set the hcp of the form
     *
     * @param hcp
     *            the hcp of the form
     */
    public void setHcp ( final String hcp ) {
        this.hcp = hcp;
    }

    /**
     * Get the date of the appointment to request
     *
     * @return the date of the appointment to request
     */
    public String getDate () {
        return date;
    }

    /**
     * Set the date of the appointment to request
     *
     * @param date
     *            the date of the appointment to request
     */
    public void setDate ( final String date ) {
        this.date = date;
    }

    /**
     * Get the time of the appointment to request
     *
     * @return the time of the appointment to request
     */
    public String getTime () {
        return time;
    }

    /**
     * Set the time of the appointment to request
     *
     * @param time
     *            the time of the appointment to request
     */
    public void setTime ( final String time ) {
        this.time = time;
    }

    /**
     * Get the comments of the appointment request
     *
     * @return the comments of the appointment request
     */
    public String getComments () {
        return comments;
    }

    /**
     * Set the comments of the appointment request
     *
     * @param comments
     *            the comments of the appointment request
     */
    public void setComments ( final String comments ) {
        this.comments = comments;
    }

    /**
     * Get the type of the appointment to request
     *
     * @return the type of the appointment to request
     */
    public String getType () {
        return type;
    }

    /**
     * Set the type of the appointment to request
     *
     * @param type
     *            the type of the appointment to request
     */
    public void setType ( final String type ) {
        this.type = type;
    }

    /**
     * Get the id of the appointment request
     *
     * @return the id of the appointment request
     */
    public String getId () {
        return id;
    }

    /**
     * Set the id of the appointment to request
     *
     * @param id
     *            the id of the appointment to request
     */
    public void setId ( final String id ) {
        this.id = id;
    }

    /**
     * Get the status of the appointment request
     *
     * @return the status of the appointment request
     */
    public String getStatus () {
        return status;
    }

    /**
     * Set the status of the appointment to request
     *
     * @param status
     *            the status of the appointment to request
     */
    public void setStatus ( final String status ) {
        this.status = status;
    }

}
