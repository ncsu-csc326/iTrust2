package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;

import org.junit.Test;

import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.LogEntry;

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
        le.setTime( Calendar.getInstance() );
        le.save();

        final LogEntry retrieve = (LogEntry) DomainObject.getBy( LogEntry.class, "primaryUser", "test" );
        assertNotNull( retrieve );
        assertEquals( retrieve.getLogCode(), TransactionType.LOGIN_SUCCESS );

    }

    @Test
    public void testDelete () {
        final Hospital h = new Hospital();
        h.setAddress( "2770 Wolf Village Drive, Raleigh" );
        h.setState( State.NC );
        h.setZip( "27607" );
        h.setName( "iTrust Test Hospital 2: Electric Boogaloo" );
        h.save();

        h.delete();
        final Hospital retrieve = Hospital.getByName( "iTrust Test Hospital 2: Electric Boogaloo" );
        assertNull( retrieve );

    }

}
