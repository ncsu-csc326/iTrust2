package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.junit.Test;

import edu.ncsu.csc.itrust2.forms.hcp.GeneralCheckupForm;
import edu.ncsu.csc.itrust2.forms.personnel.LabProcedureForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.LabStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.GeneralCheckup;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.LOINC;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Class to test LabProcedure and LabProcedureForm objects.
 *
 * @author Alex Phelps
 *
 */
public class LabProcedureTest {

    final User assignedTech  = new User( "labtech", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
            Role.ROLE_LABTECH, 1 );
    final User patient       = new User( "patient", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
            Role.ROLE_PATIENT, 1 );
    final User assignedTech2 = new User( "labtech2", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
            Role.ROLE_LABTECH, 1 );
    final User patient2      = new User( "patient2", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
            Role.ROLE_PATIENT, 1 );
    final User assignedTech3 = new User( "labtech3", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
            Role.ROLE_LABTECH, 1 );
    final User patient3      = new User( "patient3", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
            Role.ROLE_PATIENT, 1 );

    /**
     * Tests the functionality of the LabProcedureForm object.
     *
     * @throws Exception
     */
    @Test
    public void testLabProcedure () throws Exception {
        // Create LabProcedureForm
        final LabProcedureForm form = new LabProcedureForm();
        form.setId( 1L );
        form.setPatient( "onionman" );
        assertEquals( "onionman", form.getPatient() );
        form.setComments( "These are some fancy comments!" );
        assertEquals( "These are some fancy comments!", form.getComments() );
        form.setPriority( "1" );
        assertEquals( "1", form.getPriority() );
        form.setStatus( "1" );
        assertEquals( "1", form.getStatus() );
        assignedTech.save();
        form.setAssignedTech( assignedTech.getUsername() );
        assertEquals( assignedTech.getUsername(), form.getAssignedTech() );
        final LOINC l = new LOINC();
        l.setCode( "12345-1" );
        l.setCommonName( "Jump around" );
        l.setComponent( "Jump jump jump" );
        l.setProperty( "JUMP" );
        l.save();
        form.setLoincId( l.getId() );
        assertEquals( form.getLoincId(), l.getId() );
        final OfficeVisit of = makeOfficeVisit();
        form.setVisitId( of.getId() );
        assertEquals( of.getId(), form.getVisitId() );

        // Make a LabProcedure from the form
        final LabProcedure lp = new LabProcedure( form );
        assertTrue( lp.getId().equals( 1L ) );
        assertEquals( LOINC.getById( l.getId() ), lp.getLoinc() );
        assertEquals( User.getByName( "onionman" ), lp.getPatient() );
        assertEquals( "These are some fancy comments!", lp.getComments() );
        assertEquals( Priority.CRITICAL, lp.getPriority() );
        assertEquals( LabStatus.ASSIGNED, lp.getStatus() );
        assertEquals( User.getByName( assignedTech.getUsername() ), lp.getAssignedTech() );
        assertEquals( OfficeVisit.getById( of.getId() ).getId(), lp.getVisit().getId() );
        final OfficeVisit ov1 = OfficeVisit.getById( of.getId() );
        final OfficeVisit ov2 = lp.getVisit();
        assertEquals( ov1, ov2 );
        // assertEquals( OfficeVisit.getById( of.getId() ), lp.getVisit() );
    }

    /**
     * Makes an OfficeVisit for testing
     *
     * @return The newly created OfficeVisit
     */
    private OfficeVisit makeOfficeVisit () {
        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hosp.save();

        final GeneralCheckup visit = new GeneralCheckup();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( User.getByName( "hcp" ) );
        bhm.setPatient( User.getByName( "AliceThirteen" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );

        bhm.save();

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_CHECKUP );
        visit.setHospital( hosp );
        visit.setPatient( User.getByName( "AliceThirteen" ) );
        visit.setHcp( User.getByName( "AliceThirteen" ) );
        visit.setDate( ZonedDateTime.now() );

        final List<Diagnosis> diagnoses = new Vector<Diagnosis>();

        final ICDCode code = new ICDCode();
        code.setCode( "A21" );
        code.setDescription( "Top Quality" );

        code.save();

        final Diagnosis diagnosis = new Diagnosis();

        diagnosis.setCode( code );
        diagnosis.setNote( "This is bad" );
        diagnosis.setVisit( visit );

        diagnoses.add( diagnosis );

        visit.setDiagnoses( diagnoses );

        final Drug drug = new Drug();

        drug.setCode( "1234-4321-89" );
        drug.setDescription( "Lithium Compounds" );
        drug.setName( "Li2O8" );
        drug.save();

        final Prescription pres = new Prescription();
        pres.setDosage( 3 );
        pres.setDrug( drug );

        pres.setEndDate( LocalDate.now().plusDays( 10 ) );
        pres.setPatient( User.getByName( "AliceThirteen" ) );
        pres.setStartDate( LocalDate.now() );
        pres.setRenewals( 5 );

        pres.save();

        visit.setPrescriptions( Collections.singletonList( pres ) );

        visit.save();
        return visit;
    }

