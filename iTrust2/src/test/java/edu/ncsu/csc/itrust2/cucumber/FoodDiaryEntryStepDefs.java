package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Step definitions for FoodDiaryEntry feature.
 *
 * @author Jack MacDonald (jmmacdo4)
 */
public class FoodDiaryEntryStepDefs extends CucumberTest {

    private final String baseUrl       = "http://localhost:8080/iTrust2";
    private final String patientString = "bobby";
    private final String hcpString     = "bobbyDoctor";

    /**
     * Asserts that the text is on the page
     *
     * @param text
     *            text to check
     */
    public void assertTextPresent ( final String text ) {
        try {
            assertTrue( driver.getPageSource().contains(text) );
        } catch ( Exception e ) {
            fail();
        }
    }

    private void clickAndCheckDateButton( String viewDate ) {
        List<WebElement> radioList = driver.findElements( By.name( "date" ) );

        // Convert MM/dd/yyyy to yyyy-MM-dd
        final String[] dateComponents = viewDate.split("/");
        final String dateValue = String.format("%s-%s-%s", dateComponents[2], dateComponents[0], dateComponents[1]);

        for ( WebElement element : radioList ) {
            if ( element.getAttribute( "value" ).equals( dateValue ) ) {
                element.click();
                assertTextPresent( "Food Diary Entries for: " + viewDate );
                return;
            }
        }

        fail( "The date isn't in the radio list." );
    }

