package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step definitions for Ophthalmology Office Surgery Visit feature.
 *
 * @author Joseph Tysinger (jltysin2)
 */
public class OphthalmologyOfficeVisitStepDefs extends CucumberTest {

    private final String baseUrl      = "http://localhost:8080/iTrust2";
    private final String ophHcpString = "bobbyOPH";

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
     * Fills in the date and time fields with the specified date and time.
     * @param date The date to enter.
     * @param time The time to enter.
     */
    private void fillInDateTime(String dateField, String date, String timeField, String time) {
        fillInDate(dateField, date);
        fillInTime(timeField, time);
    }

    /**
     * Fills in the date field with the specified date.
     * @param date The date to enter.
     */
    private void fillInDate(String dateField, String date) {
        driver.findElement( By.name( dateField ) ).clear();
        final WebElement dateElement = driver.findElement( By.name( dateField ) );
        dateElement.sendKeys( date.replace( "/", "" ) );
    }

    /**
     * Fills in the time field with the specified time.
     * @param time The time to enter.
     */
    private void fillInTime(String timeField, String time) {
        // Zero-pad the time for entry
        if ( time.length() == 7 ) {
            time = "0" + time;
        }

        driver.findElement( By.name( timeField ) ).clear();
        final WebElement timeElement = driver.findElement( By.name( timeField ) );
        timeElement.sendKeys( time.replace( ":", "" ).replace( " ", "" ) );
    }

    /**
     * Creates a new OPH HCP User
     */
    @Given ( "^There exists an ophthalmologist HCP in the system$" )
    public void ophHCPExists () {
        attemptLogout();

        final User ophHcp = new User( ophHcpString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_OPH, 1 );
        ophHcp.save();

        // All tests can safely assume the existence of the 'ophthalmologist
        // hcp',
        // 'admin', and
        // 'patient' users
    }

    /**
     * Logs in HCP and navigates them to the document Office Visit page
     */
    @Then ( "^The HCP logs in and navigates to the Document Office Visit page$" )
    public void loginOphDiaries () {
        attemptLogout();

        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( ophHcpString );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        assertEquals( "iTrust2: HCP Home", driver.getTitle() );

        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );

