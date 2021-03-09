package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Use Case 29 Tracking Previous Contacts step def file
 *
 * @author Eddie Woodhouse (ejwoodho)
 */

public class TrackingPreviousContactsStepDefs extends CucumberTest {
    private final String baseUrl = "http://localhost:8080/iTrust2";
    private final String name    = "virologist";

    @Before
    private void setupScenarios () {
        signIn();
        clearPassengers();
    }

    /**
     * Asserts that the text is on the page
     *
     * @param text
     *            text to check
     */
    public void assertTextPresent ( final String text ) {
        waitForAngular();
        try {
            assertTrue( driver.getPageSource().contains( text ) );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * Clears database
     */
    private void clearPassengers () {
        driver.findElement( By.id( "virologist-dropdown" ) ).click();
        driver.findElement( By.id( "uploadPassengerData" ) ).click();
        waitForAngular();
        final WebElement elem = driver.findElementById( "clearDBButton" );
        elem.click();
        final Alert alert = driver.switchTo().alert();
        alert.accept();
        waitForAngular();

    }

    /**
     * Signs in as a virologist
     */
    @Before
    private void signIn () {

        attemptLogout();
        waitForAngular();
        driver.get( baseUrl );
        waitForAngular();
        setTextField( By.name( "username" ), "virologist" );
        setTextField( By.name( "password" ), "123456" );
        waitForAngular();
        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * selects byVal and enters value
     *
     * @param byVal
     *            element selected
     * @param value
     *            value entered
     */
    private void setTextField ( final By byVal, final Object value ) {
        final WebElement elem = driver.findElement( byVal );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    /**
     * Ensures there are passengers already uploaded to the system
     *
     * @param passengers
     *            filename with passenger ids/info stored
     */
    @Given ( "there are already passengers (.+)" )
    public void passengersInSystem ( final String passengers ) {
        // sign in as virologist
        signIn();

        // go to upload passengers
        driver.findElement( By.id( "virologist-dropdown" ) ).click();
        driver.findElement( By.id( "uploadPassengerData" ) ).click();

        // grab upload field
        final WebElement uploadFile = driver.findElementById( "dataUploadField" );

        // send filename to upload
        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                passengers );
        final String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        // upload new passengers
        uploadFile.sendKeys( absolutePath );
    }

    /**
     * Ensures there are contacts already uploaded to the system
     *
     * @param contacts
     *            filename with contact ids stored
     */
    @Given ( "there are already contacts (.+)" )
    public void contactsInSystem ( final String contacts ) {
        // go to contacts link
        driver.findElement( By.id( "virologist-dropdown" ) ).click();
        driver.findElement( By.id( "searchPassengerContacts" ) ).click();

        // grab upload field
        final WebElement uploadFile = driver.findElementById( "dataUploadField" );

        // send filename to upload
        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                contacts );
        final String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        // upload new contacts
        uploadFile.sendKeys( absolutePath );
    }

    /**
     * User navigates to the Tracking Previous Contacts page
     */
    @Given ( "the user navigates to Tracking Previous Contacts" )
    public void navigateToTrackingContacts () {
        signIn();
        driver.findElement( By.id( "virologist-dropdown" ) ).click();
        driver.findElement( By.id( "searchPassengerContacts" ) ).click();
        assertEquals( "Track Previous Contacts", driver.findElementById( "trackcontactsheading" ).getText() );
    }

    /**
     * User fills out the tracking contacts form with valid inputs
     *
     * @param id
     *            passenger being tracked
     * @param date
     *            of infection
     * @param depth
     *            determines what degree of passengers are being tracked
     */
    @When ( "the user validly fills out the form with (.+), (.+), and (.+)" )
    public void validTrackingInputs ( final String id, final String date, final String depth ) {
        // sets each text field and presses the button to search
        setTextField( By.name( "passengerID" ), id );
        setTextField( By.id( "date" ), date );
        setTextField( By.id( "depth" ), depth );
    }

    /**
     * Ensures there are contacts already uploaded to the system
     *
     * @param contacts
     *            filename with contact ids stored
     */
    @When ( "the user selects Choose File and adds contact data from (.+)" )
    public void addContactsToSystem ( final String contacts ) {
        // go to contacts link
        driver.findElement( By.id( "virologist-dropdown" ) ).click();
        driver.findElement( By.id( "searchPassengerContacts" ) ).click();

        // grab upload field
        final WebElement uploadFile = driver.findElementById( "dataUploadField" );

        // send filename to upload
        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                contacts );
        final String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        // upload new contacts
        uploadFile.sendKeys( absolutePath );
    }

    /**
     * User fills out the tracking contacts form with invalid inputs
     *
     * @param id
     *            passenger being tracked
     * @param date
     *            of infection
     * @param depth
     *            determines what degree of passengers are being tracked
     */
    @When ( "the user invalidly fills out the form with (.+), (.+), and (.+)" )
    public void invalidTrackingInputs ( final String id, final String date, final String depth ) {
        // sets each text field and presses the button to search
        if ( !id.equals( "null" ) ) {
            setTextField( By.name( "passengerID" ), id );
        }
        if ( !date.equals( "null" ) ) {
            setTextField( By.id( "date" ), date );
        }
        if ( !depth.equals( "null" ) ) {
            setTextField( By.id( "depth" ), depth );
        }
    }

    /**
     * Checks for a particular message on the stats page with no passenger data
     *
     * @param message
     *            the message that displays when there are no statistics to plot
     */
    @Then ( "the missing passenger data message (.+) appears on the page" )
    public void checkStatsNoData ( final String message ) {
        waitForAngular();
        assertEquals( message, driver.findElement( By.id( "errorUploading" ) ).getText() );
    }

    /**
     * User tracks contacts using the previous inputs. This method checks for
     * success
     */
    @Then ( "they can select Track Contacts to view contacts by depth" )
    public void searchContactsByDepth () {
        assertNotEquals( "ng-hide", driver.findElementById( "errorUploading" ).getAttribute( "class" ) );
        assertTrue( driver.findElement( By.id( "findPassengerContactsBtn" ) ).isEnabled() );
        driver.findElement( By.id( "findPassengerContactsBtn" ) ).click();
        waitForAngular();

    }

    /**
     * Checks that the Track Contacts button is disabled
     */
    @Then ( "they cannot select Track Contacts to view contacts by depth" )
    public void searchButtonDisabled () {
        waitForAngular();
        assertFalse( driver.findElement( By.id( "findPassengerContactsBtn" ) ).isEnabled() );
    }

}
