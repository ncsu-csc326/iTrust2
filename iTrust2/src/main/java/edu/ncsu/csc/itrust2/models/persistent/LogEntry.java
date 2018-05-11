package edu.ncsu.csc.itrust2.models.persistent;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.utils.DomainObjectCache;

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
@Table ( name = "LogEntries" )
public class LogEntry extends DomainObject<LogEntry> {

    /**
     * In-memory cache that will store instances of the LogEntries to avoid
     * retrieval trips to the database.
     */
    static private DomainObjectCache<Long, LogEntry> cache = new DomainObjectCache<Long, LogEntry>( LogEntry.class );

    /**
     * Type of event that has been logged
     */
    @NotNull
    private TransactionType                          logCode;

    /**
     * The primary user for the event that has been logged
     */
    @NotNull
    private String                                   primaryUser;

    /**
     * The timestamp of when the event occurred
     */
    @NotNull
    private Calendar                                 time;

    /**
     * The secondary user for the event that has been logged (optional)
     */
    private String                                   secondaryUser;

    /**
     * An additional elaborative message for the event that has been logged.
     * Optional.
     */
    private String                                   message;

    /**
     * ID of the LogEntry
     */
    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long                                     id;

    /**
     * Retrieve all LogEntries from the database.
     *
     * @return All LogEntries in the system
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LogEntry> getLogEntries () {
        return (List<LogEntry>) getAll( LogEntry.class );
    }

    /**
     * Retrieves a LogEntry from the database or memory cache based on its ID
     * (primary key)
     *
     * @param id
     *            The numeric ID of the LogEntry to find
     * @return Matching LogEntry, or null if nothing was found
     */
    public static LogEntry getById ( final Long id ) {
        LogEntry le = cache.get( id );
        if ( null == le ) {
            try {
                le = getWhere( " id = " + id ).get( 0 );
                cache.put( id, le );
            }
            catch ( final Exception e ) {
                // Exception ignored
            }
        }
        return le;

    }

    /**
     * Retrieve all LogEntries based on the where clause provided.
     *
     * @param where
     *            Where clause to retrieve OfficeVisits by
     * @return All matching log entries for the clause provided.
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LogEntry> getWhere ( final String where ) {
        return (List<LogEntry>) getWhere( LogEntry.class, where );
    }

    /**
     * Retrieve all LogEntries where the user provided was either the primary or
     * secondary user on the LogEntry.
     *
     * @param user
     *            The user to match on
     * @return All matching LogEntries
     */
    public static List<LogEntry> getAllForUser ( final String user ) {
        return getWhere( "primaryUser = '" + user + "' OR secondaryUser = '" + user + "'" );
    }

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
        this.setTime( Calendar.getInstance() );
    }

    /**
     * Creates an empty LogEntry. Used by Hibernate.
     */
    public LogEntry () {
    }

    /**
     * Retrieves the ID of the LogEntry
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
    public Calendar getTime () {
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
        this.secondaryUser = secondaryUser;
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
    public void setTime ( final Calendar time ) {
        this.time = time;
    }
}
