package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.ZonedDateTime;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class DomainObjectTest {

    @Test
    public void testCreateDomainObject () {
        final Hospital h = new Hospital();
        h.setAddress( "2770 Wolf Village Drive, Raleigh" );
        h.setState( State.NC );
        h.setZip( "27607" );
        h.setName( "iTrust Test Hospital" );
        h.save();

        final Hospital retrieve = Hospital.getByName( "iTrust Test Hospital" );
        assertNotNull( retrieve );
        assertEquals( retrieve.getState(), State.NC );

    }

    @Test
    public void testRetrieveDomainObject () {
        final LogEntry le = new LogEntry();
        le.setLogCode( TransactionType.LOGIN_SUCCESS );
        le.setMessage( "User has logged in" );
        le.setPrimaryUser( "test" );
        le.setTime( ZonedDateTime.now() );
        le.save();

        final LogEntry retrieve = (LogEntry) DomainObject.getBy( LogEntry.class, "primaryUser", "test" );
        assertNotNull( retrieve );
        assertEquals( retrieve.getLogCode(), TransactionType.LOGIN_SUCCESS );

    }

    @Test
    public void testDelete () {
        final Hospital h = new Hospital();
        h.setAddress( "890 Oval Drive, Raleigh" );
        h.setState( State.NC );
        h.setZip( "27607" );
        h.setName( "iTrust Test Hospital 2: Electric Boogaloo" );
        h.save();

        h.delete();
        final Hospital retrieve = Hospital.getByName( "iTrust Test Hospital 2: Electric Boogaloo" );
        assertNull( retrieve );

    }

    @Test
    public void testCopy () {
        Hospital h = new Hospital();
        h.setAddress( "890 Oval Drive, Raleigh" );
        h.setState( State.NC );
        h.setZip( "27607" );
        h.setName( "iTrust Test Hospital 2: Electric Boogaloo" );
        h.save();

        h = Hospital.getByName( "iTrust Test Hospital 2: Electric Boogaloo" );

        final Hospital h2 = new Hospital();

        h2.copyFrom( h, true );

        assertEquals( h.getAddress(), h2.getAddress() );
        assertEquals( h.getState(), h2.getState() );
        assertEquals( h.getZip(), h2.getZip() );
        assertEquals( h.getName(), h2.getName() );
        assertEquals( h.getId(), h2.getId() );

        assertEquals( h2.getName(), ( (Hospital) Hospital.getById( Hospital.class, h.getId() ) ).getName() );
    }

    @Test
    public void testGetBy () {
        assertNull( DomainObject.getBy( User.class, "a", "b" ) );

        assertNotNull( DomainObject.getBy( User.class, "username", "hcp" ) );
    }

}
