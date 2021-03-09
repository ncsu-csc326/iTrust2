package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.Passenger;

/**
 * Use Case 27 Upload Patient Data step def file
 *
 * @author Leon Li
 * @author Eddie Woodhouse (ejwoodho)
 */
public class UploadPatientDataStepDefs extends CucumberTest {

    private final String baseUrl = "http://localhost:8080/iTrust2";

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

    private void clearPassengers () {
        final WebElement elem = driver.findElementById( "clearDBButton" );
        elem.click();

        final Alert alert = driver.switchTo().alert();
        alert.accept();
        waitForAngular();

        final WebElement check = driver.findElementById( "clearMessage" );

        assertNotEquals( "ng-hide", driver.findElementById( "clearMessage" ).getAttribute( "class" ) );
        assertEquals( "Database cleared", check.getText() );
    }

    /**
     * Creates a patient in the system.
     */
    @Given ( "I navigate to the Upload Patient Data page on iTrust2" )
    public void navigateToUploadPage () {
        Passenger.deleteAll();
        attemptLogout();

        driver.get( baseUrl );
        setTextField( By.name( "username" ), "virologist" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        assertEquals( "iTrust2: HCP Home", driver.getTitle() );

        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('uploadPassengerData').click();" );
        waitForAngular();

        assertEquals( "Upload CSV", driver.findElementById( "submitUploadedDataButton" ).getText() );

        assertEquals( "Reset Passenger Database", driver.findElementById( "clearDBButton" ).getText() );

    }

    /**
     * Uploads an invalid file type extension (.txt, .pdf, etc.)
     *
     * @param fileType
     *            file type extension
     */
    @When ( "I select Choose File and upload an invalid file type: (.+)" )
    public void invalidFileType ( final String fileType ) {

        waitForAngular();
        final WebElement uploadFile = driver.findElementById( "dataUploadField" );

        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                fileType );

        final String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        assertTrue( absolutePath.endsWith( fileType ) );

        uploadFile.sendKeys( absolutePath );

    }

    /**
     * Uploads a correct file type extension, but the format of the CSV is
     * incorrect
     *
     * @param fileName
     *            the file's name
     */
    @When ( "I select Choose File and upload a valid file type: (.+) with invalid formatting" )
    public void invalidFileFormat ( final String fileName ) {
        waitForAngular();
        final WebElement uploadFile = driver.findElementById( "dataUploadField" );

        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                fileName );

