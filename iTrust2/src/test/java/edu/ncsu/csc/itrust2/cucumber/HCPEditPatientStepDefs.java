package edu.ncsu.csc.iTrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.time.LocalDate;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.BloodType;
import edu.ncsu.csc.iTrust2.models.enums.Ethnicity;
import edu.ncsu.csc.iTrust2.models.enums.Gender;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 *
 * @author Kai Presler-Marshall
 * @author 201-1 Fall 2017
 **/
public class HCPEditPatientStepDefs extends CucumberTest {

    @Given ( "^Jim Nellie and Shelly exist$" )
    public void loadRequiredUsers () throws ParseException {
        // make sure the users we need to login exist

        final User svang = new Personnel( new UserForm( "svang", "123456", Role.ROLE_HCP, 1 ) );

        userService.save( svang );

        final Patient jbean = new Patient( new UserForm( "jbean", "123456", Role.ROLE_PATIENT, 1 ) );
        jbean.setFirstName( "Jim" );
        jbean.setLastName( "Bean" );
        jbean.setEmail( "jbean@gmail.com" );
        jbean.setAddress1( "123 Jim Bean St." );
        jbean.setCity( "Raleigh" );
        jbean.setState( State.NC );
        jbean.setZip( "12345" );
        jbean.setPhone( "123-456-7890" );
        jbean.setDateOfBirth( LocalDate.parse( "1985-09-22" ) );

        jbean.setBloodType( BloodType.BNeg );

        jbean.setEthnicity( Ethnicity.Caucasian );
        jbean.setGender( Gender.Male );

        userService.save( jbean );

        // set Nellie Sanderson's demographics

        final Patient nsanderson = new Patient( new UserForm( "nsanderson", "123456", Role.ROLE_PATIENT, 1 ) );
        nsanderson.setFirstName( "Nellie" );
        nsanderson.setLastName( "Sanderson" );
        nsanderson.setEmail( "nsanderson@gmail.com" );
        nsanderson.setAddress1( "987 Nellie Sanderson Dr." );
        nsanderson.setCity( "Greensboro" );
        nsanderson.setState( State.NC );
        nsanderson.setZip( "27410" );
        nsanderson.setPhone( "946-832-4961" );
        nsanderson.setDateOfBirth( LocalDate.parse( "1986-12-25" ) );
        nsanderson.setBloodType( BloodType.ABPos );
        nsanderson.setEthnicity( Ethnicity.Caucasian );
        nsanderson.setGender( Gender.Female );

        userService.save( nsanderson );

    }

    @Given ( "^Dr Shelly Vang has logged in and chosen to edit a patient$" )
    public void gotoEditPage () throws Exception {
        attemptLogout();

        driver.get( BASE_URL );
        waitForAngular();
        enterValue( "username", "svang" );
        enterValue( "password", "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editPatientDemographics').click();" );
    }

    @When ( "^she selects the patient with first name: (.+) and last name: (.+)$" )
    public void selectPatient ( final String first, final String last ) throws Exception {
        final String username = first.toLowerCase().charAt( 0 ) + last.toLowerCase();

        // wait for the patients to load before searching
        waitForAngular();
        driver.findElement( By.cssSelector( "input[type=radio][value=" + username + "]" ) ).click();
    }

    @When ( "^she changes the zip code to: (.+)$" )
    public void changeZipcode ( final String zip ) throws Exception {
        waitForAngular();
        enterValue( "zip", zip );
    }

    @When ( "^she submits the changes$" )
    public void submitChanges () {
        waitForAngular();
        driver.findElement( By.name( "submit" ) ).click();
    }

    @Then ( "^a success message is displayed$" )
    public void checkSuccessMessage () {
        waitForAngular();

        // confirm that the message is displayed
        try {
            assertTrue( driver.findElement( By.name( "success" ) ).getText()
                    .contains( "Patient demographics updated successfully." ) );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

    @Then ( "^an error message is displayed$" )
    public void checkErrorMessage () {
        waitForAngular();

        // confirm that the message is displayed
        try {
            assertTrue( driver.findElement( By.name( "success" ) ).getText()
                    .contains( "An error occured updating demographics." ) );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

    @Then ( "^if she changes to patient to (.+) (.+), a popup indicates her changes will be lost$" )
    public void checkWarningMessage ( final String first, final String last ) throws Exception {
        // headless webdrivers do not support alerts, so override the confirm
        // function to automatically accept, but still display the message on
        // the page
        // solution here:
        // https://stackoverflow.com/questions/45242264/chromedriver-headless-alerts
        ( (JavascriptExecutor) driver ).executeScript(
                "window.confirm = function(msg) { document.getElementById('header0').textContent = msg; return true; };" );

        selectPatient( first, last );

        waitForAngular();

        try {
            // look for the alert message set in the header0 element
            assertTrue( driver.findElement( By.id( "header0" ) ).getText().contains( "You have made changes" ) );

        }
        catch ( final Exception e ) {
            fail();
        }
    }

    @When ( "^she chooses to continue$" )
    public void ignoreWarning () {
        // alert has already been overridden to automatically accept in method
        // above (checkWarningMessage)
    }

    @Then ( "^the zip code has the value: (.+)$" )
    public void checkZipcode ( final String zip ) throws Exception {
        assertEquals( zip, driver.findElement( By.name( "zip" ) ).getAttribute( "value" ) );
    }

}
