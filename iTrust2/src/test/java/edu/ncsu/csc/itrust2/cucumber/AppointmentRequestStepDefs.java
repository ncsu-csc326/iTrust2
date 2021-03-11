package edu.ncsu.csc.iTrust2.cucumber;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ncsu.csc.iTrust2.forms.AppointmentRequestForm;
import edu.ncsu.csc.iTrust2.models.AppointmentRequest;
import edu.ncsu.csc.iTrust2.services.AppointmentRequestService;
import edu.ncsu.csc.iTrust2.services.UserService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for AppointmentRequest feature.
 *
 * @author Matt Dzwonczyk (mgdzwonc)
 * @author Kai Presler-Marshall (kpresle)
 */
public class AppointmentRequestStepDefs extends CucumberTest {

    private final String              patientString = "patient";
    private final String              hcpString     = "hcp";
    private final String              odHcpString   = "bobbyOD";
    private final String              ophHcpString  = "robortOPH";

    @Autowired
    private AppointmentRequestService service;

    @Autowired
    private UserService               userService;

    /**
     * Navigates to the Manage Appointment Requests page.
     */
    @When ( "^I navigate to the Manage Appointment Requests page$" )
    public void navigateToView () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('requestappointment').click();" );
        final WebDriverWait wait = new WebDriverWait( driver, 20 );
        wait.until( ExpectedConditions.titleContains( "Request Appointment" ) );
        assertEquals( "iTrust2: Request Appointment", driver.getTitle() );
    }

    /**
     * Navigates to the Manage Appointment Requests page as a patient.
     */
    @When ( "^The patient navigates to the Manage Appointment Requests page$" )
    public void patientNavigateToView () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('requestappointment').click();" );
        final WebDriverWait wait = new WebDriverWait( driver, 20 );
        wait.until( ExpectedConditions.titleContains( "Request Appointment" ) );
        assertEquals( "iTrust2: Request Appointment", driver.getTitle() );
    }

    /**
     * Checks for text displayed for appointment requests
     *
     * @param text
     *            The text to verify as present
     */
    @Then ( "^(.+) is displayed for appointment requests$" )
    public void noAppointmentRequests ( final String text ) {
        waitForAngular();
        assertTextPresent( text );
    }

    /**
     * Adds an appointment request.
     *
     * @param type
     *            The appointment type
     * @param hcp
     *            The HCP to request
     * @param date
     *            The date to request
     * @param time
     *            The time to request
     * @param comments
     *            The comments for the request
     */
    @And ( "^I choose to request a medical appointment with type (.+), HCP (.+), date (.+), time (.+), and comments (.+)$" )
    public void addAppointmentRequest ( final String type, final String hcp, final String date, String time,
            final String comments ) {
        waitForAngular();

        final Select typeDropdown = new Select( driver.findElement( By.name( "type" ) ) );
        typeDropdown.selectByVisibleText( type );

        waitForAngular();

        final Select hcpDropdown = new Select( driver.findElement( By.name( "hcp" ) ) );
        hcpDropdown.selectByVisibleText( hcp );

        driver.findElement( By.name( "date" ) ).clear();
        final WebElement dateElement = driver.findElement( By.name( "date" ) );
        dateElement.sendKeys( date.replace( "/", "" ) );

        // Zero-pad the time for entry
        if ( time.length() == 7 ) {
            time = "0" + time;
        }

        driver.findElement( By.name( "time" ) ).clear();
        final WebElement timeElement = driver.findElement( By.name( "time" ) );
        timeElement.sendKeys( time.replace( ":", "" ).replace( " ", "" ) );

        driver.findElement( By.name( "comments" ) ).clear();
        driver.findElement( By.name( "comments" ) ).sendKeys( comments );

        driver.findElement( By.name( "submitRequest" ) ).click();
    }

    /**
     * Checks to make sure the appointment request is submitted successfully.
     *
     * @param type
     *            The appointment type
     * @param date
     *            The date to request
     * @param time
     *            The time to request
     */
    @Then ( "^The appointment request with type (.+), date (.+), and time (.+) is submitted successfully$" )
    public void checkForSuccess ( final String type, final String date, final String time ) {
        waitForAngular();

        assertTextPresent( "Your appointment request has been requested successfully." );
        assertTextPresent( "Type: " + type );
        assertTextPresent( "Date: " + date );

        deleteRequest( date ); // Delete the request and reset
    }

    /**
     * Deletes an appointment request with the specified date using the DOM.
     *
     * @param date
     *            The date of the appointment request to delete.
     */
    private void deleteRequest ( final String date ) {
        driver.findElement( By.xpath( "//label[text()[contains(.,'Date: " + date + "')]]" ) ).click();

        waitForAngular();

        driver.findElement( By.name( "deleteRequest" ) ).click();
    }

    /**
     * Checks to make sure the appointment request is not submitted.
     */
    @Then ( "^The appointment request is not submitted$" )
    public void checkForFailure () {
        waitForAngular();

        assertTextPresent( "Could not submit appointment request" );
    }

    /**
     * Adds an appointment request for the patient.
     *
     * @param type
     *            The appointment type
     * @param hcp
     *            The HCP to request
     * @param date
     *            The date to request
     * @param time
     *            The time to request
     * @param comments
     *            The comments for the request
     */
    @Given ( "^The patient has requested a medical appointment with type (.+), HCP (.+), date (.+), time (.+), and comments (.+)$" )
    public void createAppointmentRequest ( final String type, final String hcp, final String date, String time,
            final String comments ) throws ParseException {
        patientNavigateToView();

        final AppointmentRequestForm form = new AppointmentRequestForm();
        form.setType( type.toUpperCase().replace( " ", "_" ) );
        form.setHcp( hcp );
        form.setDate( date );

        final String[] dateSplit = date.split( "/" );
        final String month = dateSplit[0].length() == 1 ? "0" + dateSplit[0] : dateSplit[0];
        final String day = dateSplit[1].length() == 1 ? "0" + dateSplit[1] : dateSplit[1];
        final String year = dateSplit[2];

        time = time.replace( ":", " " );
        final String[] timeSplit = time.split( " " );
        String hour = timeSplit[0].length() == 1 ? "0" + timeSplit[0] : timeSplit[0];
        final String minute = timeSplit[1].length() == 1 ? "0" + timeSplit[1] : timeSplit[1];

        if ( timeSplit[2] == "PM" ) {
            hour = ( Integer.parseInt( hour ) + 12 ) + "";
        }

        final LocalDateTime dt = LocalDateTime.now();
        final TimeZone tz = TimeZone.getDefault();
        final ZoneId zone = ZoneId.of( tz.getID() );
        final ZonedDateTime zdt = dt.atZone( zone );
        final ZoneOffset offset = zdt.getOffset();
        final String datetime = String.format( "%s-%s-%sT%s:%s:00.000%s", year, month, day, hour, minute, offset );

        form.setDate( datetime );
        form.setComments( comments );
        form.setStatus( "PENDING" );
        form.setPatient( patientString );

        final AppointmentRequest request = service.build( form );
        service.save( request );

        // addAppointmentRequest( type, hcp, date, time, comments );
    }

    /**
     * Checks to make sure the patient can view the appointment request
     * submitted.
     *
     * @param type
     *            The appointment type
     * @param date
     *            The date to request
     * @param time
     *            The time to request
     */
    @Then ( "^The patient can view the appointment request with type (.+), date (.+), time (.+), and status (.+)$" )
    public void patientViewAppointmentRequest ( final String type, final String date, final String time,
            final String status ) {
        waitForAngular();

        assertTextPresent( "Type: " + type );
        assertTextPresent( "Date: " + date );
        assertTextPresent( "Status: " + status );

        deleteRequest( date ); // Delete the request and reset
    }

    /**
     * Logs in as the specified HCP.
     *
     * @param hcp
     *            The HCP username.
     */
    @When ( "^The HCP (.+) logs in$" )
    public void loginHcp ( final String hcp ) {
        attemptLogout();

        driver.get( BASE_URL );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( hcp );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        assertEquals( "iTrust2: HCP Home", driver.getTitle() );
    }

    /**
     * Navigates to the Appointment Requests page.
     */
    @When ( "^The HCP navigates to the Appointment Requests page$" )
    public void hcpNavigateToApptRequests () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewrequests').click();" );
        final WebDriverWait wait = new WebDriverWait( driver, 20 );
        wait.until( ExpectedConditions.titleContains( "View Appointment Requests" ) );
        assertEquals( "iTrust2: View Appointment Requests", driver.getTitle() );
    }

    /**
     * Checks to make sure the HCP can view the appointment request submitted.
     *
     * @param type
     *            The appointment type
     * @param date
     *            The date to request
     * @param time
     *            The time to request
     */
    @Then ( "^The HCP can view the appointment request with type (.+), date (.+), and time (.+)$" )
    public void hcpViewApptRequest ( final String type, final String date, final String time ) {
        waitForAngular();

        assertTextPresent( "Type: " + type );
        assertTextPresent( "Date: " + date );

    }

    /**
     * HCP selects an appointment request.
     *
     * @param type
     *            The appointment type
     * @param hcp
     *            The HCP to request
     * @param date
     *            The date to request
     * @param time
     *            The time to request
     * @param comments
     *            The comments for the request
     */
    @And ( "^The HCP selects the appointment request with type (.+), HCP (.+), date (.+), time (.+), and comments (.+)$" )
    public void hcpSelectApptRequest ( final String type, final String hcp, final String date, final String time,
            final String comments ) {
        waitForAngular();
        driver.findElement( By.xpath( "//label[text()[contains(.,'Date: " + date + "')]]" ) ).click();
    }

    /**
     * HCP approves the selected appointment request.
     */
    @And ( "^The HCP selects to approve the selected appointment request$" )
    public void hcpApproveApptRequest () {
        final Select statusDropdown = new Select( driver.findElement( By.name( "changeStatus" ) ) );
        statusDropdown.selectByVisibleText( "Approved" );

        driver.findElement( By.name( "submit" ) ).click();
    }

    /**
     * Checks to make sure the appointment request was approved correctly.
     *
     * @param type
     *            The appointment type
     * @param date
     *            The date to request
     */
    @Then ( "^The appointment request with type (.+), HCP (.+), date (.+), time (.+), and comments (.+) is moved into the upcoming medical appointment column$" )
    public void appointmentMovedToUpcoming ( final String type, final String hcp, final String date, final String time,
            final String comments ) {
        waitForAngular();

        assertTextPresent( "Type: " + type );
        assertTextPresent( "Date: " + date );

    }

    /**
     * Checks to make sure the HCP behavior is logged correctly.
     */
    @Then ( "^The HCP behavior is logged on the iTrust2 homepage$" )
    public void hcpBehaviorLoggedOnHomepage () {
        // Needs to be done
        driver.get( BASE_URL );

        final WebDriverWait wait = new WebDriverWait( driver, 20 );
        wait.until( ExpectedConditions.titleContains( "HCP Home" ) );
        assertEquals( "iTrust2: HCP Home", driver.getTitle() );

    }

    /**
     * HCP rejects the selected appointment request.
     */
    @When ( "^The HCP selects to reject the selected appointment request$" )
    public void hcpRejectApptRequest () {
        final Select statusDropdown = new Select( driver.findElement( By.name( "changeStatus" ) ) );
        statusDropdown.selectByVisibleText( "Rejected" );

        driver.findElement( By.name( "submit" ) ).click();
    }

    /**
     * Checks to make sure the appointment request was rejected correctly.
     */
    @Then ( "^The appointment request is deleted from the update medical appointment requests column$" )
    public void appointmentRequestDeleted () {
        waitForAngular();

        assertTextPresent( "Appointment request was successfully updated" );
    }

}
