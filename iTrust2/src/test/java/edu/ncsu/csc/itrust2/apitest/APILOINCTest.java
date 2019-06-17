package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;
import edu.ncsu.csc.itrust2.models.persistent.LOINC;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Tests functionality of APILOINCController class
 *
 * @author Thomas Dickerson
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APILOINCTest {
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
    public void testCodeAPI () throws Exception {
        LOINC.getAll().stream().filter( e -> ( e.getCode().equals( "46272-8" ) || e.getCode().equals( "55555-5" ) ) )
                .forEach( e -> e.delete() );

        final LOINCForm form = new LOINCForm();
        form.setCode( "46272-8" );
        form.setCommonName( "Probe of Adamantium Skeleton" );
        form.setComponent( "Adamantium" );
        form.setProperty( "NCnc" );

        String content = mvc.perform( post( "/api/v1/loinccodes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andReturn().getResponse().getContentAsString();
        final Gson gson = new GsonBuilder().create();
        LOINCForm code = gson.fromJson( content, LOINCForm.class );
        form.setId( code.getId() ); // fill in the id of the code we just
                                    // created
        assertEquals( form, code );

        content = mvc.perform( get( "/api/v1/loinccode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        code = gson.fromJson( content, LOINCForm.class );
        assertEquals( form, code );

        // edit it
        form.setCode( "55555-5" );
        content = mvc.perform( put( "/api/v1/loinccode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andReturn().getResponse().getContentAsString();
        code = gson.fromJson( content, LOINCForm.class );
        assertEquals( form, code );
        content = mvc.perform( get( "/api/v1/loinccode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        code = gson.fromJson( content, LOINCForm.class );
        assertEquals( form, code );

        // check all codes
        mvc.perform( get( "/api/v1/loinccodes/" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        // then delete it and check that its gone.
        mvc.perform( delete( "/api/v1/loinccode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        assertNull( LOINC.getById( form.getId() ) );
        mvc.perform( get( "/api/v1/loinccode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );
        mvc.perform( put( "/api/v1/loinccode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form ) ) ).andExpect( status().isNotFound() );
        mvc.perform( delete( "/api/v1/loinccode/" + form.getId() ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );
    }
}
