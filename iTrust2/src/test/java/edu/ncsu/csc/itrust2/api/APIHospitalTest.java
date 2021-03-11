package edu.ncsu.csc.iTrust2.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.services.HospitalService;

/**
 * Test for API functionality for interacting with Hospitals
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIHospitalTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private HospitalService       service;

    /**
     * Test set up
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        service.deleteAll();
    }

    /**
     * Tests getting a non existent hospital and ensures that the correct status
     * is returned.
     *
     * @throws Exception
     */
    @Test
    public void testGetNonExistentHospital () throws Exception {
        mvc.perform( get( "/api/v1/hospitals/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests HospitalAPI
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testHospitalAPI () throws Exception {
        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );
        mvc.perform( post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) );
        mvc.perform( get( "/api/v1/hospitals/iTrust Test Hospital 2" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        // Cannot create same hospital twice
        mvc.perform( post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) ).andExpect( status().isConflict() );

        // Make sure our hospital was created as expected
        Hospital retrieved = service.findByName( "iTrust Test Hospital 2" );

        Assert.assertEquals( "1 iTrust Test Street", retrieved.getAddress() );

        hospital.setAddress( "2 iTrust Test Street" );
        mvc.perform( put( "/api/v1/hospitals/iTrust Test Hospital 2" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        // Make sure that the put didn't break anything
        mvc.perform( get( "/api/v1/hospitals/iTrust Test Hospital 2" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        // And that it's updated properly
        retrieved = service.findByName( "iTrust Test Hospital 2" );

        Assert.assertEquals( "2 iTrust Test Street", retrieved.getAddress() );

        // Editing a non-existent hospital should not work
        mvc.perform( put( "/api/v1/hospitals/iTrust Test Hospital 3" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hospital ) ) ).andExpect( status().isNotFound() );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testPermissions () throws Exception {
        Assert.assertEquals( 0, service.count() );

        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "1 iTrust Test Street", "27607", "NC" );

        // No user, shouldn't be able to create anything
        try {
            mvc.perform( post( "/api/v1/hospitals" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( hospital ) ) );
        }
        catch ( final Exception e ) {
            // may throw Unauthorised Exception, carry on
        }

        Assert.assertEquals( 0, service.count() );

        service.save( hospital );

        // Or update
        try {
            mvc.perform( put( "/api/v1/hospitals/iTrust Test Hospital 2" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( hospital ) ) ).andExpect( status().isForbidden() );
        }
        catch ( final Exception e ) {
            // likewise, exception is OK too
        }
    }

}
