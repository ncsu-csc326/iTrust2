package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
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
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for API functionality for interacting with Patients
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
@FixMethodOrder ( MethodSorters.NAME_ASCENDING )
public class APIPatientTest {

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
     * Tests that getting a patient that doesn't exist returns the proper
     * status.
     *
     * @throws Exception
     */
    @Test
    public void testGetNonExistentPatient () throws Exception {
        mvc.perform( get( "/api/v1/patients/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests PatientAPI
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testPatientAPI () throws Exception {
        // Clear out all patients before running these tests.
        DomainObject.deleteAll( Patient.class );

        final UserForm p = new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );

        // Editing the patient before they exist should fail
        mvc.perform( put( "/api/v1/patients/antti" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isNotFound() );

        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        // Creating the same patient twice should fail.
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().is4xxClientError() );

        mvc.perform( get( "/api/v1/patients" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        mvc.perform( get( "/api/v1/patients/antti" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        patient.setPreferredName( "Antti Walhelm" );

        mvc.perform( put( "/api/v1/patients/antti" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Editing with the wrong username should fail.
        mvc.perform( put( "/api/v1/patients/badusername" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isConflict() );

        mvc.perform( delete( "/api/v1/patients" ) );
    }

    /**
     * Tests PatientAPI for Representatives, as an HCP
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testRepresentativeAPIHCP () throws Exception {
        // Clear out all patients before running these tests.
        DomainObject.deleteAll( Patient.class );

        // Create patient
        final UserForm p = new UserForm( "bobbo", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p ) ) );
        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "bobbo@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Bobbo" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "bobbo" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        // Create patient
        final UserForm r = new UserForm( "terry", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) );
        final PatientForm rep = new PatientForm();
        rep.setAddress1( "1 Test Street" );
        rep.setAddress2( "Some Location" );
        rep.setBloodType( BloodType.APos.toString() );
        rep.setCity( "Viipuri" );
        rep.setDateOfBirth( "1977-06-15" );
        rep.setEmail( "terry@itrust.fi" );
        rep.setEthnicity( Ethnicity.Caucasian.toString() );
        rep.setFirstName( "Terry" );
        rep.setGender( Gender.Male.toString() );
        rep.setLastName( "Walhelm" );
        rep.setPhone( "123-456-7890" );
        rep.setSelf( "terry" );
        rep.setState( State.NC.toString() );
        rep.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( rep ) ) );

        // Create patient
        final UserForm j = new UserForm( "jerry", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( j ) ) );
        final PatientForm jer = new PatientForm();
        jer.setAddress1( "1 Test Street" );
        jer.setAddress2( "Some Location" );
        jer.setBloodType( BloodType.APos.toString() );
        jer.setCity( "Viipuri" );
        jer.setDateOfBirth( "1977-06-15" );
        jer.setEmail( "terry@itrust.fi" );
        jer.setEthnicity( Ethnicity.Caucasian.toString() );
        jer.setFirstName( "Jerry" );
        jer.setGender( Gender.Male.toString() );
        jer.setLastName( "Walhelm" );
        jer.setPhone( "123-456-7890" );
        jer.setSelf( "jerry" );
        jer.setState( State.NC.toString() );
        jer.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( jer ) ) );

        // Create non-patient user
        final UserForm u = new UserForm( "candy", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) );

        // Should return empty, but ok.
        mvc.perform( get( "/api/v1/patient/representatives/bobbo" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        mvc.perform( get( "/api/v1/patient/representing/bobbo" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        mvc.perform( get( "/api/v1/patient/representatives/terry" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        mvc.perform( get( "/api/v1/patient/representing/terry" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Should return not found
        mvc.perform( get( "/api/v1/patient/representatives/nope" ) ).andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representatives/candy" ) ).andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representing/nope" ) ).andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representing/candy" ) ).andExpect( status().is4xxClientError() );

        // Should add terry and jerry as reps of bobbo
        mvc.perform( get( "/api/v1/patient/representatives/bobbo/terry" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        mvc.perform( get( "/api/v1/patient/representatives/bobbo/jerry" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Should add bobbo as rep of terry and jerry
        mvc.perform( get( "/api/v1/patient/representatives/terry/bobbo" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        mvc.perform( get( "/api/v1/patient/representatives/jerry/bobbo" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        final Patient bob = Patient.getByName( "bobbo" );

        assertEquals( bob.getRepresentatives().size(), 2 );
        assertEquals( bob.getRepresented().size(), 2 );

        // Should be fine
        mvc.perform( get( "/api/v1/patient/representatives/bobbo" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        mvc.perform( get( "/api/v1/patient/representing/bobbo" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Should return not found
        mvc.perform( get( "/api/v1/patient/representatives/nope/bobbo" ) ).andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representatives/bobbo/nope" ) ).andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representatives/candy/bobbo" ) ).andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representatives/bobbo/candy" ) ).andExpect( status().is4xxClientError() );

        // Should return various problems
        mvc.perform( get( "/api/v1/patient/representatives/bobbo/bobbo" ) ).andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representatives/bobbo/terry" ) ).andExpect( status().is4xxClientError() );

        // Should return not found
        mvc.perform( get( "/api/v1/patient/representatives/remove/bobbo/nope" ) )
                .andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representatives/remove/nope/bobbo" ) )
                .andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representatives/remove/bobbo/candy" ) )
                .andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representatives/remove/candy/bobbo" ) )
                .andExpect( status().is4xxClientError() );

        // Should remove terry as rep of bobbo
        mvc.perform( get( "/api/v1/patient/representatives/remove/bobbo/terry" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        // Should return not found as terry is removed
        mvc.perform( get( "/api/v1/patient/representatives/remove/bobbo/terry" ) )
                .andExpect( status().is4xxClientError() );
    }

    /**
     * Tests PatientAPI for Representatives, as a Patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "antti", roles = { "PATIENT" } )
    public void testRepresentativeAPIPatient () throws Exception {
        // Clear out all patients before running these tests.
        DomainObject.deleteAll( Patient.class );

        // Create patient
        final UserForm p = new UserForm( "bobbo", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p ) ) );
        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "bobbo@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Bobbo" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "bobbo" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        // Create patient
        final UserForm r = new UserForm( "terry", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) );
        final PatientForm rep = new PatientForm();
        rep.setAddress1( "1 Test Street" );
        rep.setAddress2( "Some Location" );
        rep.setBloodType( BloodType.APos.toString() );
        rep.setCity( "Viipuri" );
        rep.setDateOfBirth( "1977-06-15" );
        rep.setEmail( "terry@itrust.fi" );
        rep.setEthnicity( Ethnicity.Caucasian.toString() );
        rep.setFirstName( "Terry" );
        rep.setGender( Gender.Male.toString() );
        rep.setLastName( "Walhelm" );
        rep.setPhone( "123-456-7890" );
        rep.setSelf( "terry" );
        rep.setState( State.NC.toString() );
        rep.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( rep ) ) );

        // Create antti
        final PatientForm a = new PatientForm();
        a.setAddress1( "1 Test Street" );
        a.setAddress2( "Some Location" );
        a.setBloodType( BloodType.APos.toString() );
        a.setCity( "Viipuri" );
        a.setDateOfBirth( "1977-06-15" );
        a.setEmail( "antti@itrust.fi" );
        a.setEthnicity( Ethnicity.Caucasian.toString() );
        a.setFirstName( "Antti" );
        a.setGender( Gender.Male.toString() );
        a.setLastName( "Walhelm" );
        a.setPhone( "123-456-7890" );
        a.setSelf( "antti" );
        a.setState( State.NC.toString() );
        a.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( a ) ) );

        // Should return illegal.
        mvc.perform( get( "/api/v1/patient/representatives/bobbo" ) ).andExpect( status().is4xxClientError() );
        mvc.perform( get( "/api/v1/patient/representing/bobbo" ) ).andExpect( status().is4xxClientError() );

        // Should return illegal.
        mvc.perform( get( "/api/v1/patient/representatives/bobbo/terry" ) ).andExpect( status().is4xxClientError() );
        // Should return illegal.
        mvc.perform( get( "/api/v1/patient/representatives/remove/bobbo/terry" ) )
                .andExpect( status().is4xxClientError() );

        // Should return ok.
        mvc.perform( get( "/api/v1/patient/representatives/antti" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        mvc.perform( get( "/api/v1/patient/representing/antti" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        mvc.perform( get( "/api/v1/patient/representatives/antti/bobbo" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        mvc.perform( get( "/api/v1/patient/representatives/remove/antti/bobbo" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
    }

    /**
     * Test accessing the patient PUT request unauthenticated
     *
     * @throws Exception
     */
    @Test
    public void testPatientUnauthenticated () throws Exception {
        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );

        mvc.perform( put( "/api/v1/patients/antti" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isUnauthorized() );
    }

    /**
     * Test accessing the patient PUT request as a patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "antti", roles = { "PATIENT" } )
    public void testPatientAsPatient () throws Exception {
        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );

        final Patient antti = new Patient( patient );
        antti.save(); // create the patient if they don't exist already

        // a patient can edit their own info
        mvc.perform( put( "/api/v1/patients/antti" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() );

        // but they can't edit someone else's
        patient.setSelf( "patient" );
        mvc.perform( put( "/api/v1/patients/patient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isUnauthorized() );
    }

}
