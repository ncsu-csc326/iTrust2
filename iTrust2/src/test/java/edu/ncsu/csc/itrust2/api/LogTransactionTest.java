package edu.ncsu.csc.iTrust2.api;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.models.security.LogEntry;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import edu.ncsu.csc.iTrust2.services.security.LogEntryService;

/**
 * Tests for UC3 (Log Transactions) [S1]: any action that creates, views,
 * edits, or deletes information must be logged with the logged-in MID, an
 * appropriate secondary MID, a transaction type, and a timestamp.
 *
 * Hospitals are used here (rather than Users, covered separately for UC1) so
 * that this UC's general logging mechanism is verified independently of any
 * one specific feature area's controller.
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class LogTransactionTest {

    private static final String   HOSPITAL_NAME = "UC3 Test Hospital";

    private MockMvc                mvc;

    @Autowired
    private WebApplicationContext  context;

    @Autowired
    private HospitalService        hospitalService;

    @Autowired
    private LogEntryService        logEntryService;

    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        hospitalService.deleteAll();
    }

    private List<LogEntry> logsFor ( final String user, final TransactionType type ) {
        return logEntryService.findAllForUser( user ).stream().filter( e -> e.getLogCode() == type )
                .collect( Collectors.toList() );
    }

    /**
     * UC3 [S1]: creating and editing a record logs the correct transaction
     * code, records the logged-in MID as the primary user, and stamps the
     * event with the current time.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testCreateAndEditActions_logMidAndTimestamp () throws Exception {

        final Hospital hospital = new Hospital( HOSPITAL_NAME, "1 iTrust Test Street", "27607", "NC" );

        final ZonedDateTime before = ZonedDateTime.now().minusSeconds( 5 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        final List<LogEntry> createLogs = logsFor( "admin", TransactionType.CREATE_HOSPITAL );
        Assert.assertEquals( "Creating a Hospital should log exactly one CREATE_HOSPITAL transaction", 1,
                createLogs.size() );
        Assert.assertEquals( "The logged-in MID (admin) should be the primary user on the log entry", "admin",
                createLogs.get( 0 ).getPrimaryUser() );
        Assert.assertTrue( "The log entry's timestamp should be at/after the time the action occurred",
                !createLogs.get( 0 ).getTime().isBefore( before )
                        && createLogs.get( 0 ).getTime().truncatedTo( ChronoUnit.SECONDS )
                                .isBefore( ZonedDateTime.now().plusSeconds( 5 ) ) );

        hospital.setAddress( "2 iTrust Test Street" );
        mvc.perform( MockMvcRequestBuilders.put( "/api/v1/hospitals/" + HOSPITAL_NAME )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( hospital ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assert.assertEquals( "Editing a Hospital should log exactly one EDIT_HOSPITAL transaction", 1,
                logsFor( "admin", TransactionType.EDIT_HOSPITAL ).size() );
    }

    /**
     * UC3 [S1]: deleting a record logs the correct transaction code, both for
     * a successful deletion and for an attempted deletion of a record that no
     * longer exists.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testDeleteAction_logsCorrectCodeForSuccessAndFailure () throws Exception {

        final Hospital hospital = new Hospital( HOSPITAL_NAME, "1 iTrust Test Street", "27607", "NC" );
        hospitalService.save( hospital );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/hospitals/" + HOSPITAL_NAME ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assert.assertEquals( "Deleting an existing Hospital should log exactly one DELETE_HOSPITAL transaction", 1,
                logsFor( "admin", TransactionType.DELETE_HOSPITAL ).size() );

        // Deleting the same (now nonexistent) Hospital again is still a
        // loggable event, per UC3, even though it fails
        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/hospitals/" + HOSPITAL_NAME ) )
                .andExpect( MockMvcResultMatchers.status().isNotFound() );

        Assert.assertEquals(
                "Attempting to delete a nonexistent Hospital should still log a second DELETE_HOSPITAL transaction",
                2, logsFor( "admin", TransactionType.DELETE_HOSPITAL ).size() );
    }

}
