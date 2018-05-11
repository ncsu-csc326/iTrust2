package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.io.UnsupportedEncodingException;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIDiagnosisTest {
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

    @Test
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testDiagnoses () throws UnsupportedEncodingException, Exception {
        /*
         * Create a HCP, admin and a Patient to use. If they already exist, this
         * will do nothing
         */
        final UserForm hcp = new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcp ) ) );

        final UserForm admin = new UserForm( "admin", "123456", Role.ROLE_ADMIN, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( admin ) ) );

        final UserForm patient = new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) );

        mvc.perform( formLogin( "/login" ).user( "admin" ).password( "pass" ) );

        /* Create a Hospital to use too */
        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
        hospital.save();

        // create 2 ICDCode to use
        ICDCodeForm codeForm = new ICDCodeForm();
        codeForm.setCode( "T10" );
        codeForm.setDescription( "Test 10" );
        String content = mvc
                .perform( post( "/api/v1/icdcodes" ).with( user( "admin" ).password( "123456" ) )
                        .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( codeForm ) ) )
                .andReturn().getResponse().getContentAsString();
        final Gson gson = new GsonBuilder().create();
        final ICDCodeForm codeF2 = gson.fromJson( content, ICDCodeForm.class );
        final ICDCode code = new ICDCode( codeF2 );

        // create an ICDCode to use
        codeForm = new ICDCodeForm();
        codeForm.setCode( "T10" );
        codeForm.setDescription( "Test 10" );
        content = mvc.perform( post( "/api/v1/icdcodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( codeForm ) ) ).andReturn().getResponse().getContentAsString();
        final ICDCodeForm code2F2 = gson.fromJson( content, ICDCodeForm.class );
        final ICDCode code2 = new ICDCode( code2F2 );

        mvc.perform( delete( "/api/v1/officevisits" ) );

        // Create an office visit with two diagnoses
        final OfficeVisitForm form = new OfficeVisitForm();
        form.setDate( "4/16/2048" );
        form.setTime( "9:50 AM" );
        form.setHcp( "hcp" );
        form.setPatient( "patient" );
        form.setNotes( "Test office visit" );
        form.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        form.setHospital( "iTrust Test Hospital 2" );
        form.setHdl( 1 );
        form.setHeight( 1f );
        form.setWeight( 1f );
        form.setLdl( 1 );
        form.setTri( 100 );
        form.setDiastolic( 1 );
        form.setSystolic( 1 );
        form.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        form.setPatientSmokingStatus( PatientSmokingStatus.FORMER );

        final List<Diagnosis> list = new ArrayList<Diagnosis>();
        final Diagnosis d = new Diagnosis();
        d.setCode( code );
        d.setNote( "Test diagnosis" );
        list.add( d );
        form.setDiagnoses( list );
        final Diagnosis d2 = new Diagnosis();
        d2.setCode( code2 );
        d2.setNote( "Second Diagnosis" );
        list.add( d2 );

        content = mvc.perform( post( "/api/v1/officevisits" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andReturn().getResponse().getContentAsString();
        OfficeVisit visit = null;

        try {
            visit = gson.fromJson( content, OfficeVisit.class );
        }
        catch ( final Exception e ) {
            fail( "Received response : " + content );
        }
        // get the list of diagnoses for this office visit and make sure both
        // are there
        content = mvc.perform( get( "/api/v1/diagnosesforvisit/" + visit.getId() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( codeForm ) ) ).andReturn()
                .getResponse().getContentAsString();
        List<Diagnosis> dlist = gson.fromJson( content, new TypeToken<ArrayList<Diagnosis>>() {
        }.getType() );
        boolean flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d.getCode() ) && dd.getNote().equals( d.getNote() )
                    && dd.getVisit().getId().equals( visit.getId() ) ) {
                flag = true;
                d.setId( dd.getId() );

            }
        }
        assertTrue( flag );
        flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d2.getCode() ) && dd.getNote().equals( d2.getNote() )
                    && dd.getVisit().getId().equals( visit.getId() ) ) {
                flag = true;
                d2.setId( dd.getId() );
            }
        }
        assertTrue( flag );

        // get the list of diagnoses for this patient and make sure both are
        // there
        final List<Diagnosis> forPatient = Diagnosis.getForPatient( User.getByName( "patient" ) );
        flag = false;
        for ( final Diagnosis dd : forPatient ) {
            if ( dd.getCode().equals( d.getCode() ) && dd.getNote().equals( d.getNote() )
                    && dd.getVisit().getId().equals( visit.getId() ) ) {
                flag = true;
            }
        }
        assertTrue( flag );
        flag = false;
        for ( final Diagnosis dd : forPatient ) {
            if ( dd.getCode().equals( d2.getCode() ) && dd.getNote().equals( d2.getNote() )
                    && dd.getVisit().getId().equals( visit.getId() ) ) {
                flag = true;
            }
        }
        assertTrue( flag );

        // edit a diagnosis within the editing of office visit and check they
        // work.
        form.setId( visit.getId() + "" );
        d.setNote( "Edited" );
        content = mvc.perform( put( "/api/v1/officevisits/" + visit.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andReturn().getResponse().getContentAsString();

        content = mvc.perform( get( "/api/v1/diagnosesforvisit/" + visit.getId() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( codeForm ) ) ).andReturn()
                .getResponse().getContentAsString();
        dlist = gson.fromJson( content, new TypeToken<ArrayList<Diagnosis>>() {
        }.getType() );
        flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d.getCode() ) && dd.getNote().equals( d.getNote() )
                    && dd.getVisit().getId().equals( visit.getId() ) ) {
                flag = true;
            }
        }
        assertTrue( flag );
        flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d2.getCode() ) && dd.getNote().equals( d2.getNote() )
                    && dd.getVisit().getId().equals( visit.getId() ) ) {
                flag = true;
            }
        }
        assertTrue( flag );

        // edit the office visit and remove a diagnosis

        list.remove( d );
        content = mvc.perform( put( "/api/v1/officevisits/" + visit.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andReturn().getResponse().getContentAsString();

        // check that the removed one is gone
        content = mvc.perform( get( "/api/v1/diagnosesforvisit/" + visit.getId() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( codeForm ) ) ).andReturn()
                .getResponse().getContentAsString();
        dlist = gson.fromJson( content, new TypeToken<ArrayList<Diagnosis>>() {
        }.getType() );
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d.getCode() ) && dd.getNote().equals( d.getNote() )
                    && dd.getVisit().getId().equals( visit.getId() ) && dd.getId().equals( d.getId() ) ) {
                fail( "Was not deleted!" );
            }
        }
        flag = false;
        for ( final Diagnosis dd : dlist ) {
            if ( dd.getCode().equals( d2.getCode() ) && dd.getNote().equals( d2.getNote() )
                    && dd.getVisit().getId().equals( visit.getId() ) ) {
                flag = true;
            }
        }
        assertTrue( flag );
        // delete all resources involved with this test
        mvc.perform( delete( "/api/v1/officevisits/" + visit.getId() ) );

    }

}
