package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Class for Cucumber Testing of Personal Representatives feature.
 *
 * @author tadicke3
 */
public class PersonalRepresentativesStepDefs extends CucumberTest {

    private final String baseUrl = "http://localhost:8080/iTrust2";

    private void setTextField ( final By byval, final Object value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    /**
     * Login as admin and create users Wario and Waluigi
     */
    @Given ( "The relevant users exist" )
    public void createUsers () {
        attemptLogout();

        waitForAngular();
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "admin" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();
        waitForAngular();

        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('users').click();" );
        waitForAngular();

        setTextField( By.name( "username" ), "Wario" );
        setTextField( By.name( "password" ), "123456" );
        setTextField( By.name( "password2" ), "123456" );
        final Select dropdown = new Select( driver.findElement( By.id( "role" ) ) );
        dropdown.selectByVisibleText( "Patient" );
        driver.findElement( By.name( "enabled" ) ).click();
        driver.findElement( By.id( "submit" ) ).click();
        final Patient wario = new Patient();
        waitForAngular();
        wario.setSelf( User.getByName( "Wario" ) );
        wario.setFirstName( "wario" );
        wario.setLastName( "Smith" );
        wario.setDateOfBirth( LocalDate.now().minusYears( 13 ) ); // Thirteen years old
        wario.save();

        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('users').click();" );
        waitForAngular();

        setTextField( By.name( "username" ), "Waluigi" );
        setTextField( By.name( "password" ), "123456" );
        setTextField( By.name( "password2" ), "123456" );
        final Select other = new Select( driver.findElement( By.id( "role" ) ) );
        other.selectByVisibleText( "Patient" );
        driver.findElement( By.name( "enabled" ) ).click();
        driver.findElement( By.id( "submit" ) ).click();

        final Patient waluigi = new Patient();
        waitForAngular();
        waluigi.setSelf( User.getByName( "Waluigi" ) );
        waluigi.setFirstName( "waluigi" );
        waluigi.setLastName( "Smith" );
        waluigi.setDateOfBirth( LocalDate.now().minusYears( 13 ) ); // Thirteen years old
        waluigi.save();
        waitForAngular();
    }

    /**
     * Login as patient Wario.
     */
    @Given ( "I am a patient with no representatives" )
    public void loginAsWario () {
        attemptLogout();

        driver.get( baseUrl );
        setTextField( By.name( "username" ), "Wario" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Login as patient Waluigi.
     */
    @Given ( "I am a patient who represents another patient" )
    public void loginAsWaluigi () {
        attemptLogout();

        loginAsWario();
        navigateToPR();
        addNewPersonalReprsentative();
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('logout').click();" );
        waitForAngular();

        setTextField( By.name( "username" ), "Waluigi" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Login as HCP Shelly Vang
     */
    @Given ( "I am an HCP" )
    public void loginAsShelly () {
        attemptLogout();

        driver.get( baseUrl );
        waitForAngular();

        setTextField( By.name( "username" ), "svang" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        waitForAngular();
    }

    /**
     * Navigate to the Personal Representatives page.
     */
    @When ( "I navigate to Personal Representatives" )
    public void navigateToPR () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('personalreps').click();" );
        waitForAngular();
    }

    /**
     * Navigate to the HCP Personal Representatives page.
     */
    @When ( "I navigate to the HCP Personal Representatives" )
    public void navigateToHCPPR () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('HCPpersonalreps').click();" );
        waitForAngular();
    }

    /**
     * Search for a user to declare a representative for
     */
    @When ( "I search for a user" )
    public void findWario () {
        // Assert that the declare button will be clickable
        waitForAngular();

        setTextField( By.id( "patientSearch" ), "Wario" );
        waitForAngular();

        driver.findElement( By.id( "Wario-declare" ) ).click();
        waitForAngular();

        driver.findElement( By.id( "declareButton" ) ).click();
    }

    /**
     * Search for Waluigi to add as a representative
     */
    @When ( "I select a new representative to add" )
    public void findWaluigi () {
        waitForAngular();
        driver.findElement( By.id( "repSearch" ) ).clear();
        setTextField( By.id( "repSearch" ), "Waluigi" );

        // Assert that the radio button will be clickable
        waitForAngular();
        driver.findElement( By.id( "Waluigi-representative" ) ).click();

        waitForAngular();
        driver.findElement( By.id( "confirmDeclareButton" ) ).click();

        waitForAngular();
    }

    /**
     * Add Waluigi as new Personal Representative.
     */
    @When ( "I add a new Personal Representative" )
    public void addNewPersonalReprsentative () {
        // Assert that the declare button will be clickable
        waitForAngular();

        driver.findElement( By.id( "declareButton" ) ).click();
        waitForAngular();
        setTextField( By.id( "patientSearch" ), "Waluigi" );

        waitForAngular();
        driver.findElement( By.id( "Waluigi-declare" ) ).click();
        waitForAngular();
        driver.findElement( By.id( "confirmDeclareButton" ) ).click();
        waitForAngular();
    }

    /**
     * Remove self as Wario's Personal Representative.
     */
    @When ( "I unassign the patient who I represent" )
    public void removePersonalRepresentative () {
        waitForAngular();

        assertTrue( driver.getPageSource().contains( "Wario" ) );

        driver.findElement( By.id( "undeclareButton" ) ).click();
        waitForAngular();

        driver.findElement( By.id( "Wario-representing" ) ).click();
        driver.findElement( By.id( "confirmUndeclareButton" ) ).click();
    }

    /**
     * Verify success message of "Waluigi was successfully added as a Personal
     * Representative".
     */
    @Then ( "A success message is displayed that I added a representative" )
    public void successAdd () {
        assertTrue( driver.getPageSource().contains( "Successfully declared a representative" ) );
        driver.findElement( By.id( "closeAlert" ) ).click();
        waitForAngular();
        assertTrue( driver.findElement( By.id( "Waluigi-representativeName" ) ).isDisplayed() );
    }

    /**
     * Verify success message for adding Waluigi, and he is in the list.
     */
    @Then ( "A success message is displayed that I added another representative" )
    public void successAdd2 () {
        assertTrue( driver.getPageSource().contains( "Successfully declared a representative" ) );
        driver.findElement( By.id( "closeAlert" ) ).click();

        // now delete the representative for repeat testing
        warioDeleteRep();
    }

    /**
     * Removes Waluigi as a personal representative
     */
    public void warioDeleteRep () {

        loginAsWario();
        navigateToPR();
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('undeclareButton').click();" );
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('-representatives').click();" );
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('confirmUndeclareButton').click();" );

    }

    /**
     * Verify success message of "Successfully removed self as a Personal
     * Representative from Wario".
     */
    @Then ( "A success message is displayed that I removed myself as representative" )
    public void successRemove () {
        assertTrue( driver.getPageSource().contains( "Successfully undeclared representative" ) );
    }

    /**
     * Verify Wario is seen as being represented by me.
     */
    @Then ( "I see the patient who I represent" )
    public void seePatient () {
        // Give time for JS to load
        waitForAngular();
        driver.findElement( By.id( "Wario-representingName" ) ).isEnabled();
    }

    /**
     * Verify Wario is not seen as being represented by me.
     */
    @Then ( "I do not see the patient who I represented" )
    public void notSeePatient () {
        // Give time for JS to load
        waitForAngular();

        try {
            driver.findElement( By.id( "Wario-representingName" ) ).isEnabled();
            fail();
        }
        catch ( final NoSuchElementException e ) {
            // Very good!
        }
    }
}
