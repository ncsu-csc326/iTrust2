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
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.PersonnelService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class PersonnelTest {

    @Autowired
    private PersonnelService    service;

    private static final String USER_1 = "demoTestUser1";

    private static final String USER_2 = "demoTestUser2";

    private static final String PW     = "123456";

    @Before
    public void setup () {
        service.deleteAll();
    }

    @Transactional
    @Test
    public void testPersonnel () {

        Assert.assertEquals( "There should be no Personnel records in the system", 0, service.count() );

        final Personnel p1 = new Personnel( new UserForm( USER_1, PW, Role.ROLE_ADMIN, 1 ) );

        service.save( p1 );

        final List<Personnel> savedRecords = (List<Personnel>) service.findAll();

        Assert.assertEquals( "Creating a Personnel record should results in its creation in the DB", 1,
                savedRecords.size() );

        Assert.assertEquals( "Creating a Personnel record should results in its creation in the DB", USER_1,
                savedRecords.get( 0 ).getUsername() );

        p1.setFirstName( "Rosa" );
        p1.setLastName( "Luxemburg" );

        service.save( p1 );

        final User userRecord = service.findByName( USER_1 );

        Assert.assertEquals( USER_1, userRecord.getUsername() );

        Assert.assertEquals( Personnel.class, userRecord.getClass() );

        final Personnel retrieved = (Personnel) userRecord;

        Assert.assertEquals( "Rosa", retrieved.getFirstName() );

        try {
            final Personnel p2 = new Personnel( new UserForm( USER_2, PW, Role.ROLE_PATIENT, 1 ) );
            Assert.fail( "Should not be able to create a Personnel from a Patient user" );
        }
        catch ( final Exception e ) {
            // expected
        }

    }
}
