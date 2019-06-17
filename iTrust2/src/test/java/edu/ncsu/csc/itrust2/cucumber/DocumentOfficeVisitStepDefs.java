package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.GeneralCheckup;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class DocumentOfficeVisitStepDefs extends CucumberTest {

    static {
        java.util.logging.Logger.getLogger( "com.gargoylesoftware" ).setLevel( Level.OFF );
    }

    private final String baseUrl      = "http:localhost:8080/iTrust2";

    private final String hospitalName = "Office Visit Hospital" + ( new Random() ).nextInt();
    BasicHealthMetrics   expectedBhm;

    @Given ( "The required facilities exist" )
    public void personnelExists () throws Exception {
        attemptLogout();

        GeneralCheckup.deleteAll();
        DomainObject.deleteAll( BasicHealthMetrics.class );

        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users

        /* Make sure we create a Hospital and Patient record */

        final Hospital hospital = new Hospital( hospitalName, "Bialystok", "10101", State.NJ.toString() );
        hospital.save();

        /* Create patient record */

        final Patient patient = new Patient();
        patient.setSelf( User.getByName( "patient" ) );
        patient.setFirstName( "Karl" );
        patient.setLastName( "Liebknecht" );
        patient.setEmail( "karl_liebknecht@mail.de" );
        patient.setAddress1( "Karl Liebknecht Haus. Alexanderplatz" );
        patient.setCity( "Berlin" );
        patient.setState( State.DE );
        patient.setZip( "91505" );
        patient.setPhone( "123-456-7890" );
        patient.setDateOfBirth( LocalDate.parse( "1871-08-13" ) );

        patient.save();

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

    @When ( "I log in to iTrust2 as a HCP" )
    public void loginAsHCP () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        waitForAngular();
    }

    @When ( "I navigate to the Document Office Visit page" )
    public void navigateDocumentOV () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );
        waitForAngular();
    }

    @When ( "^I fill in information on the office visit$" )
    public void documentOV () {
        waitForAngular();

        final WebElement notes = driver.findElement( By.name( "notes" ) );
        notes.clear();
        notes.sendKeys( "Patient appears pretty much alive" );

        final WebElement patient = driver.findElement( By.name( "name" ) );
        patient.click();
        final WebElement type = driver.findElement( By.name( "GENERAL_CHECKUP" ) );
        type.click();

        final WebElement hospital = driver.findElement( By.name( "hospital" ) );
        hospital.click();

        fillInDateTime( "date", "12/19/2027", "time", "9:30 AM" );

        waitForAngular();

        final WebElement heightElement = driver.findElement( By.name( "height" ) );
        heightElement.clear();
        heightElement.sendKeys( "120" );

        final WebElement weightElement = driver.findElement( By.name( "weight" ) );
        weightElement.clear();
        weightElement.sendKeys( "120" );

        final WebElement systolicElement = driver.findElement( By.name( "systolic" ) );
        systolicElement.clear();
        systolicElement.sendKeys( "100" );

        final WebElement diastolicElement = driver.findElement( By.name( "diastolic" ) );
        diastolicElement.clear();
        diastolicElement.sendKeys( "100" );

        final WebElement hdlElement = driver.findElement( By.name( "hdl" ) );
        hdlElement.clear();
        hdlElement.sendKeys( "90" );

        final WebElement ldlElement = driver.findElement( By.name( "ldl" ) );
        ldlElement.clear();
        ldlElement.sendKeys( "100" );

        final WebElement triElement = driver.findElement( By.name( "tri" ) );
        triElement.clear();
        triElement.sendKeys( "100" );

        final WebElement houseSmokeElement = driver.findElement(
                By.cssSelector( "input[value=\"" + HouseholdSmokingStatus.NONSMOKING.toString() + "\"]" ) );
        houseSmokeElement.click();

        final WebElement patientSmokeElement = driver
                .findElement( By.cssSelector( "input[value=\"" + PatientSmokingStatus.NEVER.toString() + "\"]" ) );
        patientSmokeElement.click();

        driver.findElement( By.name( "submit" ) ).click();
    }

    @When ( "^I fill in information on the office visit with leading zeroes$" )
    public void documentOVZeroes () {
        waitForAngular();

        final WebElement notes = driver.findElement( By.name( "notes" ) );
        notes.clear();
        notes.sendKeys( "Patient appears pretty much alive" );

        final WebElement patient = driver.findElement( By.name( "name" ) );
        patient.click();
        final WebElement type = driver.findElement( By.name( "GENERAL_CHECKUP" ) );
        type.click();

        final WebElement hospital = driver.findElement( By.name( "hospital" ) );
        hospital.click();

        fillInDateTime( "date", "12/19/2027", "time", "9:30 AM" );

        waitForAngular();

        final WebElement heightElement = driver.findElement( By.name( "height" ) );
        heightElement.clear();
        heightElement.sendKeys( "0120" );

        final WebElement weightElement = driver.findElement( By.name( "weight" ) );
        weightElement.clear();
        weightElement.sendKeys( "0120" );

        final WebElement systolicElement = driver.findElement( By.name( "systolic" ) );
        systolicElement.clear();
        systolicElement.sendKeys( "0100" );

        final WebElement diastolicElement = driver.findElement( By.name( "diastolic" ) );
        diastolicElement.clear();
        diastolicElement.sendKeys( "0100" );

        final WebElement hdlElement = driver.findElement( By.name( "hdl" ) );
        hdlElement.clear();
        hdlElement.sendKeys( "090" );

        final WebElement ldlElement = driver.findElement( By.name( "ldl" ) );
        ldlElement.clear();
        ldlElement.sendKeys( "0100" );

        final WebElement triElement = driver.findElement( By.name( "tri" ) );
        triElement.clear();
        triElement.sendKeys( "0100" );

        final WebElement houseSmokeElement = driver.findElement(
                By.cssSelector( "input[value=\"" + HouseholdSmokingStatus.NONSMOKING.toString() + "\"]" ) );
        houseSmokeElement.click();

        final WebElement patientSmokeElement = driver
                .findElement( By.cssSelector( "input[value=\"" + PatientSmokingStatus.NEVER.toString() + "\"]" ) );
        patientSmokeElement.click();

        driver.findElement( By.name( "submit" ) ).click();
    }

    @Then ( "The office visit is documented successfully" )
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
     * Ensures that the correct health metrics have been entered
     *
     * @throws InterruptedException
     */
    @Then ( "The basic health metrics for the infant are correct" )
    public void healthMetricsCorrectInfant () throws InterruptedException {
        BasicHealthMetrics actualBhm = null;
        for ( int i = 1; i <= 10; i++ ) {
            try {
                actualBhm = BasicHealthMetrics.getBasicHealthMetrics().get( 0 );
                break;
            }
            catch ( final Exception e ) {
                if ( i == 10 && actualBhm == null ) {
                    fail( "Could not get basic health metrics out of database" );
                }
                waitForAngular();
            }
        }
        assertEquals( expectedBhm.getWeight(), actualBhm.getWeight() );
        assertEquals( expectedBhm.getHeight(), actualBhm.getHeight() );
        assertEquals( expectedBhm.getHeadCircumference(), actualBhm.getHeadCircumference() );
        assertEquals( expectedBhm.getHouseSmokingStatus(), actualBhm.getHouseSmokingStatus() );
    }

    /**
     * Ensures that the correct health metrics have been entered
     *
     * @throws InterruptedException
     */
    @Then ( "The basic health metrics for the child are correct" )
    public void healthMetricsCorrectChild () throws InterruptedException {
        BasicHealthMetrics actualBhm = null;
        for ( int i = 1; i <= 10; i++ ) {
            try {
                actualBhm = BasicHealthMetrics.getBasicHealthMetrics().get( 0 );
                break;
            }
            catch ( final Exception e ) {
                if ( i == 10 && actualBhm == null ) {
                    fail( "Could not get basic health metrics out of database" );
                }
                waitForAngular();
            }
        }
        assertEquals( expectedBhm.getWeight(), actualBhm.getWeight() );
        assertEquals( expectedBhm.getHeight(), actualBhm.getHeight() );
        assertEquals( expectedBhm.getSystolic(), actualBhm.getSystolic() );
        assertEquals( expectedBhm.getDiastolic(), actualBhm.getDiastolic() );
        assertEquals( expectedBhm.getHouseSmokingStatus(), actualBhm.getHouseSmokingStatus() );
    }

    /**
     * Ensures that the correct health metrics have been entered
     *
     * @throws InterruptedException
     */
    @Then ( "The basic health metrics for the adult are correct" )
    public void healthMetricsCorrectAdult () throws InterruptedException {
        BasicHealthMetrics actualBhm = null;
        for ( int i = 1; i <= 10; i++ ) {
            try {
                actualBhm = BasicHealthMetrics.getBasicHealthMetrics().get( 0 );
                break;
            }
            catch ( final Exception e ) {
                if ( i == 10 && actualBhm == null ) {
                    fail( "Could not get basic health metrics out of database" );
                }
                waitForAngular();
            }
        }
        assertEquals( expectedBhm.getWeight(), actualBhm.getWeight() );
        assertEquals( expectedBhm.getHeight(), actualBhm.getHeight() );
        assertEquals( expectedBhm.getSystolic(), actualBhm.getSystolic() );
        assertEquals( expectedBhm.getDiastolic(), actualBhm.getDiastolic() );
        assertEquals( expectedBhm.getHouseSmokingStatus(), actualBhm.getHouseSmokingStatus() );
        assertEquals( expectedBhm.getPatientSmokingStatus(), actualBhm.getPatientSmokingStatus() );
        assertEquals( expectedBhm.getHdl(), actualBhm.getHdl() );
        assertEquals( expectedBhm.getLdl(), actualBhm.getLdl() );
        assertEquals( expectedBhm.getTri(), actualBhm.getTri() );
    }

    /**
     * Ensures that the office visit was not recorded.
     */
    @Then ( "The office visit is not documented" )
    public void notDocumented () {
        waitForAngular();

        final List<BasicHealthMetrics> list = BasicHealthMetrics.getBasicHealthMetrics();
        assertTrue( 0 == list.size() );
    }

    /**
     * Ensures that a patient exists with the given name and birthday
     *
     * @param name
     *            The name of the patient.
     * @param birthday
     *            The birthday of the patient.
     * @throws ParseException
     */
    @Given ( "^A patient exists with name: (.+) and date of birth: (.+)$" )
    public void patientExistsWithName ( final String name, final String birthday ) throws ParseException {
        attemptLogout();

        final Patient patient = new Patient();
        patient.setSelf( User.getByName( "patient" ) );

        patient.setFirstName( name.split( " " )[0] );
        patient.setLastName( name.split( " " )[1] );
        patient.setEmail( "email@mail.com" );
        patient.setAddress1( "address place. address" );
        patient.setCity( "citytown" );
        patient.setState( State.CA );
        patient.setZip( "91505" );
        patient.setPhone( "123-456-7890" );
        patient.setDateOfBirth( LocalDate.parse( birthday, DateTimeFormatter.ofPattern( "MM/dd/yyyy" ) ) );

        patient.save();

    }

    /**
     * Documents an office visit with specific information.
     *
     * @param dateString
     *            The current date.
     * @param weightString
     *            The weight of the patient.
     * @param lengthString
     *            The length of the patient.
     * @param headString
     *            The head circumference of the patient.
     * @param smokingStatus
     *            The smoking status of the patient's household.
     * @param note
     *            The note that the doctor includes.
     * @throws InterruptedException
     */
    @When ( "^I fill in information on the office visit for an infant with date: (.+), weight: (.+), length: (.+), head circumference: (.+), household smoking status: (.+), and note: (.+)$" )
    public void documentOVWithSpecificInformation ( final String dateString, final String weightString,
            final String lengthString, final String headString, final String smokingStatus, final String note )
            throws InterruptedException {

        waitForAngular();
        final WebElement notes = driver.findElement( By.name( "notes" ) );
        notes.clear();
        notes.sendKeys( note );

        waitForAngular();
        final WebElement patient = driver.findElement( By.cssSelector( "input[value=\"patient\"]" ) );
        patient.click();

        waitForAngular();
        final WebElement type = driver.findElement( By.name( "GENERAL_CHECKUP" ) );
        type.click();

        waitForAngular();
        final WebElement hospital = driver.findElement( By.name( "hospital" ) );
        hospital.click();

        fillInDateTime( "date", dateString, "time", "9:30 AM" );

        expectedBhm = new BasicHealthMetrics();

        waitForAngular();
        final WebElement head = driver.findElement( By.name( "head" ) );
        head.clear();
        head.sendKeys( headString );
        try {
            expectedBhm.setHeadCircumference( Float.parseFloat( headString ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        final WebElement heightLength = driver.findElement( By.name( "height" ) );
        heightLength.clear();
        heightLength.sendKeys( lengthString );
        try {
            expectedBhm.setHeight( Float.parseFloat( lengthString ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        final WebElement weight = driver.findElement( By.name( "weight" ) );
        weight.clear();
        weight.sendKeys( weightString );
        try {
            expectedBhm.setWeight( Float.parseFloat( weightString ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }
        try {
            final WebElement smoking = driver.findElement( By.cssSelector(
                    "input[value=\"" + HouseholdSmokingStatus.getName( Integer.parseInt( smokingStatus ) ) + "\"]" ) );
            smoking.click();
            expectedBhm.setHouseSmokingStatus( HouseholdSmokingStatus.parseValue( Integer.parseInt( smokingStatus ) ) );
        }
        catch ( final Exception e ) {
            /*
             * This means that the element wasn't found, which is expected if we
             * enter an invalid value (as one of the test cases does).
             * Intentionally ignoring.
             */
        }
        waitForAngular();
        final WebElement submit = driver.findElement( By.name( "submit" ) );
        submit.click();
    }

    /**
     * Documents an office visit with specific information for patients between
     * 3 and 12
     *
     * @param dateString
     *            The current date.
     * @param weightString
     *            The weight of the patient.
     * @param heightString
     *            The height of the patient.
     * @param sys
     *            The systolic blood pressure of the patient.
     * @param dia
     *            The diastolic blood pressure of the patient.
     * @param smokingStatus
     *            The smoking status of the patient's household.
     * @param note
     *            The note that the doctor includes.
     * @throws InterruptedException
     */
    @When ( "^I fill in information on the office visit for patients of age 3 to 12 with date: (.+), weight: (.+), height: (.+), systolic blood pressure: (.+), diastolic blood pressure: (.+), household smoking status: (.+), and note: (.+)$" )
    public void documentOVWithSpecificInformation3To12 ( final String dateString, final String weightString,
            final String heightString, final String sys, final String dia, final String smokingStatus,
            final String note ) throws InterruptedException {

        waitForAngular();
        final WebElement notes = driver.findElement( By.name( "notes" ) );
        notes.clear();
        notes.sendKeys( note );

        waitForAngular();
        final WebElement patient = driver.findElement( By.cssSelector( "input[value=\"patient\"]" ) );
        patient.click();

        waitForAngular();
        final WebElement type = driver.findElement( By.name( "GENERAL_CHECKUP" ) );
        type.click();

        waitForAngular();
        final WebElement hospital = driver.findElement( By.name( "hospital" ) );
        hospital.click();

        fillInDateTime( "date", dateString, "time", "9:30 AM");

        expectedBhm = new BasicHealthMetrics();

        waitForAngular();
        final WebElement sysElem = driver.findElement( By.name( "systolic" ) );
        sysElem.clear();
        sysElem.sendKeys( sys );
        try {
            expectedBhm.setSystolic( Integer.parseInt( sys ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        waitForAngular();
        final WebElement diaElem = driver.findElement( By.name( "diastolic" ) );
        diaElem.clear();
        diaElem.sendKeys( dia );
        try {
            expectedBhm.setDiastolic( Integer.parseInt( dia ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        final WebElement heightLength = driver.findElement( By.name( "height" ) );
        heightLength.clear();
        heightLength.sendKeys( heightString );
        try {
            expectedBhm.setHeight( Float.parseFloat( heightString ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        final WebElement weight = driver.findElement( By.name( "weight" ) );
        weight.clear();
        weight.sendKeys( weightString );
        try {
            expectedBhm.setWeight( Float.parseFloat( weightString ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }
        try {
            final WebElement smoking = driver.findElement( By.cssSelector(
                    "input[value=\"" + HouseholdSmokingStatus.getName( Integer.parseInt( smokingStatus ) ) + "\"]" ) );
            smoking.click();
            expectedBhm.setHouseSmokingStatus( HouseholdSmokingStatus.parseValue( Integer.parseInt( smokingStatus ) ) );
        }
        catch ( final Exception e ) {
            /*
             * This means that the element wasn't found, which is expected if we
             * enter an invalid value (as one of the test cases does).
             * Intentionally ignoring.
             */
        }
        waitForAngular();
        final WebElement submit = driver.findElement( By.name( "submit" ) );
        submit.click();
    }

    /**
     * Documents an office visit with specific information for patients 12 and
     * over.
     *
     * @param dateString
     *            The current date.
     * @param weightString
     *            The weight of the patient.
     * @param heightString
     *            The height of the patient.
     * @param sys
     *            The systolic blood pressure of the patient.
     * @param dia
     *            The diastolic blood pressure of the patient.
     * @param houseSmoke
     *            The smoking status of the patient's household.
     * @param patientSmoke
     *            The smoking status of the patient.
     * @param hdl
     *            The patient's HDL levels.
     * @param ldl
     *            The patient's LDL levels.
     * @param tri
     *            The patient's triglycerides levels.
     * @param note
     *            The note entered by the HCP
     * @throws InterruptedException
     */
    @When ( "^I fill in information on the office visit for people 12 and over with date: (.+), weight: (.+), height: (.+), systolic blood pressure: (.+), diastolic blood pressure: (.+), household smoking status: (.+), patient smoking status: (.+), HDL cholesterol: (.+), LDL cholesterol: (.+), triglycerides: (.+), and note: (.+)$" )
    public void documentOVWithSpecificInformation12Over ( final String dateString, final String weightString,
            final String heightString, final String sys, final String dia, final String houseSmoke,
            final String patientSmoke, final String hdl, final String ldl, final String tri, final String note )
            throws InterruptedException {
        waitForAngular();
        final WebElement notes = driver.findElement( By.name( "notes" ) );
        notes.clear();
        notes.sendKeys( note );

        waitForAngular();
        final WebElement patient = driver.findElement( By.cssSelector( "input[value=\"patient\"]" ) );
        patient.click();

        waitForAngular();
        final WebElement type = driver.findElement( By.name( "GENERAL_CHECKUP" ) );
        type.click();

        waitForAngular();
        final WebElement hospital = driver.findElement( By.name( "hospital" ) );
        hospital.click();
        
        fillInDateTime( "date", dateString, "time", "9:30 AM");

        expectedBhm = new BasicHealthMetrics();

        waitForAngular();
        final WebElement sysElem = driver.findElement( By.name( "systolic" ) );
        sysElem.clear();
        sysElem.sendKeys( sys );
        try {
            expectedBhm.setSystolic( Integer.parseInt( sys ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        waitForAngular();
        final WebElement diaElem = driver.findElement( By.name( "diastolic" ) );
        diaElem.clear();
        diaElem.sendKeys( dia );
        try {
            expectedBhm.setDiastolic( Integer.parseInt( dia ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        final WebElement heightLength = driver.findElement( By.name( "height" ) );
        heightLength.clear();
        heightLength.sendKeys( heightString );
        try {
            expectedBhm.setHeight( Float.parseFloat( heightString ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        final WebElement weight = driver.findElement( By.name( "weight" ) );
        weight.clear();
        weight.sendKeys( weightString );
        try {
            expectedBhm.setWeight( Float.parseFloat( weightString ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }
        try {
            final WebElement smoking = driver.findElement( By.cssSelector(
                    "input[value=\"" + HouseholdSmokingStatus.getName( Integer.parseInt( houseSmoke ) ) + "\"]" ) );
            smoking.click();
            expectedBhm.setHouseSmokingStatus( HouseholdSmokingStatus.parseValue( Integer.parseInt( houseSmoke ) ) );
        }
        catch ( final Exception e ) {
            /*
             * This means that the element wasn't found, which is expected if we
             * enter an invalid value (as one of the test cases does).
             * Intentionally ignoring.
             */
        }
        try {
            final WebElement smoking = driver.findElement( By.cssSelector(
                    "input[value=\"" + PatientSmokingStatus.getName( Integer.parseInt( patientSmoke ) ) + "\"]" ) );
            smoking.click();
            expectedBhm.setPatientSmokingStatus( PatientSmokingStatus.parseValue( Integer.parseInt( patientSmoke ) ) );
        }
        catch ( final Exception e ) {
            /*
             * This means that the element wasn't found, which is expected if we
             * enter an invalid value (as one of the test cases does).
             * Intentionally ignoring.
             */
        }

        waitForAngular();
        final WebElement hdlElem = driver.findElement( By.name( "hdl" ) );
        hdlElem.clear();
        hdlElem.sendKeys( hdl );
        try {
            expectedBhm.setHdl( Integer.parseInt( hdl ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        waitForAngular();
        final WebElement ldlElem = driver.findElement( By.name( "ldl" ) );
        ldlElem.clear();
        ldlElem.sendKeys( ldl );
        try {
            expectedBhm.setLdl( Integer.parseInt( ldl ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        waitForAngular();
        final WebElement triElem = driver.findElement( By.name( "tri" ) );
        triElem.clear();
        triElem.sendKeys( tri );
        try {
            expectedBhm.setTri( Integer.parseInt( tri ) );
        }
        catch ( final IllegalArgumentException e ) {
            /*
             * This means that the test data provided was intentionally invalid,
             * which is okay
             */
        }

        waitForAngular();
        final WebElement submit = driver.findElement( By.name( "submit" ) );
        submit.click();
    }
}
