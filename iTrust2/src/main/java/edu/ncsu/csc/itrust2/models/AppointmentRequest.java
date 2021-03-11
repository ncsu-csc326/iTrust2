package edu.ncsu.csc.iTrust2.models;

import java.time.ZonedDateTime;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAdapter;
import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAttributeConverter;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.Status;

/**
 * Backing object for the Appointment Request system. This is the object that is
 * actually stored in the database and reflects the persistent information we
 * have on the appointment request.
 *
 * @author Kai Presler-Marshall
 */

@Entity
public class AppointmentRequest extends DomainObject {

    /**
     * Used so that Hibernate can construct and load objects
     */
    public AppointmentRequest () {
    }

    /**
     * ID of the AppointmentRequest
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long id;

    /**
     * Sets the ID of the AppointmentRequest
     *
     * @param id
     *            The new ID of the AppointmentRequest. For Hibernate.
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the id of the AppointmentRequest
     *
     * @return ID of the AppointmentRequest
     */
    @Override
    public Long getId () {
        return this.id;
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
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = ZonedDateTimeAttributeConverter.class )
    @JsonAdapter ( ZonedDateTimeAdapter.class )
    private ZonedDateTime   date;

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
     * @return ZonedDateTime for when the Request takes place
     */
    public ZonedDateTime getDate () {
        return date;
    }

    /**
     * Sets the date & time of the AppointmentRequest
     *
     * @param date
     *            ZonedDateTime object for the Date & Time of the request
     */
    public void setDate ( final ZonedDateTime date ) {
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
