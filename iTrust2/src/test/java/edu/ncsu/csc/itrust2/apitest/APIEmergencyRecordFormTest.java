package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
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
import edu.ncsu.csc.itrust2.forms.personnel.EmergencyRecordForm;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Test for the API functionality for interacting with the EmergencyRecordForm
 *
 * @author Alexander Phelps
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIEmergencyRecordFormTest {

    private MockMvc               mvc;
    private final String          FailureString = "{\"status\":\"failed\",\"message\":\"Could not find a patient entry for onionman2\"}";

    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up test
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    @Before
    public void setup () throws NumberFormatException, ParseException {
        HibernateDataGenerator.refreshDB();
        HibernateDataGenerator.generateTestEHR();
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests the fetching of an Emergency Record for a patient that does not
     * exist.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "USER", "HCP" } )

    public void testGetNonExistentPatientRecord () throws Exception {
        mvc.perform( get( "/api/v1/emergencyrecord/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests the API for EmergencyRecord for HCP
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "USER", "HCP" } )
    public void testEmergencyRecordFormHCPAPI () throws Exception {
        mvc.perform( get( "/api/v1/emergencyrecord/onionman" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        mvc.perform( get( "/api/v1/emergencyrecord/onionman2" ) ).andExpect( status().isNotFound() )
                .andExpect( content().string( FailureString ) );
    }

    /**
     * Tests the API for EmergencyRecord for ER
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "er", roles = { "USER", "ER" } )
    public void testEmergencyRecordFormERAPI () throws Exception {
        mvc.perform( get( "/api/v1/emergencyrecord/onionman" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ) );

        mvc.perform( get( "/api/v1/emergencyrecord/onionman2" ) ).andExpect( status().isNotFound() )
                .andExpect( content().string( FailureString ) );
    }

    /**
     * Tests the functionality of the EmergencyRecordForm object.
     *
     * @throws Exception
     */
    @Test
    public void testEmergencyRecordForm () throws Exception {
        // The EmergencyRecordForm
        final EmergencyRecordForm form = new EmergencyRecordForm( "onionman" );

        // The ACTUAL onionman patient to compare stats to
        final Patient comparison = Patient.getByName( "onionman" );

        // Assert that the getPatient returns the correct patient.
        assertEquals( form.getPatient().getSelf().getId(), comparison.getSelf().getId() );

        // Assert that the getDiagnoses fetch the right Diagnoses
        final List<Diagnosis> recordDiagnoses = form.getDiagnoses();
        final List<Diagnosis> actualDiagnoses = Diagnosis.getForPatient( User.getByName( "onionman" ) );
        for ( int i = 0; i < recordDiagnoses.size(); i++ ) {
            assertEquals( recordDiagnoses.get( i ).getId(), actualDiagnoses.get( i ).getId() );
        }

        // Assert that the getPrescriptions fetch the right Prescriptions
        final List<Prescription> recordPrescriptions = form.getPrescriptions();
        final List<Prescription> actualPrescriptions = Prescription.getForPatient( "onionman" );
        for ( int i = 0; i < recordPrescriptions.size(); i++ ) {
            assertEquals( recordPrescriptions.get( i ).getId(), actualPrescriptions.get( i ).getId() );
        }

        // Test all null statements in EmergencyRecordForm
        final EmergencyRecordForm form2 = new EmergencyRecordForm( "jbean" );
        assertEquals( form2.getFirstName(), "NA" );
        assertEquals( form2.getLastName(), "NA" );
        assertEquals( form2.getDateOfBirth(), "NA" );
        assertEquals( form2.getAge(), "NA" );
        assertEquals( form2.getGender(), "NA" );
        assertEquals( form2.getBloodType(), "NA" );
    }
}
