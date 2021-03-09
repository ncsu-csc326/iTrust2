package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.findexperts.ExpertTable;
import edu.ncsu.csc.itrust2.forms.findexperts.ExpertTableEntry;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;

/**
 * Test Class for ExpertTable
 *
 * @author nrshah4
 *
 *
 */
public class ExpertTableTest {

    /** hospital to make test entry */
    private final Hospital  h        = new Hospital();
    /** personnel to make test entry */
    private final Personnel p1       = new Personnel();
    /** personnel to make test entry */
    private final Personnel p2       = new Personnel();
    /** personnel list */
    private List<Personnel> personnel;
    /** distance */
    private double          distance = 0;

    /**
     * Set up.
     */
    @Before
    public void setup () {
        distance = 3.2;
        h.setName( "General Hospital" );
        h.setAddress( "201 NCSU Lane" );
        h.setState( State.NC );
        h.setZip( "27607" );
        p1.setFirstName( "June" );
        p2.setFirstName( "Robort" );
        personnel = new ArrayList<Personnel>();
        personnel.add( p1 );
        personnel.add( p2 );
    }

    /**
     * Test empty Constructor.
     */
    @Test
    public void testEmptyConstructor () {
        final ExpertTable table = new ExpertTable();
        assertNotNull( table );
        assertEquals( 0, table.getEntries().size() );

    }

    /**
     * Test addEntry()
     */
    @Test
    public void testAddEntry () {
        final ExpertTable table = new ExpertTable();
        final ExpertTableEntry entry = new ExpertTableEntry( h, personnel, distance );
        table.addEntry( entry );
        assertEquals( 1, table.getEntries().size() );
        assertEquals( entry, table.getEntries().get( 0 ) );

    }

    /**
     * Test sortTable()
     */
    @Test
    public void testSortEntries () {
        final ExpertTable table = new ExpertTable();
        final ExpertTableEntry entry = new ExpertTableEntry( h, personnel, distance );
        table.addEntry( entry );
        final ExpertTableEntry entry2 = new ExpertTableEntry( h, personnel, 1.2 );
        table.addEntry( entry2 );
        final List<ExpertTableEntry> sorted = table.sortTable();
        assertEquals( entry2, sorted.get( 0 ) );
        assertEquals( entry, sorted.get( 1 ) );

    }

}
