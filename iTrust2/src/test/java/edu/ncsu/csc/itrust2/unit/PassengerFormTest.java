package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.passenger.PassengerForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.SymptomSeverity;
import edu.ncsu.csc.itrust2.models.persistent.Passenger;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests the PassengerForm class
 *
 * @author caproven
 *
 */
public class PassengerFormTest {

    /**
     * Tests that fields are correctly set by the constructor and can be
     * retrieved
     */
    @Test
    public void testPassengerForm () {
        final User user = new User();
        user.setUsername( "neetyashah" );
        user.setRole( Role.ROLE_PATIENT );
        final LocalDateTime dateTime = LocalDateTime.now();
        user.save();
        final Passenger p = new Passenger( "neetyapassengerid", "Shah Neetya", SymptomSeverity.CRITICAL, dateTime,
                user );

        // test construction (and implicitly the setters)
        final PassengerForm form = new PassengerForm( p );
        assertNotNull( form );

        // test getters
        assertEquals( "neetyapassengerid", form.getPassengerId() );
        assertEquals( "Neetya Shah", form.getName() );
        assertEquals( SymptomSeverity.CRITICAL, form.getSymptomSeverity() );
        assertEquals( dateTime, form.getInitialSymptomDate() );
    }

}
