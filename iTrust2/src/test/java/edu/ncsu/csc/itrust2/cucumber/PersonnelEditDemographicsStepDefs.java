package edu.ncsu.csc.iTrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PersonnelEditDemographicsStepDefs extends CucumberTest {

    @When ( "I navigate to the Personnel Edit My Demographics page" )
    public void editDemographicsP () {
        waitForAngular();
        ( (JavascriptExecutor) driver )
                .executeScript( "document.getElementById('editdemographics-personnel').click();" );
        waitForAngular();
    }

    @When ( "I fill in new, updated personnel demographics" )
    public void fillDemographicsP () {
        waitForAngular();

        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        firstName.clear();
        firstName.sendKeys( "Gregor" );

        final WebElement lastName = driver.findElement( By.id( "lastName" ) );
        lastName.clear();
        lastName.sendKeys( "Gysi" );

        final WebElement email = driver.findElement( By.id( "email" ) );
        email.clear();
        email.sendKeys( "gysi@bundestag.de" );

        final WebElement address1 = driver.findElement( By.id( "address1" ) );
        address1.clear();
        address1.sendKeys( "Platz der Republik 1" );

        final WebElement city = driver.findElement( By.id( "city" ) );
        city.clear();
        city.sendKeys( "Berlin" );

        final WebElement state = driver.findElement( By.id( "state" ) );
        final Select dropdown = new Select( state );
        dropdown.selectByVisibleText( "CT" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "11011" );

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "123-456-7890" );

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        waitForAngular();

    }

    @Then ( "The personnel demographics are updated" )
    public void updatedSuccessfullyP () {
        waitForAngular();
        assertTrue( driver.getPageSource().contains( "Your demographics were updated successfully" ) );
    }

    @Then ( "The admin can view new demographics" )
    public void viewDemographicsP () {
        driver.get( BASE_URL );
        waitForAngular();

        ( (JavascriptExecutor) driver )
                .executeScript( "document.getElementById('editdemographics-personnel').click();" );

        waitForAngular();

        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        assertEquals( "Gregor", firstName.getAttribute( "value" ) );

        final WebElement address = driver.findElement( By.id( "address1" ) );
        assertEquals( "Platz der Republik 1", address.getAttribute( "value" ) );
    }
}
