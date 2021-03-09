package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
 * Find an Expert step def file Validating entries
 *
 * @author Eddie Woodhouse (ejwoodho)
 */

public class FindExpertsStepDefs extends CucumberTest {
    private final String baseUrl       = "http://localhost:8080/iTrust2";
    private final String patientString = "dude";

    /**
     * Asserts that the text is on the page
     *
     * @param text
     *            text to check
     */
    public void assertTextPresent ( final String text ) {
        waitForAngular();
        try {
            assertTrue( driver.getPageSource().contains( text ) );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

    /**
     * selects byVal and enters value
     *
     * @param byVal
     *            element selected
     * @param value
     *            value entered
     */
    private void setTextField ( final By byVal, final Object value ) {
        final WebElement elem = driver.findElement( byVal );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    /**
     * Creates a patient in the system.
     */
    @Given ( "there is at least one patient in the system" )
    public void patientExists () {
        attemptLogout();

        // Create the test User
        final User user = new User( patientString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        user.save();

        final Patient patient = new Patient( user.getUsername() );
        patient.save();

    }

    /**
     * Logs in as a patient.
     */
    @Given ( "I login as a patient" )
    public void loginPatient () {
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
     * Navigates to the Find an Expert page
     */
    @Given ( "I navigate to the Find an Expert page on iTrust2" )
    public void navigateToFindAnExpert () {

        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('findExpert').click();" );
        waitForAngular();
        final WebElement selectSpecialty = driver.findElement( By.xpath( "//form/div[1]/label" ) );
        assertEquals( "Select a Specialty", selectSpecialty.getText() );

        final WebElement enterZip = driver.findElement( By.xpath( "//form/div[2]/label" ) );
        assertEquals( "Enter your Zip Code (optional 4-digit suffix)", enterZip.getText() );

        final WebElement enterRadius = driver.findElement( By.xpath( "//form/div[3]/label" ) );
        assertEquals( "Radius (miles)", enterRadius.getText() );
    }

    /**
     * Step def to test default zip code searches
     *
     * @param zipCode
     *            current default location for the user
     */
    @Given ( "I edit demographics to include my zip (.+)" )
    public void fillDemographics ( final String zipCode ) {
        waitForAngular();
        driver.findElement( By.id( "edit" ) ).click();
        driver.findElement( By.id( "editdemographics-patient" ) ).click();
        waitForAngular();

        setTextField( By.name( "firstName" ), "Eddie" );

        setTextField( By.name( "lastName" ), "Woodhouse" );

        final WebElement preferredName = driver.findElement( By.name( "preferredName" ) );
        preferredName.clear();

        final WebElement mother = driver.findElement( By.name( "mother" ) );
        mother.clear();

        final WebElement father = driver.findElement( By.name( "father" ) );
        father.clear();

        setTextField( By.name( "email" ), "test@email.com" );

        setTextField( By.name( "address1" ), "123 Place" );

        setTextField( By.name( "city" ), "Raleigh" );

        final WebElement stateDropdown = driver.findElement( By.id( "state" ) );
        final Select dropdown = new Select( stateDropdown );
        dropdown.selectByVisibleText( "NC" );
        waitForAngular();

        setTextField( By.id( "zip" ), zipCode );
        setTextField( By.name( "phone" ), "123-456-7890" );

        setTextField( By.name( "dateOfBirth" ), "01012000" ); // Enter date
                                                              // without slashes

        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        waitForAngular();
    }

    /**
     * Test for confirming bug issue #29
     *
     * @param specialty
     *            specialty of the experts
     * @param zipCode
     *            current location
     */
    @When ( "I leave radius as the default value and enter a valid specialty (.+) and zip (.+)" )
    public void defaultInputRadiusValidation ( final String specialty, final String zipCode ) {
        waitForAngular();
        if ( specialty != null ) {
            final WebElement specialtyDropDown = driver.findElement( By.id( "specialty" ) );
            final Select dropdown = new Select( specialtyDropDown );
            dropdown.selectByVisibleText( specialty );
            waitForAngular();
        }

        setTextField( By.name( "zipCode" ), zipCode );
    }

    /**
     * Test for confirming bug issue #29
     *
     * @param specialty
     *            specialty of the experts
     * @param radius
     *            current location
     */
    @When ( "I leave zip code as the default value and enter a valid specialty (.+) and radius (.+)" )
    public void defaultInputZipCodeValidation ( final String specialty, final String radius ) {
        waitForAngular();
        if ( specialty != null ) {
            final WebElement specialtyDropDown = driver.findElement( By.id( "specialty" ) );
            final Select dropdown = new Select( specialtyDropDown );
            dropdown.selectByVisibleText( specialty );
            waitForAngular();
        }
        setTextField( By.name( "radius" ), radius );
    }

    /**
     * Fills form with invalid values
     *
     * @param specialty
     *            specialty of the experts
     * @param zipCode
     *            current location
     * @param radius
     *            mile radius from current location
     */
    @When ( "I enter invalid values for specialty (.+); zip: (.+); and radius: (.+)" )
    public void invalidSearch ( final String specialty, final String zipCode, final String radius ) {
        waitForAngular();
        if ( specialty != null ) {
            final WebElement specialtyDropDown = driver.findElement( By.id( "specialty" ) );
            final Select dropdown = new Select( specialtyDropDown );
            dropdown.selectByVisibleText( specialty );
            waitForAngular();
        }
        setTextField( By.name( "zipCode" ), zipCode );
        setTextField( By.name( "radius" ), radius );

    }

    /**
     * Fills form with valid values
     *
     * @param specialty
     *            specialty of the experts
     * @param zipCode
     *            current location
     * @param radius
     *            mile radius from current location
     */
    @When ( "I enter valid values for specialty (.+); zip: (.+); and radius: (.+)" )
    public void validSearch ( final String specialty, final String zipCode, final String radius ) {
        waitForAngular();
        if ( specialty != null ) {
            final WebElement specialtyDropDown = driver.findElement( By.id( "specialty" ) );
            final Select dropdown = new Select( specialtyDropDown );
            dropdown.selectByVisibleText( specialty );
            waitForAngular();
        }
        setTextField( By.name( "zipCode" ), zipCode );
        setTextField( By.name( "radius" ), radius );

    }

    /**
     * Checks to see if the button is disabled after invalid inputs
     */
    @Then ( "the Find Experts button is disabled" )
    public void checkButtonDisabled () {
        waitForAngular();
        final WebElement btn = driver.findElement( By.name( "findExperts" ) );
        assertFalse( btn.isEnabled() );
    }

    /**
     * Checks for appropriate error messages on the page
     *
     * @param error
     *            message for error
     */
    @Then ( "appropriate error messages (.+) are displayed" )
    public void checkErrorMessages ( final String error ) {
        waitForAngular();
        final WebElement eMessage = driver.findElement( By.xpath( "//form/div[3]/div" ) );
        assertNotEquals( "ng-hide", eMessage.getAttribute( "class" ) );
    }

    /**
     * #61 Test step which shows that a 9 digit zip code demographic setting
     * does not enable the Find Experts button (because it is an empty box)
     *
     * Checks to see if the button is enabled after valid inputs
     *
     */
    @Then ( "the Find Experts button is enabled" )
    public void checkButtonEnabled () {
        waitForAngular();
        final WebElement btn = driver.findElement( By.name( "findExperts" ) );
        assertTrue( btn.isEnabled() );
    }

    /**
     * Checks for correct output
     *
     * @param message
     *            displayed above the expert table
     * @throws InterruptedException
     */
    @Then ( "searching loads a table or message about experts (.+)" )
    public void checkSuccessfulSearch ( final String message ) throws InterruptedException {
        waitForAngular();
        final WebElement btn = driver.findElement( By.name( "findExperts" ) );
        btn.click();
        waitForAngular();
        Thread.sleep( 5000 );
        assertTextPresent( "Hospitals and Specialists" );
        final WebElement result = driver.findElement( By.id( "responseOverTable" ) );
        assertEquals( message, result.getText() );
    }
}
