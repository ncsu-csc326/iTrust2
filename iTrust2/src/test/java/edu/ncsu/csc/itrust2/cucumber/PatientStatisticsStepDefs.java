package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Use Case 28 Patient Statistics step def file
 *
 * @author Eddie Woodhouse (ejwoodho)
 */

public class PatientStatisticsStepDefs extends CucumberTest {
    private final String baseUrl = "http://localhost:8080/iTrust2";
    private final String name    = "virologist";

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
     * Deletes existing passenger data in the system
     */
    @Given ( "there is no passenger data in the system" )
    public void resetPassengers () {
        // sign in as virologist
        signIn();
        // navigate to Upload Passenger Data to reset
        waitForAngular();
        driver.findElement( By.id( "virologist-dropdown" ) ).click();
        waitForAngular();
        driver.findElement( By.id( "uploadPassengerData" ) ).click();
        waitForAngular();
        driver.findElement( By.id( "clearDBButton" ) ).click();
        final WebDriverWait wait = new WebDriverWait( driver, 5 );
        wait.until( ExpectedConditions.alertIsPresent() );
        final Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    /**
     * User navigates to the View Patient Statistics page
     */
    @When ( "the user navigates to Patient Statistics" )
    public void navigateToPatientStatistics () {
        waitForAngular();
        driver.findElement( By.id( "virologist-dropdown" ) ).click();
        waitForAngular();
        driver.findElement( By.id( "viewPatientStatistics" ) ).click();
    }

    /**
     * Checks for a particular message on the stats page with no passenger data
     *
     * @param message
     *            the message that displays when there are no statistics to plot
     */
    @Then ( "the missing data (.+) appears" )
    public void checkStatsNoData ( final String message ) {
        waitForAngular();
        assertEquals( message, driver.findElement( By.id( "noDataMessage" ) ).getText() );
        assertTrue( driver.findElement( By.id( "noDataMessage" ) ).getAttribute( "ng-show" )
                .equals( "!dataLoaded && !loadingStatistics" ) );
        clearPassengers();
    }

    /**
     * Ensures some passenger data is in the system
     *
     * @param file
     *            the filename of the passenger data being uploaded
     */
    @Given ( "there is some passenger data from (.+) in the system" )
    public void addDataForStats ( final String file ) {
        signIn();
        driver.findElement( By.id( "virologist-dropdown" ) ).click();
        waitForAngular();
        driver.findElement( By.id( "uploadPassengerData" ) ).click();
        waitForAngular();
        driver.findElement( By.id( "clearDBButton" ) ).click();
        final WebDriverWait wait = new WebDriverWait( driver, 5 );
        wait.until( ExpectedConditions.alertIsPresent() );
        final Alert alert = driver.switchTo().alert();
        alert.accept();
        waitForAngular();
        final WebElement uploadFile = driver.findElementById( "dataUploadField" );

        final Path resourceDirectory = Paths.get( "src", "test", "resources", "edu", "ncsu", "csc", "itrust", "zipdata",
                file );

        final String absolutePath = resourceDirectory.toFile().getAbsolutePath();

        System.out.println( absolutePath );

        assertTrue( absolutePath.endsWith( file ) );

        uploadFile.sendKeys( absolutePath );
        waitForAngular();
        driver.findElement( By.id( "submitUploadedDataButton" ) ).click();

    }

    /**
     * Check plot for total infected passengers by day
     */
    @Then ( "the total infected patients are plotted" )
    public void checkTotalInfections () {
        driver.findElement( By.id( "totalInfectionsTab" ) ).click();
        assertTrue( driver.findElement( By.id( "noDataMessage" ) ).getAttribute( "ng-show" )
                .equals( "!dataLoaded && !loadingStatistics" ) );
    }

    /**
     * Check plot for new infected passengers by day
     */
    @Then ( "the new infected patients are plotted" )
    public void checkNewInfections () {
        driver.findElement( By.id( "newInfectionsPerDayTab" ) ).click();
        assertTrue( driver.findElement( By.id( "noDataMessage" ) ).getAttribute( "ng-show" )
                .equals( "!dataLoaded && !loadingStatistics" ) );
    }

    /**
     * Check plot for infections by severity code
     */
    @Then ( "the patients by severity are plotted" )
    public void checkPatientSeverity () {
        driver.findElement( By.id( "severitiesTab" ) ).click();
        assertTrue( driver.findElement( By.id( "noDataMessage" ) ).getAttribute( "ng-show" )
                .equals( "!dataLoaded && !loadingStatistics" ) );
        clearPassengers();
    }

    /**
     * Check R-Naught value after data is put in the system
     *
     * @param rNaught
     *            expected value of R-Naught
     * @param description
     *            which should describe the meaning of the R-Naught value
     */
    @Then ( "R-Naught (.+) is displayed and its corresponding (.+)" )
    public void checkRNaught ( final String rNaught, final String description ) {

        driver.findElement( By.id( "rNaughtTab" ) ).click();
        try {
            Thread.sleep( 4000 );
        }
        catch ( final InterruptedException e ) {

            e.printStackTrace();
        }
        assertEquals( rNaught, driver.findElement( By.id( "rNaughtValueDisplay" ) ).getText() );
        assertEquals( description, driver.findElement( By.id( "rNaughtMessageDisplay" ) ).getText() );
        clearPassengers();
    }

    /**
     * Check for the 'no data' message on the R-Naught tab
     *
     * @param message
     *            the message explaining there is no data to display the
     *            R-Naught value
     */
    @Then ( "the R-Naught tab displays the missing data (.+)" )
    public void noDataRNaught ( final String message ) {
        driver.findElement( By.id( "rNaughtTab" ) ).click();
        assertTrue( driver.findElement( By.id( "noDataMessage" ) ).getAttribute( "ng-show" )
                .equals( "!dataLoaded && !loadingStatistics" ) );
        clearPassengers();
    }
}
