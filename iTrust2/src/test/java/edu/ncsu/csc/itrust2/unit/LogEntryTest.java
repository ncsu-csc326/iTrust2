package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ncsu.csc.itrust2.controllers.api.comm.LogEntryTableRow;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;

public class LogEntryTest {

    @Test
    public void testLogEntryTableRow () {
        final LogEntryTableRow row = new LogEntryTableRow();

        row.setNumPages( 12 );
        row.setPatient( true );
        row.setPrimary( "patient" );
        row.setSecondary( "hcp" );
        row.setTransactionType( TransactionType.APPOINTMENT_REQUEST_APPROVED.toString() );

        assertEquals( 12, row.getNumPages() );

        assertEquals( true, row.isPatient() );

        assertEquals( "patient", row.getPrimary() );

        assertEquals( "hcp", row.getSecondary() );

        assertEquals( TransactionType.APPOINTMENT_REQUEST_APPROVED.toString(), row.getTransactionType() );

    }

}
