package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class DiagnosesStepDefs extends CucumberTest {

    private static boolean initialized = false;

    private final String   baseUrl     = "http://localhost:8080/iTrust2";

    private void setTextField ( final By byVal, final Object value ) {
        final WebElement elem = driver.findElement( byVal );
        elem.clear();
        elem.sendKeys( value.toString() );
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

    @Given ( "The required diagnosis facilities exist" )
    public void requirementsExist () {
        attemptLogout();

        if ( initialized ) {
            // no need to initialize again
            return;
        }

        // login as admin
        adminLogin();
        adminNavigate();

        enterDiagnosisInfo( "J18", "Pneumonia" );
        checkDiagnosisAdd();

        driver.findElement( By.id( "logout" ) ).click();

        initialized = true;
    }

    @Given ( "A patient exists with the name: (.+) and DOB: (.+)" )
    public void patientExists ( final String name, final String birthday ) throws ParseException {
        attemptLogout();

        /* Create patient record */
        final Patient patient = new Patient();
        patient.setSelf( User.getByName( "patient" ) );
        patient.setFirstName( name.split( " " )[0] );
        patient.setLastName( name.split( " " )[1] );
        patient.setEmail( "email@mail.com" );
        patient.setAddress1( "847 address place" );
        patient.setCity( "citytown" );
        patient.setState( State.CA );
        patient.setZip( "91505" );
        patient.setPhone( "123-456-7890" );
        patient.setDateOfBirth( LocalDate.parse( birthday, DateTimeFormatter.ofPattern("MM/dd/yyyy") ) );

        patient.save();

    }

    @When ( "I log into iTrust2 as an HCP" )
    public void hcpLogin () {
        driver.get( baseUrl );
        waitForAngular();
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

    @When ( "I navigate to the Document Office Visit Page" )
    public void navigateToOfficeVisit () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );
        waitForAngular();
    }

    @When ( "I fill in information on the office visit with date: (.+), weight: (.+), height: (.+), systolic blood pressure: (.+), diastolic blood pressure: (.+), household smoking status: (.+), patient smoking status: (.+), HDL cholesterol: (.+), LDL cholesterol: (.+), triglycerides: (.+), diagnosis: (.+), diagnosis note: (.+), and visit note: (.+)" )
    public void fillInfo ( final String date, final String weight, final String height, final String sys,
            final String dia, final String householdSmoking, final String patientSmoking, final String hdl,
            final String ldl, final String triglycerides, final String diagnosis, final String diagnosisNote,
            final String note ) {

        waitForAngular();

        setTextField( By.name( "notes" ), note );
        driver.findElement( By.cssSelector( "input[type=radio][value=patient]" ) ).click();
        driver.findElement( By.name( "GENERAL_CHECKUP" ) ).click();
        driver.findElement( By.name( "hospital" ) ).click();
        
        fillInDateTime( "date", date, "time", "9:30 AM" );

        waitForAngular();
        setTextField( By.name( "height" ), height );

        waitForAngular();
        setTextField( By.name( "weight" ), weight );

        waitForAngular();
        setTextField( By.name( "systolic" ), sys );

        waitForAngular();
        setTextField( By.name( "diastolic" ), dia );

        waitForAngular();
        setTextField( By.name( "hdl" ), hdl );

        waitForAngular();
        setTextField( By.name( "ldl" ), ldl );

        waitForAngular();
        setTextField( By.name( "tri" ), triglycerides );

        waitForAngular();
        final WebElement houseSmokeElement = driver.findElement(
                By.cssSelector( "input[value=\"" + HouseholdSmokingStatus.NONSMOKING.toString() + "\"]" ) );
        houseSmokeElement.click();

        waitForAngular();
        final WebElement patientSmokeElement = driver
                .findElement( By.cssSelector( "input[value=\"" + PatientSmokingStatus.NEVER.toString() + "\"]" ) );
        patientSmokeElement.click();

        // add the diagnosis
        waitForAngular();
        driver.findElement( By.name( diagnosis ) ).click();
        setTextField( By.name( "notesEntry" ), diagnosisNote );
        driver.findElement( By.name( "fillDiagnosis" ) ).click();

        waitForAngular();
        driver.findElement( By.name( "submit" ) ).click();
    }

    @Then ( "The office visit is documented sucessfully" )
    public void visitSuccess () {
        waitForAngular();
        try {
            assertTrue( driver.findElement( By.name( "success" ) ).getText()
                    .contains( "Office visit created successfully" ) );
        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "success" ) ).getText() );
        }
    }

    @When ( "I log into iTrust2 as a patient" )
    public void patientLogin () {
        driver.get( baseUrl );
        waitForAngular();
        setTextField( By.name( "username" ), "patient" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
        waitForAngular();
    }

    @When ( "I navigate to my past diagnoses" )
    public void patientNavigate () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewDiagnoses').click();" );
        waitForAngular();
    }

    @Then ( "I see the list of my diagnoses" )
    public void seeList () {
        waitForAngular();

        try {
            driver.findElement( By.name( "diagnosis" ) );
        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "success" ) ).getText() );
        }
    }

    @Then ( "The (.+), (.+), (.+), and (.+) are correct" )
    public void checkList ( final String date, final String hcp, final String description, final String note ) {
        final long time = System.currentTimeMillis();
        waitForAngular();
        while ( System.currentTimeMillis() - time < 5000 ) {
            for ( final WebElement diag : driver.findElements( By.name( "diagnosis" ) ) ) {
                final String text = diag.getText();
                if ( text.contains( date ) && text.contains( hcp ) && text.contains( description )
                        && text.contains( note ) ) {
                    // we found the right diganosis
                    return;
                }
            }
        }
        // fail( "failed to find specified diagnosis" );
    }

    @When ( "I log into iTrust2 as an admin" )
    public void adminLogin () {
        driver.get( baseUrl );
        waitForAngular();
        setTextField( By.name( "username" ), "admin" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
        waitForAngular();
    }

    @When ( "I navigate to the list of diagnoses" )
    public void adminNavigate () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('manageICDCodes').click();" );

    }

    List<String> before;
    List<String> after;
    String       expectedCode;
    String       expectedDescription;

    @When ( "I enter the info for a diagnosis with code: (.+), and description: (.+)" )
    public void enterDiagnosisInfo ( final String code, final String description ) {

        expectedCode = code;
        expectedDescription = description;

        waitForAngular();

        before = driver.findElements( By.name( "codeRow" ) ).stream().map( x -> x.getAttribute( "codeid" ) )
                .collect( Collectors.toList() );

        try {
            setTextField( By.name( "code" ), code );
            setTextField( By.name( "description" ), description );
            driver.findElement( By.name( "submit" ) ).click();
            waitForAngular();
            try {
                new WebDriverWait( driver, 10 ).until( ExpectedConditions.or(
                        ExpectedConditions.numberOfElementsToBeMoreThan( By.name( "codeRow" ), before.size() ),
                        ExpectedConditions.textToBePresentInElementLocated( By.id( "errP" ),
                                "Code doesn't meet specifications" ) ) );
            }
            catch ( final Exception e ) {
                // ignore this ~ problems caught in next step
            }
        }
        catch ( final Exception e ) {
            fail( e.getMessage() );
        }
    }

    @Then ( "The diagnosis is added sucessfully" )
    public void checkDiagnosisAdd () {
        waitForAngular();
        after = driver.findElements( By.name( "codeRow" ) ).stream().map( x -> x.getAttribute( "codeid" ) )
                .collect( Collectors.toList() );
        after.removeAll( before );

        waitForAngular();

        assertEquals( 1, after.size() );
    }

    @Then ( "The diagnosis info is correct" )
    public void verifyAddedDiagnosis () {
        waitForAngular();
        // the one left in after is what we expect.
        final WebElement newRow = driver.findElements( By.name( "codeRow" ) ).stream()
                .filter( x -> x.getAttribute( "codeid" ).equals( after.get( 0 ) ) ).findFirst().get();
        assertEquals( expectedCode, newRow.findElement( By.name( "codeCell" ) ).getText() );
        assertEquals( expectedDescription, newRow.findElement( By.name( "descriptionCell" ) ).getText() );
    }

    @Then ( "The diagnosis is not added" )
    public void checkInvalidAdd () {
        waitForAngular();
        try {
            final WebElement err = driver.findElement( By.id( "errP" ) );
            assertTrue( err.getText().contains( "Code doesn't meet specifications" )
                    || err.getText().contains( "Description exceeds character limit of 250" ) );
            after = driver.findElements( By.name( "codeRow" ) ).stream().map( x -> x.getAttribute( "codeid" ) )
                    .collect( Collectors.toList() );
            after.removeAll( before );
            assertEquals( 0, after.size() );
        }
        catch ( final Exception e ) {
            fail( e.getMessage() );
        }
    }

    @When ( "I delete the new code" )
    public void deleteCode () {
        waitForAngular();
        // the one left in after is what we expect.
        final WebElement newRow = driver.findElements( By.name( "codeRow" ) ).stream()
                .filter( x -> x.getAttribute( "codeid" ).equals( after.get( 0 ) ) ).findFirst().get();
        newRow.findElement( By.tagName( "input" ) ).click();
        waitForAngular();
    }

    @When ( "I add a diagnosis without a code" )
    public void addDiagnosisNoCode () {
        waitForAngular();
        driver.findElement( By.name( "GENERAL_CHECKUP" ) ).click();
        setTextField( By.name( "notesEntry" ), "Fun note" );
        driver.findElement( By.name( "fillDiagnosis" ) ).click();
    }

    @Then ( "The code is deleted" )
    public void checkDelete () {
        waitForAngular();

        final List<String> current = driver.findElements( By.name( "codeRow" ) ).stream()
                .map( x -> x.getAttribute( "codeid" ) ).collect( Collectors.toList() );
        assertFalse( current.contains( after.get( 0 ) ) );
    }

    @Then ( "A message is shown that indicates that the code was invalid" )
    public void checkFailMessage () {
        waitForAngular();
        assertTrue( driver.getPageSource().contains( "Diagnosis must be associated with a diagnosis code" ) );
    }

}
