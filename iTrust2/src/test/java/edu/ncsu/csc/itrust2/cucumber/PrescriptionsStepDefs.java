package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class PrescriptionsStepDefs extends CucumberTest {

    private static final String BASE_URL  = "http://localhost:8080/iTrust2/";
    private static final String VISIT_URL = BASE_URL + "hcp/documentOfficeVisit.html";
    private static final String VIEW_URL  = BASE_URL + "patient/officeVisit/viewPrescriptions.html";
    private static final String DRUG_URL  = BASE_URL + "admin/drugs.html";

    private final String        baseUrl   = "http://localhost:8080/iTrust2";

    private String getUserName ( final String first, final String last ) {
        return first.substring( 0, 1 ).toLowerCase() + last.toLowerCase();
    }

    private void enterValue ( final String name, final String value ) {
        final WebElement field = driver.findElement( By.name( name ) );
        field.clear();
        field.sendKeys( String.valueOf( value ) );
    }

    private void selectItem ( final String name, final String value ) {
        final By selector = By.cssSelector( "input[name='" + name + "'][value='" + value + "']" );
        waitForAngular();
        final WebElement element = driver.findElement( selector );
        element.click();
    }

    private void selectName ( final String name ) {
        final WebElement element = driver.findElement( By.cssSelector( "input[name='" + name + "']" ) );
        element.click();
    }

    @Given ( "I have logged in with username: (.+)" )
    public void login ( final String username ) {
        attemptLogout();

        driver.get( baseUrl );

        enterValue( "username", username );
        enterValue( "password", "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    @When ( "I start documenting an office visit for the patient with name: (.+) (.+) and date of birth: (.+)" )
    public void startOfficeVisit ( final String firstName, final String lastName, final String dob ) {

        driver.get( VISIT_URL );
        final String patient = getUserName( firstName, lastName );

        try {
            Prescription.getForPatient( patient ).forEach( e -> e.delete() );
        }
        catch ( final Exception e ) {
            /* Ignored */
        }
        try {
            Diagnosis.getForPatient( User.getByName( patient ) ).forEach( e -> e.delete() );
        }
        catch ( final Exception e ) {
            /* Ignored */
        }
        try {
            OfficeVisit.getForPatient( patient ).forEach( e -> e.delete() );
        }
        catch ( final Exception e ) {
            /* Ignored */
        }

        waitForAngular();
        selectItem( "name", patient );
    }

    @When ( "fill in the office visit with date: (.+), hospital: (.+), notes: (.*), weight: (.+), height: (.+), blood pressure: (.+), household smoking status: (.+), patient smoking status: (.+), hdl: (.+), ldl: (.+), and triglycerides: (.+)" )
    public void fillOfficeVisitForm ( final String date, final String hospital, final String notes, final String weight,
            final String height, final String bloodPressure, final String hss, final String pss, final String hdl,
            final String ldl, final String triglycerides ) {

        waitForAngular();

        enterValue( "date", date );
        enterValue( "time", "10:10 AM" );
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementsByName('hospital')[0].click();" );

        waitForAngular();
        enterValue( "notes", notes );
        enterValue( "weight", weight );
        enterValue( "height", height );
        enterValue( "systolic", bloodPressure.split( "/" )[0] );
        enterValue( "diastolic", bloodPressure.split( "/" )[1] );
        selectItem( "houseSmokingStatus", hss );
        selectItem( "patientSmokingStatus", pss );
        enterValue( "hdl", hdl );
        enterValue( "ldl", ldl );
        enterValue( "tri", triglycerides );
    }

    @When ( "add a prescription for (.+) with a dosage of (.+) starting on (.+) and ending on (.+) with (.+) renewals" )
    public void addPrescription ( final String drug, final String dosage, final String startDate, final String endDate,
            final String renewals ) {
        waitForAngular();
        enterValue( "dosageEntry", dosage );
        enterValue( "startEntry", startDate );
        enterValue( "endEntry", endDate );
        enterValue( "renewalEntry", renewals );
        selectName( drug );
        driver.findElement( By.name( "fillPrescription" ) ).click();
        assertEquals( "", driver.findElement( By.name( "errorMsg" ) ).getText() );
    }

    @When ( "submit the office visit" )
    public void submitOfficeVisit () {
        driver.findElement( By.name( "submit" ) ).click();
        waitForAngular();
    }

    @Then ( "A message indicates the visit was submitted successfully" )
    public void officeVisitSuccessful () {
        waitForAngular();
        final WebElement msg = driver.findElement( By.name( "success" ) );
        assertEquals( "Office visit created successfully", msg.getText() );
    }

    @When ( "I choose to view my prescriptions" )
    public void viewPrescriptions () {
        driver.get( VIEW_URL );
    }

    @Then ( "I see a prescription for (.+) with a dosage of (.+) starting on (.+) and ending on (.+) with (.+) renewals" )
    public void prescriptionVisible ( final String drug, final String dosage, final String startDate,
            final String endDate, final String renewals ) {
        waitForAngular();
        final List<WebElement> rows = driver.findElements( By.name( "prescriptionTableRow" ) );

        List<WebElement> data = null;
        for ( final WebElement r : rows ) {
            if ( r.getText().contains( drug ) ) {
                waitForAngular();
                data = r.findElements( By.tagName( "td" ) );
                break;
            }
        }

        assertEquals( drug, data.get( 0 ).getText() );
        assertEquals( dosage, data.get( 1 ).getText() );
        assertEquals( renewals, data.get( 4 ).getText() );
    }

    @When ( "I choose to add a new drug" )
    public void addDrug () {
        driver.get( DRUG_URL );
    }

    @When ( "submit the values for NDC (.+), name (.+), and description (.*)" )
    public void submitDrug ( final String ndc, final String name, final String description )
            throws InterruptedException {

        waitForAngular();
        assertEquals( "Admin Manage Drugs", driver.findElement( By.tagName( "h3" ) ).getText() );

        waitForAngular();
        enterValue( "drug", name );
        enterValue( "code", ndc );
        enterValue( "description", description );
        driver.findElement( By.name( "submit" ) ).click();
    }

    @Then ( "the drug (.+) is successfully added to the system" )
    public void drugSuccessful ( final String drug ) throws InterruptedException {
        waitForAngular();
        assertEquals( "", driver.findElement( By.id( "errP" ) ).getText() );

        for ( final WebElement r : driver.findElements( By.name( "drugTableRow" ) ) ) {
            if ( r.getText().contains( drug ) ) {
                r.findElement( By.name( "deleteDrug" ) ).click();
            }
        }
        waitForAngular();

        try {
            assertFalse( driver.findElement( By.tagName( "body" ) ).getText().contains( drug ) );

        }
        catch ( final Exception e ) {
            fail();
        }
    }

}
