package edu.ncsu.csc.iTrust2.unit;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.PatientService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class PatientTest {

    @Autowired
    private PatientService      service;

    private static final String USER_1 = "demoTestUser1";

    private static final String USER_2 = "demoTestUser2";

    private static final String PW     = "123456";

    @Before
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testCreatePatientRecord () {

        Assert.assertEquals( "There should be no Patient records in the system!", 0, service.count() );

        final Patient p1 = new Patient( new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 ) );

        service.save( p1 );

        final List<Patient> savedRecords = (List<Patient>) service.findAll();

        Assert.assertEquals( "Creating a Patient record should results in its creation in the DB", 1,
                savedRecords.size() );

        Assert.assertEquals( "Creating a Patient record should results in its creation in the DB", USER_1,
                savedRecords.get( 0 ).getUsername() );

        p1.setFirstName( "Karl" );
        p1.setLastName( "Liebknecht" );

        service.save( p1 );

        final User userRecord = service.findByName( USER_1 );

        Assert.assertEquals( USER_1, userRecord.getUsername() );

        Assert.assertEquals( Patient.class, userRecord.getClass() );

        final Patient retrieved = (Patient) userRecord;

        Assert.assertEquals( "Karl", retrieved.getFirstName() );

        try {
            final Patient p2 = new Patient( new UserForm( USER_2, PW, Role.ROLE_ADMIN, 1 ) );
            Assert.fail( "Should not be able to create a Patient from a non-Patient user" );
        }
        catch ( final Exception e ) {
            // expected
        }

    }
}
