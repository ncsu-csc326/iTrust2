package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.forms.hcp.GeneralCheckupForm;
import edu.ncsu.csc.itrust2.forms.hcp.OphthalmologySurgeryForm;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.EyeSurgeryType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.GeneralCheckup;
import edu.ncsu.csc.itrust2.models.persistent.GeneralOphthalmology;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.OphthalmologySurgery;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step definitions for and HCP to view all of the patients' office visits.
 *
 * @author Domenick DiBiase (dndibias)
 */
public class HCPViewPatientOfficeVisitsStepDefs extends CucumberTest {

    private final String OPH_HCP_TYPE  = "ophthalmologist";
    private final String OD_HCP_TYPE   = "optometrist";

    private final String baseUrl       = "http://localhost:8080/iTrust2";
    private final String hcpString     = "patrickHCP";
    private final String ophHcpString  = "bobbyOPH";
    private final String odHcpString   = "masonOD";
    private final String patientString = "bobby";

    /**
     * Asserts that the text is on the page
     *
     * @param text
     *            text to check
     */
    public void assertTextPresent ( final String text ) {
        try {
            assertTrue( driver.getPageSource().contains( text ) );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Deletes all current office visits and creates one of each type
     */
    @Given ( "^there are office visits of all types$" )
    public void allOfficeVisitTypesExist () {
        GeneralCheckup.deleteAll();
        DomainObject.deleteAll( GeneralOphthalmology.class );
        DomainObject.deleteAll( OphthalmologySurgery.class );

        final GeneralCheckup genCheckup = getGenCheckup();
        if ( genCheckup != null ) {
            genCheckup.save();
        }

        final GeneralOphthalmology genOph = getOphOfficeVisit();
        genOph.save();

        final OphthalmologySurgery ophSurgery = getOphSurgery();
        if ( ophSurgery != null ) {
            ophSurgery.save();
        }
    }

    /**
     * Creates an HCP of the given type
     *
     * @param hcpType
     *            type of the HCP
     */
    @Given ( "^there exists an (.+) HCP in the iTrust system$" )
    public void HCPExists ( final String hcpType ) {
        attemptLogout();

        final User hcp;

        switch ( hcpType ) {
            case OPH_HCP_TYPE:
                hcp = new User( ophHcpString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                        Role.ROLE_OPH, 1 );
                break;
            case OD_HCP_TYPE:
                hcp = new User( odHcpString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                        Role.ROLE_OD, 1 );
                break;
            default:
                hcp = new User( hcpString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                        Role.ROLE_HCP, 1 );
        }
        hcp.save();
    }

    /**
     * The given type of HCP logs in and navigates to the view patient office
     * visits page
     *
     * @param hcpType
     *            type of the HCP
     */
    @Then ( "^the (.+) HCP logs in and navigates to the view patient office visits page$" )
    public void hcpLoginNavToView ( final String hcpType ) {
        attemptLogout();

        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();

        switch ( hcpType ) {
            case OPH_HCP_TYPE:
                username.sendKeys( ophHcpString );
                break;
            case OD_HCP_TYPE:
                username.sendKeys( odHcpString );
                break;
            default:
                username.sendKeys( hcpString );
        }

        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        assertEquals( "iTrust2: HCP Home", driver.getTitle() );

        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('HCPOfficeVisits').click();" );

        assertEquals( "iTrust2: View Patient Office Visits", driver.getTitle() );
    }

    /**
     * Checks that of all the patient office visits shown, the HCP can see at
     * least of of each type
     */
    @And ( "^all of the office visit types are options to select$" )
    public void allOfficeVisitTypesShown () {
        waitForAngular();
        assertTextPresent( "General Checkup" );
        assertTextPresent( "General Ophthalmology" );
        assertTextPresent( "Ophthalmology Surgery" );
    }

    /**
     * The HCP selects the given type of office visit from the list shown and
     * validates the information shown
     *
     * @param visitType
     *            type of office visit
     */
    @When ( "^the HCP selects (.+) office visit it shows the correct visit information$" )
    public void hcpSelectOfficeVisit ( final String visitType ) {
        waitForAngular();
        int value = 1;
        if ( visitType.equals( "General Ophthalmology" ) ) {
            value = 2;
        }
        else if ( visitType.equals( "Ophthalmology Surgery" ) ) {
            value = 3;
        }

        final List<OfficeVisit> visits = OfficeVisit.getOfficeVisits();
        long targetId = 0;

        for ( int i = 0; i < visits.size(); i++ ) {
            if ( visits.get( i ).getType().getCode() == value ) {
                targetId = visits.get( i ).getId();
            }
        }

        final WebElement smokingElement = driver.findElement( By.cssSelector( "input[value=\"" + targetId + "\"]" ) );
        smokingElement.click();

        waitForAngular();

        switch ( value ) {
            case 1:
                validateGenCheckup();
                break;
            case 2:
                validateGenOph();
                break;
            case 3:
                validateOphSurgery();
                break;
            default:
                break;
        }
    }

    /**
     * Generates a general checkup office visit with mock data
     */
    private GeneralCheckup getGenCheckup () {
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

        final GeneralCheckupForm form = new GeneralCheckupForm();
        form.setDate( "2036-02-14T15:50:00.000-05:00" ); // 2/14/2036 3:50 PM
                                                         // EST
        form.setHcp( "hcp" );
        form.setPatient( patientString );
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
        estusD.setNote( "Maybe try a bandaid" );
        diagnoses.add( estusD );
        final Diagnosis peach = new Diagnosis();
        peach.setCode( poisoned );
        peach.setNote( "This guy is poisoned! Give him a peach" );
        diagnoses.add( peach );
        form.setDiagnoses( diagnoses );

        try {
            return new GeneralCheckup( form );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Generates a general ophthalmology office visit with mock data
     */
    private GeneralOphthalmology getOphOfficeVisit () {
        final Hospital hosp = new Hospital( "Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC" );
        hosp.save();

        final GeneralOphthalmology visit = new GeneralOphthalmology();

        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setHcp( User.getByName( "bobbyOD" ) );
        bhm.setPatient( User.getByName( patientString ) );
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
        visit.setPatient( User.getByName( patientString ) );
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

        final List<String> diagnoses = new ArrayList<String>();
        diagnoses.add( "Cataracts" );
        diagnoses.add( "Glaucoma" );
        visit.setDiagnosis( diagnoses );

        visit.save();

        return visit;
    }

    /**
     * Generates a ophthalmology surgery office visit with mock data
     */
    private OphthalmologySurgery getOphSurgery () {
        final Hospital hosp = new Hospital( "iTrust Test Hospital 2", "Rainbow Road", "32923", "NC" );
        hosp.save();
        final OphthalmologySurgeryForm visit = new OphthalmologySurgeryForm();
        visit.setPreScheduled( null );
        visit.setDate( "2048-04-16T09:50:00.000-04:00" ); // 4/16/2048 9:50 AM
                                                          // EDT
        visit.setHcp( "hcp" );
        visit.setPatient( patientString );
        visit.setNotes( "Test office visit" );
        visit.setType( AppointmentType.OPHTHALMOLOGY_SURGERY.toString() );
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

        visit.setSurgeryType( EyeSurgeryType.CATARACT );

        try {
            return new OphthalmologySurgery( visit );
        }
        catch ( final Exception e ) {
            // Do nothing
            return null;
        }
    }

    /**
     * Validates the data shown is correct for the general checkup office visit
     */
    private void validateGenCheckup () {
        waitForAngular();

        assertEquals( "General Hospital", driver.findElement( By.name( "hospitalName" ) ).getText() );
        assertEquals( "1", driver.findElement( By.name( "diastolic" ) ).getText() );
        assertEquals( patientString, driver.findElement( By.name( "patientName" ) ).getText() );
        assertEquals( "1", driver.findElement( By.name( "hdl" ) ).getText() );
        assertEquals( "1", driver.findElement( By.name( "ldl" ) ).getText() );
        assertEquals( "1", driver.findElement( By.name( "height" ) ).getText() );
        assertEquals( "1", driver.findElement( By.name( "weight" ) ).getText() );
        assertEquals( "100", driver.findElement( By.name( "tri" ) ).getText() );
        assertEquals( "1", driver.findElement( By.name( "systolic" ) ).getText() );
        assertEquals( "NONSMOKING", driver.findElement( By.name( "houseSmokingStatus" ) ).getText() );
        assertEquals( "NEVER", driver.findElement( By.name( "patientSmokingStatus" ) ).getText() );
        assertEquals( "General Checkup", driver.findElement( By.name( "visitType" ) ).getText() );
        assertEquals( "Date: 02/14/2036", driver.findElement( By.name( "date" ) ).getText() );
        assertEquals( "Time: 3:50 PM", driver.findElement( By.name( "time" ) ).getText() );
        assertEquals( "Office Visit For SiegwardOf Catarina", driver.findElement( By.name( "notes" ) ).getText() );
        assertEquals( "Injury of lumbar and sacral spinal cord. Probably carrying teammates.",
                driver.findElement( By.name( "diagnoseDesc" ) ).getText() );
        assertEquals( "Maybe try a bandaid", driver.findElement( By.name( "diagnoseNote" ) ).getText() );
    }

    /**
     * Validates the data shown is correct for the general ophthalmology office
     * visit
     */
    private void validateGenOph () {
        assertEquals( "Dr. Jenkins' Insane Asylum", driver.findElement( By.name( "hospitalName" ) ).getText() );
        assertEquals( "150", driver.findElement( By.name( "diastolic" ) ).getText() );
        assertEquals( patientString, driver.findElement( By.name( "patientName" ) ).getText() );
        assertEquals( "75", driver.findElement( By.name( "hdl" ) ).getText() );
        assertEquals( "75", driver.findElement( By.name( "ldl" ) ).getText() );
        assertEquals( "75", driver.findElement( By.name( "height" ) ).getText() );
        assertEquals( "130", driver.findElement( By.name( "weight" ) ).getText() );
        assertEquals( "300", driver.findElement( By.name( "tri" ) ).getText() );
        assertEquals( "150", driver.findElement( By.name( "systolic" ) ).getText() );
        assertEquals( "NONSMOKING", driver.findElement( By.name( "houseSmokingStatus" ) ).getText() );
        assertEquals( "NEVER", driver.findElement( By.name( "patientSmokingStatus" ) ).getText() );
        assertEquals( "General Ophthalmology", driver.findElement( By.name( "visitType" ) ).getText() );
        assertEquals( "20", driver.findElement( By.name( "acuityOD" ) ).getText() );
        assertEquals( "40", driver.findElement( By.name( "acuityOS" ) ).getText() );
        assertEquals( "1.5", driver.findElement( By.name( "sphereOD" ) ).getText() );
        assertEquals( "-1.5", driver.findElement( By.name( "sphereOS" ) ).getText() );
        assertEquals( "1", driver.findElement( By.name( "cylinderOD" ) ).getText() );
        assertEquals( "-1", driver.findElement( By.name( "cylinderOS" ) ).getText() );
        assertEquals( "45", driver.findElement( By.name( "axisOD" ) ).getText() );
        assertEquals( "90", driver.findElement( By.name( "axisOS" ) ).getText() );
    }

    /**
     * Validates the data shown is correct for the ophthalmology surgery office
     * visit
     */
    private void validateOphSurgery () {
        assertEquals( "iTrust Test Hospital 2", driver.findElement( By.name( "hospitalName" ) ).getText() );
        assertEquals( "150", driver.findElement( By.name( "diastolic" ) ).getText() );
        assertEquals( patientString, driver.findElement( By.name( "patientName" ) ).getText() );
        assertEquals( "75", driver.findElement( By.name( "hdl" ) ).getText() );
        assertEquals( "75", driver.findElement( By.name( "ldl" ) ).getText() );
        assertEquals( "75", driver.findElement( By.name( "height" ) ).getText() );
        assertEquals( "130", driver.findElement( By.name( "weight" ) ).getText() );
        assertEquals( "300", driver.findElement( By.name( "tri" ) ).getText() );
        assertEquals( "150", driver.findElement( By.name( "systolic" ) ).getText() );
        assertEquals( "NONSMOKING", driver.findElement( By.name( "houseSmokingStatus" ) ).getText() );
        assertEquals( "NEVER", driver.findElement( By.name( "patientSmokingStatus" ) ).getText() );
        assertEquals( "Ophthalmology Surgery", driver.findElement( By.name( "visitType" ) ).getText() );
        assertEquals( "20", driver.findElement( By.name( "acuityOD" ) ).getText() );
        assertEquals( "40", driver.findElement( By.name( "acuityOS" ) ).getText() );
        assertEquals( "1.5", driver.findElement( By.name( "sphereOD" ) ).getText() );
        assertEquals( "-1.5", driver.findElement( By.name( "sphereOS" ) ).getText() );
        assertEquals( "1", driver.findElement( By.name( "cylinderOD" ) ).getText() );
        assertEquals( "-1", driver.findElement( By.name( "cylinderOS" ) ).getText() );
        assertEquals( "45", driver.findElement( By.name( "axisOD" ) ).getText() );
        assertEquals( "90", driver.findElement( By.name( "axisOS" ) ).getText() );
        assertEquals( "Date: 04/16/2048", driver.findElement( By.name( "date" ) ).getText() );

        final String time = driver.findElement( By.name( "time" ) ).getText();
        assertTrue( time.equals( "Time: 10:50 AM" ) || time.equals( "Time: 9:50 AM" ) );

        assertEquals( "Test office visit", driver.findElement( By.name( "notes" ) ).getText() );
        assertEquals( "CATARACT", driver.findElement( By.name( "surgeryType" ) ).getText() );
    }
}
