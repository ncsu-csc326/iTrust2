package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class AppointmentRequestStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";

    @Given ( "There is a sample HCP and sample Patient in the database" )
    public void startingUsers () {
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
        driver.findElement( By.className( "btn" ) ).click();

    }

    @Then ( "The appointment is requested successfully" )
    public void requestedSuccessfully () {
        assertTrue( driver.getPageSource().contains( "Your appointment has been requested successfully" ) );
    }

    @Then ( "The appointment can be found in the list" )
    public void findAppointment () {
        driver.findElement( By.linkText( "iTrust2" ) ).click();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewrequests').click();" );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Long value = Calendar.getInstance().getTimeInMillis()
                + 1000 * 60 * 60 * 24 * 14; /* Two weeks */
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( value );
        final String dateString = sdf.format( future.getTime() );
        assertTrue( driver.getPageSource().contains( dateString ) );

    }

    @Given ( "An appointment request exists" )
    public void createAppointmentRequest () {
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
        driver.findElement( By.name( "appointment" ) ).click();
        driver.findElement( By.className( "btn" ) ).click();
    }

    @Then ( "The request is successfully updated" )
    public void requestUpdated () {
        assertTrue( driver.getPageSource().contains( "Appointment request was successfully updated" ) );
    }

    @Then ( "The appointment is in the list of upcoming events" )
    public void upcomingEvents () {
        driver.findElement( By.linkText( "iTrust2" ) ).click();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('upcomingrequests').click();" );

        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Long value = Calendar.getInstance().getTimeInMillis()
                + 1000 * 60 * 60 * 24 * 14; /* Two weeks */
        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( value );
        final String dateString = sdf.format( future.getTime() );
        assertTrue( driver.getPageSource().contains( dateString ) );
        assertTrue( driver.getPageSource().contains( "patient" ) );
    }
}
