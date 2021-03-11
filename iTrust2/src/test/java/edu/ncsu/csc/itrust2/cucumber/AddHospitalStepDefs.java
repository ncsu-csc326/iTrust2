package edu.ncsu.csc.iTrust2.cucumber;

import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for AddHosptial feature
 *
 * @author kpresle
 */
public class AddHospitalStepDefs extends CucumberTest {

    private final String hospitalName = "TimHortons";

    /**
     * Add hospital page
     */
    @When ( "I navigate to the Add Hospital page" )
    public void addHospitalPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('deletehospital').click();" );
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
        dropdown.selectByVisibleText( "California" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "00912" );

        driver.findElement( By.id( "submit" ) ).click();
    }

    /**
     * Create hospital successfully
     */
    @Then ( "The hospital is created successfully" )
    public void createdSuccessfully () {
        waitForAngular();
        assertTrue( driver.getPageSource().contains( "Hospital added successfully" ) );
    }

}
