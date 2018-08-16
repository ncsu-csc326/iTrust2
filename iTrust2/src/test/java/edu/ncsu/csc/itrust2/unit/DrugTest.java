package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.DrugForm;
import edu.ncsu.csc.itrust2.models.persistent.Drug;

public class DrugTest {

    @Test
    public void testDrugLookup () {
        assertNull( Drug.getById( 10000000000L ) );
    }

    @Test
    public void testDrugForm () {
        final Drug drug = new Drug();
        drug.setCode( "1234-1234-12" );
        drug.setDescription( "Quality Drugs" );
        drug.setName( "Magnets" );

        final DrugForm form = new DrugForm( drug );

        form.setId( 101L );

        assertEquals( Long.valueOf( 101L ), form.getId() );

    }

}
