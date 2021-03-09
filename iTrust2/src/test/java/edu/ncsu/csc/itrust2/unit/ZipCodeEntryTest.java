package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.persistent.ZipCodeEntry;

/**
 * Zip Code Entry Test Class
 *
 * @author nrshah4
 *
 */
public class ZipCodeEntryTest {

    /**
     * Test Constructor.
     */
    @Test
    public void testConstructor () {
        final ZipCodeEntry zip1 = new ZipCodeEntry();
        assertNotNull( zip1 );
        assertEquals( null, zip1.getZip() );
        assertEquals( 0, zip1.getLatitude(), .00001 );
        assertEquals( 0, zip1.getLongitude(), .00001 );
    }

    /**
     * Test setters and getters.
     */
    @Test
    public void testSettersGetters () {
        final ZipCodeEntry zip1 = new ZipCodeEntry();
        assertNotNull( zip1 );
        zip1.setZip( "256707" );
        assertEquals( "256707", zip1.getZip() );
        zip1.setLatitude( 2.2 );
        assertEquals( 2.2, zip1.getLatitude(), .001 );
        zip1.setLongitude( 13 );
        assertEquals( 13, zip1.getLongitude(), .001 );

    }

    /**
     * Test Calculate Distance method.
     */
    @Test
    public void testCalculateDistance () {
        final ZipCodeEntry zip1 = new ZipCodeEntry();
        final ZipCodeEntry zip2 = new ZipCodeEntry();
        zip1.setLatitude( 10 );
        zip1.setLongitude( 15 );
        zip2.setLatitude( 10 );
        zip2.setLongitude( 15 );
        double dist = ZipCodeEntry.calculateDistance( zip1, zip2 );
        assertEquals( 0, dist, .001 );

        zip1.setLatitude( 10 );
        zip1.setLongitude( 15 );
        zip2.setLatitude( 10 );
        zip2.setLongitude( 16 );
        dist = ZipCodeEntry.calculateDistance( zip1, zip2 );
        assertEquals( 68, dist, .10 );

    }

}
