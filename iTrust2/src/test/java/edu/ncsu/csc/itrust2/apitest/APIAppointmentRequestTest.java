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
import edu.ncsu.csc.itrust2.forms.patient.AppointmentRequestForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for the API functionality for interacting with appointment requests
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIAppointmentRequestTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up tests
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests that getting an appointment that doesn't exist returns the proper
     * status
     *
     * @throws Exception
     */
    @Test
    public void testGetNonExistentAppointment () throws Exception {
        mvc.perform( get( "/api/v1/appointmentrequests/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests that deleting an appointment that doesn't exist returns the proper
     * status.
     */
    @Test
    public void testDeleteNonExistentAppointment () throws Exception {
        mvc.perform( delete( "/api/v1/appointmentrequests/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests creating an appointment request with bad data. Should return a bad
     * request.
     *
     * @throws Exception
     */
    @Test
    public void testCreateBadAppointmentRequest () throws Exception {
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

        mvc.perform( delete( "/api/v1/appointmentrequests" ) );

        final AppointmentRequestForm appointmentForm = new AppointmentRequestForm();
        appointmentForm.setDate( "aa/19/2030" );
        appointmentForm.setTime( "aa:50 AM" );
        appointmentForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        appointmentForm.setStatus( Status.PENDING.toString() );
        appointmentForm.setHcp( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setComments( "Test appointment please ignore" );

        mvc.perform( post( "/api/v1/appointmentrequests" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isBadRequest() );
        mvc.perform( delete( "/api/v1/appointmentrequests" ) );
    }

    /**
     * Tests AppointmentRequestAPi
     *
     * @throws Exception
     */
    @Test
    public void testAppointmentRequestAPI () throws Exception {

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

        mvc.perform( delete( "/api/v1/appointmentrequests" ) );

        final AppointmentRequestForm appointmentForm = new AppointmentRequestForm();
        appointmentForm.setDate( "11/19/2030" );
        appointmentForm.setTime( "4:50 AM" );
        appointmentForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        appointmentForm.setStatus( Status.PENDING.toString() );
        appointmentForm.setHcp( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setComments( "Test appointment please ignore" );

        /* Create the request */
        mvc.perform( post( "/api/v1/appointmentrequests" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) );

        mvc.perform( get( "/api/v1/appointmentrequests" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        /*
         * We need the ID of the appointment request that actually got _saved_
         * when calling the API above. This will get it
         */
        final Long id = AppointmentRequest.getAppointmentRequestsForPatient( patient.getUsername() ).get( 0 ).getId();

        mvc.perform( get( "/api/v1/appointmentrequests/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        appointmentForm.setTime( "3:30 AM" );

        mvc.perform( put( "/api/v1/appointmentrequests/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Updating a nonexistent ID should not work
        mvc.perform( put( "/api/v1/appointmentrequests/-1" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isNotFound() );

        mvc.perform( delete( "/api/v1/appointmentrequests/" + id ) ).andExpect( status().isOk() );

    }

}