    /**
     * Tests that you can get LOINC codes.
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    @Test
    public void testGetBy () throws NumberFormatException, ParseException {
        LabProcedure.deleteAll();

        final LOINC l = new LOINC();
        l.setCode( "12345-1" );
        l.setCommonName( "Jump around" );
        l.setComponent( "Jump jump jump" );
        l.setProperty( "JUMP" );
        l.save();
        patient.save();
        assignedTech.save();
        patient2.save();
        assignedTech2.save();
        patient3.save();
        assignedTech3.save();
        final GeneralCheckupForm visitForm = new GeneralCheckupForm();
        visitForm.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
        visitForm.setHcp( "hcp" );
        visitForm.setPatient( "patient" );
        visitForm.setNotes( "Test office visit" );
        visitForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visitForm.setHospital( "Dr. Jenkins' Insane Asylum" );
        visitForm.setDiastolic( 150 );
        visitForm.setHdl( 75 );
        visitForm.setLdl( 75 );
        visitForm.setHeight( 75f );
        visitForm.setWeight( 130f );
        visitForm.setTri( 300 );
        visitForm.setSystolic( 150 );
        visitForm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        visitForm.setPatientSmokingStatus( PatientSmokingStatus.NEVER );
        final GeneralCheckup visit = new GeneralCheckup( visitForm );
        visit.save();
        final LabProcedureForm form = new LabProcedureForm();
        form.setPatient( "patient" );
        assertEquals( "patient", form.getPatient() );
        form.setComments( "These are some fancy comments!" );
        assertEquals( "These are some fancy comments!", form.getComments() );
        form.setPriority( "1" );
        assertEquals( "1", form.getPriority() );
        form.setStatus( "1" );
        assertEquals( "1", form.getStatus() );
        form.setAssignedTech( assignedTech.getUsername() );
        assertEquals( assignedTech.getUsername(), form.getAssignedTech() );
        form.setLoincId( l.getId() );
        assertEquals( form.getLoincId(), l.getId() );
        form.setVisitId( visit.getId() );
        final LabProcedure lp = new LabProcedure( form );
        form.setAssignedTech( assignedTech2.getUsername() );
        form.setPatient( patient2.getUsername() );
        final LabProcedure lp2 = new LabProcedure( form );
        form.setAssignedTech( assignedTech2.getUsername() );
        form.setPatient( patient3.getUsername() );
        final LabProcedure lp3 = new LabProcedure( form );
        form.setAssignedTech( assignedTech3.getUsername() );
        form.setPatient( patient2.getUsername() );
        final LabProcedure lp4 = new LabProcedure( form );
        assertTrue( lp.getLoinc() != null );
        assertTrue( lp.getAssignedTech() != null );
        assertTrue( lp.getPatient() != null );
        assertTrue( lp.getVisit() != null );
        lp.save();
        lp2.save();
        lp3.save();
        lp4.save();

        LabProcedure c = null;
        try {
            c = LabProcedure.getById( -1L );
        }
        catch ( final IllegalArgumentException e ) {
            assertNull( c );
        }

        c = LabProcedure.getById( lp.getId() );
        assertEquals( LOINC.getById( l.getId() ), c.getLoinc() );
        assertEquals( User.getByName( "patient" ), c.getPatient() );
        assertEquals( "These are some fancy comments!", c.getComments() );
        assertEquals( Priority.CRITICAL, c.getPriority() );
        assertEquals( LabStatus.ASSIGNED, c.getStatus() );
        assertEquals( User.getByName( assignedTech.getUsername() ), c.getAssignedTech() );
        assertEquals( OfficeVisit.getById( visit.getId() ), lp.getVisit() );

        final List<LabProcedure> patientLPs = LabProcedure.getForPatient( "patient2" );
        assertEquals( 2, patientLPs.size() );
        assertEquals( patientLPs.get( 0 ), lp2 );
        assertEquals( patientLPs.get( 1 ), lp4 );

        final List<LabProcedure> labtechLPs = LabProcedure.getForLabtech( assignedTech2.getUsername() );
        assertEquals( 2, labtechLPs.size() );
        assertEquals( labtechLPs.get( 0 ), lp2 );
        assertEquals( labtechLPs.get( 1 ), lp3 );

        final List<LabProcedure> bothLPs = LabProcedure.getForTechAndPatient( assignedTech2.getUsername(),
                patient2.getUsername() );
        assertEquals( 1, bothLPs.size() );
        assertEquals( bothLPs.get( 0 ), lp2 );

        assertEquals( 4, LabProcedure.getLabProcedures().size() );
        c.delete();
        assertEquals( 3, LabProcedure.getLabProcedures().size() );
        LabProcedure.deleteAll();
        assertEquals( 0, LabProcedure.getLabProcedures().size() );
    }

    /**
     * Tests that you can get LOINC codes.
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    @Test
    public void testLabProcedureForm () throws NumberFormatException, ParseException {
        LabProcedure.deleteAll();

        final LOINC l = new LOINC();
        l.setCode( "12345-1" );
        l.setCommonName( "Jump around" );
        l.setComponent( "Jump jump jump" );
        l.setProperty( "JUMP" );
        l.save();
        patient.save();
        assignedTech.save();
        final GeneralCheckupForm visitForm = new GeneralCheckupForm();
        visitForm.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
        visitForm.setHcp( "hcp" );
        visitForm.setPatient( "patient" );
        visitForm.setNotes( "Test office visit" );
        visitForm.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        visitForm.setHospital( "Dr. Jenkins' Insane Asylum" );
        visitForm.setDiastolic( 150 );
        visitForm.setHdl( 75 );
        visitForm.setLdl( 75 );
        visitForm.setHeight( 75f );
        visitForm.setWeight( 130f );
        visitForm.setTri( 300 );
        visitForm.setSystolic( 150 );
        visitForm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        visitForm.setPatientSmokingStatus( PatientSmokingStatus.NEVER );
        final GeneralCheckup visit = new GeneralCheckup( visitForm );
        visit.save();
        final LabProcedureForm form = new LabProcedureForm();
        form.setPatient( "patient" );
        assertEquals( "patient", form.getPatient() );
        form.setComments( "These are some fancy comments!" );
        assertEquals( "These are some fancy comments!", form.getComments() );
        form.setPriority( "1" );
        assertEquals( "1", form.getPriority() );
        form.setStatus( "1" );
        assertEquals( "1", form.getStatus() );
        form.setAssignedTech( assignedTech.getUsername() );
        assertEquals( assignedTech.getUsername(), form.getAssignedTech() );
        form.setLoincId( l.getId() );
        assertEquals( form.getLoincId(), l.getId() );
        form.setVisitId( visit.getId() );
        final LabProcedure lp = new LabProcedure( form );
        assertTrue( lp.getLoinc() != null );
        assertTrue( lp.getAssignedTech() != null );
        assertTrue( lp.getPatient() != null );
        assertTrue( lp.getVisit() != null );
        lp.save();

        final LabProcedureForm lpf = new LabProcedureForm( lp );
        assertEquals( "patient", lpf.getPatient() );
        assertEquals( "These are some fancy comments!", lpf.getComments() );
        assertEquals( "1", lpf.getPriority() );
        assertEquals( "1", lpf.getStatus() );
        assertEquals( assignedTech.getUsername(), lpf.getAssignedTech() );
        assertEquals( l.getId(), lpf.getLoincId() );

        LabProcedure.deleteAll();
        assertEquals( 0, LabProcedure.getLabProcedures().size() );
    }
}
