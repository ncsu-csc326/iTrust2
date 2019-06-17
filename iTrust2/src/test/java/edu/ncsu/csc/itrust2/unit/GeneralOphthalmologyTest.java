package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.hcp.GeneralOphthalmologyForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.GeneralOphthalmology;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Tests the GeneralOphthalmology class.
 * 
 * @author Jack MacDonald
 */
public class GeneralOphthalmologyTest {

    /**
     * Tests the GeneralOphthalmology office visit class fields.
     * @throws ParseException if the form data cannot be parsed to a GeneralOphthalmology office visit.
     */
    @Test
    public void testGeneralOphthalmology () throws ParseException {
        DomainObject.deleteAll( GeneralOphthalmology.class );

        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hosp.save();

        final GeneralOphthalmology visit = new GeneralOphthalmology();

        BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setHcp( User.getByName( "bobbyOD" ) );
        bhm.setPatient( User.getByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setLdl( 75 );
        bhm.setHeight( 75f );
        bhm.setWeight( 130f );
        bhm.setTri( 300 );
        bhm.setSystolic( 150 );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        bhm.setPatientSmokingStatus( PatientSmokingStatus.NEVER );

        bhm.save();

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_OPHTHALMOLOGY );
        visit.setHospital( hosp );
        visit.setPatient( User.getByName( "AliceThirteen" ) );
        visit.setHcp( User.getByName( "bobbyOD" ) );
        visit.setDate( ZonedDateTime.now() );
        visit.save();

        visit.setVisualAcuityOD( 20 );
        visit.setVisualAcuityOS( 40 );
        visit.setSphereOD( 1.5 );
        visit.setSphereOS( -1.5 );
        visit.setCylinderOD( 1.0 );
        visit.setCylinderOS( -1.0 );
        visit.setAxisOD( 45 );
        visit.setAxisOS( 90 );
        visit.save();

        // Hibernate.initialize( visit.getDiagnoses() );
        final List<String> diagnoses = new ArrayList<String>();
        diagnoses.add( "Cataracts" );
        diagnoses.add( "Glaucoma" );
        visit.setDiagnosis( diagnoses );

        visit.save();

        // Test the visit's persistence
        final GeneralOphthalmology copy = GeneralOphthalmology.getById( visit.getId() );
        assertEquals( visit.getId(), copy.getId() );
        assertEquals( visit.getAppointment(), copy.getAppointment() );
        assertEquals( visit.getBasicHealthMetrics(), copy.getBasicHealthMetrics() );
        assertEquals( visit.getHcp(), copy.getHcp() );
        assertEquals( visit.getHospital().getName(), copy.getHospital().getName() );
        assertEquals( visit.getPatient(), copy.getPatient() );
        assertEquals( visit.getDiagnosis(), copy.getDiagnosis() );
        assertEquals( visit.getVisualAcuityOD(), copy.getVisualAcuityOD() );
        assertEquals( visit.getVisualAcuityOS(), copy.getVisualAcuityOS() );
        assertEquals( visit.getSphereOD(), copy.getSphereOD() );
        assertEquals( visit.getSphereOS(), copy.getSphereOS() );
        assertEquals( visit.getCylinderOD(), copy.getCylinderOD() );
        assertEquals( visit.getCylinderOS(), copy.getCylinderOS() );
        assertEquals( visit.getAxisOD(), copy.getAxisOD() );
        assertEquals( visit.getAxisOS(), copy.getAxisOS() );

        // Test the form object
        final GeneralOphthalmologyForm form = new GeneralOphthalmologyForm( visit );
        form.setPreScheduled( null );
        assertEquals( visit.getId().toString(), form.getId() );
        assertEquals( visit.getHcp().getUsername(), form.getHcp() );
        assertEquals( visit.getHospital().getName(), form.getHospital() );
        assertEquals( visit.getPatient().getUsername(), form.getPatient() );
        assertEquals( visit.getDiagnosis(), form.getDiagnosis() );

        final GeneralOphthalmology clone = new GeneralOphthalmology( form );
        assertEquals( visit.getId(), clone.getId() );
        assertEquals( visit.getAppointment(), clone.getAppointment() );
        assertEquals( visit.getBasicHealthMetrics().getDiastolic(), clone.getBasicHealthMetrics().getDiastolic() );
        assertEquals( visit.getHcp(), clone.getHcp() );
        assertEquals( visit.getHospital().getName(), clone.getHospital().getName() );
        assertEquals( visit.getPatient(), clone.getPatient() );

        visit.save();

        visit.delete();

    }

