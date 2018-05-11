package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Unit tests for the Patient class
 *
 * @author jshore
 *
 */
public class PatientTest {

    /**
     * Creates a patient from a patient form with both date and cause of death.
     * Additionally, tests getters and setters that were untested up to this
     * point.
     *
     * @throws ParseException
     */
    @Test
    public void testPatientDateOfDeath () throws ParseException {
        final User patient = new User( "patientTestPatient", "123456", Role.ROLE_PATIENT, 1 );
        patient.save();
        final User mom = new User( "patientTestMom", "123456", Role.ROLE_PATIENT, 1 );
        mom.save();
        final User dad = new User( "patientTestDad", "123456", Role.ROLE_PATIENT, 1 );
        dad.save();
        final PatientForm form = new PatientForm();
        form.setMother( mom.getUsername() );
        form.setFather( dad.getUsername() );
        form.setFirstName( "patient" );
        form.setPreferredName( "patient" );
        form.setLastName( "mcpatientface" );
        form.setEmail( "bademail@ncsu.edu" );
        form.setAddress1( "Some town" );
        form.setAddress2( "Somewhere" );
        form.setCity( "placecity" );
        form.setState( State.AL.getName() );
        form.setZip( "27606" );
        form.setPhone( "111-111-1111" );
        form.setDateOfBirth( "01/01/1901" );
        form.setDateOfDeath( "01/01/2001" );
        form.setCauseOfDeath( "Hit by a truck" );
        form.setBloodType( BloodType.ABPos.getName() );
        form.setEthnicity( Ethnicity.Asian.getName() );
        form.setGender( Gender.Male.getName() );
        form.setSelf( patient.getUsername() );

        final Patient testPatient = new Patient( form );
        testPatient.save();
        assertEquals( "patientTestMom", testPatient.getMother().getUsername() );
        assertEquals( "patientTestDad", testPatient.getFather().getUsername() );
        assertEquals( "patient", testPatient.getFirstName() );
        assertEquals( "patient", testPatient.getPreferredName() );
        assertEquals( "mcpatientface", testPatient.getLastName() );
        assertEquals( "bademail@ncsu.edu", testPatient.getEmail() );
        assertEquals( "Some town", testPatient.getAddress1() );

        assertEquals( "Somewhere", testPatient.getAddress2() );

        assertEquals( "placecity", testPatient.getCity() );

        assertEquals( State.AL, testPatient.getState() );
        assertEquals( "27606", testPatient.getZip() );
        assertEquals( "111-111-1111", testPatient.getPhone() );

        final SimpleDateFormat sdfBirth = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Date parsedBirthDate = sdfBirth.parse( form.getDateOfBirth() );
        final Calendar birth = Calendar.getInstance();
        birth.setTime( parsedBirthDate );

        assertEquals( birth.getTime(), testPatient.getDateOfBirth().getTime() );

        final SimpleDateFormat sdfDeath = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Date parsedDeathDate = sdfDeath.parse( form.getDateOfDeath() );
        final Calendar death = Calendar.getInstance();
        death.setTime( parsedDeathDate );

        assertEquals( death.getTime(), testPatient.getDateOfDeath().getTime() );
        assertEquals( "Hit by a truck", testPatient.getCauseOfDeath() );
        assertEquals( BloodType.ABPos, testPatient.getBloodType() );
        assertEquals( Ethnicity.Asian, testPatient.getEthnicity() );
        assertEquals( Gender.Male, testPatient.getGender() );

        // forms can also be constructed in the reverse direction so we'll check
        // that

        final PatientForm pf2 = new PatientForm( testPatient );
        assertEquals( testPatient.getMother().getUsername(), pf2.getMother() );
        assertEquals( testPatient.getFather().getUsername(), pf2.getFather() );
        assertEquals( testPatient.getFirstName(), pf2.getFirstName() );
        assertEquals( testPatient.getPreferredName(), pf2.getPreferredName() );
        assertEquals( testPatient.getLastName(), pf2.getLastName() );
        assertEquals( testPatient.getEmail(), pf2.getEmail() );
        assertEquals( testPatient.getAddress1(), pf2.getAddress1() );
        assertEquals( testPatient.getAddress2(), pf2.getAddress2() );
        assertEquals( testPatient.getCity(), pf2.getCity() );
        assertEquals( testPatient.getState().getAbbrev(), pf2.getState() );
        assertEquals( testPatient.getZip(), pf2.getZip() );
        assertEquals( testPatient.getPhone(), pf2.getPhone() );
        assertEquals( sdfBirth.format( testPatient.getDateOfBirth().getTime() ), pf2.getDateOfBirth() );
        assertEquals( testPatient.getCauseOfDeath(), pf2.getCauseOfDeath() );
        assertEquals( testPatient.getBloodType().getName(), pf2.getBloodType() );
        assertEquals( testPatient.getEthnicity().getName(), pf2.getEthnicity() );
        assertEquals( testPatient.getGender().getName(), pf2.getGender() );

    }

