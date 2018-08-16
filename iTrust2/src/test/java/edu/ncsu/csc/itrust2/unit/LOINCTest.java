package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.LOINC;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;

/**
 * Class to test LOINC and LOINCForm objects.
 *
 * @author Thomas Dickerson
 *
 */
public class LOINCTest {

    /**
     * Test that the codes and forms are correctly generated from one another
     */
    @Test
    public void testCodes () {
        final LOINCForm form = new LOINCForm();
        form.setId( 1L );
        form.setCode( "12345-1" );
        form.setCommonName( "Do cool things" );
        form.setProperty( "Some" );
        form.setComponent( "None" );
        final LOINC base = new LOINC();
        base.setId( 1L );
        base.setCode( "12345-1" );
        base.setCommonName( "Do cool things" );
        base.setProperty( "Some" );
        base.setComponent( "None" );

        final LOINC code = new LOINC( form );
        assertEquals( code, base );

        final LOINCForm f2 = new LOINCForm( code );
        assertEquals( form, f2 );

        assertEquals( "12345-1", code.getCode() );
        assertTrue( code.getId().equals( 1L ) );
        assertEquals( "Do cool things", code.getCommonName() );
        assertEquals( "Some", code.getProperty() );
        assertEquals( "None", code.getComponent() );

        final String s = "meme";
        assertNotEquals( form, s );
        f2.setId( 2L );
        assertNotEquals( form, f2 );
        f2.setId( 1L );
        f2.setCode( "54321-8" );
        assertNotEquals( form, f2 );
        f2.setCode( "12345-1" );
        f2.setCommonName( "Nothing" );
        assertNotEquals( form, f2 );
        f2.setCommonName( "Do cool things" );
        f2.setComponent( "Amazing" );
        assertNotEquals( form, f2 );
        f2.setComponent( "None" );
        f2.setProperty( "Fifty" );
        assertNotEquals( form, f2 );
        f2.setProperty( "Some" );

        assertNotEquals( code, s );
        base.setId( 2L );
        assertNotEquals( code, base );
        base.setId( 1L );
        base.setCode( "54321-8" );
        assertNotEquals( code, base );
        base.setCode( "12345-1" );
        base.setCommonName( "Nothing" );
        assertNotEquals( code, base );
        base.setCommonName( "Do cool things" );
        base.setComponent( "Amazing" );
        assertNotEquals( code, base );
        base.setComponent( "None" );
        base.setProperty( "Fifty" );
        assertNotEquals( code, base );
        base.setProperty( "Some" );
    }

    /**
     * Tests that invalid codes will fail and prompt errors.
     */
    @Test
    public void testInvalidCodes () {
        final LOINCForm form = new LOINCForm();
        form.setCode( "111" );
        form.setCommonName( "Something" );
        form.setComponent( "Whatever" );
        form.setProperty( "Who knows?" );

        String tooLong = "";
        for ( int i = 0; i < 51; i++ ) {
            tooLong += "HELLO";
        }

        // Code doesn't fit Regex.
        LOINC code = null;
        try {
            code = new LOINC( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( code );
        }

        // Code is null.
        form.setCode( null );
        code = null;
        try {
            code = new LOINC( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( code );
        }
        form.setCode( "12345-1" );

        // CommonName is null.
        form.setCommonName( null );
        code = null;
        try {
            code = new LOINC( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( code );
        }
        // CommonName is too long.
        form.setCommonName( tooLong );
        code = null;
        try {
            code = new LOINC( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( code );
        }
        form.setCommonName( "Something" );

        // Component is null.
        form.setComponent( null );
        code = null;
        try {
            code = new LOINC( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( code );
        }
        // Component is too long.
        form.setComponent( tooLong );
        code = null;
        try {
            code = new LOINC( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( code );
        }
        form.setComponent( "Whatever" );

        // Property is null.
        form.setProperty( null );
        code = null;
        try {
            code = new LOINC( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( code );
        }
        // Property is too long.
        form.setProperty( tooLong );
        code = null;
        try {
            code = new LOINC( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( code );
        }
        form.setProperty( "Who knows?" );

        code = new LOINC( form );
        assertEquals( code.getCode(), "12345-1" );
        assertEquals( code.getCommonName(), "Something" );
        assertEquals( code.getComponent(), "Whatever" );
        assertEquals( code.getProperty(), "Who knows?" );
    }

    /**
     * Tests that you can get LOINC codes.
     */
    @Test
    public void testGetBy () {

        // To make sure that the LOINCs can be deleted.
        LabProcedure.deleteAll();
        DomainObject.deleteAll( LOINC.class );

        final LOINC l = new LOINC();
        l.setCode( "12345-1" );
        l.setCommonName( "Jump around" );
        l.setComponent( "Jump jump jump" );
        l.setProperty( "JUMP" );
        l.save();

        LOINC c = null;
        try {
            c = LOINC.getById( 20L );
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( c );
        }

        c = LOINC.getById( l.getId() );
        assertEquals( c.getCode(), "12345-1" );
        assertEquals( c.getCommonName(), "Jump around" );
        assertEquals( c.getComponent(), "Jump jump jump" );
        assertEquals( c.getProperty(), "JUMP" );

        final List<LOINC> loincs = LOINC.getAll();
        assertEquals( loincs.size(), 1 );
    }
}
