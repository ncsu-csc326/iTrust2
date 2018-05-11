package edu.ncsu.csc.itrust2.utils;

import java.util.Comparator;
import java.util.List;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Logging class to handle saving log-worthy events and for retrieving those
 * that previously occurred. All actions that need to be logged (as defined in
 * the iTrust Wiki) should be logged using one of the three `Log` methods here.
 *
 * @author Kai Presler-Marshall
 *
 */
public class LoggerUtil {

    /**
     * Most complete logger utility. Usually won't need all of this information,
     * but if you do, it has it all. The time of the event is added
     * automatically and is assumed to be the current time.
     *
     * @param code
     *            The TransactionType of the event that occurred
     * @param primaryUser
     *            The primary user involved in the event that was logged.
     * @param secondaryUser
     *            An (optional) secondary user involved in the event.
     * @param message
     *            An (optional) message for further details.
     */
    static public void log ( final TransactionType code, final String primaryUser, final String secondaryUser,
            final String message ) {
        final LogEntry le = new LogEntry( code, primaryUser, secondaryUser, message );
        le.save();
    }

    /**
     * Abbreviated Logger. Same as the full one, but no secondaryUser.
     *
     * @param code
     *            The TransactionType of the event that occurred
     * @param primaryUser
     *            The primary user involved in the event that was logged.
     * @param message
     *            A message for further details
     */
    static public void log ( final TransactionType code, final String primaryUser, final String message ) {
        log( code, primaryUser, null, message );
    }

    /**
     * Most abbreviated Logger utility. Just an event code and a single user.
     *
     * @param code
     *            The TransactionType of the event that occurred
     * @param primaryUser
     *            The primary user involved in the event that was logged.
     */
    static public void log ( final TransactionType code, final String primaryUser ) {
        log( code, primaryUser, null, null );
    }

    /**
     * Log a minimal set of information
     *
     * @param code
     *            The type of event that occurred
     * @param primaryUser
     *            The Primary User involved
     */
    static public void log ( final TransactionType code, final User primaryUser ) {
        log( code, primaryUser.getUsername() );
    }

    /**
     * Get all logged events for a single user specified by name.
     *
     * @param user
     *            User to find LogEntries for
     * @return A List of all LogEntry events for the user
     */
    static public List<LogEntry> getAllForUser ( final String user ) {
        return LogEntry.getAllForUser( user );
    }

    /**
     * Retrieve all of the Log Entries for a given user
     *
     * @param user
     *            The User to retrieve log entries for
     * @return The List of Log Entries that was found
     */
    static public List<LogEntry> getAllForUser ( final User user ) {
        return getAllForUser( user.getUsername() );
    }

    /**
     * Get the top logged events for a single user specified by name.
     *
     * @param user
     *            User to find LogEntries for
     * @param top
     *            Number of events to find
     * @return A List of the LogEntry Entries for the user. If the number of
     *         Entries is less than `top`, returns all
     */
    static public List<LogEntry> getTopForUser ( final String user, final Integer top ) {
        final List<LogEntry> all = getAllForUser( user );
        all.sort( new Comparator<Object>() {
            @Override
            public int compare ( final Object arg0, final Object arg1 ) {
                return ( (LogEntry) arg0 ).getTime().compareTo( ( (LogEntry) arg1 ).getTime() );
            }

        } );
        try {
            return all.subList( 0, top );
        }
        catch ( final IndexOutOfBoundsException e ) { /*
                                                       * If num < top (ie, fewer
                                                       * records exist than were
                                                       * requested) return all
                                                       */
            return all;
        }
    }

    /**
     * Log an event
     *
     * @param code
     *            The type of event that occurred
     * @param primary
     *            The primary user involved
     * @param secondary
     *            The secondary user involved
     */
    public static void log ( final TransactionType code, final User primary, final User secondary ) {
        log( code, primary.getUsername(), secondary.getUsername(), null );

    }
}
