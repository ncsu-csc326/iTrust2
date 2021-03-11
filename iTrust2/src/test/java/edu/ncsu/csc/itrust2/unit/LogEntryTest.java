package edu.ncsu.csc.iTrust2.unit;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.models.security.LogEntry;
import edu.ncsu.csc.iTrust2.services.security.LogEntryService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class LogEntryTest {

    @Autowired
    private LogEntryService     service;

    private static final String USER_1 = "testUser123";

    private static final String USER_2 = "testUser1234";

    @Before
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testCreateLogEntries () {

        ZonedDateTime today = ZonedDateTime.now();

        today = today.truncatedTo( ChronoUnit.DAYS );

        final ZonedDateTime tomorrow = today.plusDays( 1 );

        final List<LogEntry> entries = new ArrayList<LogEntry>();

        entries.add( new LogEntry( TransactionType.LOGIN_SUCCESS, USER_1, null, "User has logged in successfully" ) );

        service.saveAll( entries );

        Assert.assertEquals( "Creating one Log Entry for a user should find one log entry", 1, service.count() );

        Assert.assertEquals( "Creating one Log Entry for a user should find one log entry", 1,
                service.findAllForUser( USER_1 ).size() );

        Assert.assertEquals( "Creating one Log Entry for a user should find one log entry", 1,
                service.findByDateRange( USER_1, today, tomorrow ).size() );

        service.save( new LogEntry( TransactionType.PASSWORD_UPDATE_FAILURE, USER_1, null, "Password update failed" ) );

        Assert.assertEquals( "Creating a second Log Entry for a user should find both entries", 2,
                service.findAllForUser( USER_1 ).size() );

        service.save( new LogEntry( TransactionType.PASSWORD_UPDATE_FAILURE, USER_2, null, "Password update failed" ) );

        Assert.assertEquals( "Creating a Log Entry for a different user should not be found for the first one", 2,
                service.findAllForUser( USER_1 ).size() );

        service.save( new LogEntry( TransactionType.APPOINTMENT_REQUEST_SUBMITTED, USER_1, USER_2,
                "An appointment was requested" ) );

        Assert.assertEquals(
                "Creating a Log Entry with a secondary user should still find the Log Entry when querying by the primary user",
                3, service.findAllForUser( USER_1 ).size() );

    }
}
