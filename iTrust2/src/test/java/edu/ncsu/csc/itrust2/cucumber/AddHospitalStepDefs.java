package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;

/**
 * Step definitions for AddHosptial feature
 */
public class AddHospitalStepDefs {

    private final WebDriver driver       = new HtmlUnitDriver( true );
    private final String    baseUrl      = "http://localhost:8080/iTrust2";

    private final String    hospitalName = "TimHortons" + ( new Random() ).nextInt();

    /**
     * Hospital doesn't exist
     */
    @Given ( "The desired hospital doesn't exist" )
    public void noHospital () {
        final List<Hospital> hospitals = Hospital.getHospitals();
        for ( final Hospital hospital : hospitals ) {
            try {
                hospital.delete();
            }
            catch ( final Exception e ) {
                // Assume this hospital is in use & can't be deleted.
            }
        }

    }

    /**
     * Admin login
     */
    @When ( "I log in as an admin" )
    public void loginAdminH () {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "admin" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Add hospital page
     */
    @When ( "I navigate to the Add Hospital page" )
    public void addHospitalPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addhospital').click();" );
    }

    /**
     * Fill hosptial forms
     */
    @When ( "I fill in the values in the Add Hospital form" )
    public void fillHospitalFields () {
        final WebElement name = driver.findElement( By.id( "name" ) );
        name.clear();
        name.sendKeys( hospitalName );

        final WebElement address = driver.findElement( By.id( "address" ) );
        address.clear();
        address.sendKeys( "121 Canada Road" );

        final WebElement state = driver.findElement( By.id( "state" ) );
        final Select dropdown = new Select( state );
        dropdown.selectByVisibleText( "CA" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "00912" );

        driver.findElement( By.className( "btn" ) ).click();
    }

    /**
     * Create hospital successfully
     */
    @Then ( "The hospital is created successfully" )
    public void createdSuccessfully () {
        assertTrue( driver.getPageSource().contains( "Hospital added successfully" ) );
    }

}