    /**
     * Tests the GeneralOphthalmology office visit form.
     * @throws NumberFormatException if the form String can't be converted to an integer
     * @throws ParseException if the form data cannot be parsed to a GeneralOphthalmology office visit.
     */
    @Test
    public void testGeneralOphthalmologyForm () throws NumberFormatException, ParseException {
        final GeneralOphthalmologyForm visit = new GeneralOphthalmologyForm();
        visit.setPreScheduled( null );
        visit.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
        visit.setHcp( "hcp" );
        visit.setPatient( "patient" );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.GENERAL_OPHTHALMOLOGY.toString() );
        visit.setHospital( "iTrust Test Hospital 2" );
        visit.setDiastolic( 150 );
        visit.setHdl( 75 );
        visit.setLdl( 75 );
        visit.setHeight( 75f );
        visit.setWeight( 130f );
        visit.setTri( 300 );
        visit.setSystolic( 150 );
        visit.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        visit.setPatientSmokingStatus( PatientSmokingStatus.NEVER );

        visit.setVisualAcuityOD( 20 );
        visit.setVisualAcuityOS( 40 );
        visit.setSphereOD( 1.5 );
        visit.setSphereOS( -1.5 );
        visit.setCylinderOD( 1.0 );
        visit.setCylinderOS( -1.0 );
        visit.setAxisOD( 45 );
        visit.setAxisOS( 90 );

        final List<String> diagnoses = new ArrayList<String>();
        diagnoses.add( "Cataracts" );
        diagnoses.add( "Glaucoma" );
        visit.setDiagnosis( diagnoses );

        final GeneralOphthalmology ov = new GeneralOphthalmology( visit );

        assertEquals( visit.getHcp(), ov.getHcp().getUsername() );
        assertEquals( visit.getPatient(), ov.getPatient().getUsername() );
    }

    /**
     * Tests optional Basic Health metrics.
     */
    @Test
    public void testMissingBHM() {
        DomainObject.deleteAll( GeneralOphthalmology.class );

        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hosp.save();

        final GeneralOphthalmology visit = new GeneralOphthalmology();

        BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setHcp( User.getByName( "bobbyOD" ) );
        bhm.setPatient( User.getByName( "AliceThirteen" ) );
        bhm.setHeight( 75f );
        bhm.setWeight( 130f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );

        bhm.save();

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_OPHTHALMOLOGY );
        visit.setHospital( hosp );
        visit.setPatient( User.getByName( "AliceThirteen" ) );
        visit.setHcp( User.getByName( "bobbyOD" ) );
        visit.setDate( ZonedDateTime.now() );
        visit.save();

        visit.setVisualAcuityOD( 20 );
        visit.setVisualAcuityOS( 40 );
        visit.setSphereOD( 1.5 );
        visit.setSphereOS( -1.5 );
        visit.setCylinderOD( 1.0 );
        visit.setCylinderOS( -1.0 );
        visit.setAxisOD( 45 );
        visit.setAxisOS( 90 );
        visit.save();

        // Hibernate.initialize( visit.getDiagnoses() );
        final List<String> diagnoses = new ArrayList<String>();
        diagnoses.add( "Cataracts" );
        diagnoses.add( "Glaucoma" );
        visit.setDiagnosis( diagnoses );

        visit.save();

        // Test the visit's persistence
        final GeneralOphthalmology copy = GeneralOphthalmology.getById( visit.getId() );
        assertEquals( visit.getId(), copy.getId() );
        assertEquals( visit.getAppointment(), copy.getAppointment() );
        assertEquals( visit.getBasicHealthMetrics(), copy.getBasicHealthMetrics() );
        assertEquals( visit.getHcp(), copy.getHcp() );
        assertEquals( visit.getHospital().getName(), copy.getHospital().getName() );
        assertEquals( visit.getPatient(), copy.getPatient() );
        assertEquals( visit.getDiagnosis(), copy.getDiagnosis() );
        assertEquals( visit.getVisualAcuityOD(), copy.getVisualAcuityOD() );
        assertEquals( visit.getVisualAcuityOS(), copy.getVisualAcuityOS() );
        assertEquals( visit.getSphereOD(), copy.getSphereOD() );
        assertEquals( visit.getSphereOS(), copy.getSphereOS() );
        assertEquals( visit.getCylinderOD(), copy.getCylinderOD() );
        assertEquals( visit.getCylinderOS(), copy.getCylinderOS() );
        assertEquals( visit.getAxisOD(), copy.getAxisOD() );
        assertEquals( visit.getAxisOS(), copy.getAxisOS() );

        visit.delete();
    }

}
