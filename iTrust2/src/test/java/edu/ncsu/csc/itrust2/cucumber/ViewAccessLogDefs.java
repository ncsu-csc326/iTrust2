package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ViewAccessLogDefs extends CucumberTest {

    private final String    baseUrl = "http://localhost:8080/iTrust2";
    private final LocalDate today   = LocalDate.now();

    private void setTextField ( final By byval, final Object value ) {
        final WebElement elem = driver.findElement( byval );
        elem.sendKeys( value.toString() );
    }

    @When ( "(.+) has logged in with password and chosen to view the access log" )
    public void goToLogPage ( final String user ) {

        attemptLogout();
        driver.get( baseUrl );
        waitForAngular();

        setTextField( By.name( "username" ), user );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
        waitForAngular();

    }

    @Then ( "The first ten record should appear on the screen" )
    public void goToTheLogPage () {
        waitForAngular();
        driver.findElement( By.className( "navbar-brand" ) ).click();

        waitForAngular();
        assertTrue( driver.getPageSource().contains( "svang" ) );
    }

    @When ( "She selects the start date and end date" )
    public void correctTime () {
        waitForAngular();

        final String pattern = "MM/dd/yyyy";
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern( pattern );
        final String dateInString = today.format( dtf );
        setTextField( By.name( "startDate" ), dateInString.replace( "/", "" ) );
        setTextField( By.name( "endDate" ), dateInString.replace( "/", "" ) );
        driver.findElement( By.name( "submit" ) ).click();

        waitForAngular();

    }

    @Then ( "She sees the access log within this time frame." )
    public void checkLog () {
        waitForAngular();

        assertTrue( driver.findElement( By.name( "dateCell" ) ).getText().contains( "" + today.getDayOfMonth() ) );
        final String monthString = today.getMonth().toString().substring( 0, 1 ).toUpperCase()
                + today.getMonth().toString().substring( 1, 3 ).toLowerCase();
        assertTrue( driver.findElement( By.name( "dateCell" ) ).getText().contains( monthString ) );
        assertTrue( driver.findElement( By.name( "dateCell" ) ).getText().contains( "" + today.getYear() ) );

    }

    @And ( "She enter the date in the wrong text box" )
    public void startDateLaterThanEndDate () {
        waitForAngular();
        setTextField( By.name( "startDate" ), "02/21/2018".replace( "/", "" ) );
        setTextField( By.name( "endDate" ), "02/10/2018".replace( "/", "" ) );
    }

    @Then ( "The Search By Date button is disabled" )
    public void disableBtn () {
        waitForAngular();
        assertTrue( Boolean.parseBoolean( driver.findElement( By.name( "submit" ) ).getAttribute( "disabled" ) ) );
    }

    @And ( "She didn't enter the end date" )
    public void noEndDate () {
        waitForAngular();
        setTextField( By.name( "startDate" ), "01/01/2018".replace( "/", "" ) );
    }

    @And ( "She didn't enter the start date" )
    public void noStartDate () {
        waitForAngular();
        setTextField( By.name( "endDate" ), "12/31/2018".replace( "/", "" ) );
    }

    /**
     * Navigate to prescriptions. Issue #106
     */
    @When ( "The patient goes to the prescriptions page" )
    public void patientGotoPrescriptions () throws Exception {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewPrescriptions').click();" );
    }

    /**
     * Navigate to homepage. Issue #106
     */
    @When ( "The user goes to the HomePage" )
    public void goesToHomePage () throws Exception {
        waitForAngular();
        driver.get( baseUrl );
        waitForAngular();
    }

    /**
     * Testing the bug where viewing prescriptions does not generate a log Issue
     * #106
     */
    @Then ( "The patient sees a PATIENT_PRESCRIPTION_VIEW log" )
    public void checkPrescriptionViewLog () throws InterruptedException {
        waitForAngular();

        assertTrue( driver.getPageSource().contains( "patient" ) );
        // wait.until( ExpectedConditions.textToBePresentInElementLocated(
        // By.tagName( "tbody" ), "patient" ) );
        // wait.until( ExpectedConditions.textToBePresentInElementLocated(
        // By.name( "logTableRow" ),
        // "Patient viewed their list of prescriptions" ) );
    }

}
