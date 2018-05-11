package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.admin.HospitalForm;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;

/**
 * Tests for the HospitalForm class
 *
 * @author jshore
 *
 */
public class HospitalFormTest {

    /**
     * Test the HospitalForm class.
     */
    @Test
    public void testHospitalForm () {
        final Hospital hospital = new Hospital();
        hospital.setAddress( "somewhere" );
        hospital.setName( "hospital" );
        hospital.setState( State.NC );
        hospital.setZip( "27040" );
        final HospitalForm form = new HospitalForm( hospital );
        assertEquals( hospital.getAddress(), form.getAddress() );
        assertEquals( hospital.getName(), form.getName() );
        assertEquals( hospital.getState().getName(), form.getState() );
        assertEquals( hospital.getZip(), form.getZip() );
    }
}
