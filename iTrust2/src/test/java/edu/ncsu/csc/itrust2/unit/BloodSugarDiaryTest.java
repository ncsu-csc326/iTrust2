/**
 *
 */
package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.patient.BloodSugarDiaryForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.BloodSugarDiaryEntry;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Test class for the Blood Sugar Diary Entry Object
 *
 * @author Thomas Landsberg
 *
 */
public class BloodSugarDiaryTest {

    /** Patient to be used for testing */
    private final String  username = "patient";
    private final String  encoded  = "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.";
    private final User    user     = new User( username, encoded, Role.ROLE_PATIENT, 1 );
    private final Patient patient  = new Patient( user );

    /**
     * Delete all the blood sugar diary entries before running each test
     */
    @Before
    public void setUp () {
        user.save();
        patient.save();
        DomainObject.deleteAll( BloodSugarDiaryEntry.class );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc.itrust2.models.persistent.BloodSugarDiaryEntry#BloodSugarDiary(edu.ncsu.csc.itrust2.forms.patient.BloodSugarDiaryForm)}.
     */
    @Test
    public void testBloodSugarDiaryBloodSugarDiaryForm () {

        assertEquals( BloodSugarDiaryEntry.getByDateAndPatient( "2018-09-03", patient ), null );
        final BloodSugarDiaryForm form = new BloodSugarDiaryForm();

        form.setDate( "2018-09-03" );
        form.setFastingLevel( 20 );
        form.setFirstLevel( 30 );
        form.setSecondLevel( 10 );
        form.setThirdLevel( 70 );

        final BloodSugarDiaryEntry test = new BloodSugarDiaryEntry( form );
        test.setPatient( patient );
        test.save();

        final BloodSugarDiaryEntry copy = BloodSugarDiaryEntry.getById( test.getId() );
        assertEquals( test.getId(), copy.getId() );
        assertEquals( test.getFastingLevel(), copy.getFastingLevel() );
        assertEquals( test.getFirstLevel(), copy.getFirstLevel() );
        assertEquals( test.getSecondLevel(), copy.getSecondLevel() );
        assertEquals( test.getThirdLevel(), copy.getThirdLevel() );
        assertEquals( test.getPatient(), copy.getPatient() );
        assertEquals( test.getDate(), copy.getDate() );

        assertEquals( BloodSugarDiaryEntry.getById( test.getId() ).getDate(), test.getDate() );
        assertEquals( BloodSugarDiaryEntry.getByDateAndPatient( "2018-09-03", patient ).getId(), test.getId() );

        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 1 );

        final BloodSugarDiaryEntry test1 = new BloodSugarDiaryEntry();
        test1.setPatient( patient );
        test1.setDate( LocalDate.parse( "2018-08-01" ) );
        test1.setFastingLevel( 10 );
        test1.save();

        assertEquals( BloodSugarDiaryEntry.getByPatient( patient ).size(), 2 );
    }

}
