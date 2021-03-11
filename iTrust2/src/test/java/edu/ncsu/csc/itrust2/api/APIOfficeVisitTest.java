package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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
import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.BasicHealthMetrics;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.BloodType;
import edu.ncsu.csc.iTrust2.models.enums.Ethnicity;
import edu.ncsu.csc.iTrust2.models.enums.Gender;
import edu.ncsu.csc.iTrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.models.enums.Status;
import edu.ncsu.csc.iTrust2.services.AppointmentRequestService;
import edu.ncsu.csc.iTrust2.services.BasicHealthMetricsService;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Test for the API functionality for interacting with office visits
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIOfficeVisitTest {

    private MockMvc                   mvc;

    @Autowired
    private WebApplicationContext     context;

    @Autowired
    private OfficeVisitService        officeVisitService;

    @Autowired
    private UserService               userService;

    @Autowired
    private AppointmentRequestService appointmentRequestService;

    @Autowired
    private HospitalService           hospitalService;

    @Autowired
    private BasicHealthMetricsService bhmService;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        officeVisitService.deleteAll();

        appointmentRequestService.deleteAll();

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final Patient antti = buildPatient();

        userService.saveAll( List.of( patient, hcp, antti ) );

        final Hospital hosp = new Hospital();
        hosp.setAddress( "123 Raleigh Road" );
        hosp.setState( State.NC );
        hosp.setZip( "27514" );
        hosp.setName( "iTrust Test Hospital 2" );

        hospitalService.save( hosp );
    }

    private Patient buildPatient () {
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        antti.setAddress1( "1 Test Street" );
        antti.setAddress2( "Some Location" );
        antti.setBloodType( BloodType.APos );
        antti.setCity( "Viipuri" );
        final LocalDate date = LocalDate.of( 1977, 6, 15 );
        antti.setDateOfBirth( date );
        antti.setEmail( "antti@itrust.fi" );
        antti.setEthnicity( Ethnicity.Caucasian );
        antti.setFirstName( "Antti" );
        antti.setGender( Gender.Male );
        antti.setLastName( "Walhelm" );
        antti.setPhone( "123-456-7890" );
        antti.setState( State.NC );
        antti.setZip( "27514" );

        return antti;
    }

    /**
     * Tests getting a non existent office visit and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetNonExistentOfficeVisit () throws Exception {
        mvc.perform( get( "/api/v1/officevisits/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests handling of errors when creating a visit for a pre-scheduled
     * appointment.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testPreScheduledOfficeVisit () throws Exception {

        final AppointmentRequestForm appointmentForm = new AppointmentRequestForm();

        // 2030-11-19 4:50 AM EST
        appointmentForm.setDate( "2030-11-19T04:50:00.000-05:00" );

        appointmentForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        appointmentForm.setStatus( Status.APPROVED.toString() );
        appointmentForm.setHcp( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setComments( "Test appointment please ignore" );

        appointmentRequestService.save( appointmentRequestService.build( appointmentForm ) );

        final OfficeVisitForm visit = new OfficeVisitForm();
        visit.setPreScheduled( "yes" );
        visit.setDate( "2030-11-19T04:50:00.000-05:00" );
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );

        mvc.perform( post( "/api/v1/officevisits" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        Assert.assertEquals( 1, officeVisitService.count() );

        officeVisitService.deleteAll();

        Assert.assertEquals( 0, officeVisitService.count() );

        visit.setDate( "2030-12-19T04:50:00.000-05:00" );
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
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testOfficeVisitAPI () throws Exception {

        Assert.assertEquals( 0, officeVisitService.count() );

        final OfficeVisitForm visit = new OfficeVisitForm();
        visit.setDate( "2030-11-19T04:50:00.000-05:00" );
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );

        /* Create the Office Visit */
        mvc.perform( post( "/api/v1/officevisits" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        Assert.assertEquals( 1, officeVisitService.count() );

        mvc.perform( get( "/api/v1/officevisits" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        /* Test getForHCP and getForHCPAndPatient */
        OfficeVisit v = officeVisitService.build( visit );
        List<OfficeVisit> vList = officeVisitService.findByHcp( v.getHcp() );
        assertEquals( vList.get( 0 ).getHcp(), v.getHcp() );
        vList = officeVisitService.findByHcpAndPatient( v.getHcp(), v.getPatient() );
        assertEquals( vList.get( 0 ).getHcp(), v.getHcp() );
        assertEquals( vList.get( 0 ).getPatient(), v.getPatient() );

        visit.setPatient( "antti" );
        visit.setDiastolic( 83 );
        visit.setHdl( 70 );
        visit.setHeight( 69.1f );
        visit.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );
        visit.setLdl( 30 );
        visit.setPatientSmokingStatus( PatientSmokingStatus.FORMER );
        visit.setSystolic( 102 );
        visit.setTri( 150 );
        visit.setWeight( 175.2f );
        v = officeVisitService.build( visit );

        /* Test that all fields have been filled successfully */
        assertNotNull( v );
        assertEquals( "antti", visit.getPatient() );
        assertEquals( Integer.valueOf( 83 ), visit.getDiastolic() );
        assertEquals( Integer.valueOf( 70 ), visit.getHdl() );
        assertEquals( Float.valueOf( 69.1f ), visit.getHeight() );
        assertEquals( HouseholdSmokingStatus.INDOOR, visit.getHouseSmokingStatus() );
        assertEquals( Integer.valueOf( 30 ), visit.getLdl() );
        assertEquals( PatientSmokingStatus.FORMER, visit.getPatientSmokingStatus() );
        assertEquals( Integer.valueOf( 102 ), visit.getSystolic() );
        assertEquals( Integer.valueOf( 150 ), visit.getTri() );
        assertEquals( Float.valueOf( 175.2f ), visit.getWeight() );

        /* Create new BasicHealthMetrics for testing */
        final BasicHealthMetrics bhm1 = bhmService.build( visit );
        final BasicHealthMetrics bhm2 = bhmService.build( visit );
        assertTrue( bhm1.equals( bhm1 ) );
        assertTrue( bhm1.equals( bhm2 ) );
        assertTrue( bhm2.equals( bhm1 ) );

        /* Hash codes are the same for equal objects */
        assertEquals( bhm1.hashCode(), bhm2.hashCode() );
        assertTrue( bhm1.equals( bhm2 ) );
        assertTrue( bhm2.equals( bhm1 ) );

        /* Weights are different */
        bhm2.setWeight( 172.3f );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );

        /* One weight is null */
        bhm2.setWeight( null );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setWeight( 175.2f );

        /* Tri is different */
        bhm2.setTri( 140 );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );

        /* One tri is null */
        bhm2.setTri( null );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setTri( 150 );

        /* Systolics are different */
        bhm2.setSystolic( 100 );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );

        /* One systolic is null */
        bhm2.setSystolic( null );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setSystolic( 102 );

        /* Patient smoking statuses are different */
        bhm2.setPatientSmokingStatus( PatientSmokingStatus.NEVER );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setPatientSmokingStatus( PatientSmokingStatus.FORMER );

        /* One patient is null */
        bhm2.setPatient( null );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setPatient( userService.findByName( "antti" ) );

        /* LDL's are different */
        bhm2.setLdl( 40 );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );

        /* One LDL is null */
        bhm2.setLdl( null );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setLdl( 30 );

        /* Household smoking statuses are different */
        bhm2.setHouseSmokingStatus( HouseholdSmokingStatus.OUTDOOR );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setHouseSmokingStatus( HouseholdSmokingStatus.INDOOR );

        /* Heights are different */
        bhm2.setHeight( 60.2f );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );

        /* One height is null */
        bhm2.setHeight( null );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setHeight( 69.1f );

        /* Different head circumferences */
        bhm2.setHeadCircumference( 8.7f );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm1.setHeadCircumference( 8.7f );

        /* HDL's are different */
        bhm2.setHdl( 80 );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );

        /* One HDL is null */
        bhm2.setHdl( null );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setHdl( 70 );

        /* Diastolics are different */
        bhm2.setDiastolic( 85 );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );

        /* One diastolic is null */
        bhm2.setDiastolic( null );
        assertFalse( bhm1.equals( bhm2 ) );
        assertFalse( bhm2.equals( bhm1 ) );
        bhm2.setDiastolic( 83 );
        assertTrue( bhm1.equals( bhm2 ) );
        assertTrue( bhm2.equals( bhm1 ) );

        /* Create appointment with patient younger than 12 years old */

        final Patient patient2 = buildPatient();
        userService.save( patient2 );
        patient2.setDateOfBirth( LocalDate.of( 2040, 6, 15 ) );
        visit.setPatient( patient2.getUsername() );
        v = officeVisitService.build( visit );
        assertNotNull( v );

        /* Create appointment with patient younger than 3 years old */
        final Patient patient3 = buildPatient();
        patient3.setDateOfBirth( LocalDate.of( 2046, 6, 15 ) );
        userService.save( patient3 );
        visit.setHeadCircumference( 20.0f );
        visit.setPatient( patient3.getUsername() );
        v = officeVisitService.build( visit );
        assertNotNull( v );

        /*
         * We need the ID of the office visit that actually got _saved_ when
         * calling the API above. This will get it
         */
        final Long id = officeVisitService.findByPatient( userService.findByName( "patient" ) ).get( 0 ).getId();

        visit.setId( id.toString() );

        // Second post should fail with a conflict since it already exists
        mvc.perform( post( "/api/v1/officevisits" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isConflict() );

        mvc.perform( get( "/api/v1/officevisits/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        visit.setDate( "2030-11-19T09:45:00.000-05:00" );

        mvc.perform( put( "/api/v1/officevisits/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        // PUT with ID not in database should fail
        final long tempId = 101;
        visit.setId( "101" );
        mvc.perform( put( "/api/v1/officevisits/" + tempId ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isNotFound() );

    }

}
