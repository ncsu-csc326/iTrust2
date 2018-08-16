package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Class for Cucumber Testing of Personal Representatives feature.
 *
 * @author tadicke3
 */
public class LabProcedureStepDefs extends CucumberTest {

    private final String baseUrl = "http://localhost:8080/iTrust2";

    private void setTextField ( final By byval, final Object value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    /**
     * Login as HCP Shelly Vang.
     */
    @Given ( "I log in to iTrust2 as an HCP" )
    public void loginAsShelly () {
        attemptLogout();

        HibernateDataGenerator.generateTestLOINC();

        driver.get( baseUrl );
        waitForAngular();

        setTextField( By.name( "username" ), "svang" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Login as lab tech Larry Teacher.
     */
    @Given ( "I log in to iTrust2 as a Lab Tech" )
    public void livingLikeLarry () {
        attemptLogout();

        driver.get( baseUrl );
        waitForAngular();

        setTextField( By.name( "username" ), "larrytech" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Login as admin Al Minister.
     */
    @Given ( "I log in to iTrust2 as an Admin" )
    public void loginAsAl () {
        attemptLogout();

        driver.get( baseUrl );
        waitForAngular();

        setTextField( By.name( "username" ), "alminister" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Create and fill out new Office Visit for patient Nellie Sanderson.
     */
    @When ( "I create a new Office Visit" )
    public void createOfficeVisit () {

        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('documentOfficeVisit').click();" );

        waitForAngular();
        setTextField( By.name( "notes" ), "Billy has been experiencing symptoms of a cold or flu" );
        waitForAngular();

        driver.findElement( By.id( "patient" ) ).click();
        driver.findElement( By.name( "type" ) ).click();
        driver.findElement( By.name( "hospital" ) ).click();
        setTextField( By.name( "date" ), "10/17/2018" );
        setTextField( By.name( "time" ), "9:30 AM" );

        waitForAngular();
        setTextField( By.name( "height" ), "62.3" );

        waitForAngular();
        setTextField( By.name( "weight" ), "125" );

        waitForAngular();
        setTextField( By.name( "systolic" ), "110" );

        waitForAngular();
        setTextField( By.name( "diastolic" ), "75" );

        waitForAngular();
        setTextField( By.name( "hdl" ), "65" );

        waitForAngular();
        setTextField( By.name( "ldl" ), "102" );

        waitForAngular();
        setTextField( By.name( "tri" ), "147" );

        waitForAngular();
        final WebElement houseSmokeElement = driver.findElement(
                By.cssSelector( "input[value=\"" + HouseholdSmokingStatus.NONSMOKING.toString() + "\"]" ) );
        houseSmokeElement.click();

        waitForAngular();
        final WebElement patientSmokeElement = driver
                .findElement( By.cssSelector( "input[value=\"" + PatientSmokingStatus.FORMER.toString() + "\"]" ) );
        patientSmokeElement.click();
    }

    /**
     * Add lab procedure to Office Visit, assigned to Larry Teacher.
     */
    @When ( "I add a lab procedure to that visit" )
    public void addLabProc () {
        // add the lab proc
        waitForAngular();
        driver.findElement( By.name( "manual count of white blood cells in cerebral spinal fluid specimen" ) ).click();
        final Select pri = new Select( driver.findElement( By.name( "priority" ) ) );
        pri.selectByVisibleText( "High" );

        waitForAngular();
        driver.findElement( By.id( "radio-larrytech" ) ).click();
        driver.findElement( By.name( "addProcedure" ) ).click();

        waitForAngular();
        driver.findElement( By.name( "submit" ) ).click();
    }

    /**
     * Navigate Lab Tech from home page to assigned procedures.
     */
    @When ( "I navigate to Assigned Procedures" )
    public void navigateToAssigned () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('LTprocedures').click();" );

        waitForAngular();
    }

    /**
     * Update the status of your newest procedure to in-progress.
     */
    @When ( "I change my newest procedures status to In-Progress" )
    public void changeToInProgress () {
        waitForAngular();

        driver.findElement( By.id( "update-806-0" ) ).click();
        waitForAngular();

        final Select status = new Select( driver.findElement( By.id( "selectStatus" ) ) );
        status.selectByVisibleText( "IN_PROGRESS" );
        driver.findElement( By.id( "updateStatus" ) ).click();
    }

    /**
     * Navigate Admin from home page to procedures.
     */
    @When ( "I navigate to Admin Procedures" )
    public void navigateToAdminProc () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('manageLOINCCodes').click();" );

        waitForAngular();
    }

    /**
     * Create and fill-out a new lab procedure
     */
    @When ( "I add a new lab procedure" )
    public void addNewProc () {
        waitForAngular();
        setTextField( By.name( "iCode" ), "35548-7" );
        setTextField( By.name( "iComName" ), "Allergen, Fungi/Mold, Fus Monilifor, IgG" );
        setTextField( By.name( "iComponent" ), "Allergen, Fungi/Mold, F moniliforme IgG" );
        setTextField( By.name( "iProperty" ), "mcg/mL" );

        driver.findElement( By.id( "submitLOINC" ) ).click();
    }

    /**
     * Verify success message of "Office Visit created successfully".
     */
    @Then ( "I recieve a message that office visit details were changed successfully" )
    public void successOffice () {
        waitForAngular();

        // confirm that the message is displayed
        try {
            assertTrue( driver.findElement( By.name( "success" ) ).getText()
                    .contains( "Office visit created successfully" ) );

        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Verify success message of "Lab Procedure updated successfully".
     */
    @Then ( "I recieve a message that lab procedure details were changed successfully" )
    public void successInProgress () {
        waitForAngular();

        // confirm that the message is displayed
        try {
            assertTrue(
                    driver.findElement( By.id( "succUpd" ) ).getText().contains( "Successfully updated procedure" ) );

        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Verify success message of "Successfully added code".
     */
    @Then ( "I recieve a message that the procedure list was changed successfully" )
    public void successAdmin () {
        waitForAngular();

        try {
            assertTrue( driver.findElement( By.id( "succP" ) ).getText().contains( "Successfully added code" ) );

        }
        catch ( final Exception e ) {
            fail( "Success message: " + driver.findElement( By.id( "succP" ) ).getText() + "; Failure message: "
                    + driver.findElement( By.id( "errP" ) ).getText() );
        }
    }
}
