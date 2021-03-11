package edu.ncsu.csc.iTrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.PatientService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * The Step Definitions for Editing user Demographics.
 *
 * @author NC State University Faculty
 * @author Ryan Catalfu (rpcatalf)
 * @author Eddie Woodhouse (ejwoodho)
 *
 */
public class EditMyDemographicsStepDefs extends CucumberTest {

    private final String   patientString = "newGuy";

    private final String   jillString    = "JillBob";

    @Autowired
    private PatientService service;

    @Given ( "A patient exists in the system" )
    public void patientExists () {
        attemptLogout();

        // Create the test User
        final User user = new Patient( new UserForm( patientString, "123456", Role.ROLE_PATIENT, 1 ) );

        final User jill = new Patient( new UserForm( jillString, "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( user );

        service.save( jill );

    }

    @When ( "I log in as a patient" )
    public void loginAsPatient () {
        attemptLogout();

        driver.get( BASE_URL );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( patientString );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        assertEquals( "iTrust2: Patient Home", driver.getTitle() );
    }

    /**
     * Log in as JillBob.
     */
    @When ( "I log in as a specific patient" )
    public void loginAsSpecificPatient () {
        driver.get( BASE_URL );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( jillString );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    @When ( "I navigate to the Edit My Demographics page" )
    public void editDemographics () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics-patient').click();" );
    }

    @When ( "I fill in new, updated demographics" )
    public void fillDemographics () {
        waitForAngular();
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        firstName.clear();
        firstName.sendKeys( "Karl" );

        final WebElement lastName = driver.findElement( By.id( "lastName" ) );
        lastName.clear();
        lastName.sendKeys( "Liebknecht" );

        final WebElement preferredName = driver.findElement( By.id( "preferredName" ) );
        preferredName.clear();

        final WebElement email = driver.findElement( By.id( "email" ) );
        email.clear();
        email.sendKeys( "karl_liebknecht@mail.de" );

        final WebElement address1 = driver.findElement( By.id( "address1" ) );
        address1.clear();
        address1.sendKeys( "Karl Liebknecht Haus. Alexanderplatz" );

        final WebElement city = driver.findElement( By.id( "city" ) );
        city.clear();
        city.sendKeys( "Berlin" );

        final WebElement state = driver.findElement( By.id( "state" ) );
        final Select dropdown = new Select( state );
        dropdown.selectByVisibleText( "DE" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "91505" );

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "123-456-7890" );

        final WebElement dob = driver.findElement( By.id( "dateOfBirth" ) );
        dob.clear();
        dob.sendKeys( "08131950" ); // Enter date without slashes

        final WebElement bt = driver.findElement( By.id( "bloodType" ) );
        final Select btDrop = new Select( bt );
        btDrop.selectByVisibleText( "A+" );

        final WebElement ethnicity = driver.findElement( By.id( "ethnicity" ) );
        final Select ethDrop = new Select( ethnicity );
        ethDrop.selectByVisibleText( "African American" );

        final WebElement gender = driver.findElement( By.id( "gender" ) );
        final Select genderDrop = new Select( gender );
        genderDrop.selectByVisibleText( "Male" );

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        waitForAngular();

    }

    /**
     * Tests editing Jill Bob's demographics with a multi-word city.
     */
    @When ( "I fill in updated demographics with a multi-word city" )
    public void fillUpdatedDemographicsMultiCity () {

        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        firstName.clear();
        firstName.sendKeys( "Jill" );

        final WebElement lastName = driver.findElement( By.id( "lastName" ) );
        lastName.clear();
        lastName.sendKeys( "Bob" );

        final WebElement preferredName = driver.findElement( By.id( "preferredName" ) );
        preferredName.clear();

        final WebElement email = driver.findElement( By.id( "email" ) );
        email.clear();
        email.sendKeys( "sample@ncstate.edu" );

        final WebElement address1 = driver.findElement( By.id( "address1" ) );
        address1.clear();
        address1.sendKeys( "1000 Cates Avenue" );

        final WebElement city = driver.findElement( By.id( "city" ) );
        city.clear();
        city.sendKeys( "Chapel Hill" );

        final WebElement state = driver.findElement( By.id( "state" ) );
        final Select dropdown = new Select( state );
        dropdown.selectByVisibleText( "NC" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "27606" );

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "555-515-1000" );

        final WebElement dob = driver.findElement( By.id( "dateOfBirth" ) );
        dob.clear();
        dob.sendKeys( "08131998" ); // Enter date without slashes

        final WebElement bt = driver.findElement( By.id( "bloodType" ) );
        final Select btDrop = new Select( bt );
        btDrop.selectByVisibleText( "A+" );

        final WebElement ethnicity = driver.findElement( By.id( "ethnicity" ) );
        final Select ethDrop = new Select( ethnicity );
        ethDrop.selectByVisibleText( "Caucasian" );

        final WebElement gender = driver.findElement( By.id( "gender" ) );
        final Select genderDrop = new Select( gender );
        genderDrop.selectByVisibleText( "Female" );

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        waitForAngular();

    }

    @When ( "I fill in a bad phone number" )
    public void fillBadPhone () {

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "111111111111" );

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

    }

    @Then ( "An error message occurs for my phone number" )
    public void errorPhone () {
        assertTrue( driver.getPageSource()
                .contains( "phone can not be empty and must have correct format (e.g. 123-456-7890)" ) );
    }

    @Then ( "The demographics are updated" )
    public void updatedSuccessfully () {
        waitForAngular();
        assertTrue( driver.getPageSource().contains( "Your demographics were updated successfully" ) );
        // Verifies the message actually appears (visible) for demographics
        // updating successfully.
        assertEquals( "Your demographics were updated successfully.",
                driver.findElement( By.xpath( ".//div[@name='success']" ) ).getText() );
    }

    @Then ( "The new demographics can be viewed" )
    public void viewDemographics () {

        driver.get( BASE_URL );
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics-patient').click()" );
        waitForAngular();
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        assertEquals( firstName.getAttribute( "value" ), "Karl" );
        final WebElement address = driver.findElement( By.id( "address1" ) );
        assertEquals( address.getAttribute( "value" ), "Karl Liebknecht Haus. Alexanderplatz" );

        // #51 Bug Update Ethnicity
        final WebElement ethnicity = driver.findElement( By.id( "ethnicity" ) );
        assertEquals( "African American", ethnicity.getAttribute( "value" ) );
    }

    /**
     * Tests viewing JillBob's updated demographics.
     */
    @Then ( "The edited demographics can be viewed" )
    public void viewEditedDemographics () {

        driver.get( BASE_URL );
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics-patient').click()" );
        waitForAngular();
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        assertEquals( "Jill", firstName.getAttribute( "value" ) );
        final WebElement address = driver.findElement( By.id( "address1" ) );
        assertEquals( "1000 Cates Avenue", address.getAttribute( "value" ) );
    }

}
