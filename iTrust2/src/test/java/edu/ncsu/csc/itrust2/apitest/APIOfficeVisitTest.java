package edu.ncsu.csc.itrust2.apitest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.forms.patient.AppointmentRequestForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for the API functionality for interacting with office visits
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration

public class APIOfficeVisitTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests getting a non existent office visit and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    public void testGetNonExistentOfficeVisit () throws Exception {
        mvc.perform( get( "/api/v1/officevisits/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests deleting a non existent office visit and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    public void testDeleteNonExistentOfficeVisit () throws Exception {
        mvc.perform( delete( "/api/v1/officevisits/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests handling of errors when creating a visit for a pre-scheduled
     * appointment.
     *
     * @throws Exception
     */
    @Test
    public void testPreScheduledOfficeVisit () throws Exception {
        final UserForm hcp = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcp ) ) );

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        mvc.perform( delete( "/api/v1/appointmentrequests" ) );

        final AppointmentRequestForm appointmentForm = new AppointmentRequestForm();
        appointmentForm.setDate( "11/19/2030" );
        appointmentForm.setTime( "4:50 AM" );
        appointmentForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        appointmentForm.setStatus( Status.APPROVED.toString() );
        appointmentForm.setHcp( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setComments( "Test appointment please ignore" );
        mvc.perform( post( "/api/v1/appointmentrequests" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/officevisits" ) );
        final OfficeVisitForm visit = new OfficeVisitForm();
        visit.setPreScheduled( "yes" );
        visit.setDate( "11/19/2030" );
        visit.setTime( "4:50 AM" );
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );

        mvc.perform( post( "/api/v1/officevisits" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/officevisits" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        mvc.perform( delete( "/api/v1/officevisits" ) );
        visit.setDate( "12/20/2031" );
        // setting a pre-scheduled appointment that doesn't match should not
        // work.
        mvc.perform( post( "/api/v1/officevisits" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isBadRequest() );

    }

    /**
     * Tests OfficeVisitAPI
     *
     * @throws Exception
     */
    @Test
    public void testOfficeVisitAPI () throws Exception {

        /*
         * Create a HCP and a Patient to use. If they already exist, this will
         * do nothing
         */
        final UserForm hcp = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcp ) ) );

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        /* Create a Hospital to use too */
        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) );

        mvc.perform( delete( "/api/v1/officevisits" ) );
        final OfficeVisitForm visit = new OfficeVisitForm();
        visit.setDate( "4/16/2048" );
        visit.setTime( "9:50 AM" );
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );

        /* Create the Office Visit */
        mvc.perform( post( "/api/v1/officevisits" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) );

        mvc.perform( get( "/api/v1/officevisits" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        /*
         * We need the ID of the office visit that actually got _saved_ when
         * calling the API above. This will get it
         */
        final Long id = OfficeVisit.getForPatient( patient.getUsername() ).get( 0 ).getId();

        visit.setId( id.toString() );

        // Second post should fail with a conflict since it already exists
        mvc.perform( post( "/api/v1/officevisits" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isConflict() );

        mvc.perform( get( "/api/v1/officevisits/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        visit.setTime( "9:45 AM" );

        mvc.perform( put( "/api/v1/officevisits/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // PUT with the non-matching IDs should fail
        mvc.perform( put( "/api/v1/officevisits/" + 1 ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isConflict() );

        // PUT with ID not in database should fail
        final long tempId = 101;
        visit.setId( "101" );
        mvc.perform( put( "/api/v1/officevisits/" + tempId ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isNotFound() );

        // Reset ID to old id
        visit.setId( id.toString() );

        mvc.perform( delete( "/api/v1/officevisits/" + id ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/officevisits" ) );

    }

}
