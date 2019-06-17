package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.patient.AppointmentRequestForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests the AppointmentRequest class.
 * 
 * @author Matt Dzwonczyk
 */
public class AppointmentRequestTest {

    /**
     * Tests patient creating an appointment request for a general checkup
     */
    @Test
    public void testGeneralCheckupAppointmentRequest () {
        final AppointmentRequest request = new AppointmentRequest();

        request.setComments( "Please I need help here!" );
        request.setDate( ZonedDateTime.now() );
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

    /**
     * Tests GENERAL_OPHTHALMOLOGY AppointmentRequests.
     */
    @Test
    public void testGeneralOpthalmologyAppointmentRequest () {
        final AppointmentRequest request = new AppointmentRequest();

        request.setComments( "Somethings stuck in my eye!" );
        request.setDate( ZonedDateTime.now() );
        request.setHcp( User.getByName( "bobbyod" ) );
        request.setPatient( User.getByName( "patient" ) );
        request.setStatus( Status.PENDING );
        request.setType( AppointmentType.GENERAL_OPHTHALMOLOGY );

        final AppointmentRequestForm form = new AppointmentRequestForm( request );
        
        assertEquals( request.getHcp().getUsername(), form.getHcp() );
        assertEquals( request.getPatient().getUsername(), form.getPatient() );

        form.setId( "13" );
        assertEquals( String.valueOf( 13 ), form.getId() );
    }

    /**
     * Tests OPHTHALMOLOGY_SURGERY AppointmentRequests.
     */
    @Test
    public void testOpthalmologyAppointmentRequest () {
        final AppointmentRequest request = new AppointmentRequest();

        request.setComments( "Gimme some of that laser surgery!" );
        request.setDate( ZonedDateTime.now() );
        request.setHcp( User.getByName( "robortoph" ) );
        request.setPatient( User.getByName( "patient" ) );
        request.setStatus( Status.PENDING );
        request.setType( AppointmentType.OPHTHALMOLOGY_SURGERY );

        final AppointmentRequestForm form = new AppointmentRequestForm( request );
        
        assertEquals( request.getHcp().getUsername(), form.getHcp() );
        assertEquals( request.getPatient().getUsername(), form.getPatient() );

        form.setId( "14" );
        assertEquals( String.valueOf( 14 ), form.getId() );
    }

}
