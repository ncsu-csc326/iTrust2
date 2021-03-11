package edu.ncsu.csc.iTrust2.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.models.security.LogEntry;
import edu.ncsu.csc.iTrust2.services.security.LogEntryService;

/**
 * Logging class to handle saving log-worthy events and for retrieving those
 * that previously occurred. All actions that need to be logged (as defined in
 * the iTrust Wiki) should be logged using one of the three `Log` methods here.
 *
 * @author Kai Presler-Marshall
 *
 */
@Component
public class LoggerUtil {

    @Autowired
    private LogEntryService service;

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
    public void log ( final TransactionType code, final String primaryUser, final String secondaryUser,
            final String message ) {
        final LogEntry le = new LogEntry( code, primaryUser, secondaryUser, message );
        service.save( le );
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
    public void log ( final TransactionType code, final String primaryUser, final String message ) {
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
    public void log ( final TransactionType code, final String primaryUser ) {
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
    public void log ( final TransactionType code, final User primaryUser ) {
        log( code, primaryUser.getUsername() );
    }

    /**
     * Get all logged events for a single user specified by name.
     *
     * @param user
     *            User to find LogEntries for
     * @return A List of all LogEntry events for the user
     */
    public List<LogEntry> getAllForUser ( final String user ) {
        return service.findAllForUser( user );
    }

    /**
     * Retrieve all of the Log Entries for a given user
     *
     * @param user
     *            The User to retrieve log entries for
     * @return The List of Log Entries that was found
     */
    public Object getAllForUser ( final User user ) {
        return service.findAllForUser( user.getUsername() );
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
    public Object getTopForUser ( final String user, final Integer top ) {
        final List<LogEntry> all = getAllForUser( user );
        all.sort( ( x1, x2 ) -> x1.getTime().compareTo( x2.getTime() ) );
        try {
            return all.subList( 0, top );
        }
        /*
         * If num < top (ie, fewer records exist than were requested) return all
         */
        catch ( final IndexOutOfBoundsException e ) {
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
    public void log ( final TransactionType code, final User primary, final User secondary ) {
        log( code, primary.getUsername(), secondary.getUsername(), null );

    }

    /**
     * Gets the name of the currently authenticated user
     *
     * @return the name of the current user
     */
    public static String currentUser () {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        catch ( final NullPointerException npe ) {
            return "SPRING_API_TEST_USER"; // API tests have no explicit user
        }
    }
}
