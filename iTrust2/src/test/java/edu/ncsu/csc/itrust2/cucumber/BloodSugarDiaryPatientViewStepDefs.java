package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.BloodSugarDiaryEntry;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step definitions for BloodSugarDiaryPatientView feature
 *
 * @author Nicholas Bruinsma (nabruins)
 *
 */
public class BloodSugarDiaryPatientViewStepDefs extends CucumberTest {

    private final String                     baseUrl  = "http://localhost:8080/iTrust2";
    private final String                     username = "Frodo";
    private final String                     password = "123456";
    private final String                     encoded  = "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.";
    private final User                       user     = new User( username, encoded, Role.ROLE_PATIENT, 1 );
    private final Patient                    patient  = new Patient( user );

    /** Keeps track of BloodSugarDiaryEntry created **/
    private final List<BloodSugarDiaryEntry> entries  = new ArrayList<BloodSugarDiaryEntry>();

    /**
     * Saves users to database
     */
    @Before ( "@PatientBloodView" )
    public void setupScenarios () {
        attemptLogout();
        user.save();
        patient.save();
    }

    /**
     * Logs the current user out
     *
     * Removes any BloodSugarDiaryEntrys created during the last scenario
     */
    @After ( "@PatientBloodView" )
    public void tearDownScenarios () {
        attemptLogout();
        for ( final BloodSugarDiaryEntry entry : entries ) {
            entry.delete();
        }
        patient.delete();
        entries.clear();
    }

    /**
     * Asserts that the text is on the page
     *
     * @param text
     *            text to check
     */
    public void assertTextPresent ( final String text ) {
        final String bodyText = driver.findElement( By.tagName( "body" ) ).getText();
        assertTrue( "Text not found: " + text, bodyText.contains( text ) );
    }

    /**
     * Logs in as a Frodo and ensures the landing page loads
     */
    @Given ( "^I am logged in as Frodo$" )
    public void createPatient () {
        attemptLogout();
        patient.save();
        driver.get( baseUrl );
        waitForAngular();
        final WebElement usernameField = driver.findElement( By.name( "username" ) );
        usernameField.clear();
        usernameField.sendKeys( username );
        final WebElement passwordField = driver.findElement( By.name( "password" ) );
        passwordField.clear();
        passwordField.sendKeys( password );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        waitForAngular();
        assertTextPresent( "Welcome to iTrust2 - Patient" );
    }

    /**
     * Navigates to the Blood Sugar Journal page and ensures it loads
     */
    @Given ( "^I navigate to the Blood Sugar Journal page$" )
    public void viewBloodJournal () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewBloodSugarJournal').click();" );
        waitForAngular();
        assertTextPresent( "Blood Sugar Journal" );
    }

    /**
     * Creates the given entries
     *
     * @param dt
     *            Data table containing blood sugar entries
     */
    @Given ( "^Frodo has the following entries$" )
    public void entriesExist ( final DataTable dt ) {
        final List<Map<String, String>> list = dt.asMaps( String.class, String.class );
        for ( int i = 0; i < list.size(); i++ ) {
            final BloodSugarDiaryEntry entry = new BloodSugarDiaryEntry();

            // Get data from table
            final String date = list.get( i ).get( "Date" );
            final int fasting = Integer.valueOf( list.get( i ).get( "Fasting" ) );
            final int first = Integer.valueOf( list.get( i ).get( "First" ) );
            final int second = Integer.valueOf( list.get( i ).get( "Second" ) );
            final int third = Integer.valueOf( list.get( i ).get( "Third" ) );

            // Set the fields
            entry.setPatient( patient );
            entry.setDate( LocalDate.parse( date ) );
            entry.setFastingLevel( fasting );
            entry.setFirstLevel( first );
            entry.setSecondLevel( second );
            entry.setThirdLevel( third );
            entry.save();
            entries.add( entry );
        }
    }

    /**
     * Selects the week view and ensures that tab is active
     */
    @When ( "^I select the week view$" )
    public void weekViewSelect () {
        final WebElement week = driver.findElement( By.id( "week" ) );
        week.click();
        waitForAngular();
        final WebElement parent = week.findElement( By.xpath( "./.." ) );
        assertTrue( parent.getAttribute( "class" ).contains( "active" ) );
    }

    /**
     * Enters the given date in the date picker
     *
     * @param date
     *            given date
     */
    @When ( "^I enter the given date$" )
    public void enterDate ( final String date ) {
        final WebElement datePicker = driver.findElement( By.id( "datePicker" ) );
        datePicker.click();
        datePicker.clear();
        datePicker.sendKeys( date );
        datePicker.sendKeys( Keys.RETURN );
        waitForAngular();
        assertEquals( date, datePicker.getAttribute( "value" ) );
    }

    /**
     * Ensures that the table on the page contain the given data
     *
     * @param dt
     *            Data table containing expected results
     */
    @Then ( "^The table displays the following entries$" )
    public void checkTable ( final DataTable dt ) {
        final List<Map<String, String>> list = dt.asMaps( String.class, String.class );
        for ( int i = 0; i < list.size(); i++ ) {

            // Get data from table
            final String date = list.get( i ).get( "Date" );
            final String fasting = list.get( i ).get( "Fasting" );
            final String first = list.get( i ).get( "First" );
            final String second = list.get( i ).get( "Second" );
            final String third = list.get( i ).get( "Third" );

            // Assert present
            assertTextPresent( date );
            assertTextPresent( fasting );
            assertTextPresent( first );
            assertTextPresent( second );
            assertTextPresent( third );
        }
    }
}