    @Given ( "^There exists a patient in the system.$" )
    public void patientExistsDiaries () {
        attemptLogout();

        // Create the test User
        final User user = new User( patientString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        user.save();

        // The User must also be created as a Patient 
        // to show up in the list of Patients
        final Patient patient = new Patient( user.getUsername() );
        patient.save();

        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    @Given ( "^There exists an HCP in the system.$" )
    public void hcpExistsDiaries () {
        attemptLogout();

        final User hcp = new User( hcpString, "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_HCP, 1 );
        hcp.save();

        // All tests can safely assume the existence of the 'hcp', 'admin', and
        // 'patient' users
    }

    @Then ( "^I log on as a patient for diaries.$" )
    public void loginPatientDiaries () {
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

    @When ( "^I navigate to the view food diary entries page.$" )
    public void navigateToView() {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewFoodDiaryEntries').click();" );

        assertEquals( "iTrust2: View Food Diary Entries", driver.getTitle() );
    }

    @Then ( "^(.+) is displayed.$")
    public void noDiaryEntries( String text) {
        waitForAngular();
        assertTextPresent( text );
    }


    @When ( "^I navigate to the Add Diary Entry page.$" )
    public void requestAddDiaryPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addFoodDiaryEntry').click();" );
        WebDriverWait wait = new WebDriverWait(driver, 20 );
        wait.until( ExpectedConditions.titleContains( "Add Food Diary Entry" ) );
        assertEquals( "iTrust2: Add Food Diary Entry", driver.getTitle() );
    }

    @And( "^I choose to add a new diary entry with (.+), (.+), (.+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+).$")
    public void addEntry( String date, String type, String name, int servings, int calories, int fat, int sodium, int carbs, int sugars, int fiber, int protein ) {
        waitForAngular();

        WebElement dateElement = driver.findElement( By.name( "date" ) );
        dateElement.sendKeys( date.replace( "/", "" ) );

        Select dropdown = new Select( driver.findElement( By.name( "mealType" ) ) );
        dropdown.selectByVisibleText(type);

        driver.findElement( By.name( "food" ) ).clear();
        driver.findElement( By.name( "food" ) ).sendKeys( name );
        driver.findElement( By.name( "servings" ) ).clear();
        driver.findElement( By.name( "servings" ) ).sendKeys( Integer.toString( servings ) );
        driver.findElement( By.name( "calories" ) ).clear();
        driver.findElement( By.name( "calories" ) ).sendKeys( Integer.toString( calories ) );
        driver.findElement( By.name( "fat" ) ).clear();
        driver.findElement( By.name( "fat" ) ).sendKeys( Integer.toString( fat ) );
        driver.findElement( By.name( "sodium" ) ).clear();
        driver.findElement( By.name( "sodium" ) ).sendKeys( Integer.toString( sodium ) );
        driver.findElement( By.name( "carbs" ) ).clear();
        driver.findElement( By.name( "carbs" ) ).sendKeys( Integer.toString( carbs ) );
        driver.findElement( By.name( "sugars" ) ).clear();
        driver.findElement( By.name( "sugars" ) ).sendKeys( Integer.toString( sugars ) );
        driver.findElement( By.name( "fiber" ) ).clear();
        driver.findElement( By.name( "fiber" ) ).sendKeys( Integer.toString( fiber ) );
        driver.findElement( By.name( "protein" ) ).clear();
        driver.findElement( By.name( "protein" ) ).sendKeys( Integer.toString( protein ) );

        driver.findElement ( By.name( "submit" ) ).click();

    }
    @Then ( "^The diary entry is added successfully.$")
    public void checkForSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, 20 );
        wait.until( ExpectedConditions.titleContains( "View Food Diary Entries" ) );
        assertEquals( "iTrust2: View Food Diary Entries", driver.getTitle() );
    }

    @And ( "^I choose to incorrectly add a new diary entry with (.+), (.+), (.+), (.+), (.+), (.+), (.+), (.+), (.+), (.+), (.+).$" )
    public void incorrectlyAdd( String date, String type, String name, String servings, String calories, String fat, String sodium, String carbs, String sugars, String fiber, String protein ) {
        waitForAngular();
        
        WebElement dateElement = driver.findElement( By.name( "date" ) );
        dateElement.sendKeys( date.replace( "/", "" ) );

        Select dropdown = new Select(driver.findElement(By.name("mealType")));
        dropdown.selectByVisibleText(type);
        driver.findElement( By.name( "food" ) ).clear();
        driver.findElement( By.name( "food" ) ).sendKeys( name );
        driver.findElement( By.name( "servings" ) ).clear();
        driver.findElement( By.name( "servings" ) ).sendKeys( servings );
        driver.findElement( By.name( "calories" ) ).clear();
        driver.findElement( By.name( "calories" ) ).sendKeys( calories );
        driver.findElement( By.name( "fat" ) ).clear();
        driver.findElement( By.name( "fat" ) ).sendKeys( fat );
        driver.findElement( By.name( "sodium" ) ).clear();
        driver.findElement( By.name( "sodium" ) ).sendKeys( sodium );
        driver.findElement( By.name( "carbs" ) ).clear();
        driver.findElement( By.name( "carbs" ) ).sendKeys( carbs );
        driver.findElement( By.name( "sugars" ) ).clear();
        driver.findElement( By.name( "sugars" ) ).sendKeys( sugars );
        driver.findElement( By.name( "fiber" ) ).clear();
        driver.findElement( By.name( "fiber" ) ).sendKeys( fiber );
        driver.findElement( By.name( "protein" ) ).clear();
        driver.findElement( By.name( "protein" ) ).sendKeys( protein );

        driver.findElement ( By.name( "submit" ) ).click();
    }

    @Then ( "^The diary entry is not added.$" )
    public void checkForFailure () {
        assertTrue( driver.getPageSource().contains( "Could not add diary entry." ) );
        //assertTextPresent( "Could not add diary entry", driver );
    }

    @And( "^The patient has added a entry (.+), (.+), (.+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+).$" )
    public void addDiaryEntry ( String date, String type, String name, int servings, int calories, int fat, int sodium, int carbs, int sugars, int fiber, int protein ) {
        loginPatientDiaries();
        requestAddDiaryPage();
        addEntry( date, type, name, servings, calories, fat, sodium, carbs, sugars, fiber, protein );
    }

    @Then( "^The patient has navigated to food diary dashboard.$")
    public void onViewPagePatient() {
        WebDriverWait wait = new WebDriverWait(driver, 20 );
        wait.until( ExpectedConditions.titleContains( "View Food Diary Entries" ) );
        
        assertEquals( "iTrust2: View Food Diary Entries", driver.getTitle() );
    }

    @And( "^The patient selects (.+).")
    public void selectDate( String viewDate ) {
        clickAndCheckDateButton( viewDate );
    }

    @Then( "^The patient can view the entry (.+), (.+), (.+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+).$")
    public void viewEntryPatient( String date, String type, String name, int servings, int calories, int fat, int sodium, int carbs, int sugars, int fiber, int protein ) {
        assertEquals( type, driver.findElement( By.id( "mealType-0" ) ).getText() );
        assertEquals( name, driver.findElement( By.id( "name-0" ) ).getText() );
        assertEquals( servings, Integer.parseInt(driver.findElement( By.id( "servings-0" ) ).getText() ) );
        assertEquals( calories, Integer.parseInt(driver.findElement( By.id( "calories-0" ) ).getText() ) );
        assertEquals( fat, Integer.parseInt(driver.findElement( By.id( "fat-0" ) ).getText() ) );
        assertEquals( sodium, Integer.parseInt(driver.findElement( By.id( "sodium-0" ) ).getText() ) );
        assertEquals( carbs, Integer.parseInt(driver.findElement( By.id( "carbs-0" ) ).getText() ) );
        assertEquals( sugars, Integer.parseInt(driver.findElement( By.id( "sugars-0" ) ).getText() ) );
        assertEquals( fiber, Integer.parseInt(driver.findElement( By.id( "fiber-0" ) ).getText() ) );
        assertEquals( protein, Integer.parseInt(driver.findElement( By.id( "protein-0" ) ).getText() ) );
    }

    @And ( "^This patient behavior is logged on the iTrust2 homepage.$" )
    public void checkLogsPatient() {
        driver.get( baseUrl );

        waitForAngular();
        assertTextPresent( "Patient Views Food Diary Entry" );
    }

    @Then( "^The HCP logs in and has navigated to the food diary entry dashboard.$" )
    public void onViewPageHCP() {
        attemptLogout();

        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( hcpString );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        assertEquals( "iTrust2: HCP Home", driver.getTitle() );

        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('HCPFoodDiary').click();" );

        assertEquals( "iTrust2: View Patient Food Diary", driver.getTitle() );
    }

    @And( "^The HCP selects the entries for the patient on (.+).$" )
    public void selectDateHCP( String viewDate ) {
        waitForAngular();
        driver.findElement( By.id( "bobby" ) ).click();

        waitForAngular();
        clickAndCheckDateButton( viewDate );
    }

    @Then("^The HCP can view the entry (.+), (.+), (.+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+)\\.$")
    public void viewEntryHCP( String date, String type, String name, int servings, int calories, int fat, int sodium, int carbs, int sugars, int fiber, int protein ) throws Throwable {
        assertEquals( type, driver.findElement( By.id( "mealType-0" ) ).getText() );
        assertEquals( name, driver.findElement( By.id( "name-0" ) ).getText() );
        assertEquals( servings, Integer.parseInt(driver.findElement( By.id( "servings-0" ) ).getText() ) );
        assertEquals( calories, Integer.parseInt(driver.findElement( By.id( "calories-0" ) ).getText() ) );
        assertEquals( fat, Integer.parseInt(driver.findElement( By.id( "fat-0" ) ).getText() ) );
        assertEquals( sodium, Integer.parseInt(driver.findElement( By.id( "sodium-0" ) ).getText() ) );
        assertEquals( carbs, Integer.parseInt(driver.findElement( By.id( "carbs-0" ) ).getText() ) );
        assertEquals( sugars, Integer.parseInt(driver.findElement( By.id( "sugars-0" ) ).getText() ) );
        assertEquals( fiber, Integer.parseInt(driver.findElement( By.id( "fiber-0" ) ).getText() ) );
        assertEquals( protein, Integer.parseInt(driver.findElement( By.id( "protein-0" ) ).getText() ) );
    }

    @And ( "^The HCP behavior is logged on the iTrust2 homepage.$" )
    public void checkLoggingHCP() {
        driver.get( baseUrl );

        waitForAngular();
        assertTextPresent( "HCP Views Food Diary Entry" );
    }

    @Then ("^The patient can view the proper totals based on (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+), (\\d+).$" )
    public void checkTotals( int calories, int fat, int sodium, int carbs, int sugars, int fiber, int protein ) {
        waitForAngular();

        int index = 2;
        assertEquals( index * calories, Integer.parseInt(driver.findElement( By.id( "totalCalories" ) ).getText() ) );
        assertEquals( index * fat, Integer.parseInt(driver.findElement( By.id( "totalFat" ) ).getText() ) );
        assertEquals( index * sodium, Integer.parseInt(driver.findElement( By.id( "totalSodium" ) ).getText() ) );
        assertEquals( index * carbs, Integer.parseInt(driver.findElement( By.id( "totalCarbs" ) ).getText() ) );
        assertEquals( index * sugars, Integer.parseInt(driver.findElement( By.id( "totalSugars" ) ).getText() ) );
        assertEquals( index * fiber, Integer.parseInt(driver.findElement( By.id( "totalFiber" ) ).getText() ) );
        assertEquals( index * protein, Integer.parseInt(driver.findElement( By.id( "totalProtein" ) ).getText() ) );
    }

}
