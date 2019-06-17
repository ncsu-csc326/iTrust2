package edu.ncsu.csc.itrust2.apitest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.google.gson.Gson;

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
import edu.ncsu.csc.itrust2.controllers.api.comm.LogEntryRequestBody;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;

/**
 * Test for API functionality for interacting with log entries.
 *
 * @author Kai Presler-Marshall
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RootConfiguration.class, WebMvcConfiguration.class })
@WebAppConfiguration
public class APILogEntryTest {

    private MockMvc mvc;
    private final Gson gson = new Gson();
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
     * Test for getLogByDate
     */
    @WithMockUser ( username = "admin", roles = { "USER", "ADMIN" } )
    @Test
    public void testLogByDate () throws Exception {
        final LogEntryRequestBody temp = new LogEntryRequestBody();
        temp.setStartDate( "2017-01-01" );
        temp.setEndDate( "2018-12-31" );
        temp.setPage( 1 );
        temp.setPageLength( 10 );

        mvc.perform( post( "/api/v1/logentries/range" ).content( gson.toJson( temp ) )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() );
    }

    @WithMockUser ( username = "logapitest", roles = { "USER", "ADMIN" } )
    @Test
    public void testLogByDateLarge () throws Exception {

        final User user = new User( "logapitest", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        user.save();

        for ( Integer i = 0; i < 500; ++i ) {
            final LogEntry le = new LogEntry();
            le.setLogCode( TransactionType.USER_BANNED );
            le.setPrimaryUser( "logapitest" );
            le.setTime( ZonedDateTime.now() );

            le.save();
        }

        final LocalDate start = LocalDate.now().minusMonths( 6 );
        final LocalDate end = LocalDate.now().plusMonths( 6 );

        final LogEntryRequestBody temp = new LogEntryRequestBody();
        temp.setStartDate( start.toString() );
        temp.setEndDate( end.toString() );
        temp.setPage( 6 );
        temp.setPageLength( 10 );

        mvc.perform( post( "/api/v1/logentries/range" ).content( gson.toJson( temp ) )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() );
    }

}