        assertEquals( "iTrust2: Document Office Visit", driver.getTitle() );
    }

    /**
     * Adds basic information about the office visit into the page
     * 
     * @param date
     *            the date of the office visit
     * @param time
     *            the time of the office visit
     * @param patient
     *            the patient to select from the list of patients
     * @param type
     *            the type of office visit to select
     * @param hospital
     *            the hospital that the office visit is scheduled at
     */
    @When ( "^The HCP enters the date (.+), time (.+), patient (.+), type of visit (.+), hospital (.+)$" )
    public void addBasicInfo ( final String date, final String time, final String patient, final String type,
            final String hospital ) {
        waitForAngular();

        fillInDateTime( "date", date, "time", time );
        
        final WebElement patientElement = driver.findElement( By.cssSelector( "input[value=\"" + patient + "\"]" ) );
        patientElement.click();

        final WebElement typeElement = driver.findElement( By.cssSelector( "input[value=\"" + type + "\"]" ) );
        typeElement.click();

        final WebElement hospitalElement = driver.findElement( By.cssSelector( "input[value=\"" + hospital + "\"]" ) );
        hospitalElement.click();
    }

    /**
     * Enter the basic health metrics into the page
     * 
     * @param height
     *            the patient's height
     * @param weight
     *            the patient's weight
     * @param systolic
     *            the patient's systolic blood pressure
     * @param diastolic
     *            the patient's diastolic blood pressure
     * @param hdl
     *            the patient's hdl
     * @param ldl
     *            the patient's ldl
     * @param tri
     *            the patient's tri
     * @param patientSmoking
     *            the patient's smoking status
     * @param smokingStatus
     *            the patient's household smoking status
     */
    @And ( "^The HCP enters the basic health metrics with height (.+), weight (.+), systolic (.+), diastolic (.+), HDL (.+), LDL (.+), Triglycerides (.+), patient smoking status (.+), and household smoking status (.+)$" )
    public void addBasicHealthMetrics ( final String height, final String weight, final String systolic,
            final String diastolic, final String hdl, final String ldl, final String tri, final String patientSmoking,
            final String smokingStatus ) {
        waitForAngular();

        driver.findElement( By.name( "height" ) ).clear();
        driver.findElement( By.name( "height" ) ).sendKeys( height );

        driver.findElement( By.name( "weight" ) ).clear();
        driver.findElement( By.name( "weight" ) ).sendKeys( weight );

        driver.findElement( By.name( "systolic" ) ).clear();
        driver.findElement( By.name( "systolic" ) ).sendKeys( systolic );

        driver.findElement( By.name( "diastolic" ) ).clear();
        driver.findElement( By.name( "diastolic" ) ).sendKeys( diastolic );

        driver.findElement( By.name( "hdl" ) ).clear();
        driver.findElement( By.name( "hdl" ) ).sendKeys( hdl );

        driver.findElement( By.name( "ldl" ) ).clear();
        driver.findElement( By.name( "ldl" ) ).sendKeys( ldl );

        driver.findElement( By.name( "tri" ) ).clear();
        driver.findElement( By.name( "tri" ) ).sendKeys( tri );

        final WebElement patientSmokingElement = driver
                .findElement( By.cssSelector( "input[value=\"" + patientSmoking + "\"]" ) );
        patientSmokingElement.click();

        final WebElement smokingElement = driver
                .findElement( By.cssSelector( "input[value=\"" + smokingStatus + "\"]" ) );
        smokingElement.click();
    }

    /**
     * Enters the visual acuity fields into the page
     * 
     * @param leftAcuity
     *            the patient's left eye visual acuity
     * @param rightAcuity
     *            the patient's right eye visual acuity
     * @param leftSphere
     *            the patient's left eye sphere
     * @param rightSphere
     *            the patient's right eye sphere
     * @param leftCylinder
     *            the patient's left eye cylinder
     * @param rightCylinder
     *            the patient's right eye cylinder
     * @param leftAxis
     *            the patient's left eye axis
     * @param rightAxis
     *            the patient's right eye axis
     * @param surgeryType
     *            the surgery type
     */
    @And ( "^The HCP enters the eye metrics with left eye visual acuity (.+), right eye visual acuity (.+), left eye sphere (.+), right eye sphere (.+), left eye cylinder (.+), right eye cylinder (.+), left eye axis (.+), right eye axis (.+), surgery type (.+)$" )
    public void addEyeMetricsSurgery ( final String leftAcuity, final String rightAcuity, final String leftSphere,
            final String rightSphere, final String leftCylinder, final String rightCylinder, final String leftAxis,
            final String rightAxis, final String surgeryType ) {
        waitForAngular();

        driver.findElement( By.name( "VAL" ) ).clear();
        driver.findElement( By.name( "VAL" ) ).sendKeys( leftAcuity );

        driver.findElement( By.name( "VAR" ) ).clear();
        driver.findElement( By.name( "VAR" ) ).sendKeys( rightAcuity );

        driver.findElement( By.name( "SL" ) ).clear();
        driver.findElement( By.name( "SL" ) ).sendKeys( leftSphere );

        driver.findElement( By.name( "SR" ) ).clear();
        driver.findElement( By.name( "SR" ) ).sendKeys( rightSphere );

        driver.findElement( By.name( "CL" ) ).clear();
        driver.findElement( By.name( "CL" ) ).sendKeys( leftCylinder );

        driver.findElement( By.name( "CR" ) ).clear();
        driver.findElement( By.name( "CR" ) ).sendKeys( rightCylinder );

        driver.findElement( By.name( "AL" ) ).clear();
        driver.findElement( By.name( "AL" ) ).sendKeys( leftAxis );

        driver.findElement( By.name( "AR" ) ).clear();
        driver.findElement( By.name( "AR" ) ).sendKeys( rightAxis );

        final WebElement surgeryElement = driver
                .findElement( By.cssSelector( "input[value=\"" + surgeryType + "\"]" ) );
        surgeryElement.click();
    }

    /**
     * Enters the visual acuity fields into the page
     * 
     * @param leftAcuity
     *            the patient's left eye visual acuity
     * @param rightAcuity
     *            the patient's right eye visual acuity
     * @param leftSphere
     *            the patient's left eye sphere
     * @param rightSphere
     *            the patient's right eye sphere
     * @param leftCylinder
     *            the patient's left eye cylinder
     * @param rightCylinder
     *            the patient's right eye cylinder
     * @param leftAxis
     *            the patient's left eye axis
     * @param rightAxis
     *            the patient's right eye axis
     * @param diagnoses
     *            the diagnosis for the patient
     */
    @And ( "^The HCP enters the eye metrics with left eye visual acuity (.+), right eye visual acuity (.+), left eye sphere (.+), right eye sphere (.+), left eye cylinder (.+), right eye cylinder (.+), left eye axis (.+), right eye axis (.+), and diagnoses (.+)$" )
    public void addEyeMetricsVisit ( final String leftAcuity, final String rightAcuity, final String leftSphere,
            final String rightSphere, final String leftCylinder, final String rightCylinder, final String leftAxis,
            final String rightAxis, final String diagnoses ) {
        waitForAngular();

        driver.findElement( By.name( "VAL" ) ).clear();
        driver.findElement( By.name( "VAL" ) ).sendKeys( leftAcuity );

        driver.findElement( By.name( "VAR" ) ).clear();
        driver.findElement( By.name( "VAR" ) ).sendKeys( rightAcuity );

        driver.findElement( By.name( "SL" ) ).clear();
        driver.findElement( By.name( "SL" ) ).sendKeys( leftSphere );

        driver.findElement( By.name( "SR" ) ).clear();
        driver.findElement( By.name( "SR" ) ).sendKeys( rightSphere );

        driver.findElement( By.name( "CL" ) ).clear();
        driver.findElement( By.name( "CL" ) ).sendKeys( leftCylinder );

        driver.findElement( By.name( "CR" ) ).clear();
        driver.findElement( By.name( "CR" ) ).sendKeys( rightCylinder );

        driver.findElement( By.name( "AL" ) ).clear();
        driver.findElement( By.name( "AL" ) ).sendKeys( leftAxis );

        driver.findElement( By.name( "AR" ) ).clear();
        driver.findElement( By.name( "AR" ) ).sendKeys( rightAxis );

        driver.findElement( By.name( diagnoses ) ).click();
        final WebElement diagnosisButton = driver.findElement( By.name( "fillEyeDiagnosis" ) );
        diagnosisButton.click();
    }

    /**
     * Method to enter the notes into the page
     * 
     * @param notes
     *            the notes for the visit
     */
    @And ( "^The HCP enters notes (.+)$" )
    public void addOfficeVisitNotes ( final String notes ) {
        waitForAngular();

        driver.findElement( By.name( "notes" ) ).clear();
        driver.findElement( By.name( "notes" ) ).sendKeys( notes );
    }

    /**
     * Method to submit the office visit
     */
    @And ( "^The HCP submits the ophthalmology office visit$" )
    public void submitOfficeVisit () {
        waitForAngular();
        driver.findElement( By.name( "submit" ) ).click();
    }

    /**
     * Method to check if the documentation was successful
     */
    @Then ( "^The ophthalmology office visit is documented successfully$" )
    public void documentedSuccessfully () {
        waitForAngular();

        // confirm that the message is displayed
        try {
            driver.findElement( By.name( "success" ) ).getText().contains( "Office visit created successfully" );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Method to check if the documentation failed
     */
    @Then ( "^The ophthalmology office visit is not submitted$" )
    public void notSubmitted () {
        waitForAngular();

        // confirm that the error message is displayed
        try {
            if ( driver.findElement( By.name( "success" ) ).getText().contains( "Office visit created successfully" ) ) {
                fail();
            }
        }
        catch ( final Exception e ) {
        }

    }

    /**
     * Method to check if the log was updated for surgeries
     */
    @Then ( "The log is updated stating that the surgery was documented" )
    public void logSurgery () {
        waitForAngular();
        driver.get( baseUrl );
        final WebDriverWait wait = new WebDriverWait( driver, 20 );
        wait.until( ExpectedConditions.titleContains( "HCP Home" ) );
        assertEquals( "iTrust2: HCP Home", driver.getTitle() );
        wait.until( ExpectedConditions.elementToBeClickable( By.name( "transactionTypeCell" ) ) );
        assertTextPresent( "Create ophthalmology surgery for patient" );
    }

    /**
     * Method to check if the log was updated for oph office visits
     */
    @Then ( "The log is updated stating that the oph office visit was documented" )
    public void logOPHVisit () {
        waitForAngular();
        driver.get( baseUrl );
        final WebDriverWait wait = new WebDriverWait( driver, 20 );
        wait.until( ExpectedConditions.titleContains( "HCP Home" ) );
        assertEquals( "iTrust2: HCP Home", driver.getTitle() );
        wait.until( ExpectedConditions.elementToBeClickable( By.name( "transactionTypeCell" ) ) );
        assertTextPresent( "Create general ophthalmology visit for patient" );

    }

}
