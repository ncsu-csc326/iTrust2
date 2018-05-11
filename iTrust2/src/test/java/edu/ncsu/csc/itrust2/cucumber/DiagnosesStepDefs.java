package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class DiagnosesStepDefs {

    private static boolean initialized = false;

    private WebDriver      driver;
    private final String   baseUrl     = "http://localhost:8080/iTrust2";

    WebDriverWait          wait;

    @Before
    public void setup () {

        driver = new HtmlUnitDriver( true );
        wait = new WebDriverWait( driver, 5 );
    }

    @After
    public void tearDown () {
        driver.close();
    }

    private void setTextField ( final By byVal, final Object value ) {
        final WebElement elem = driver.findElement( byVal );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    @Given ( "The required diagnosis facilities exist" )
    public void requirementsExist () {
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
        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/DD/YYYY", Locale.ENGLISH );

        final Calendar time = Calendar.getInstance();
        time.setTime( sdf.parse( birthday ) );

        patient.setDateOfBirth( time );

        patient.save();

    }

    @When ( "I log into iTrust2 as an HCP" )
    public void hcpLogin () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

    }

    @When ( "I navigate to the Document Offive Visit Page" )
    public void navigateToOfficeVisit () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );
    }

    @When ( "I fill in information on the office visit with date: (.+), weight: (.+), height: (.+), systolic blood pressure: (.+), diastolic blood pressure: (.+), household smoking status: (.+), patient smoking status: (.+), HDL cholesterol: (.+), LDL cholesterol: (.+), triglycerides: (.+), diagnosis: (.+), diagnosis note: (.+), and visit note: (.+)" )
    public void fillInfo ( final String date, final String weight, final String height, final String sys,
            final String dia, final String householdSmoking, final String patientSmoking, final String hdl,
            final String ldl, final String triglycerides, final String diagnosis, final String diagnosisNote,
            final String note ) {

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "notes" ) ) );
        setTextField( By.name( "notes" ), note );
        driver.findElement( By.cssSelector( "input[type=radio][value=patient]" ) ).click();
        driver.findElement( By.name( "type" ) ).click();
        driver.findElement( By.name( "hospital" ) ).click();
        setTextField( By.name( "date" ), date );
        setTextField( By.name( "time" ), "9:30 AM" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "height" ) ) );
        setTextField( By.name( "height" ), height );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "weight" ) ) );
        setTextField( By.name( "weight" ), weight );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "systolic" ) ) );
        setTextField( By.name( "systolic" ), sys );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "diastolic" ) ) );
        setTextField( By.name( "diastolic" ), dia );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "hdl" ) ) );
        setTextField( By.name( "hdl" ), hdl );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "ldl" ) ) );
        setTextField( By.name( "ldl" ), ldl );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "tri" ) ) );
        setTextField( By.name( "tri" ), triglycerides );

        wait.until( ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector( "input[value=\"" + HouseholdSmokingStatus.NONSMOKING.toString() + "\"]" ) ) );
        final WebElement houseSmokeElement = driver.findElement(
                By.cssSelector( "input[value=\"" + HouseholdSmokingStatus.NONSMOKING.toString() + "\"]" ) );
        houseSmokeElement.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector( "input[value=\"" + PatientSmokingStatus.NEVER.toString() + "\"]" ) ) );
        final WebElement patientSmokeElement = driver
                .findElement( By.cssSelector( "input[value=\"" + PatientSmokingStatus.NEVER.toString() + "\"]" ) );
        patientSmokeElement.click();

        // add the diagnosis
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( diagnosis ) ) );
        driver.findElement( By.name( diagnosis ) ).click();
        setTextField( By.name( "notesEntry" ), diagnosisNote );
        driver.findElement( By.name( "fillDiagnosis" ) ).click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "submit" ) ) );
        driver.findElement( By.name( "submit" ) ).click();
    }

    @Then ( "The office visit is documented sucessfully" )
    public void visitSuccess () {
        try {
            wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "success" ),
                    "Office visit created successfully" ) );
        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "success" ) ).getText() );
        }
    }

    @When ( "I log into iTrust2 as a patient" )
    public void patientLogin () {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "patient" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    @When ( "I navigate to my past diagnoses" )
    public void patientNavigate () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewDiagnoses').click();" );
    }

    @Then ( "I see the list of my diagnoses" )
    public void seeList () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "diagnosis" ) ) );
    }

    @Then ( "The (.+), (.+), (.+), and (.+) are correct" )
    public void checkList ( final String date, final String hcp, final String description, final String note ) {
        final long time = System.currentTimeMillis();
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
        fail( "failed to find specified diagnosis" );
    }

    @When ( "I log into iTrust2 as an admin" )
    public void adminLogin () {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "admin" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    @When ( "I navigate to the list of diagnoses" )
    public void adminNavigate () {
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

        try {
            // waiting for angular to reassign DOM attributes from model
            Thread.sleep( 2000 );
        }
        catch ( final Exception e ) {
            // ignore, there might not be any to load
        }

        before = driver.findElements( By.name( "codeRow" ) ).stream().map( x -> x.getAttribute( "codeid" ) )
                .collect( Collectors.toList() );

        try {
            setTextField( By.name( "code" ), code );
            setTextField( By.name( "description" ), description );
            driver.findElement( By.name( "submit" ) ).click();
            try {
                wait.until( ExpectedConditions.or(
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
        after = driver.findElements( By.name( "codeRow" ) ).stream().map( x -> x.getAttribute( "codeid" ) )
                .collect( Collectors.toList() );
        after.removeAll( before );
        assertEquals( 1, after.size() );
    }

    @Then ( "The diagnosis info is correct" )
    public void verifyAddedDiagnosis () {
        // the one left in after is what we expect.
        final WebElement newRow = driver.findElements( By.name( "codeRow" ) ).stream()
                .filter( x -> x.getAttribute( "codeid" ).equals( after.get( 0 ) ) ).findFirst().get();
        assertEquals( expectedCode, newRow.findElement( By.name( "codeCell" ) ).getText() );
        assertEquals( expectedDescription, newRow.findElement( By.name( "descriptionCell" ) ).getText() );
    }

    @Then ( "The diagnosis is not added" )
    public void checkInvalidAdd () {
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
        // the one left in after is what we expect.
        final WebElement newRow = driver.findElements( By.name( "codeRow" ) ).stream()
                .filter( x -> x.getAttribute( "codeid" ).equals( after.get( 0 ) ) ).findFirst().get();
        newRow.findElement( By.tagName( "input" ) ).click();
        try {
            Thread.sleep( 2000 );
        }
        catch ( final Exception e ) {
            // ignore
        }
    }

    @Then ( "The code is deleted" )
    public void checkDelete () {
        final List<String> current = driver.findElements( By.name( "codeRow" ) ).stream()
                .map( x -> x.getAttribute( "codeid" ) ).collect( Collectors.toList() );
        assertFalse( current.contains( after.get( 0 ) ) );
    }
}
