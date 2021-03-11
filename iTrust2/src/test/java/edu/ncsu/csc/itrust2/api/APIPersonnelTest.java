package edu.ncsu.csc.iTrust2.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.PersonnelForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.services.PersonnelService;

/**
 * Test for API functionality for interacting with Personnel
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIPersonnelTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PersonnelService      service;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    /**
     * Tests getting a non existent personnel and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetNonExistentPersonnel () throws Exception {
        mvc.perform( get( "/api/v1/personnel/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests PersonnelAPI
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testPersonnelAPI () throws Exception {

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        service.save( hcp );

        final PersonnelForm personnel = new PersonnelForm();

        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prag" );
        personnel.setEmail( "hcp@itrust.cz" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setUsername( "hcp" );
        personnel.setState( State.NC.toString() );
        personnel.setZip( "27514" );

        // Should be able to update with the new values
        mvc.perform( put( "/api/v1/personnel/hcp" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) ).andExpect( status().isOk() );

        final Personnel retrieved = (Personnel) service.findByName( "hcp" );

        Assert.assertEquals( "Prag", retrieved.getCity() );
        Assert.assertEquals( State.NC, retrieved.getState() );

        mvc.perform( get( "/api/v1/personnel" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        mvc.perform( get( "/api/v1/personnel/hcp" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        // Edit with wrong url ID should fail
        mvc.perform( put( "/api/v1/personnel/badhcp" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) ).andExpect( status().isNotFound() );

        // Edit with matching, but nonexistent ID should fail.
        personnel.setUsername( "badhcp" );
        mvc.perform( put( "/api/v1/personnel/badhcp" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) ).andExpect( status().is4xxClientError() );

    }

    /**
     * Tests getting personnel by their roles.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "ADMIN" } )
    public void testGetByRole () throws Exception {
        // Valid get requests
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_LABTECH" ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_ER" ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_HCP" ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_OD" ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_OPH" ) ).andExpect( status().isOk() );

        // Invalid get request
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_SCHMOO" ) ).andExpect( status().is4xxClientError() );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testRoleFiltering () throws Exception {

        final User hcp = new Personnel( new UserForm( "hcp_test1", "123456", Role.ROLE_HCP, 1 ) );

        final User hcp2 = new Personnel( new UserForm( "hcp_test2", "123456", Role.ROLE_HCP, 1 ) );
        hcp2.addRole( Role.ROLE_ER );

        final User admin = new Personnel( new UserForm( "admin_test", "123456", Role.ROLE_ADMIN, 1 ) );

        service.saveAll( List.of( hcp, hcp2, admin ) );

        final MvcResult result = mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_HCP" ) )
                .andExpect( status().isOk() ).andReturn();

        final String content = result.getResponse().getContentAsString();

        Assert.assertTrue( content.contains( "hcp_test1" ) );
        Assert.assertTrue( content.contains( "hcp_test2" ) );
        Assert.assertFalse( content.contains( "admin_test" ) );

    }

}
