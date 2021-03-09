package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * The Step Definitions for Editing user Demographics.
 *
 * @author NC State University Faculty
 * @author Ryan Catalfu (rpcatalf)
 * @author Eddie Woodhouse (ejwoodho)
 *
 */
public class EditMyDemographicsStepDefs extends CucumberTest {
    static {
        java.util.logging.Logger.getLogger( "com.gargoylesoftware" ).setLevel( Level.OFF );
    }

    private final String baseUrl       = "http://localhost:8080/iTrust2";
    private final String patientString = "newGuy";

    @Given ( "A patient exists in the system" )
    public void patientExists () {
        attemptLogout();

        // Create the test User
        final User user = new User( patientString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        user.save();

        final Patient patient = new Patient( user.getUsername() );
        patient.save();
    }

    @When ( "I log in as a patient" )
    public void loginAsPatient () {
        attemptLogout();

        driver.get( baseUrl );
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
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "JillBob" );
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

        final WebElement mother = driver.findElement( By.id( "mother" ) );
        mother.clear();

        final WebElement father = driver.findElement( By.id( "father" ) );
        father.clear();

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
        dropdown.selectByVisibleText( "CA" );

        final WebElement zip = driver.findElement( By.id( "zip" ) );
        zip.clear();
        zip.sendKeys( "91505" );

        final WebElement phone = driver.findElement( By.id( "phone" ) );
        phone.clear();
        phone.sendKeys( "123-456-7890" );

        final WebElement dob = driver.findElement( By.id( "dateOfBirth" ) );
        dob.clear();
        dob.sendKeys( "08131950" ); // Enter date without slashes

        // #51 Bug Edit Ethnicity
        final WebElement ethnicity = driver.findElement( By.id( "ethnicity" ) );
        final Select drop = new Select( ethnicity );
        drop.selectByVisibleText( "African American" );

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

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

        final WebElement mother = driver.findElement( By.id( "mother" ) );
        mother.clear();

        final WebElement father = driver.findElement( By.id( "father" ) );
        father.clear();

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

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

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

        driver.get( baseUrl );
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

        driver.get( baseUrl );
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('editdemographics-patient').click()" );
        waitForAngular();
        final WebElement firstName = driver.findElement( By.id( "firstName" ) );
        assertEquals( "Jill", firstName.getAttribute( "value" ) );
        final WebElement address = driver.findElement( By.id( "address1" ) );
        assertEquals( "1000 Cates Avenue", address.getAttribute( "value" ) );
    }

    /**
     * Stepdef for bug fix #49 Checks the last two transactions in the log
     *
     * @param message1
     *            most recent log
     * @param message2
     *            second most recent log
     * @throws InterruptedException
     */
    @Then ( "I go to the Edit My Demographics page and back to the log and check the last 3 messages, (.+) and (.+) and (.+)" )
    public void viewLog ( final String message1, final String message2, final String message3 )
            throws InterruptedException {
        driver.get( driver.getCurrentUrl() );
        waitForAngular();
        driver.get( baseUrl );
        waitForAngular();
        final WebElement firstLog = driver
                .findElement( By.xpath( "//table/tbody/tr[1]/td[@name='transactionTypeCell']" ) );
        final WebElement secondLog = driver
                .findElement( By.xpath( "//table/tbody/tr[2]/td[@name='transactionTypeCell']" ) );
        final WebElement thirdLog = driver
                .findElement( By.xpath( "//table/tbody/tr[3]/td[@name='transactionTypeCell']" ) );
        assertEquals( message1, firstLog.getText() );
        System.out.println( "firstlog: " + firstLog.getText() );
        assertEquals( message2, secondLog.getText() );
        System.out.println( "secondLog: " + secondLog.getText() );
        assertEquals( message3, thirdLog.getText() );
        System.out.println( "thirdLog: " + thirdLog.getText() );
        Thread.sleep( 4000 );
    }

}
