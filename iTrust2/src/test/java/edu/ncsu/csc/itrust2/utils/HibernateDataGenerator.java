package edu.ncsu.csc.itrust2.utils;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.forms.hcp.GeneralCheckupForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.GeneralCheckup;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.LOINC;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Newly revamped Test Data Generator. This class is used to generate database
 * records for the various different types of persistent objects that exist in
 * the system. Takes advantage of Hibernate persistence. To use, instantiate the
 * type of object in question, set all of its parameters, and then call the
 * save() method on the object.
 *
 * @author Kai Presler-Marshall
 *
 */
public class HibernateDataGenerator {

    /**
     * Starts the data generator program.
     *
     * @param args
     *            command line arguments
     * @throws ParseException
     * @throws NumberFormatException
     */
    public static void main ( final String args[] ) throws NumberFormatException, ParseException {
        refreshDB();
        generateUsers();

        System.exit( 0 );
        return;
    }

    /**
     * Generate sample users for the iTrust2 system.
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    public static void refreshDB () throws NumberFormatException, ParseException {
        // using the config to drop/create taken from here:
        // https://stackoverflow.com/questions/20535423/how-to-manually-invoke-create-drop-from-jpa-on-hibernate
        // how to actually generate the schemaexport taken from here:
        // http://www.javarticles.com/2015/06/generating-database-schema-using-hibernate.html

        final StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
        ssrb.configure( "/hibernate.cfg.xml" );
        ssrb.applySetting( "hibernate.connection.url", DBUtil.getUrl() );
        ssrb.applySetting( "hibernate.connection.username", DBUtil.getUsername() );
        ssrb.applySetting( "hibernate.connection.password", DBUtil.getPassword() );
        final SchemaExport export = new SchemaExport(
                (MetadataImplementor) new MetadataSources( ssrb.build() ).buildMetadata() );
        export.drop( true, true );
        export.create( true, true );

        generateUsers();
        generateTestFaculties();
    }

    /**
     * Generate sample users for the iTrust2 system.
     */
    public static void generateUsers () {
        final User hcp = new User( "hcp", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_HCP,
                1 );
        hcp.save();

        final Personnel p = new Personnel();
        p.setSelf( hcp );
        p.setFirstName( "HCP" );
        p.setLastName( "HCP" );
        p.setEmail( "csc326.201.1@gmail.com" );
        p.setAddress1( "1234 Road St." );
        p.setCity( "town" );
        p.setState( State.AK );
        p.setZip( "12345" );
        p.setPhone( "111-222-3333" );
        p.save();

        final User patient = new User( "patient", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        patient.save();

        final User admin = new User( "admin", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_ADMIN, 1 );
        admin.save();

        final User er = new User( "er", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_ER,
                1 );
        er.save();

        final User alminister = new User( "alminister", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_ADMIN, 1 );
        alminister.save();

        final User jbean = new User( "jbean", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        jbean.save();

        final User nsanderson = new User( "nsanderson", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        nsanderson.save();

        final User svang = new User( "svang", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_HCP, 1 );
        svang.save();

        // generate users for testing password change & reset
        for ( int i = 1; i <= 5; i++ ) {
            final User pwtestuser = new User( "pwtestuser" + i,
                    "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_HCP, 1 );
            pwtestuser.save();
        }

        final User lockoutUser = new User( "lockoutUser",
                "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_HCP, 1 );
        lockoutUser.save();

        final User lockoutUser2 = new User( "lockoutUser2",
                "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_HCP, 1 );
        lockoutUser2.save();

        final User knightSolaire = new User( "knightSolaire",
                "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_ER, 1 );
        knightSolaire.save();
        final Personnel kniSolai = new Personnel();
        kniSolai.setSelf( knightSolaire );
        kniSolai.setFirstName( "Knight" );
        kniSolai.setLastName( "Solaire" );
        kniSolai.save();

        final User labTech = new User( "labtech", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_LABTECH, 1 );
        labTech.save();
        final Personnel labTechPerson = new Personnel();
        labTechPerson.setSelf( labTech );
        labTechPerson.setFirstName( "Lab" );
        labTechPerson.setLastName( "Technician" );
        labTechPerson.save();

        final User larryTech = new User( "larrytech", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_LABTECH, 1 );
        larryTech.save();
        final Personnel larryTechPerson = new Personnel();
        larryTechPerson.setSelf( larryTech );
        larryTechPerson.setFirstName( "Larry" );
        larryTechPerson.setLastName( "Teacher" );
        larryTechPerson.save();

        final Patient billy = new Patient();
        billy.setFirstName( "Billy" );
        final User billyUser = new User( "BillyBob", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        billyUser.save();
        billy.setSelf( billyUser );
        billy.setLastName( "Bob" );
        billy.setDateOfBirth( LocalDate.now().minusYears( 40 ) ); // 40 years old
        billy.save();

        final Patient jill = new Patient();
        jill.setFirstName( "Jill" );
        final User jillUser = new User( "JillBob", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        jillUser.save();
        jill.setSelf( jillUser );
        jill.setLastName( "Bob" );
        jill.setDateOfBirth( LocalDate.now().minusYears( 40 ) ); // 40 years old
        jill.save();

        /** Optometrist Bobby Ibajnup. Robort's twin brother. */
        final User bobby = new User( "bobbyOD", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_OD, 1 );
        bobby.save();

        /** Ophthalmologist Robort Ibajnup. Bobby's twin brother. */
        final User robort = new User( "robortOPH", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_OPH, 1 );
        robort.save();
    }

    /**
     * Generates the patients, hospitals, drugs, etc. needed for testing.
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    public static void generateTestFaculties () throws NumberFormatException, ParseException {
        final Patient tim = new Patient();
        final User timUser = new User( "TimTheOneYearOld",
                "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_PATIENT, 1 );
        timUser.save();
        tim.setSelf( timUser );
        tim.setFirstName( "TimTheOneYearOld" );
        tim.setLastName( "Smith" );
        tim.setDateOfBirth( LocalDate.now().minusYears( 1 ) ); // 1 year old
        tim.save();

        final Patient bob = new Patient();
        bob.setFirstName( "BobTheFourYearOld" );
        final User bobUser = new User( "BobTheFourYearOld",
                "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_PATIENT, 1 );
        bobUser.save();
        bob.setSelf( bobUser );
        bob.setLastName( "Smith" );
        bob.setDateOfBirth( LocalDate.now().minusYears( 4 ) ); // 4 years old
        bob.save();

        final Patient alice = new Patient();
        alice.setFirstName( "AliceThirteen" );
        final User aliceUser = new User( "AliceThirteen",
                "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_PATIENT, 1 );
        aliceUser.save();
        alice.setSelf( aliceUser );
        alice.setLastName( "Smith" );
        alice.setDateOfBirth( LocalDate.now().minusYears( 13 ) ); // 13 years old
        alice.save();

        final Hospital hosp = new Hospital( "General Hospital", "123 Main St", "12345", "NC" );
        hosp.save();

        final Drug d = new Drug();
        d.setCode( "1000-0001-10" );
        d.setName( "Quetiane Fumarate" );
        d.setDescription( "atypical antipsychotic and antidepressant" );
        d.save();
    }

    /**
     * Generated LOINC codes for Lab Procedure Tests.
     */
    public static void generateTestLOINC () {
        final LOINC l = new LOINC();
        l.setCode( "806-0" );
        l.setCommonName( "manual count of white blood cells in cerebral spinal fluid specimen" );
        l.setComponent( "white blood cells" );
        l.setProperty( "manual count" );
        l.save();
    }

    /**
     * Generates the patients, hospitals, drugs, etc. needed for testing EHR.
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    public static void generateTestEHR () throws NumberFormatException, ParseException {

        // Used for APIEmergencyRecordFormTest
        final Patient siegward = new Patient();
        siegward.setFirstName( "SiegwardOf" );
        final User siegwardUser = new User( "onionman", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        siegwardUser.save();
        siegward.setSelf( siegwardUser );
        siegward.setLastName( "Catarina" );
        siegward.setGender( Gender.Male );
        siegward.setBloodType( BloodType.OPos );
        siegward.setDateOfBirth( LocalDate.now().minusYears( 30 ) ); // 30 years old
        siegward.save();

        final Patient king1 = new Patient();
        king1.setFirstName( "King" );
        final User king1User = new User( "kingone", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        king1User.save();
        king1.setSelf( king1User );
        king1.setLastName( "One" );
        king1.setGender( Gender.Male );
        king1.setBloodType( BloodType.OPos );
        king1.setDateOfBirth( LocalDate.now().minusYears( 30 ) ); // 30 years old
        king1.save();

        final Patient king2 = new Patient();
        king2.setFirstName( "King" );
        final User king2User = new User( "kingtwo", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        king2User.save();
        king2.setSelf( king2User );
        king2.setLastName( "Two" );
        king2.setGender( Gender.Male );
        king2.setBloodType( BloodType.OPos );
        king2.setDateOfBirth( LocalDate.now().minusYears( 30 ) ); // 30 years old
        king2.save();

        final Patient king3 = new Patient();
        king3.setFirstName( "King" );
        final User king3User = new User( "kingthree", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        king3User.save();
        king3.setSelf( king3User );
        king3.setLastName( "Three" );
        king3.setGender( Gender.Male );
        king3.setBloodType( BloodType.OPos );
        king3.setDateOfBirth( LocalDate.now().minusYears( 30 ) ); // 30 years old
        king3.save();

        final Patient king4 = new Patient();
        king4.setFirstName( "King" );
        final User king4User = new User( "kingfour", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        king4User.save();
        king4.setSelf( king4User );
        king4.setLastName( "Four" );
        king4.setGender( Gender.Male );
        king4.setBloodType( BloodType.OPos );
        king4.setDateOfBirth( LocalDate.now().minusYears( 30 ) ); // 30 years old
        king4.save();

        // First Prescription for APIEmergencyRecordFormTest
        final Drug estus = new Drug();
        estus.setCode( "1111-2222-33" );
        estus.setName( "Sunny D" );
        estus.setDescription( "Estus to heal up those wounds!" );
        estus.save();

        final Prescription estusPresc = new Prescription();
        estusPresc.setDosage( 1 );
        estusPresc.setDrug( estus );
        estusPresc.setRenewals( 20 );
        estusPresc.setStartDate( LocalDate.now().minusDays( 30 ) ); // Prescribed 30 days ago
        estusPresc.setEndDate( LocalDate.now().plusDays( 60 ) ); // Ends in 60 days
        estusPresc.setPatient( siegwardUser );
        estusPresc.save();

        // Second Prescription for APIEmergencyRecordFormTest
        final Drug purpMoss = new Drug();
        purpMoss.setCode( "3333-2222-11" );
        purpMoss.setName( "Purple Moss" );
        purpMoss.setDescription( "Medicinal purple moss clump.\n" + "Reduces poison build-up. Cures poison." );
        purpMoss.save();

        final Prescription purpMossPresc = new Prescription();
        purpMossPresc.setDosage( 1 );
        purpMossPresc.setDrug( purpMoss );
        purpMossPresc.setRenewals( 99 );

        purpMossPresc.setStartDate( LocalDate.now().minusDays( 60 ) ); // Prescribed 60 days ago
        purpMossPresc.setEndDate( LocalDate.now().plusDays( 30 ) ); // Ends in 30 days
        purpMossPresc.setPatient( siegwardUser );
        purpMossPresc.save();

        // Set First Diagnosis Code for APIEmergencyRecordFormTest
        ICDCodeForm codeForm = new ICDCodeForm();
        codeForm.setCode( "T49" );
        codeForm.setDescription( "Poisoned by topical agents.  Probably in Blighttown" );
        final ICDCode poisoned = new ICDCode( codeForm );
        poisoned.save();

        // Create Second Diagnosis Code for APIEmergencyRecordFormTest
        codeForm = new ICDCodeForm();
        codeForm.setCode( "S34" );
        codeForm.setDescription( "Injury of lumbar and sacral spinal cord.  Probably carrying teammates." );
        final ICDCode backPain = new ICDCode( codeForm );
        backPain.save();

        // Create an office visit with two diagnoses
        final GeneralCheckupForm form = new GeneralCheckupForm();
        form.setDate( "2048-04-20T16:20:00.000-04:00" ); // 4/20/2048 4:20 PM

        form.setHcp( "hcp" );
        form.setPatient( "onionman" );
        form.setNotes( "Office Visit For SiegwardOf Catarina" );
        form.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        form.setHospital( "General Hospital" );
        form.setHdl( 1 );
        form.setHeight( 1f );
        form.setWeight( 1f );
        form.setLdl( 1 );
        form.setTri( 100 );
        form.setDiastolic( 1 );
        form.setSystolic( 1 );
        form.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );
        form.setPatientSmokingStatus( PatientSmokingStatus.NEVER );

        final List<Diagnosis> diagnoses = new ArrayList<Diagnosis>();
        final Diagnosis estusD = new Diagnosis();
        estusD.setCode( backPain );
        estusD.setNote( "This guy needs some Sunny D stat!" );
        diagnoses.add( estusD );
        final Diagnosis purpMossD = new Diagnosis();
        purpMossD.setCode( poisoned );
        purpMossD.setNote( "This guy is poisoned!  Quick, eat some purple moss!" );
        diagnoses.add( purpMossD );
        form.setDiagnoses( diagnoses );
        final GeneralCheckup siegOffVisit = new GeneralCheckup( form );
        siegOffVisit.save();
    }
}
