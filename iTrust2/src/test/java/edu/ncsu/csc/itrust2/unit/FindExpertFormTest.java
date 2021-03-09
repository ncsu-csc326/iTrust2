package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.findexperts.FindExpertForm;

/**
 * Test class for Find Expert Form
 *
 * @author nrshah4
 *
 */
public class FindExpertFormTest {

    /**
     * Test empty constructor.
     */
    @Test
    public void testEmptyForm () {
        final FindExpertForm form = new FindExpertForm();
        assertNotNull( form );
        assertEquals( null, form.getSpecialty() );
        assertEquals( null, form.getZip() );
    }

    /**
     * Test non-empty constructor.
     */
    @Test
    public void testFormConstructor () {
        final String zip = "27519";
        final String specialty = "Cardiologist";
        final int radius = 4;
        final FindExpertForm form = new FindExpertForm( specialty, zip, radius );
        assertNotNull( form );
        assertEquals( zip, form.getZip() );
        assertEquals( specialty, form.getSpecialty() );
        assertEquals( radius, form.getRadius() );

    }

    /**
     * Test getters and setters.
     */
    @Test
    public void testSetters () {
        final FindExpertForm form = new FindExpertForm();
        form.setZip( "12345" );
        assertEquals( "12345", form.getZip() );
        form.setSpecialty( "Cardiologist" );
        assertEquals( "Cardiologist", form.getSpecialty() );
        form.setRadius( 5 );
        assertEquals( 5, form.getRadius() );
    }

}
