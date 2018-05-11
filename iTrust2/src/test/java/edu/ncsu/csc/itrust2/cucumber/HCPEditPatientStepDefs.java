package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
import edu.ncsu.csc.itrust2.models.enums.BloodType;
import edu.ncsu.csc.itrust2.models.enums.Ethnicity;
import edu.ncsu.csc.itrust2.models.enums.Gender;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step Definitions for Edit Demographics class
 *
 * @author Kai Presler-Marshall
 * @author 201-1 Fall 2017
 *
 */
public class HCPEditPatientStepDefs {

    private WebDriver    driver;
    private final String baseUrl = "http://localhost:8080/iTrust2";

    WebDriverWait        wait;

    @Before
    public void setup () {

        driver = new HtmlUnitDriver( true );
        wait = new WebDriverWait( driver, 5 );
    }

    @After
    public void tearDown () {
        driver.quit();
    }

    private void setTextField ( final By byval, final String value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value );
    }

    @Given ( "the required users exist" )
    public void loadRequiredUsers () throws ParseException {

        // make sure the users we need to login exist

        final Patient dbJim = Patient.getByName( "jbean" );
        final Patient jbean = null == dbJim ? new Patient() : dbJim;
        jbean.setSelf( User.getByName( "jbean" ) );
        jbean.setFirstName( "Jim" );
        jbean.setLastName( "Bean" );
        jbean.setEmail( "jbean@gmail.com" );
        jbean.setAddress1( "123 Jim Bean St." );
        jbean.setCity( "Raleigh" );
        jbean.setState( State.NC );
        jbean.setZip( "12345" );
        jbean.setPhone( "123-456-7890" );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/DD/YYYY", Locale.ENGLISH );
        final Calendar time = Calendar.getInstance();
        time.setTime( sdf.parse( "09/22/1985" ) );
        jbean.setDateOfBirth( time );

        jbean.setBloodType( BloodType.BNeg );

        jbean.setEthnicity( Ethnicity.Caucasian );
        jbean.setGender( Gender.Male );

        jbean.save();

        // set Nellie Sanderson's demographics

        final Patient dbNellie = Patient.getByName( "nsanderson" );

        final Patient nsanderson = null == dbNellie ? new Patient() : dbNellie;
        nsanderson.setSelf( User.getByName( "nsanderson" ) );
        nsanderson.setFirstName( "Nellie" );
        nsanderson.setLastName( "Sanderson" );
        nsanderson.setEmail( "nsanderson@gmail.com" );
        nsanderson.setAddress1( "987 Nellie Sanderson Dr." );
        nsanderson.setCity( "Greensboro" );
        nsanderson.setState( State.NC );
        nsanderson.setZip( "27410" );
        nsanderson.setPhone( "946-832-4961" );
        time.setTime( sdf.parse( "12/25/1986" ) );
        nsanderson.setDateOfBirth( time );
        nsanderson.setBloodType( BloodType.ABPos );
        nsanderson.setEthnicity( Ethnicity.Caucasian );
        nsanderson.setGender( Gender.Female );

        nsanderson.save();

    }

    @Given ( "Dr Shelly Vang has logged in and chosen to edit a patient" )
    public void gotoEditPage () throws Exception {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "svang" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editPatientDemographics').click();" );
    }

    @When ( "she selects the patient with first name: (.+) and last name: (.+)" )
    public void selectPatient ( final String first, final String last ) throws Exception {
        final String username = first.toLowerCase().charAt( 0 ) + last.toLowerCase();

        // wait for the patients to load before searching
        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[type=radio][value=" + username + "]" ) ) );
        driver.findElement( By.cssSelector( "input[type=radio][value=" + username + "]" ) ).click();
    }

    @When ( "she changes the zip code to: (.+)" )
    public void changeZipcode ( final String zip ) throws Exception {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "zip" ) ) );
        setTextField( By.name( "zip" ), zip );
    }

    @When ( "she submits the changes" )
    public void submitChanges () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "submit" ) ) );
        driver.findElement( By.name( "submit" ) ).click();
    }

    @Then ( "a success message is displayed" )
    public void checkSuccessMessage () {
        // this will timeout and fail if the page shows an error message
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "success" ),
                "Patient demographics updated successfully." ) );
    }

    @Then ( "an error message is displayed" )
    public void checkErrorMessage () {
        // this will timeout and fail if the page shows a success message
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.name( "success" ),
                "An error occured updating demographics." ) );
    }

    @Then ( "if she changes to patient to (.+) (.+), a popup indicates her changes will be lost" )
    public void checkWarningMessage ( final String first, final String last ) throws Exception {
        // headless webdrivers do not support alerts, so override the confirm
        // function to automatically accept, but still display the message on
        // the page
        // solution here:
        // https://stackoverflow.com/questions/45242264/chromedriver-headless-alerts
        ( (JavascriptExecutor) driver ).executeScript(
                "window.confirm = function(msg) { document.getElementById('header0').textContent = msg; return true; };" );

        selectPatient( first, last );

        // look for the alert message set in the header0 element
        wait.until( ExpectedConditions.textToBePresentInElementLocated( By.id( "header0" ), "You have made changes" ) );
    }

    @When ( "she chooses to continue" )
    public void ignoreWarning () {
        // alert has already been overridden to automatically accept in method
        // above (checkWarningMessage)
    }

    @Then ( "the zip code has the value: (.+)" )
    public void checkZipcode ( final String zip ) throws Exception {
        assertEquals( zip, driver.findElement( By.name( "zip" ) ).getAttribute( "value" ) );
    }

}
