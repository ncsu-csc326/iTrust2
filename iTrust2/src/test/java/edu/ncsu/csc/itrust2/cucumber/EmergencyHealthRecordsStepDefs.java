package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

/**
 * Class for Cucumber Testing of Emergency Health Records feature.
 *
 * @author tadicke3
 * @author akiyeng2
 */
public class EmergencyHealthRecordsStepDefs extends CucumberTest {

    private final String baseUrl = "http://localhost:8080/iTrust2";
    private boolean      isHCP   = false;

    private void setTextField ( final By byval, final Object value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    private void logout () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('logout').click();" );
        waitForAngular();

    }

    /**
     * Login as user Knight Solaire (an ER).
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    @Given ( "I am logged in as an ER" )
    public void loginAsER () throws NumberFormatException, ParseException {
        attemptLogout();

        createTestData();

        isHCP = false;
        driver.get( baseUrl );
        waitForAngular();
        setTextField( By.name( "username" ), "knightSolaire" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Login as user Shelly Vang (an HCP).
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    @Given ( "I am logged in as an HCP" )
    public void loginAsHCP () throws NumberFormatException, ParseException {
        attemptLogout();

        createTestData();

        isHCP = true;
        driver.get( baseUrl );
        waitForAngular();
        setTextField( By.name( "username" ), "sVang	" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Creates the required informations for the tests
     *
     * @throws ParseException
     * @throws NumberFormatException
     */
    public void createTestData () throws NumberFormatException, ParseException {

        HibernateDataGenerator.generateTestFaculties();
        HibernateDataGenerator.generateTestEHR();

    }

    /**
     * Navigate to the Emergency Health Records page.
     */
    @When ( "I navigate to Emergency Health Records" )
    public void navigateToEHR () {
        waitForAngular();
        if ( isHCP ) {
            ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('HCPrecords').click();" );
        }
        else {
            ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('ERrecords').click();" );
        }
        waitForAngular();
    }

    /**
     * Search for patient with query, "Siegwardof Catarina".
     */
    @When ( "I search a full patient name" )
    public void searchPatientFullName () {
        waitForAngular();
        setTextField( By.name( "search" ), "Siegwardof Catarina" );
        waitForAngular();
    }

    /**
     * Search for patient with query, "onionman".
     */
    @When ( "I search a full patient MID" )
    public void searchPatientFullMID () {
        waitForAngular();
        setTextField( By.name( "search" ), "onionman" );
        waitForAngular();
    }

    /**
     * Search for patient with query, "smith".
     */
    @When ( "I search a partial patient MID" )
    public void searchPatientPartialMID () {
        waitForAngular();
        setTextField( By.name( "search" ), "king" );
        waitForAngular();
    }

    /**
     * Verify Siegwardof Catarina has appeared in the list.
     */
    @Then ( "The patient appears as a result in the list of matching patients" )

    public void patientAppearsInList () {
        final String username = "onionman";
        waitForAngular();
        driver.findElement( By.cssSelector( "input[type=radio][value=" + username + "]" ) ).click();
        waitForAngular();
    }

    /**
     * Verify King One has appeared in the list, and that the list is at least
     * three entries in length.
     */
    @Then ( "The patient appears among other names containing the substring" )

    public void patientAppearsInListWithOthers () {

        final String username = "kingone";

        waitForAngular();
        driver.findElement( By.cssSelector( "input[type=radio][value=" + username + "]" ) ).click();
        waitForAngular();
        ExpectedConditions.numberOfElementsToBeMoreThan( By.cssSelector( "input[type=radio]" ), 2 );
        waitForAngular();

    }

    /**
     * Verify Siegwardof Catarina's information displays correctly.
     */
    @Then ( "The patients information can be displayed" )

    public void patientCanBeDisplayed () {
        waitForAngular();
        logout();
    }

    /**
     * Verify King One's information displays correctly.
     */
    @Then ( "The patient can be selected from the others, and has accurate information" )

    public void patientCanBeDisplayedWithOthers () {
        waitForAngular();

        assertTrue( driver.findElement( By.id( "Name" ) ).getText().contains( "Name: King One" ) );
        assertTrue( driver.findElement( By.id( "Gender" ) ).getText().contains( "Gender: Male" ) );

        String dateOfBirth;
        try {
            LocalDate date = Patient.getByName( "kingone" ).getDateOfBirth();
            dateOfBirth = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch ( final NullPointerException e ) {
            dateOfBirth = "NA";
        }

        assertTrue( driver.findElement( By.id( "Blood" ) ).getText().contains( "Blood Type: O+" ) );
        assertTrue( driver.findElement( By.id( "DOB" ) ).getText().contains( "Date of Birth: " + dateOfBirth ) );
        assertTrue( driver.findElement( By.id( "age" ) ).getText().contains( "Age: 30" ) );

        logout();
    }
}
