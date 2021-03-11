package edu.ncsu.csc.iTrust2.models.security;

import java.time.ZonedDateTime;

import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.google.gson.annotations.JsonAdapter;

import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAdapter;
import edu.ncsu.csc.iTrust2.adapters.ZonedDateTimeAttributeConverter;
import edu.ncsu.csc.iTrust2.models.DomainObject;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;

/**
 * Class that represents a LogEntry that is created in response to certain user
 * actions. Contains a required TransactionType code (specifying the event that
 * happened), a username, and a time when the event occurred. Has support for an
 * optional secondary user and message for further elaboration
 *
 * @author Kai Presler-Marshall
 *
 */
@Entity
public class LogEntry extends DomainObject {

    /**
     * Type of event that has been logged
     */
    @NotNull
    private TransactionType logCode;

    /**
     * The primary user for the event that has been logged
     */
    @NotNull
    private String          primaryUser;

    /**
     * The timestamp of when the event occurred
     */
    @NotNull
    @Basic
    // Allows the field to show up nicely in the database
    @Convert ( converter = ZonedDateTimeAttributeConverter.class )
    @JsonAdapter ( ZonedDateTimeAdapter.class )
    private ZonedDateTime   time;

    /**
     * The secondary user for the event that has been logged (optional)
     */
    private String          secondaryUser;

    /**
     * An additional elaborative message for the event that has been logged.
     * Optional.
     */
    private String          message;

    /**
     * ID of the LogEntry
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long            id;

    /**
     * Create a LogEntry from the most complete set of information.
     *
     * @param code
     *            The type of event that occurred and will be logged.
     * @param primaryUser
     *            The primary user that triggered the event
     * @param secondaryUser
     *            The secondary user involved
     * @param message
     *            An optional message for the event
     */
    public LogEntry ( final TransactionType code, final String primaryUser, final String secondaryUser,
            final String message ) {
        this.setLogCode( code );
        this.setPrimaryUser( primaryUser );
        this.setSecondaryUser( secondaryUser );
        this.setMessage( message );
        this.setTime( ZonedDateTime.now() );
    }

    /**
     * Creates an empty LogEntry. Used by Hibernate.
     */
    public LogEntry () {
    }

    /**
     * Retrieves the ID of the LogEntry
     * 
     * @return the ID of the LogEntry
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID on the LogEntry. Used by Hibernate.
     *
     * @param id
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Retrieves the time when the LogEntry occurred.
     *
     * @return Time
     */
    public ZonedDateTime getTime () {
        return this.time;
    }

    /**
     * Retrieves the secondary (optional) user on the LogEntry
     *
     * @return Username of the secondary user
     */
    public String getSecondaryUser () {
        return secondaryUser;
    }

    /**
     * Sets the SecondaryUser on the Log Entry
     *
     * @param secondaryUser
     *            Optional secondary user for the LogEntry
     */
    public void setSecondaryUser ( final String secondaryUser ) {
        if ( !this.getPrimaryUser().equals( secondaryUser ) ) {
            this.secondaryUser = secondaryUser;
        }
    }

    /**
     * Retrieves the optional Message on the LogEntry
     *
     * @return Any message present
     */
    public String getMessage () {
        return message;
    }

    /**
     * Sets the optional Message on the LogEntry
     *
     * @param message
     *            New Message to set
     */
    public void setMessage ( final String message ) {
        this.message = message;
    }

    /**
     * Retrieves the type of the LogEntry
     *
     * @return The type
     */
    public TransactionType getLogCode () {
        return logCode;
    }

    /**
     * Sets the Type of the LogEntry
     *
     * @param logCode
     *            New Type of the LogEntry
     */
    public void setLogCode ( final TransactionType logCode ) {
        this.logCode = logCode;
    }

    /**
     * Retrieves the primary User of the LogEntry
     *
     * @return The primary user
     */
    public String getPrimaryUser () {
        return primaryUser;
    }

    /**
     * Sets the primary User of the LogEntry
     *
     * @param primaryUser
     *            The primary user to set.
     */
    public void setPrimaryUser ( final String primaryUser ) {
        this.primaryUser = primaryUser;
    }

    /**
     * Sets the time at which the LogEntry occurred.
     *
     * @param time
     *            Timestamp when the event occurred.
     */
    public void setTime ( final ZonedDateTime time ) {
        this.time = time;
    }

}
