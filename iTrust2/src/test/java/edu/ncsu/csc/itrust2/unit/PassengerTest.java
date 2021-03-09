package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.passenger.PassengerForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.SymptomSeverity;
import edu.ncsu.csc.itrust2.models.persistent.Passenger;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Test Class for Passenger
 *
 * @author nrshah4
 *
 */
public class PassengerTest {

    /** Test Passenger */
    private Passenger                      p;
    /** Test User */
    private User                           passenger;
    /** Test localdatetime */
    private LocalDateTime                  dateTime;

    /**
     * A map of file-paths to the stringified file contents. Used to prevent
     * repeat file reading during testing for Passengers.
     */
    private static final Map<Path, String> fileContentsMap = new HashMap<>();

    /**
     * Fetches the file contents for the given filename. Files will be searched
     * for in the src/test/resources/edu/ncsu/csc/itrust/zipdata/ directory.
     *
     * @param fileName
     *            name of the file to fetch
     * @return the contents of the file, as a string
     */
    public static String getFileContents ( final String fileName ) {
        final Path filePath = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                fileName );
        String fileContents = fileContentsMap.get( filePath );
        if ( fileContents == null ) {
            try {
                fileContents = new String( Files.readAllBytes( filePath ) );
                fileContentsMap.put( filePath, fileContents );
            }
            catch ( final IOException e ) {
                fail( "Failed to open input file for test case" );
            }
        }
        return fileContents;
    }

    /**
     * Set up.
     */
    @Before
    public void setup () {
        passenger = new User();
        passenger.setUsername( "neetyashah" );
        passenger.setRole( Role.ROLE_PATIENT );
        dateTime = LocalDateTime.now();
        passenger.save();

    }

    /**
     * Test constructors.
     */
    @Test
    public void testConstructions () {
        Passenger.deleteAll();
        /* Test invalid scenarios */
        try {
            p = new Passenger( "3647a", "Shah, Neetya", SymptomSeverity.MILD, null, passenger );
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( e.getMessage(), "Must specify initial Symptom Date" );
            assertNull( p );
        }
        assertNull( p );
        try {
            p = new Passenger( "3647a", "Shah, Neetya", SymptomSeverity.NOT_INFECTED, dateTime, passenger );
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( e.getMessage(), "A Not Infected Passenger should not have an Initial Symptom Date" );
            assertNull( p );
        }
        assertNull( p );
        try {
            p = new Passenger( null, "Shah, Neetya", SymptomSeverity.SEVERE, dateTime, passenger );
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( e.getMessage(), "Invalid Id." );
            assertNull( p );
        }
        assertNull( p );
        try {
            p = new Passenger( "3647a", "Shah, Neetya", null, dateTime, passenger );
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( e.getMessage(), "Invalid severity." );
            assertNull( p );
        }
        assertNull( p );

        /* Valid Add */
        try {
            p = new Passenger( "3647a", "Shah, Neetya", SymptomSeverity.NOT_INFECTED, null, passenger );
        }
        catch ( final IllegalArgumentException e ) {
            fail();
        }
        assertNotNull( p );
        assertEquals( p.getPassengerId(), "3647a" );
        assertEquals( p.getSymptomSeverity(), SymptomSeverity.NOT_INFECTED );
        assertEquals( p.getFirstName(), "Neetya" );
        assertEquals( p.getLastName(), "Shah" );
        assertNull( p.getInitialSymptomArrival() );
        p.save();

        assertNotNull( Passenger.getById( "3647a" ) );
        assertEquals( 1, Passenger.getPassengers().size() );

        /* Attempt adding Duplicate */
        try {
            p = new Passenger( "3647a", "Shah, Neetya", SymptomSeverity.NOT_INFECTED, null, passenger );
            p.save();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( e.getMessage(), "Invalid Id." );
        }

    }

    /**
     * Test Passenger.parse()
     */
    @Test
    public void testParse () {
        Passenger.deleteAll();

        /* Test Successful Parse */
        int[] result = Passenger.parse( getFileContents( "passenger-data-short.csv" ) );
        assertEquals( 11, result[0] );
        assertEquals( 0, result[1] );
        assertEquals( 11, Passenger.getPassengers().size() );

        /* Test Duplicate Parse */
        Passenger.deleteAll();
        result = Passenger.parse( getFileContents( "passenger-data-short-duplicates.csv" ) );
        assertEquals( result[0], result[1] );

        /* Test Invalid Strings */
        result = Passenger.parse( " " );
        assertEquals( result[0], 0 );
        assertEquals( result[1], 0 );
        result = Passenger.parse( "error" );
        assertEquals( result[0], 0 );
        assertEquals( result[1], 0 );
        result = Passenger.parse( null );
        assertEquals( result[0], 0 );
        assertEquals( result[1], 0 );
        result = Passenger.parse( "i have commas, but i am invalid" );
        assertEquals( result[0], 0 );
        assertEquals( result[1], 0 );

    }

    /**
     * Test Passenger.parseContacts()
     */
    @Test
    public void testParseContacts () {
        Passenger.deleteAll();

        /* Test Successful Parse */
        Passenger.parse( getFileContents( "abcpassengers.csv" ) );

        /* Test Successful Contact Parse */
        assertTrue( Passenger.parseContacts( getFileContents( "abccontacts.csv" ) ) );
        assertEquals( 3, Passenger.getById( "A" ).getContacts().size() );

        /* Test Unsuccessful Contact Parse */
        assertFalse( Passenger.parseContacts( getFileContents( "passenger-contacts-invalid.csv" ) ) );

        Passenger.deleteAll();

        /* Test Successful Parse */
        Passenger.parse( getFileContents( "passenger-data-short.csv" ) );

        /* Test Successful Contact Parse */
        assertTrue( Passenger.parseContacts( getFileContents( "passenger-contacts-short.csv" ) ) );
        assertEquals( 3, Passenger.getById( "3b9c8a7e" ).getContacts().size() );
    }

    /**
     * Tests n-depth searches through passenger contacts
     */
    @Test
    public void testContactDepthSearch () {
        Passenger.deleteAll();

        // upload data to work with
        Passenger.parse( getFileContents( "passenger-data-short.csv" ) );
        assertTrue( Passenger.parseContacts( getFileContents( "passenger-contacts-short.csv" ) ) );

        final Map<Integer, List<PassengerForm>> map = Passenger.getContactsByDepth( "3b9c8a08", 5 );
        assertEquals( 5, map.size() );
        assertEquals( 3, map.get( 1 ).size() );
        assertEquals( 2, map.get( 2 ).size() );
        assertEquals( 0, map.get( 3 ).size() );
        assertEquals( 0, map.get( 4 ).size() );
        assertEquals( 0, map.get( 5 ).size() );
    }

    /**
     * Delete from database
     */
    @After
    public void teardown () {
        if ( p != null ) {
            p.delete();
        }
        if ( passenger != null ) {
            passenger.delete();
        }

    }

}
