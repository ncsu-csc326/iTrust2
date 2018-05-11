package edu.ncsu.csc.itrust2.apitest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.hamcrest.Matchers;
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
import edu.ncsu.csc.itrust2.forms.admin.DrugForm;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Class for testing drug API.
 * 
 * @author Connor
 *
 */
@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class APIDrugTest {
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
     * Tests basic drug API functionality.
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    public void testDrugAPI () throws UnsupportedEncodingException, Exception {
        // Create drugs for testing
        final DrugForm form1 = new DrugForm();
        form1.setCode( "0000-0000-00" );
        form1.setName( "TEST1" );
        form1.setDescription( "DESC1" );

        final DrugForm form2 = new DrugForm();
        form2.setCode( "0000-0000-01" );
        form2.setName( "TEST2" );
        form2.setDescription( "Desc2" );

        // Add drug1 to system
        final String content1 = mvc
                .perform( post( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as Drug object
        final Gson gson = new GsonBuilder().create();
        final Drug drug1 = gson.fromJson( content1, Drug.class );
        assertEquals( form1.getCode(), drug1.getCode() );
        assertEquals( form1.getName(), drug1.getName() );
        assertEquals( form1.getDescription(), drug1.getDescription() );

        // Attempt to add same drug twice
        mvc.perform( post( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form1 ) ) ).andExpect( status().isConflict() );

        // Add drug2 to system
        final String content2 = mvc
                .perform( post( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Drug drug2 = gson.fromJson( content2, Drug.class );
        assertEquals( form2.getCode(), drug2.getCode() );
        assertEquals( form2.getName(), drug2.getName() );
        assertEquals( form2.getDescription(), drug2.getDescription() );

        // Verify drugs have been added
        mvc.perform( get( "/api/v1/drugs" ) ).andExpect( status().isOk() )
                .andExpect( content().string( Matchers.containsString( form1.getCode() ) ) )
                .andExpect( content().string( Matchers.containsString( form2.getCode() ) ) );

        // Edit first drug's description
        drug1.setDescription( "This is a better description" );
        final String editContent = mvc
                .perform( put( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( drug1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final Drug editedDrug = gson.fromJson( editContent, Drug.class );
        assertEquals( drug1.getId(), editedDrug.getId() );
        assertEquals( drug1.getCode(), editedDrug.getCode() );
        assertEquals( drug1.getName(), editedDrug.getName() );
        assertEquals( "This is a better description", editedDrug.getDescription() );

        // Attempt invalid edit
        drug2.setCode( "0000-0000-00" );
        mvc.perform( put( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drug2 ) ) ).andExpect( status().isConflict() );

        // Follow up with valid edit
        drug2.setCode( "0000-0000-03" );
        mvc.perform( put( "/api/v1/drugs" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drug2 ) ) ).andExpect( status().isOk() );

        // Delete test drugs
        mvc.perform( delete( "/api/v1/drugs/" + drug1.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( drug1.getId().toString() ) );
        mvc.perform( delete( "/api/v1/drugs/" + drug2.getId() ) ).andExpect( status().isOk() )
                .andExpect( content().string( drug2.getId().toString() ) );
    }

}
