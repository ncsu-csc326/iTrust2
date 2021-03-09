package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

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
import edu.ncsu.csc.itrust2.forms.findexperts.FindExpertForm;
import edu.ncsu.csc.itrust2.managers.ZipCodeManager;
import edu.ncsu.csc.itrust2.models.enums.Specialty;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * API Find Expert Controller Test
 *
 * @author nrshah4
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIFindExpertControllerTest {

    /**
     * Web Context
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * MVC
     */
    private MockMvc               mvc;

    /**
     * Find Expert Form
     */
    private final FindExpertForm  form     = new FindExpertForm();

    /**
     * singleton manager
     */
    private ZipCodeManager        manager;

    /**
     * file path for sample csv data
     */
    private final String          filepath = "src/test/resources/edu/ncsu/csc/itrust/zipdata/sample.csv";

    /**
     * Set up.
     */
    @Before
    public void setup () {

        manager = ZipCodeManager.getInstance();

        manager.clearDatabase();

        try {
            manager.loadDatabase( filepath );
        }
        catch ( final IOException e ) {
            fail( "Could not load database." );
        }

        assertTrue( manager.checkDatabase() );

        form.setRadius( 5 );
        form.setZip( "27601" );
        form.setSpecialty( "Cardiologist" );

        HibernateDataGenerator.generateFindExpertTest();

        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

    }

    /**
     * Test a valid zipcode and valid table result.
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testvalidZip () {

        try {
            mvc.perform( post( "/api/v1/findexperts" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail();
        }

        final List<LogEntry> entries = LoggerUtil.getAllForUser( "patient" );
        assertEquals( TransactionType.FIND_EXPERTS_PATIENT, entries.get( entries.size() - 1 ).getLogCode() );

    }

    /**
     * Test invalid zip code.
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testInvalidZip () {
        form.setZip( "1111" );
        try {
            mvc.perform( post( "/api/v1/findexperts" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isNotFound() );
        }
        catch ( final Exception e ) {
            fail( e.toString() );
        }
    }

    /**
     * Test no matches found.
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testInvalidSearch () {

        final Personnel p = Personnel.getByName( "Eddie" );
        p.setSpecialty( Specialty.NEUROLOGIST );
        p.save();

        try {
            mvc.perform( post( "/api/v1/findexperts" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isOk() );
        }
        catch ( final Exception e ) {
            fail( e.toString() );
        }

    }

    /**
     * Test if the database is cleared.
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testNoDatabase () {

        manager.clearDatabase();
        try {
            mvc.perform( post( "/api/v1/findexperts" ).contentType( MediaType.APPLICATION_JSON_UTF8 )
                    .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isBadRequest() );
        }
        catch ( final Exception e ) {
            fail( e.toString() );
        }

    }

}
