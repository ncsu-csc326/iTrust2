package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.findexperts.ExpertTableEntry;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;

/**
 * Test class for ExpertTableEntry
 *
 * @author nrshah4
 *
 */
public class ExpertTableEntryTest {

    /**
     * Test ExpertTableEntry Constructor
     *
     */
    @Test
    public void expertTableEntryTest () {
        final ExpertTableEntry entry = new ExpertTableEntry();
        assertNotNull( entry );
        assertEquals( null, entry.getHospital() );
        assertEquals( 0, entry.getPersonnel().size() );

    }

    /**
     * Test a nonempty constructor.
     */
    @Test
    public void testNonEmptyConstructor () {
        final Hospital h = new Hospital();
        final Personnel p1 = new Personnel();
        final Personnel p2 = new Personnel();
        final List<Personnel> personnel = new ArrayList<Personnel>();
        personnel.add( p1 );
        personnel.add( p2 );
        final double distance = 3.2;
        final ExpertTableEntry entry = new ExpertTableEntry( h, personnel, distance );
        assertNotNull( entry );
        assertEquals( h, entry.getHospital() );
        assertEquals( personnel, entry.getPersonnel() );
        assertEquals( distance, entry.getDistance(), .001 );
    }

    /**
     * Test setters and getters.
     */
    @Test
    public void testSetters () {
        final Hospital h = new Hospital();
        final Personnel p1 = new Personnel();
        final Personnel p2 = new Personnel();
        final List<Personnel> p = new ArrayList<Personnel>();
        p.add( p1 );
        p.add( p2 );

        final ExpertTableEntry entry = new ExpertTableEntry();
        assertNotNull( entry );
        assertEquals( null, entry.getHospital() );
        entry.setHospital( h );
        assertEquals( h, entry.getHospital() );
        assertEquals( 0, entry.getPersonnel().size() );
        entry.addPersonnel( p1 );
        entry.addPersonnel( p2 );
        assertEquals( 2, entry.getPersonnel().size() );
        entry.setDistance( 2.4 );
        assertEquals( 2.4, entry.getDistance(), .001 );
        entry.setPersonnel( p );
        assertEquals( 2, entry.getPersonnel().size() );

    }

}