    /**
     * Tests that validation of all fields is working
     */
    @Test
    public void testFieldValidation () {
        final Patient p = new Patient();

        // invalid chars
        expectFailure( p::setFirstName, "first name *" );
        expectFailure( p::setLastName, "last name &\n" );
        expectFailure( p::setPreferredName, "preferred name +#" );
        expectFailure( p::setEmail, "preferred name +#" );
        expectFailure( p::setAddress1, "address 1 ()" );
        expectFailure( p::setAddress2, "address 2 ----" );
        expectFailure( p::setCity, "city38" );
        expectFailure( p::setZip, "zip" );
        expectFailure( p::setPhone, "phone" );

        // invalid sizes
        expectFailure( p::setFirstName, "123456789012345678901" );
        expectFailure( p::setFirstName, "" );
        expectFailure( p::setLastName, "1234567890123456789012345678901" );
        expectFailure( p::setLastName, "" );
        expectFailure( p::setPreferredName, "1234567890123456789012345678901" );
        expectFailure( p::setEmail, "1234567890123456789012345678901" );
        expectFailure( p::setEmail, "" );
        expectFailure( p::setAddress1, "123456789012345678901234567890123456789012345678901" );
        expectFailure( p::setAddress1, "" );
        expectFailure( p::setAddress2, "123456789012345678901234567890123456789012345678901" );
        expectFailure( p::setCity, "abcdefghijklmnop" );
        expectFailure( p::setCity, "" );
        expectFailure( p::setZip, "1234" );
        expectFailure( p::setZip, "123456" );
        expectFailure( p::setZip, "12345-678" );
        expectFailure( p::setZip, "12345-67890" );
        expectFailure( p::setPhone, "123-456-789" );
        expectFailure( p::setPhone, "123-456-78901" );

        // required values
        expectFailure( p::setFirstName, null );
        expectFailure( p::setLastName, null );
        expectFailure( p::setEmail, null );
        expectFailure( p::setAddress1, null );
        expectFailure( p::setCity, null );
        expectFailure( p::setZip, null );
        expectFailure( p::setPhone, null );

        // valid values
        p.setFirstName( "Alexander" );
        p.setLastName( "Johnson" );
        p.setPreferredName( "Alex" );
        p.setEmail( "alex_johnson@gmail.com" );
        p.setAddress1( "1234 Main St." );
        p.setAddress2( "still 1234 Main St." );
        p.setCity( "Raleigh" );
        p.setZip( "27607" );
        p.setZip( "27607-1234" );
        p.setPhone( "123-456-7890" );
    }

    private void expectFailure ( final Consumer<String> setter, final String value ) {
        try {
            setter.accept( value );
            fail( "expected exception during validation" );
        }
        catch ( final Exception e ) {
            // we want an exception
        }
    }

}