        final String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        uploadFile.sendKeys( absolutePath );

    }

    /**
     * Uploads a valid file type extension and correct format
     *
     * @param file
     *            the file's name
     */
    @When ( "I select Choose File and upload a valid file type: (.+) with valid formatting" )
    public void validFile ( final String file ) {
        waitForAngular();
        final WebElement uploadFile = driver.findElementById( "dataUploadField" );

        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                file );

        final String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        assertTrue( absolutePath.endsWith( file ) );

        uploadFile.sendKeys( absolutePath );
        waitForAngular();

    }

    /**
     * Checks the if the upload button is enabled
     */
    @Then ( "Upload CSV button is enabled" )
    public void checkUploadButtonStatusValid () {
        waitForAngular();
        // The submit button is enabled = False
        assertTrue( driver.findElementById( "submitUploadedDataButton" ).isEnabled() );

    }

    /**
     * Outputs the invalid file extension message
     *
     * @param message
     *            the message
     */
    @Then ( "clicking Upload CSV makes an incorrect file extension (.+) appear" )
    public void incorrectFileExtensionMessage ( final String message ) {
        waitForAngular();
        driver.findElement( By.id( "submitUploadedDataButton" ) ).click();
        // clicking button makes the error message appear
        waitForAngular();
        assertNotEquals( "ng-hide", driver.findElementById( "uploadErrorMessage" ).getAttribute( "class" ) );
        assertEquals( message, driver.findElementById( "uploadErrorMessage" ).getText() );
        clearPassengers();
    }

    /**
     * Outputs the invalid format message
     *
     * @param message
     *            the message
     */
    @Then ( "uploading displays the incorrect format (.+)" )
    public void incorrectFileFormatMessage ( final String message ) {
        waitForAngular();
        driver.findElement( By.id( "submitUploadedDataButton" ) ).click();
        // clicking button makes the error message appear
        waitForAngular();
        assertNotEquals( "ng-hide", driver.findElementById( "uploadErrorMessage" ).getAttribute( "class" ) );
        assertEquals( message, driver.findElementById( "uploadErrorMessage" ).getText() );
        clearPassengers();
    }

    /**
     * Outputs the success message and displays the number of skipped & added
     * passengers
     *
     * @param skipped
     *            the skipped message
     * @param added
     *            the added message
     */
    @Then ( "uploading displays messages for passengers (.+) and (.+)" )
    public void successfulUpload ( final String skipped, final String added ) {
        waitForAngular();
        driver.findElement( By.id( "submitUploadedDataButton" ) ).click();
        // clicking button makes the error message appear
        // waitForAngular();
        boolean checking = false;
        final WebDriverWait wait = new WebDriverWait( driver, 60000 );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.xpath( "//*[@id=\"numAddedMessage\"]" ) ) );
        waitForAngular();
        if ( !added.equals( "null" ) ) {
            assertNotEquals( "ng-hide", driver.findElementById( "numAddedMessage" ).getAttribute( "class" ) );
            assertEquals( added, driver.findElementById( "numAddedMessage" ).getText() );
            checking = true;
        }

        if ( !skipped.equals( "null" ) ) {
            assertNotEquals( "ng-hide", driver.findElementById( "numDupMessage" ).getAttribute( "class" ) );
            assertEquals( skipped, driver.findElementById( "numDupMessage" ).getText() );
            checking = true;
        }

        // Makes sure that at least one of the if statements have been checked
        // If this assert fails, that means neither messages were checked
        assertTrue( checking );
        clearPassengers();

    }

    // Functionality for testing for Upload File While Database Is Not Empty

    /**
     * Outputs the success message and displays the number of skipped & added
     * passengers
     *
     * @param skipped
     *            the skipped message
     * @param added
     *            the added message
     */
    @Then ( "the messages displayed for passengers are (.+) and (.+)" )
    public void noResetDatabaseUploadTest ( final String skipped, final String added ) {
        waitForAngular();
        driver.findElement( By.id( "submitUploadedDataButton" ) ).click();
        // clicking button makes the error message appear
        waitForAngular();
        boolean checking = false;
        final WebDriverWait wait = new WebDriverWait( driver, 60000 );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.xpath( "//*[@id=\"numAddedMessage\"]" ) ) );
        if ( !added.equals( "null" ) ) {
            assertNotEquals( "ng-hide", driver.findElementById( "numAddedMessage" ).getAttribute( "class" ) );
            assertEquals( added, driver.findElementById( "numAddedMessage" ).getText() );
            checking = true;
        }

        if ( !skipped.equals( "null" ) ) {
            assertNotEquals( "ng-hide", driver.findElementById( "numDupMessage" ).getAttribute( "class" ) );
            assertEquals( skipped, driver.findElementById( "numDupMessage" ).getText() );
            checking = true;
        }

        // Makes sure that at least one of the if statements have been checked
        // If this assert fails, that means neither messages were checked
        assertTrue( checking );

    }

    /**
     * Uploads a valid file type extension and correct format for the second
     * time
     *
     * @param file
     *            the file's name
     */
    @Then ( "I select Choose File and upload another valid file type: (.+) with valid formatting" )
    public void validFileAgain ( final String file ) {
        waitForAngular();
        final WebElement uploadFile = driver.findElementById( "dataUploadField" );

        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                file );

        final String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        assertTrue( absolutePath.endsWith( file ) );

        uploadFile.sendKeys( absolutePath );

    }

    /**
     * Displays the number of skipped & added passengers or error message
     *
     * @param skipped
     *            the skipped message
     * @param added
     *            the added message
     * @param error
     *            the error message
     */
    @Then ( "upload the file which displays messages for passengers (.+) and (.+) or (.+)" )
    public void successfulUpload ( final String skipped, final String added, final String error ) {
        waitForAngular();
        driver.findElement( By.id( "submitUploadedDataButton" ) ).click();
        // clicking button makes the error message appear
        waitForAngular();
        boolean checking = false;

        if ( !added.equals( "null" ) ) {
            assertNotEquals( "ng-hide", driver.findElementById( "numAddedMessage" ).getAttribute( "class" ) );
            assertEquals( added, driver.findElementById( "numAddedMessage" ).getText() );
            checking = true;
        }

        if ( !skipped.equals( "null" ) ) {
            assertNotEquals( "ng-hide", driver.findElementById( "numDupMessage" ).getAttribute( "class" ) );
            assertEquals( skipped, driver.findElementById( "numDupMessage" ).getText() );
            checking = true;
        }

        if ( !error.equals( "null" ) ) {
            assertNotEquals( "ng-hide", driver.findElementById( "uploadErrorMessage" ).getAttribute( "class" ) );
            assertEquals( error, driver.findElementById( "uploadErrorMessage" ).getText() );
            checking = true;
        }

        // Makes sure that at least one of the if statements have been checked
        // If this assert fails, that means neither messages were checked
        assertTrue( checking );
        clearPassengers();

    }

}
