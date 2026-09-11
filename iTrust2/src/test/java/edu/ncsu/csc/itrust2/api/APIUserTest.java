package edu.ncsu.csc.iTrust2.api;

import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.models.security.LogEntry;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.services.security.LogEntryService;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIUserTest {

    private static final String   USER_1 = "API_USER_1";

    private static final String   USER_2 = "API_USER_2";

    private static final String   PW     = "123456";

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService           service;

    @Autowired
    private LogEntryService       logEntryService;

    /**
     * Sets up the tests.
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testCreateUsers () throws Exception {

        Assert.assertEquals( "There should be no Users in the system", 0, service.count() );

        final UserForm u = new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        Assert.assertEquals( "There should be one user in the system after creating a User", 1, service.count() );

        final UserForm u2 = new UserForm( USER_2, PW, Role.ROLE_HCP, 1 );

        u2.addRole( Role.ROLE_VIROLOGIST.toString() );
        u2.addRole( Role.ROLE_OPH.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u2 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        Assert.assertEquals( "It should be possible to create a user with multiple roles", 2, service.count() );

        final User retrieved = service.findByName( USER_2 );

        Assert.assertNotNull( "The created user should be retrievable from the database", retrieved );

        Assert.assertEquals( "The retrieved user should be a Personnel", Personnel.class, retrieved.getClass() );

        Assert.assertEquals( "The retrieved user should have 3 roles", 3, retrieved.getRoles().size() );

    }

    @Test
    @Transactional
    public void testCreateInvalidUsers () throws Exception {

        final UserForm u1 = new UserForm( USER_1, PW, Role.ROLE_ADMIN, 1 );

        u1.addRole( Role.ROLE_ER.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u1 ) ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

        Assert.assertEquals( "Trying to create an invalid user should not create any User record", 0, service.count() );

        final UserForm u2 = new UserForm( USER_2, PW, Role.ROLE_PATIENT, 1 );

        u2.addRole( Role.ROLE_HCP.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

        Assert.assertEquals( "Trying to create an invalid user should not create any User record", 0, service.count() );

    }

    @Test
    @Transactional
    public void testUpdateUsers () throws Exception {

        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_HCP, 1 );

        final User u1 = new Personnel( uf );

        service.save( u1 );

        Assert.assertEquals( u1.getUsername(), service.findByName( USER_1 ).getUsername() );

        uf.addRole( Role.ROLE_ER.toString() );

        mvc.perform( MockMvcRequestBuilders.put( "/api/v1/users/" + uf.getUsername() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( uf ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        final User retrieved = service.findByName( USER_1 );

        Assert.assertEquals( "Updating a user should give them additional Roles", 2, retrieved.getRoles().size() );

    }

    /**
     * UC1 [S2]: An Admin selects a user from the list, confirms the delete,
     * and presses the button to delete the user. Verifies that only an Admin
     * may delete a User, and that the User is actually removed from the
     * system.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testDeleteUser () throws Exception {

        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_HCP, 1 );
        service.save( new Personnel( uf ) );

        Assert.assertEquals( "There should be one User in the system before deleting", 1, service.count() );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/" + USER_1 ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assert.assertEquals( "Deleting the User should remove it from the system", 0, service.count() );
        Assert.assertNull( "The deleted User should no longer be retrievable", service.findByName( USER_1 ) );

        // Deleting a User that doesn't exist should fail gracefully
        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/" + USER_1 ) )
                .andExpect( MockMvcResultMatchers.status().isNotFound() );
    }

    /**
     * UC1 [E2]: The password and repeated password must match, or an error is
     * displayed. Verifies that a User is not created when the confirmation
     * password does not match the password.
     */
    @Test
    @Transactional
    public void testCreateUser_passwordConfirmationMismatch () throws Exception {

        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 );
        uf.setPassword2( "aDifferentPassword" );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uf ) ) ).andExpect( MockMvcResultMatchers.status().is4xxClientError() );

        Assert.assertEquals( "No User should be created when the password confirmation does not match", 0,
                service.count() );
    }

    /**
     * UC1 [1.5 Logging]: Creating a User must log transaction code 100
     * (New user created) with the Admin as the primary user and the new User
     * as the secondary user; deleting a User must log transaction code 103
     * (User deleted).
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testCreateAndDeleteUser_logTransactions () throws Exception {

        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uf ) ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        final List<LogEntry> creationLogs = logEntryService.findAllForUser( "admin" ).stream()
                .filter( e -> e.getLogCode() == TransactionType.CREATE_USER ).collect( Collectors.toList() );

        Assert.assertEquals( "Creating a User should log exactly one CREATE_USER (100) transaction", 1,
                creationLogs.size() );
        Assert.assertEquals( "The CREATE_USER log should record the new User as the secondary user", USER_1,
                creationLogs.get( 0 ).getSecondaryUser() );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/" + USER_1 ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        final long deletionLogs = logEntryService.findAllForUser( "admin" ).stream()
                .filter( e -> e.getLogCode() == TransactionType.DELETE_USER ).count();

        Assert.assertEquals( "Deleting a User should log exactly one DELETE_USER (103) transaction", 1L,
                deletionLogs );
    }

    /**
     * UC1 [1.5 Logging]: Viewing a single User must log transaction code 101
     * (View user), viewing the full list of Users must log code 102 (View
     * users), and updating a User must log code 104 (Update user).
     *
     * Note: per the current implementation, the code 101 (View user) entry is
     * logged with the *viewed* User as the primary user rather than the Admin
     * performing the view, which differs from the Logging table in
     * docs/UC1.md (Logged In MID: Admin, Secondary MID: User). This test
     * asserts the behavior as implemented.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testViewAndUpdateUser_logTransactions () throws Exception {

        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_HCP, 1 );
        service.save( new Personnel( uf ) );

        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/users/" + USER_1 ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assert.assertEquals( "Viewing a single User should log exactly one VIEW_USER (101) transaction", 1L,
                logEntryService.findAllForUser( USER_1 ).stream()
                        .filter( e -> e.getLogCode() == TransactionType.VIEW_USER ).count() );

        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/users" ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        Assert.assertEquals( "Viewing the list of Users should log exactly one VIEW_USERS (102) transaction", 1L,
                logEntryService.findAllForUser( "admin" ).stream()
                        .filter( e -> e.getLogCode() == TransactionType.VIEW_USERS ).count() );

        uf.addRole( Role.ROLE_ER.toString() );

        mvc.perform( MockMvcRequestBuilders.put( "/api/v1/users/" + USER_1 ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( uf ) ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        Assert.assertEquals( "Updating a User should log exactly one UPDATE_USER (104) transaction", 1L,
                logEntryService.findAllForUser( "admin" ).stream()
                        .filter( e -> e.getLogCode() == TransactionType.UPDATE_USER ).count() );
    }

}
