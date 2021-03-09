package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * BloodSugarDiaryStepDef File
 *
 * @author ericstarner
 *
 */
public class BloodSugarDiaryEntryStepDefs extends CucumberTest {
    static {
        java.util.logging.Logger.getLogger( "com.gargoylesoftware" ).setLevel( Level.OFF );
    }

    private final String  baseUrl  = "http://localhost:8080/iTrust2";
    private final String  username = "BillyBob";
    private final String  password = "123456";
    private final String  encoded  = "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.";
    private final User    user     = new User( username, encoded, Role.ROLE_PATIENT, 1 );
    private final Patient patient  = new Patient( user );

    @Before
    public void setupScenarios () {
        attemptLogout();
        user.save();
        patient.save();
    }

    /**
     * Log into iTrust2 system
     *
     */
    @Given ( "^I log in to iTrust2 as a Patient$" )
    public void login () {
        attemptLogout();
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( this.username );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( this.password );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Navigate to Blood Sugar Diary
     */
    @Given ( "^I click the link to enter a new blood sugar diary entry$" )
    public void navigateDiaryEntry () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('addBloodSugarDiaryEntry').click();" );
    }

    /**
     * Make a new Diary Entry
     *
     * @param fasting
     *            fasting blood sugar value
     * @param meal1
     *            meal1 blood sugar value
     * @param meal2
     *            meal2 blood sugar value
     * @param meal3
     *            meal3 blood sugar value
     */
    @When ( "^I enter (\\d+), (\\d+), (\\d+), and (\\d+)$" )
    public void newDiaryEntry ( final int fasting, final int meal1, final int meal2, final int meal3 ) {

        String fastingStr;
        String meal1Str;
        String meal2Str;
        String meal3Str;
        if ( fasting == 0 ) {
            fastingStr = "";
        }
        else {
            fastingStr = Integer.toString( fasting );
        }

        if ( meal1 == 0 ) {
            meal1Str = "";
        }
        else {
            meal1Str = Integer.toString( meal1 );
        }

        if ( meal2 == 0 ) {
            meal2Str = "";
        }
        else {
            meal2Str = Integer.toString( meal2 );
        }

        if ( meal3 == 0 ) {
            meal3Str = "";
        }
        else {
            meal3Str = Integer.toString( meal3 );
        }

        waitForAngular();
        final WebElement fastingValue = driver.findElement( By.id( "wakingUp" ) );
        fastingValue.clear();
        fastingValue.sendKeys( fastingStr );

        final WebElement firstMealValue = driver.findElement( By.id( "firstMeal" ) );
        firstMealValue.clear();
        firstMealValue.sendKeys( meal1Str );

        final WebElement secondMealValue = driver.findElement( By.id( "secondMeal" ) );
        secondMealValue.clear();
        secondMealValue.sendKeys( meal2Str );

        final WebElement thirdMealValue = driver.findElement( By.id( "thirdMeal" ) );
        thirdMealValue.clear();
        thirdMealValue.sendKeys( meal3Str );
    }

    /**
     * Form submission
     *
     * @throws Throwable
     */
    @When ( "^I submit the form successfully$" )
    public void submitForm () {
        waitForAngular();
        final WebElement submit = driver.findElement( By.name( "submitForm" ) );
        submit.click();
        assertTrue( driver.getPageSource().contains( "Successfully Diary Submission" ) );

    }

    /**
     * Previous values for the day in question are displayed
     *
     * @param fasting
     *            fasting blood sugar value
     * @param meal1
     *            meal1 blood sugar value
     * @param meal2
     *            meal2 blood sugar value
     * @param meal3
     *            meal3 blood sugar value
     */
    @Given ( "^The previous values are displayed: (\\d+), (\\d+), (\\d+), and (\\d+)$" )
    public void prevValues ( final int fasting, final int meal1, final int meal2, final int meal3 ) {
        String fastingStr;
        String meal1Str;
        String meal2Str;
        String meal3Str;
        if ( fasting == 0 ) {
            fastingStr = "";
        }
        else {
            fastingStr = Integer.toString( fasting );
        }

        if ( meal1 == 0 ) {
            meal1Str = "";
        }
        else {
            meal1Str = Integer.toString( meal1 );
        }

        if ( meal2 == 0 ) {
            meal2Str = "";
        }
        else {
            meal2Str = Integer.toString( meal2 );
        }

        if ( meal3 == 0 ) {
            meal3Str = "";
        }
        else {
            meal3Str = Integer.toString( meal3 );
        }
        waitForAngular();
        assertEquals( fastingStr, driver.findElement( By.id( "wakingUp" ) ).getAttribute( "value" ) );

        assertEquals( meal1Str, driver.findElement( By.id( "firstMeal" ) ).getAttribute( "value" ) );

        assertEquals( meal2Str, driver.findElement( By.id( "secondMeal" ) ).getAttribute( "value" ) );

        assertEquals( meal3Str, driver.findElement( By.id( "thirdMeal" ) ).getAttribute( "value" ) );
    }

    /**
     * Enter Invalid entries
     *
     * @param fasting
     *            fasting blood sugar value
     * @param meal1
     *            meal1 blood sugar value
     * @param meal2
     *            meal2 blood sugar value
     * @param meal3
     *            meal3 blood sugar value
     */
    @When ( "^I enter invalid data: ([-+]?[0-9]+\\.*[0-9]*), ([-+]?[0-9]+\\.*[0-9]*), ([-+]?[0-9]+\\.*[0-9]*), and ([-+]?[0-9]+\\.*[0-9]*)$$" )
    public void invalidEntry ( final double fasting, final double meal1, final double meal2, final double meal3 ) {
        waitForAngular();
        final WebElement fastingValue = driver.findElement( By.id( "wakingUp" ) );
        fastingValue.clear();
        fastingValue.sendKeys( Double.toString( fasting ) );

        final WebElement firstMealValue = driver.findElement( By.id( "firstMeal" ) );
        firstMealValue.clear();
        firstMealValue.sendKeys( Double.toString( meal1 ) );

        final WebElement secondMealValue = driver.findElement( By.id( "secondMeal" ) );
        secondMealValue.clear();
        secondMealValue.sendKeys( Double.toString( meal2 ) );

        final WebElement thirdMealValue = driver.findElement( By.id( "thirdMeal" ) );
        thirdMealValue.clear();
        thirdMealValue.sendKeys( Double.toString( meal3 ) );
    }

    /**
     * Submission disabled
     *
     */
    @Then ( "^I  attempt to submit the form, but the submit button is disabled$" )
    public void submissionFailed () {
        waitForAngular();
        assertFalse( driver.findElement( By.name( "submitForm" ) ).isEnabled() );
    }

}
