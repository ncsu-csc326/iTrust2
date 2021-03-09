/**
 *
 */
package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
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
import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.forms.patient.BloodSugarDiaryForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.BloodSugarDiaryEntry;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Test class for the blood sugar diary api controller
 *
 * @author Thomas Landsberg
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIBloodSugarDiaryControllerTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        DomainObject.deleteAll( BloodSugarDiaryEntry.class );
        final Patient p = Patient.getByName( "patient" );
        if ( p != null ) {
            p.delete();
        }
    }

    /**
     * Tests APIFoodDiaryController's addEntry Endpoint with an invalid entry
     */
    @Test
    @WithMockUser ( username = "patent", roles = { "PATIENT" } )
    public void testBloodSugarDiaryAPIInvalidEntry () throws Exception {
        final UserForm uForm = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uForm ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "patient@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Patient" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "patient" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        final BloodSugarDiaryForm def = new BloodSugarDiaryForm();
        def.setDate( "2018-09-03" );
        def.setFastingLevel( -1 );
        def.setFirstLevel( 30 );
        def.setSecondLevel( 50 );
        def.setThirdLevel( 40 );

        mvc.perform( post( "/api/v1//bloodSugarDiaries/patient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( def ) ) ).andExpect( status().isBadRequest() );
    }

    /**
     * Tests APIBloodSugarDiaryController's endpoints as a patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testBloodSugarDiaryAPIAsPatient () throws Exception {
        final UserForm uForm = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uForm ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "patient@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Patient" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "patient" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        final BloodSugarDiaryForm def = new BloodSugarDiaryForm();
        def.setDate( "2018-09-03" );
        def.setFastingLevel( 30 );
        def.setFirstLevel( 20 );
        def.setSecondLevel( 30 );
        def.setThirdLevel( 50 );

        mvc.perform( post( "/api/v1/bloodSugarDiaries/patient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( def ) ) ).andExpect( status().isOk() );

        List<LogEntry> entries = LoggerUtil.getAllForUser( "patient" );
        assertEquals( TransactionType.CREATE_BLOOD_SUGAR_DIARY, entries.get( entries.size() - 1 ).getLogCode() );

        def.setFastingLevel( 10 );
        mvc.perform( post( "/api/v1/bloodSugarDiaries/patient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( def ) ) ).andExpect( status().isOk() );

        entries = LoggerUtil.getAllForUser( "patient" );
        assertEquals( TransactionType.EDIT_BLOOD_SUGAR_DIARY, entries.get( entries.size() - 1 ).getLogCode() );

        mvc.perform( get( "/api/v1/bloodSugarDiaries/patient" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        entries = LoggerUtil.getAllForUser( "patient" );
        assertEquals( TransactionType.VIEW_BLOOD_SUGAR_DIARY_PATIENT, entries.get( entries.size() - 1 ).getLogCode() );

        mvc.perform( get( "/api/v1/bloodSugarDiaries/Patient" ) ).andExpect( status().isNotFound() );

        final BloodSugarDiaryForm def1 = new BloodSugarDiaryForm();
        def1.setDate( LocalDate.now().toString() );
        def1.setFastingLevel( 30 );
        def1.setFirstLevel( 20 );
        def1.setSecondLevel( 30 );
        def1.setThirdLevel( 50 );

        mvc.perform( post( "/api/v1/bloodSugarDiaries/patient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( def1 ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/bloodSugarDiaries/patient" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

    }

    /**
     * Tests APIBloodSugarDiaryController's endpoints as a patient
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testBloodSugarDiaryAPIAsPatientCSV () throws Exception {
        final UserForm uForm = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uForm ) ) );

        final PatientForm patientForm = new PatientForm();
        patientForm.setAddress1( "1 Test Street" );
        patientForm.setAddress2( "Some Location" );
        patientForm.setBloodType( BloodType.APos.toString() );
        patientForm.setCity( "Viipuri" );
        patientForm.setDateOfBirth( "1977-06-15" );
        patientForm.setEmail( "patient@itrust.fi" );
        patientForm.setEthnicity( Ethnicity.Caucasian.toString() );
        patientForm.setFirstName( "Patient" );
        patientForm.setGender( Gender.Male.toString() );
        patientForm.setLastName( "Walhelm" );
        patientForm.setPhone( "123-456-7890" );
        patientForm.setSelf( "patient" );
        patientForm.setState( State.NC.toString() );
        patientForm.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patientForm ) ) );
        final Patient patient = Patient.getByName( "patient" );

        String testx = "";
        testx += "date, fasting, meal_1, meal_2, meal_3";
        testx += "\n" + "2018-09-03, 10, 20, 30, 40";
        mvc.perform( post( "/api/v1//bloodSugarDiaries/patient/csv" ).contentType( MediaType.APPLICATION_JSON ) // simple
                // header
                // and
                // one
                // line
                // test
                .content( testx ) ).andExpect( status().isOk() );
        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 1 );

        final String test1 = "date, fasting, meal_1, meal_2, meal_3\n2018-09-09, 11, 12, 13, 14\n 2018-09-12,13,14,15,16"; // two
                                                                                                                           // lines
                                                                                                                           // and
                                                                                                                           // differing
                                                                                                                           // spaces/format
        mvc.perform( post( "/api/v1//bloodSugarDiaries/patient/csv" ).contentType( MediaType.APPLICATION_JSON )
                .content( test1 ) ).andExpect( status().isOk() );
        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 3 );

        final String test2 = "d8, fasting, meal_x, meal_y, meal_z\n2018-09-04, 11, 12, 13, 14"; // Try
                                                                                                // an
                                                                                                // invalid
                                                                                                // header
        mvc.perform( post( "/api/v1//bloodSugarDiaries/patient/csv" ).contentType( MediaType.APPLICATION_JSON )
                .content( test2 ) ).andExpect( status().isBadRequest() );
        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 3 );

        final String test3 = "date, fasting, meal_1, meal_2, meal_3\n2018-09-05, 11, x, 13, 14\n 2018-09-20,13,14,15,z"; // Try
                                                                                                                         // invalid
                                                                                                                         // fields
        mvc.perform( post( "/api/v1//bloodSugarDiaries/patient/csv" ).contentType( MediaType.APPLICATION_JSON )
                .content( test3 ) ).andExpect( status().isBadRequest() );
        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 3 );

        final String test4 = "date, fasting, meal_1, meal_2, meal_3\n2018-09-21, 11, 12, 13, 14\n 2018-0X-22,13,14,15,16"; // Try
                                                                                                                           // invalid
                                                                                                                           // date
        mvc.perform( post( "/api/v1//bloodSugarDiaries/patient/csv" ).contentType( MediaType.APPLICATION_JSON )
                .content( test4 ) ).andExpect( status().isBadRequest() );
        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 3 );

        final String test5 = "date, fasting, meal_1, meal_2, meal_3\n2018-09-04, 11, 12, 14\n 2018-09-05,13,14,15,16"; // One
                                                                                                                       // of
                                                                                                                       // the
                                                                                                                       // entries
                                                                                                                       // is
                                                                                                                       // missing
                                                                                                                       // a
                                                                                                                       // field
        mvc.perform( post( "/api/v1//bloodSugarDiaries/patient/csv" ).contentType( MediaType.APPLICATION_JSON )
                .content( test5 ) ).andExpect( status().isBadRequest() );
        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 3 );

        final String test6 = "date, fasting, meal_1, meal_2, meal_3\n2018-09-25, 11, 12,, 14\n 2018-09-26,13, ,15,16"; // Valid
                                                                                                                       // entry
                                                                                                                       // with
                                                                                                                       // some
                                                                                                                       // fields
                                                                                                                       // not
                                                                                                                       // specified
        mvc.perform( post( "/api/v1//bloodSugarDiaries/patient/csv" ).contentType( MediaType.APPLICATION_JSON )
                .content( test6 ) ).andExpect( status().isOk() );
        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 5 );

        final String test7 = "date, fasting, meal_1, meal_2, meal_3\n2018-09-25, 11, 14,28, 14\n 2018-09-26,13, ,15,20"; // Now
                                                                                                                         // lets
                                                                                                                         // use
                                                                                                                         // a
                                                                                                                         // date
                                                                                                                         // that
                                                                                                                         // has
                                                                                                                         // been
                                                                                                                         // entered
                                                                                                                         // and
                                                                                                                         // try
                                                                                                                         // to
                                                                                                                         // edit
                                                                                                                         // the
                                                                                                                         // entry.
        mvc.perform( post( "/api/v1//bloodSugarDiaries/patient/csv" ).contentType( MediaType.APPLICATION_JSON )
                .content( test7 ) ).andExpect( status().isOk() );
        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 5 );

    }

    /**
     * Tests APIFoodDiaryController's endpoints as an HCP.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testBloodSugarDiaryAPIAsHCP () throws Exception {
        final UserForm uForm = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uForm ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "patient@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Patient" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "patient" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        mvc.perform( get( "/api/v1/bloodSugarDiaries/hcp/patientdne" ) ).andExpect( status().isNotFound() );

        mvc.perform( get( "/api/v1/bloodSugarDiaries/hcp/patient" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        final List<LogEntry> entries = LoggerUtil.getAllForUser( "patient" );
        assertEquals( TransactionType.VIEW_BLOOD_SUGAR_DIARY_HCP, entries.get( entries.size() - 1 ).getLogCode() );
    }

    /**
     * Tests viewing blood sugar limits as a patient user
     *
     * @throws Exception
     *             for incorrect calls
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testGetPatientBloodSugarLimits () throws Exception {
        final UserForm uForm = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uForm ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "patient@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Patient" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "patient" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        patient.setDiabetic( true );
        patient.setFastingLimit( 120 );
        patient.setMealLimit( 120 );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        mvc.perform( get( "/api/v1/bloodSugarDiaries/patient/limits" ) ).andExpect( status().isOk() );
    }

    /**
     * Tests the get method in the api for getting the limits as an hcp
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetPatientBloodSugarLimitsAsHCP () throws Exception {
        final UserForm uForm = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uForm ) ) );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "patient@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Patient" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setSelf( "patient" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        patient.setDiabetic( true );
        patient.setFastingLimit( 120 );
        patient.setMealLimit( 120 );
        mvc.perform( post( "/api/v1/patients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        final List<Integer> list = new ArrayList<Integer>();
        list.add( 125 );
        list.add( 120 );

        mvc.perform( get( "/api/v1/bloodSugarDiaries/hcp/limits/patient" ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/bloodSugarDiaries/hcp/limits/patient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( list ) ) ).andExpect( status().isOk() ); // Valid
                                                                                           // call

        mvc.perform( post( "/api/v1/bloodSugarDiaries/hcp/limits/patientdne" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( list ) ) ).andExpect( status().isBadRequest() ); // Invalid
                                                                                                   // call
    }

}
