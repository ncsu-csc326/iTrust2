package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.itrust2.managers.ZipCodeManager;
import edu.ncsu.csc.itrust2.models.persistent.ZipCodeEntry;

/**
 * Test class for ZipCodeManager
 *
 * @author nrshah4
 *
 */
public class ZipCodeManagerTest {

    /**
     * singleton manager
     */
    private ZipCodeManager manager;

    /**
     * file path for sample csv data
     */
    private final String   filepath = "src/test/resources/edu/ncsu/csc/itrust/zipdata/sample.csv";

    /**
     * Set up. Get instance of manager.
     */
    @Before
    public void setUp () {
        manager = ZipCodeManager.getInstance();
    }

    @Test
    public void checkLoadDatabase () {

        assertNotNull( manager );
        manager.clearDatabase();
        assertFalse( manager.checkDatabase() );
        try {
            manager.loadDatabase( filepath );

        }
        catch ( final IOException e ) {
            fail( "IO Exception: Could not load csv file." );
        }

        assertTrue( manager.checkDatabase() );

        final List<ZipCodeEntry> entries = manager.getDatabase();
        assertEquals( 15, entries.size() );
        assertEquals( "27601", entries.get( 0 ).getZip() );
        assertEquals( 35.774451, entries.get( 0 ).getLatitude(), .01 );
        assertEquals( -78.63274, entries.get( 0 ).getLongitude(), .01 );
        assertEquals( "27603", entries.get( 1 ).getZip() );
        assertEquals( 35.716105, entries.get( 1 ).getLatitude(), .01 );
        assertEquals( -78.65734, entries.get( 1 ).getLongitude(), .01 );
        assertEquals( "27604", entries.get( 2 ).getZip() );
        assertEquals( 35.814572, entries.get( 2 ).getLatitude(), .01 );
        assertEquals( -78.58348, entries.get( 2 ).getLongitude(), .01 );
        assertEquals( "27616-1234", entries.get( entries.size() - 1 ).getZip() );

        final ZipCodeEntry entry = ZipCodeEntry.getByZip( "1111" );
        assertEquals( null, entry );

        final ZipCodeEntry entry2 = ZipCodeEntry.getByZip( "27601" );
        assertEquals( "27601", entry2.getId() );

    }

}
