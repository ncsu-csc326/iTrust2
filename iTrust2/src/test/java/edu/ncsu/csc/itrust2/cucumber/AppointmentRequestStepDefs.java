package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class AppointmentRequestStepDefs extends CucumberTest {

    private final String baseUrl = "http://localhost:8080/iTrust2";

    @Given ( "There is a sample HCP and sample Patient in the database" )
    public void startingUsers () {
        attemptLogout();

        final User hcp = new User( "hcp", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_HCP,
                1 );
        hcp.save();

        final User patient = new User( "patient", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        patient.save();
    }

    @When ( "I log in as patient" )
    public void loginPatient () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "patient" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    @When ( "I navigate to the Request Appointment page" )
    public void requestPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('requestappointment').click();" );
    }

    @When ( "I fill in values in the Appointment Request Fields" )
    public void fillFields () {
        waitForAngular();
        final Select hcp = new Select( driver.findElement( By.id( "hcp" ) ) );
        hcp.selectByVisibleText( "hcp" );

        final WebElement date = driver.findElement( By.id( "date" ) );
        date.clear();
        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Long value = Calendar.getInstance().getTimeInMillis()
                + 1000 * 60 * 60 * 24 * 14; /* Two weeks */
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( value );
        date.sendKeys( sdf.format( future.getTime() ) );
        final WebElement time = driver.findElement( By.id( "time" ) );
        time.clear();
        time.sendKeys( "11:59 PM" );
        final WebElement comments = driver.findElement( By.id( "comments" ) );
        comments.clear();
        comments.sendKeys( "Test appointment please ignore" );
        driver.findElement( By.name( "submit" ) ).click();

    }

    /**
     * Fields filled with bad values
     */
    @When ( "I improperly fill in values in the Appointment Request Fields" )
    public void fillFieldsWrong () {
        waitForAngular();
        final WebElement date = driver.findElement( By.id( "date" ) );
        date.clear();
        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Long value = Calendar.getInstance().getTimeInMillis()
                - 1000 * 60 * 60 * 24 * 14; /* Two weeks */
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( value );
        date.sendKeys( sdf.format( future.getTime() ) );
        final WebElement time = driver.findElement( By.id( "time" ) );
        time.clear();
        time.sendKeys( "11:59 PM" );
        final WebElement comments = driver.findElement( By.id( "comments" ) );
        comments.clear();
        comments.sendKeys( "Test appointment please ignore" );
        driver.findElement( By.className( "btn" ) ).click();

    }

    /**
     * Testing the bug where the message "'s on at" is displayed.
     */
    @Then ( "The page does not say quote s on at" )
    public void noSOnAt () {
        assertTrue( !driver.getPageSource().contains( "'s on at" ) );
    }

    @Then ( "The appointment is requested successfully" )
    public void requestedSuccessfully () {
        waitForAngular();
        assertTrue( driver.getPageSource().contains( "Your appointment has been requested successfully" ) );
        waitForAngular();
    }

    /**
     * An error message for a date gone by
     */
    @Then ( "An error message appears telling me what is wrong" )
    public void requestedUnsucessfully () {
        assertTrue( driver.getPageSource().contains( "Cannot request an appointment before the current time" ) );
    }

    @Then ( "The appointment can be found in the list" )
    public void findAppointment () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('requestappointment').click();" );

        final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy", Locale.ENGLISH );
        final Long value = Calendar.getInstance().getTimeInMillis()
                + 1000 * 60 * 60 * 24 * 14; /* Two weeks */
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( value );
        final String dateString = sdf.format( future.getTime() );
        waitForAngular();

        assertTrue( driver.getPageSource().contains( dateString ) );

    }

    @Given ( "An appointment request exists" )
    public void createAppointmentRequest () {
        attemptLogout();

        DomainObject.deleteAll( AppointmentRequest.class );

        final AppointmentRequest ar = new AppointmentRequest();
        ar.setComments( "Test request" );
        ar.setPatient( User.getByNameAndRole( "patient", Role.ROLE_PATIENT ) );
        ar.setHcp( User.getByNameAndRole( "hcp", Role.ROLE_HCP ) );
        final Calendar time = Calendar.getInstance();
        time.setTimeInMillis( Calendar.getInstance().getTimeInMillis() + 1000 * 60 * 60 * 24 * 14 );
        ar.setDate( time );
        ar.setStatus( Status.PENDING );
        ar.setType( AppointmentType.GENERAL_CHECKUP );
        ar.save();
    }

    @When ( "I log in as hcp" )
    public void loginHcp () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    @When ( "I navigate to the View Requests page" )
    public void viewRequests () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewrequests').click();" );

    }

    @When ( "I approve the Appointment Request" )
    public void approveRequest () {
        waitForAngular();
        driver.findElement( By.name( "appointment" ) ).click();

        final Select role = new Select( driver.findElement( By.id( "status" ) ) );
        role.selectByVisibleText( "APPROVED" );

        driver.findElement( By.name( "submit" ) ).click();
    }

    @Then ( "The request is successfully updated" )
    public void requestUpdated () {
        assertTrue( driver.getPageSource().contains( "Appointment request was successfully updated" ) );
    }

    @Then ( "The appointment is in the list of upcoming events" )
    public void upcomingEvents () {
        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Long value = Calendar.getInstance().getTimeInMillis()
                + 1000 * 60 * 60 * 24 * 14; /* Two weeks */
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( value );
        final String dateString = sdf.format( future.getTime() );
        assertTrue( driver.getPageSource().contains( dateString ) );
        assertTrue( driver.getPageSource().contains( "patient" ) );
    }

    /**
     * Navigate the user to the view appointment requests page
     */
    @When ( "I navigate to the View Appointment Requests page" )
    public void requestViewAppointmentPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('requestappointment').click();" );
    }
}
