package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.virologist.ContactsByDepthForm;
import edu.ncsu.csc.itrust2.models.persistent.Passenger;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.unit.PassengerTest;

/**
 * Class for testing Passenger API
 *
 * @author Leon Li
 * @author Christian Provenzano
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIPassengerTest {
    /**
     * MVC
     */
    private MockMvc               mvc;
    /**
     * Web Context
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * Used for inputing file data
     */
    private String                fileInfo;
    /**
     * Used for inputting contacts by depth info
     */
    private ContactsByDepthForm   form;

    /**
     * Sets up.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        Passenger.deleteAll();
    }

    /**
     * Test with no passengers
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testGetStatisticsNoData () throws Exception {
        mvc.perform( get( "/api/v1/diseasecontrol/statistics" ) ).andExpect( status().isBadRequest() );
    }

    /**
     * Test with passengers
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testGetStatisticsWithData () throws Exception {
        Passenger.parse( PassengerTest.getFileContents( "rnaught_equalto_one.csv" ) );

        mvc.perform( get( "/api/v1/diseasecontrol/statistics" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.newInfectionsPerDay['2020-02-13']" ).value( 4 ) )
                .andExpect( jsonPath( "$.newInfectionsPerDay['2020-02-14']" ).value( 4 ) )
                .andExpect( jsonPath( "$.newInfectionsPerDay['2020-02-15']" ).value( 4 ) )
                .andExpect( jsonPath( "$.totalInfectionsPerDay['2020-02-13']" ).value( 4 ) )
                .andExpect( jsonPath( "$.totalInfectionsPerDay['2020-02-14']" ).value( 8 ) )
                .andExpect( jsonPath( "$.totalInfectionsPerDay['2020-02-15']" ).value( 12 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.notInfected[0]" ).value( 8 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.notInfected[1]" ).value( 4 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.notInfected[2]" ).value( 0 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.mild[0]" ).value( 2 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.mild[1]" ).value( 6 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.mild[2]" ).value( 8 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.severe[0]" ).value( 2 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.severe[1]" ).value( 2 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.severe[2]" ).value( 3 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.critical[0]" ).value( 0 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.critical[1]" ).value( 0 ) )
                .andExpect( jsonPath( "$.passengersBySeverity.critical[2]" ).value( 1 ) );
    }

    /**
     * Test with no passengers
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testGetRNaughtNoData () throws Exception {
        mvc.perform( get( "/api/v1/diseasecontrol/rnaught" ) ).andExpect( status().isBadRequest() );
    }

    /**
     * Test with passengers
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testGetRNaughtWithData () throws Exception {
        Passenger.parse( PassengerTest.getFileContents( "rnaught_equalto_one.csv" ) );

        mvc.perform( get( "/api/v1/diseasecontrol/rnaught" ) ).andExpect( status().isOk() )
                .andExpect( content().string( "1.0" ) );
    }

    /**
     * Test with passengers but not enough data to calculate R0
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testGetRNaughtWithInvalidData () throws Exception {
        Passenger.parse( PassengerTest.getFileContents( "rnaught_invalid.csv" ) );

        mvc.perform( get( "/api/v1/diseasecontrol/rnaught" ) ).andExpect( status().isOk() )
                .andExpect( content().string( "-1.0" ) );
    }

    /**
     * Test with exclusively duplicate passengers (such as re-uploading the same
     * file)
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testUploadPassengerDataFailureAllDuplicates () throws Exception {
        // add the passengers to the database
        final String fileContents = PassengerTest.getFileContents( "passenger-data-short.csv" );
        Passenger.parse( fileContents );

        // then, try adding the same contents
        mvc.perform( post( "/api/v1/diseasecontrol/passengers/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( fileContents ) ).andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.message" ).value( "All passengers in this file are already in the system" ) );
        assertEquals( 11, Passenger.getPassengers().size() );
    }

    /**
     * Test with incorrectly formatted file contents
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testUploadPassengerDataFailureInvalidFile () throws Exception {
        final String fileContents = PassengerTest.getFileContents( "passenger-data-invalid.csv" );

        mvc.perform( post( "/api/v1/diseasecontrol/passengers/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( fileContents ) ).andExpect( status().isBadRequest() ).andExpect(
                        jsonPath( "$.message" ).value( "No successful uploads, check file formatting and content" ) );
        assertEquals( 0, Passenger.getPassengers().size() );
    }

    /**
     * Test with new passengers (no duplicates)
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testUploadPassengerDataSuccessNoDuplicates () throws Exception {
        final String fileContents = PassengerTest.getFileContents( "passenger-data-short.csv" );

        mvc.perform( post( "/api/v1/diseasecontrol/passengers/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( fileContents ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$[0]" ).value( "11" ) )
                .andExpect( jsonPath( "$[1]" ).value( "0" ) );
        assertEquals( 11, Passenger.getPassengers().size() );
    }

    /**
     * Test with new passengers and some duplicates in the uploaded file
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testUploadPassengerDataSuccessWithDuplicates () throws Exception {
        final String fileContents = PassengerTest.getFileContents( "passenger-data-short-duplicates.csv" );

        mvc.perform( post( "/api/v1/diseasecontrol/passengers/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                .content( fileContents ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$[0]" ).value( "5" ) )
                .andExpect( jsonPath( "$[1]" ).value( "5" ) );
        assertEquals( 5, Passenger.getPassengers().size() );
    }

    /**
     * Test with a bad list of contacts
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testUploadContactsFailure () throws Exception {
        /**
         * Test with no passengers in database
         */
        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                "passenger-contacts-invalid.csv" );
        try {

            fileInfo = new String( Files.readAllBytes( resourceDirectory ) );
            // Need to put the file in the .content()

            mvc.perform( post( "/api/v1/diseasecontrol/contacts/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( fileInfo ) ) ).andExpect( status().isConflict() );

        }
        catch ( final Exception e ) {
            fail();
        }
        /**
         * Add passengers back to the database
         */
        final Path resourceDirectory2 = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust",
                "zipdata", "abcpassengers.csv" );
        String fileContents;
        try {
            fileContents = new String( Files.readAllBytes( resourceDirectory2 ) );
            mvc.perform( post( "/api/v1/diseasecontrol/passengers/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( fileContents ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$[0]" ).value( "8" ) )
                    .andExpect( jsonPath( "$[1]" ).value( "0" ) );
            assertEquals( 8, Passenger.getPassengers().size() );
        }
        catch ( final IOException e ) {
            fail( "Failed to open input file for test case" );
        }
        /**
         * Test with passengers in database that won't work
         */
        final Path resourceDirectory3 = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust",
                "zipdata", "passenger-contacts-invalid.csv" );
        try {

            fileInfo = new String( Files.readAllBytes( resourceDirectory3 ) );
            // Need to put the file in the .content()

            mvc.perform( post( "/api/v1/diseasecontrol/contacts/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( fileInfo ) ) ).andExpect( status().isConflict() );

        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Tests with a passable list of contacts
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testUploadContactsSuccess () throws Exception {
        /**
         * Add passengers back to the database
         */
        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                "abcpassengers.csv" );
        String fileContents;
        try {
            fileContents = new String( Files.readAllBytes( resourceDirectory ) );
            mvc.perform( post( "/api/v1/diseasecontrol/passengers/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( fileContents ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$[0]" ).value( "8" ) )
                    .andExpect( jsonPath( "$[1]" ).value( "0" ) );
            assertEquals( 8, Passenger.getPassengers().size() );
        }
        catch ( final IOException e ) {
            fail( "Failed to open input file for test case" );
        }
        /**
         * Add contacts into the database
         */
        final Path resourceDirectory2 = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust",
                "zipdata", "abccontacts.csv" );
        String contactInfo;
        try {
            contactInfo = new String( Files.readAllBytes( resourceDirectory2 ) );
            mvc.perform( post( "/api/v1/diseasecontrol/contacts/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( contactInfo ) ) );//.andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Test with all correct data inputed
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testContactsByDepthSuccess () throws Exception {
        /**
         * Add passengers back to the database
         */
        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                "abcpassengers.csv" );
        String fileContents;
        try {
            fileContents = new String( Files.readAllBytes( resourceDirectory ) );
            mvc.perform( post( "/api/v1/diseasecontrol/passengers/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( fileContents ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$[0]" ).value( "8" ) )
                    .andExpect( jsonPath( "$[1]" ).value( "0" ) );
            assertEquals( 8, Passenger.getPassengers().size() );
        }
        catch ( final IOException e ) {
            fail( "Failed to open input file for test case" );
        }
        /**
         * Add contacts into the database
         */
        final Path resourceDirectory2 = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust",
                "zipdata", "abccontacts.csv" );
        String contactInfo;
        try {
            contactInfo = new String( Files.readAllBytes( resourceDirectory2 ) );
            mvc.perform( post( "/api/v1/diseasecontrol/contacts/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( contactInfo ) ) );//.andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail();
        }
        final ContactsByDepthForm form = new ContactsByDepthForm();

        final int validDepth = 2;
        final String validId = "A";

        form.setDepth( validDepth );
        form.setPassengerId( validId );

        try {
            mvc.perform( post( "/api/v1/diseasecontrol/contacts" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail();
        }

    }

    /**
     * Test with different types of faulty data (may need to separate into three
     * different tests
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "virologist", roles = { "VIROLOGIST" } )
    public void testContactsByDepthFailure () throws Exception {
        /**
         * Add passengers back to the database
         */
        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                "abcpassengers.csv" );
        String fileContents;
        try {
            fileContents = new String( Files.readAllBytes( resourceDirectory ) );
            mvc.perform( post( "/api/v1/diseasecontrol/passengers/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( fileContents ) ).andExpect( status().isOk() ).andExpect( jsonPath( "$[0]" ).value( "8" ) )
                    .andExpect( jsonPath( "$[1]" ).value( "0" ) );
            assertEquals( 8, Passenger.getPassengers().size() );
        }
        catch ( final IOException e ) {
            fail( "Failed to open input file for test case" );
        }
        /**
         * Add contacts into the database
         */
        final Path resourceDirectory2 = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust",
                "zipdata", "abccontacts.csv" );
        String contactInfo;
        try {
            contactInfo = new String( Files.readAllBytes( resourceDirectory2 ) );
            mvc.perform( post( "/api/v1/diseasecontrol/contacts/csv" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( contactInfo ) ) );//.andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail();
        }
        final ContactsByDepthForm form = new ContactsByDepthForm();

        final int validDepth = 2;
        final String validId = "A";

        final int invalidDepth = 0;
        final String invalidId = "Z";

        form.setDepth( validDepth );
        form.setPassengerId( validId );

        try {
            mvc.perform( post( "/api/v1/diseasecontrol/contacts" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail();
        }

        form.setDepth( invalidDepth );
        form.setPassengerId( validId );

        try {
            mvc.perform( post( "/api/v1/diseasecontrol/contacts" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );
        }
        catch ( final Exception e ) {
            fail();
        }

        form.setDepth( validDepth );
        form.setPassengerId( invalidId );

        try {
            mvc.perform( post( "/api/v1/diseasecontrol/contacts" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

}
