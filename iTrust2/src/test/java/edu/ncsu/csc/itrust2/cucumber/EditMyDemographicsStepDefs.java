package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class EditMyDemographicsStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";

    @Given ( "A patient exists in the system" )
    public void patientExists () {
        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    @When ( "I log in as a patient" )
    public void loginAsPatient () {
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

    @When ( "I navigate to the Edit My Demographics page" )
    public void editDemographics () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics').click();" );
    }

    @When ( "I fill in new, updated demographics" )
    public void fillDemographics () {
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        firstName.clear();
        firstName.sendKeys( "Karl" );

        final WebElement lastName = driver.findElement( By.id( "lastName" ) );
        lastName.clear();
        lastName.sendKeys( "Liebknecht" );

        final WebElement preferredName = driver.findElement( By.id( "preferredName" ) );
        preferredName.clear();

        final WebElement mother = driver.findElement( By.id( "mother" ) );
        mother.clear();

        final WebElement father = driver.findElement( By.id( "father" ) );
        father.clear();

        final WebElement email = driver.findElement( By.id( "email" ) );
        email.clear();
        email.sendKeys( "karl_liebknecht@mail.de" );

        final WebElement address1 = driver.findElement( By.id( "address1" ) );
        address1.clear();
        address1.sendKeys( "Karl-Liebknecht-Haus, Alexanderplatz" );

        final WebElement city = driver.findElement( By.id( "city" ) );
        city.clear();
        city.sendKeys( "Berlin" );

        final WebElement state = driver.findElement( By.id( "state" ) );
        final Select dropdown = new Select( state );
        dropdown.selectByVisibleText( "CA" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "91505" );

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "123-456-7890" );

        final WebElement dob = driver.findElement( By.id( "dateOfBirth" ) );
        dob.clear();
        dob.sendKeys( "08/13/1871" );

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

    }

    @Then ( "The demographics are updated" )
    public void updatedSuccessfully () {
        assertTrue( driver.getPageSource().contains( "Your demographics were updated successfully" ) );
    }

    @Then ( "The new demographics can be viewed" )
    public void viewDemographics () {

        driver.get( baseUrl );
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics').click();" );
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        assertEquals( firstName.getAttribute( "value" ), "Karl" );

        final WebElement address = driver.findElement( By.id( "address1" ) );
        assertEquals( address.getAttribute( "value" ), "Karl-Liebknecht-Haus, Alexanderplatz" );
    }

}
