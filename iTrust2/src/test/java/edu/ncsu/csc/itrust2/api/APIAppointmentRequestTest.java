package edu.ncsu.csc.iTrust2.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.AppointmentRequestForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.AppointmentRequest;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.Status;
import edu.ncsu.csc.iTrust2.services.AppointmentRequestService;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Test for the API functionality for interacting with appointment requests
 *
 * @author Kai Presler-Marshall
 * @author Matt Dzwonczyk
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIAppointmentRequestTest {

    private MockMvc                   mvc;

    @Autowired
    private WebApplicationContext     context;

    @Autowired
    private AppointmentRequestService arService;

    @Autowired
    private UserService               service;

    /**
     * Sets up tests
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        service.deleteAll();
        arService.deleteAll();

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        service.saveAll( List.of( patient, hcp ) );

    }

    /**
     * Tests that getting an appointment that doesn't exist returns the proper
     * status
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    @Transactional
    public void testGetNonExistentAppointment () throws Exception {
        mvc.perform( get( "/api/v1/appointmentrequests/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests that deleting an appointment that doesn't exist returns the proper
     * status.
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    @Transactional
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
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    @Transactional
    public void testCreateBadAppointmentRequest () throws Exception {

        final AppointmentRequestForm appointmentForm = new AppointmentRequestForm();
        appointmentForm.setDate( "0" );
        appointmentForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        appointmentForm.setStatus( Status.PENDING.toString() );
        appointmentForm.setHcp( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setComments( "Test appointment please ignore" );

        mvc.perform( post( "/api/v1/appointmentrequests" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isBadRequest() );
    }

    /**
     * Tests AppointmentRequestAPi
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    @Transactional
    public void testAppointmentRequestAPI () throws Exception {

        final User patient = service.findByName( "patient" );

        final AppointmentRequestForm appointmentForm = new AppointmentRequestForm();
        appointmentForm.setDate( "2030-11-19T04:50:00.000-05:00" ); // 2030-11-19
                                                                    // 4:50 AM
                                                                    // EST
        appointmentForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        appointmentForm.setStatus( Status.PENDING.toString() );
        appointmentForm.setHcp( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setComments( "Test appointment please ignore" );

        /* Create the request */
        mvc.perform( post( "/api/v1/appointmentrequests" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) );

        mvc.perform( get( "/api/v1/appointmentrequest" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        List<AppointmentRequest> forPatient = (List<AppointmentRequest>) arService.findAll();
        Assert.assertEquals( 1, forPatient.size() );

        /*
         * We need the ID of the appointment request that actually got _saved_
         * when calling the API above. This will get it
         */
        final Long id = arService.findByPatient( patient ).get( 0 ).getId();

        mvc.perform( get( "/api/v1/appointmentrequests/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        appointmentForm.setDate( "2030-11-19T03:30:00.000-05:00" ); // 2030-11-19
                                                                    // 3:30 AM

        mvc.perform( put( "/api/v1/appointmentrequests/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        forPatient = (List<AppointmentRequest>) arService.findAll();
        Assert.assertEquals( 1, forPatient.size() );
        Assert.assertEquals( "2030-11-19T03:30-05:00", forPatient.get( 0 ).getDate().toString() );

        // Updating a nonexistent ID should not work
        mvc.perform( put( "/api/v1/appointmentrequests/-1" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isNotFound() );

        mvc.perform( delete( "/api/v1/appointmentrequests/" + id ) ).andExpect( status().isOk() );

    }

}
