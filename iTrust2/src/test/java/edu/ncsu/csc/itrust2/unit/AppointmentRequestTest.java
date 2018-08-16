package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.patient.AppointmentRequestForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class AppointmentRequestTest {

    @Test
    public void testAR () {

        final AppointmentRequest request = new AppointmentRequest();

        request.setComments( "Please I need help here!" );
        request.setDate( Calendar.getInstance() );
        request.setHcp( User.getByName( "hcp" ) );
        request.setPatient( User.getByName( "patient" ) );
        request.setStatus( Status.PENDING );
        request.setType( AppointmentType.GENERAL_CHECKUP );

        final AppointmentRequestForm form = new AppointmentRequestForm( request );

        assertEquals( request.getHcp().getUsername(), form.getHcp() );

        assertEquals( request.getPatient().getUsername(), form.getPatient() );

        form.setId( "12" );

        assertEquals( String.valueOf( 12 ), form.getId() );

    }

}
