package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp.OphthalmologySurgeryForm;
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.forms.patient.AppointmentRequestForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.EyeSurgeryType;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.OphthalmologySurgery;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for the API functionality for ophthalmology surgeries
 *
 * @author Jack MacDonald
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIOphthalmologySurgeryTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        final Patient p = Patient.getByName( "patient" );
        if ( p != null ) {
            p.delete();
        }

    }

    /**
     * Tests getting a non existent office visit and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "robortOPH", roles = { "OPH" } )
    public void testGetNonExistentOfficeVisit () throws Exception {
        mvc.perform( get( "/api/v1/ophthalmologysurgeries/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests deleting a non existent office visit and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "robortOPH", roles = { "OPH" } )
    public void testDeleteNonExistentOfficeVisit () throws Exception {
        mvc.perform( delete( "/api/v1/ophthalmologysurgeries/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests handling of errors when creating a visit for a pre-scheduled
     * appointment.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "robortOPH", roles = { "OPH", "PATIENT" } )
    public void testPreScheduledOphthalmologySurgery () throws Exception {
        final UserForm hcp = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcp ) ) );

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        mvc.perform( delete( "/api/v1/appointmentrequests" ) );

        final AppointmentRequestForm appointmentForm = new AppointmentRequestForm();
        appointmentForm.setDate( "2030-11-19T04:50:00.000-05:00" ); // 2030-11-19
                                                                    // 4:50 AM
                                                                    // EST
        appointmentForm.setType( AppointmentType.GENERAL_OPHTHALMOLOGY.toString() );
        appointmentForm.setStatus( Status.APPROVED.toString() );
        appointmentForm.setHcp( "hcp" );
        appointmentForm.setPatient( "patient" );
        appointmentForm.setComments( "Test appointment please ignore" );
        mvc.perform( post( "/api/v1/appointmentrequests" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( appointmentForm ) ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/officevisits" ) );
        final OphthalmologySurgeryForm visit = new OphthalmologySurgeryForm();
        visit.setPreScheduled( "yes" );
        visit.setDate( "2030-11-19T04:50:00.000-05:00" ); // 11/19/2030 4:50 AM
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_OPHTHALMOLOGY.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );

        visit.setDate( "2031-12-20T04:50:00.000-05:00" ); // 12/20/2031 4:50 AM
        // setting a pre-scheduled appointment that doesn't match should not
        // work.
        mvc.perform( post( "/api/v1/ophthalmologysurgeries" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isBadRequest() );
    }

    /**
     * Tests OfficeVisitAPI
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT", "OPH", "ADMIN" } )
    public void testOphthalmologySurgeryAPI () throws Exception {

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
        final OphthalmologySurgeryForm visit = new OphthalmologySurgeryForm();
        visit.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_OPHTHALMOLOGY.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );
        visit.setSurgeryType( EyeSurgeryType.REFRACTIVE );

        /* Create the Office Visit */
        mvc.perform( post( "/api/v1/ophthalmologysurgeries" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/officevisits" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        /* Test getForHCP and getForHCPAndPatient */
        OfficeVisit v = new OphthalmologySurgery( visit );
        List<OfficeVisit> vList = OfficeVisit.getForHCP( v.getHcp().getUsername() );
        assertEquals( vList.get( 0 ).getHcp(), v.getHcp() );
        vList = OfficeVisit.getForHCPAndPatient( v.getHcp().getUsername(), v.getPatient().getUsername() );
        assertEquals( vList.get( 0 ).getHcp(), v.getHcp() );
        assertEquals( vList.get( 0 ).getPatient(), v.getPatient() );

        /* Put new user into system */
        DomainObject.deleteAll( Patient.class );
        final UserForm p = new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p ) ) );

        /* Create new PatientForm, filling fields */
        final PatientForm patient1 = new PatientForm();
        patient1.setAddress1( "1 Test Street" );
        patient1.setAddress2( "Some Location" );
        patient1.setBloodType( BloodType.APos.toString() );
        patient1.setCity( "Viipuri" );
        patient1.setDateOfBirth( "6/15/1977" );
        patient1.setEmail( "antti@itrust.fi" );
        patient1.setEthnicity( Ethnicity.Caucasian.toString() );
        patient1.setFirstName( "Antti" );
        patient1.setGender( Gender.Male.toString() );
        patient1.setLastName( "Walhelm" );
        patient1.setPhone( "123-456-7890" );
        patient1.setSelf( "antti" );
        patient1.setState( State.NC.toString() );
        patient1.setZip( "27514" );

        /* Test that all fields have been filled successfully */
        assertEquals( "1 Test Street", patient1.getAddress1() );
        assertEquals( "Some Location", patient1.getAddress2() );
        assertEquals( BloodType.APos.toString(), patient1.getBloodType() );
        assertEquals( "Viipuri", patient1.getCity() );
        assertEquals( "6/15/1977", patient1.getDateOfBirth() );
        assertEquals( "antti@itrust.fi", patient1.getEmail() );
        assertEquals( Ethnicity.Caucasian.toString(), patient1.getEthnicity() );
        assertEquals( "Antti", patient1.getFirstName() );
        assertEquals( Gender.Male.toString(), patient1.getGender() );
        assertEquals( "Walhelm", patient1.getLastName() );
        assertEquals( "123-456-7890", patient1.getPhone() );
        assertEquals( "antti", patient1.getSelf() );
        assertEquals( State.NC.toString(), patient1.getState() );
        assertEquals( "27514", patient1.getZip() );

        /* Put new user into system, fill in OfficeVisitForm fields */
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient1 ) ) );
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
        v = new OphthalmologySurgery( visit );

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
        final BasicHealthMetrics bhm1 = new BasicHealthMetrics( visit );
        final BasicHealthMetrics bhm2 = new BasicHealthMetrics( visit );
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
        bhm2.setPatient( User.getByName( "antti" ) );

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
        final PatientForm patient2 = patient1;
        patient2.setDateOfBirth( "6/15/2040" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient2 ) ) );
        visit.setPatient( patient2.getSelf() );
        v = new OphthalmologySurgery( visit );
        assertNotNull( v );

        /* Create appointment with patient younger than 3 years old */
        final PatientForm patient3 = patient1;
        patient3.setDateOfBirth( "6/15/2046" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient3 ) ) );
        visit.setHeadCircumference( 20.0f );
        visit.setPatient( patient3.getSelf() );
        v = new OphthalmologySurgery( visit );
        assertNotNull( v );

        /*
         * We need the ID of the office visit that actually got _saved_ when
         * calling the API above. This will get it
         */
        final Long id = OfficeVisit.getForPatient( patient.getUsername() ).get( 0 ).getId();

        visit.setId( id.toString() );

        // Second post should fail with a conflict since it already exists
        mvc.perform( post( "/api/v1/ophthalmologysurgeries" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isConflict() );

        mvc.perform( get( "/api/v1/ophthalmologysurgeries/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        visit.setDate( "2048-04-16T09:45:00.000-04:00" ); // 4/16/2048 9:45 AM

        mvc.perform( put( "/api/v1/ophthalmologysurgeries/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // PUT with the non-matching IDs should fail
        mvc.perform( put( "/api/v1/ophthalmologysurgeries/" + 1 ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isConflict() );

        // PUT with ID not in database should fail
        final long tempId = 101;
        visit.setId( "101" );
        mvc.perform( put( "/api/v1/ophthalmologysurgeries/" + tempId ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( visit ) ) ).andExpect( status().isNotFound() );

        // Reset ID to old id
        visit.setId( id.toString() );

        mvc.perform( delete( "/api/v1/ophthalmologysurgeries/" + id ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/officevisits" ) );

    }

}
