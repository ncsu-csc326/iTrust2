package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.Drug;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;

public class PrescriptionsStepDefs {

    private static final String BASE_URL  = "http://localhost:8080/iTrust2/";
    private static final String VISIT_URL = BASE_URL + "hcp/documentOfficeVisit.html";
    private static final String VIEW_URL  = BASE_URL + "patient/viewPrescriptions.html";
    private static final String DRUG_URL  = BASE_URL + "admin/drugs.html";

    private final WebDriver     driver    = new HtmlUnitDriver( true );
    private final String        baseUrl   = "http://localhost:8080/iTrust2";

    WebDriverWait               wait      = new WebDriverWait( driver, 5 );

    @Before
    public void setup () {
        final Hospital hosp = new Hospital( "General Hospital", "123 Main St", "12345", "NC" );
        hosp.save();

        final Drug d = new Drug();
        d.setCode( "1000-0001-10" );
        d.setName( "Quetiane Fumarate" );
        d.setDescription( "atypical antipsychotic and antidepressant" );
        d.save();

    }

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
        wait.until( ExpectedConditions.visibilityOfElementLocated( selector ) );
        final WebElement element = driver.findElement( selector );
        element.click();
    }

    private void selectName ( final String name ) {
        final WebElement element = driver.findElement( By.cssSelector( "input[name='" + name + "']" ) );
        element.click();
    }

    @Given ( "I have logged in with username: (.+)" )
    public void login ( final String username ) {
        driver.get( baseUrl );

        enterValue( "username", username );
        enterValue( "password", "123456" );
        driver.findElement( By.className( "btn" ) ).click();
    }

    @When ( "I start documenting an office visit for the patient with name: (.+) (.+) and date of birth: (.+)" )
    public void startOfficeVisit ( final String firstName, final String lastName, final String dob ) {
        driver.get( VISIT_URL );
        final String patient = getUserName( firstName, lastName );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.cssSelector( "[value='" + patient + "']" ) ) );
        selectItem( "name", patient );
    }

    @When ( "fill in the office visit with date: (.+), hospital: (.+), notes: (.*), weight: (.+), height: (.+), blood pressure: (.+), household smoking status: (.+), patient smoking status: (.+), hdl: (.+), ldl: (.+), and triglycerides: (.+)" )
    public void fillOfficeVisitForm ( final String date, final String hospital, final String notes, final String weight,
            final String height, final String bloodPressure, final String hss, final String pss, final String hdl,
            final String ldl, final String triglycerides ) {
        enterValue( "date", date );
        enterValue( "time", "10:10 AM" );
        selectItem( "hospital", hospital );
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
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "success" ), " " ) );
    }

    @Then ( "A message indicates the visit was submitted successfully" )
    public void officeVisitSuccessful () {
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
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.tagName( "body" ), drug ) );
        final List<WebElement> rows = driver.findElements( By.name( "prescriptionTableRow" ) );

        List<WebElement> data = null;
        for ( final WebElement r : rows ) {
            if ( r.getText().contains( drug ) ) {
                data = r.findElements( By.tagName( "td" ) );
                break;
            }
        }

        assertEquals( drug, data.get( 0 ).getText() );
        assertEquals( dosage, data.get( 1 ).getText() );
        assertEquals( startDate, data.get( 2 ).getText() );
        assertEquals( endDate, data.get( 3 ).getText() );
        assertEquals( renewals, data.get( 4 ).getText() );
    }

    @When ( "I choose to add a new drug" )
    public void addDrug () {
        driver.get( DRUG_URL );
    }

    @When ( "submit the values for NDC (.+), name (.+), and description (.*)" )
    public void submitDrug ( final String ndc, final String name, final String description )
            throws InterruptedException {
        // Piazza post 381
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.tagName( "h3" ) ) );
        assertEquals( "Admin Manage Drugs", driver.findElement( By.tagName( "h3" ) ).getText() );

        Thread.sleep( 1000 );
        enterValue( "drug", name );
        enterValue( "code", ndc );
        enterValue( "description", description );
        driver.findElement( By.name( "submit" ) ).click();
    }

    @Then ( "the drug (.+) is successfully added to the system" )
    public void drugSuccessful ( final String drug ) throws InterruptedException {
        Thread.sleep( 2000 );

        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.tagName( "body" ), drug ) );
        assertEquals( "", driver.findElement( By.id( "errP" ) ).getText() );

        for ( final WebElement r : driver.findElements( By.name( "drugTableRow" ) ) ) {
            if ( r.getText().contains( drug ) ) {
                r.findElement( By.name( "deleteDrug" ) ).click();
            }
        }
        wait.until( ExpectedConditions
                .not( ExpectedConditions.textToBePresentInElementLocated( By.tagName( "body" ), drug ) ) );
    }

}
