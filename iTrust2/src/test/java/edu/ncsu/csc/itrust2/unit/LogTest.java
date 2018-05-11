package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Unit tests for the LoggerUtil and LogEntry classes
 *
 * @author jshore
 *
 */
public class LogTest {

    /**
     * Tests logging behavior, filtering by user, hcp, etc.
     */
    @Test
    public void testLogging () {
        // Create 3 log entries to mess around with.
        LoggerUtil.log( TransactionType.VIEW_SCHEDULED_APPOINTMENT, "logPatient", "logHcp",
                "test log entry with patient and hcp" );
        LoggerUtil.log( TransactionType.CREATE_HOSPITAL, "logAdmin" );
        LoggerUtil.log( TransactionType.LOGIN_SUCCESS, "logPatient", "login succeeded for logPatient." );
        int hcpEntries = LoggerUtil.getAllForUser( "logHcp" ).size();

        // Test that searching by name and user object works.
        assertTrue( hcpEntries == 1 );
        final User testUser = new User();
        testUser.setUsername( "logHcp" );
        hcpEntries = LoggerUtil.getAllForUser( testUser ).size();
        assertTrue( hcpEntries == 1 );

        // Test various edge cases of getTopForUser - all entries and more than
        // the total. Both lists should have length 1.
        hcpEntries = LoggerUtil.getTopForUser( "logHcp", 1 ).size();
        assertTrue( hcpEntries == 1 );

        hcpEntries = LoggerUtil.getTopForUser( "logHcp", 10 ).size();
        assertTrue( hcpEntries == 1 );

        // Now test similar methods for LogEntry
        hcpEntries = LogEntry.getAllForUser( "logHcp" ).size();
        assertTrue( hcpEntries == 1 );

        final LogEntry twoUserEntry = LogEntry.getAllForUser( "logHcp" ).get( 0 );
        assertEquals( "logPatient", twoUserEntry.getPrimaryUser() );
        assertEquals( "logHcp", twoUserEntry.getSecondaryUser() );
        assertNotNull( twoUserEntry.getTime() );
        assertEquals( "test log entry with patient and hcp", twoUserEntry.getMessage() );
    }
}
