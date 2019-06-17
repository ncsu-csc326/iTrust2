
package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
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
import edu.ncsu.csc.itrust2.forms.patient.FoodDiaryEntryForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.MealType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Test for the API functionality for interacting with diary entries
 *
 * @author Brendan Boss (blboss)
 * @author Matt Dzwonczyk (mgdzwonc)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RootConfiguration.class, WebMvcConfiguration.class })
@WebAppConfiguration
public class APIFoodDiaryTest {

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
     * Tests APIFoodDiaryController's addEntry Endpoint with an invalid entry
     */
    @Test
    @WithMockUser( username = "patent", roles = { "PATIENT" } )
    public void testFoodDiaryAPIInvalidEntry() throws Exception {
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
        
        final FoodDiaryEntryForm def = new FoodDiaryEntryForm();
        def.setDate( "2018-09-03" );
        def.setMealType(MealType.Lunch);
        def.setFood("Peanut Butter and Jelly Sandwich");
        def.setServings(-1);
        def.setCalories(900);
        def.setFat(30);
        def.setSodium(60);
        def.setCarbs(100);
        def.setSugars(50);
        def.setFiber(40);
        def.setProtein(10);
        
        mvc.perform( post( "/api/v1/diary" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( def ) ) ).andExpect(status().isBadRequest());
    }
    
    /**
     * Tests APIFoodDiaryController's endpoints as a patient
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testFoodDiaryAPIAsPatient() throws Exception {
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
        
        final FoodDiaryEntryForm def = new FoodDiaryEntryForm();
        def.setDate( "2018-09-03" );
        def.setMealType(MealType.Lunch);
        def.setFood("Peanut Butter and Jelly Sandwich");
        def.setServings(1);
        def.setCalories(900);
        def.setFat(30);
        def.setSodium(60);
        def.setCarbs(100);
        def.setSugars(50);
        def.setFiber(40);
        def.setProtein(10);
        
        try {
            mvc.perform( get( "/api/v1/diary/patient")).andExpect(status().isForbidden());
            fail();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof AccessDeniedException);
        }
        
        mvc.perform( post( "/api/v1/diary" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( def ) ) ).andExpect(status().isOk());
        
        List<LogEntry> entries = LoggerUtil.getAllForUser("patient");
        assertEquals(TransactionType.CREATE_FOOD_DIARY_ENTRY, entries.get(entries.size()-1).getLogCode());
        
        mvc.perform( get( "/api/v1/diary" ) ).andExpect( status().isOk() )
        .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        
        entries = LoggerUtil.getAllForUser("patient");
        assertEquals(TransactionType.PATIENT_VIEW_FOOD_DIARY_ENTRY, entries.get(entries.size()-1).getLogCode());
        
    }

    /**
     * Tests APIFoodDiaryController's endpoints as an HCP.
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testFoodDiaryAPIAsHCP() throws Exception {
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
        
        try {
            mvc.perform( get( "/api/v1/diary" ) ).andExpect( status().isForbidden() );
            fail();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof AccessDeniedException);
        }
        
        mvc.perform( get( "/api/v1/diary/patientdne" ) ).andExpect( status().isNotFound() );
        
        mvc.perform( get( "/api/v1/diary/patient" ) ).andExpect( status().isOk() )
        .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );
        
        List<LogEntry> entries = LoggerUtil.getAllForUser("patient");
        assertEquals(TransactionType.HCP_VIEW_FOOD_DIARY_ENTRY, entries.get(entries.size()-1).getLogCode());
    }

}
