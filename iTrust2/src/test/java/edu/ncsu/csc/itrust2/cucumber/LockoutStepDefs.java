package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.LoginAttempt;
import edu.ncsu.csc.itrust2.models.persistent.LoginBan;
import edu.ncsu.csc.itrust2.models.persistent.LoginLockout;

public class LockoutStepDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";
    WebDriverWait           wait    = new WebDriverWait( driver, 2 );

    @Given ( "The user (.+) with password (.+) and the current machine have no failed login attempts" )
    public void clearAttempts ( final String username, final String correct ) {
        // attempts cleared by logging in
        driver.get( baseUrl );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "username" ) ) );

        final WebElement usernameField = driver.findElement( By.name( "username" ) );
        usernameField.clear();
        usernameField.sendKeys( username );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( correct );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "logout" ) ) );
        driver.findElement( By.id( "logout" ) ).click();
    }

    @When ( "I try to login as (.+) with password (.+)" )
    public void attemptLogin ( final String username, final String password ) {
        driver.get( baseUrl );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "username" ) ) );

        final WebElement usernameField = driver.findElement( By.name( "username" ) );
        usernameField.clear();
        usernameField.sendKeys( username );
        final WebElement passwordField = driver.findElement( By.name( "password" ) );
        passwordField.clear();
        passwordField.sendKeys( password );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

    }

    @Then ( "My credentials are incorrect" )
    public void verifyIncorrectCredentials () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.className( "alert-error" ) ) );
        assertTrue( driver.findElement( By.className( "alert-error" ) ).getText()
                .contains( "Invalid username and password." ) );
    }

    @Then ( "My account is locked for one hour" )
    public void verifyAccountLocked () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.className( "alert-error" ) ) );
        assertTrue( driver.findElement( By.className( "alert-error" ) ).getText()
                .contains( "Too many invalid logins. Account locked for 1 hour." ) );
    }

    @Then ( "I login successfully" )
    public void verifyLoginSuccess () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "logout" ) ) );
    }

    @When ( "I logout" )
    public void logout () {
        driver.findElement( By.id( "logout" ) ).click();
    }

    @When ( "I try to login (.+) times as (.+) with password (.+)" )
    public void login3Times ( final String n, final String username, final String password ) {
        // n-1 b/c dont verify results of last login
        for ( int i = 0; i < Integer.parseInt( n ) - 1; i++ ) {
            attemptLogin( username, password );
            verifyIncorrectCredentials();
        }
        attemptLogin( username, password );
    }

    @Then ( "My account is banned" )
    public void verifyAccountBan () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.className( "alert-error" ) ) );
        assertTrue( driver.findElement( By.className( "alert-error" ) ).getText()
                .contains( "This account has been locked. Please contact a system administrator to re-enable." ) );
    }

    @Then ( "My IP is locked for one hour" )
    public void verifyIPLock () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.className( "alert-error" ) ) );
        assertTrue( driver.findElement( By.className( "alert-error" ) ).getText()
                .contains( "Too many invalid logins. This IP is blocked for 1 hour." ) );
    }

    @Then ( "My IP is banned" )
    public void verifyIPBan () {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.className( "alert-error" ) ) );
        assertTrue( driver.findElement( By.className( "alert-error" ) ).getText()
                .contains( "This IP has been banned. Please contact a system administrator to re-enable." ) );
    }

    @When ( "I wait one hour" )
    public void wait1Hour () {
        /*
         * While generally bad practice to access the DB/backend in a black box
         * test, this simulates waiting 1 hour by moving the timestamp for all
         * LoginLockout entries back 61 minutes. This is significantly faster
         * than waiting one hour for the tests to run, easier and less dangerous
         * than moving the system time forward, and maintains security of the
         * system by not requiring exposing endpoints to manipulate the status
         * of locked out and banned users or IPs (Since shifting a lockout
         * should never be a real operation).
         */
        LoginLockout.getLockouts().stream().forEach( x -> {
            final LoginLockout a = x;
            a.getTime().setTimeInMillis( a.getTime().getTimeInMillis() - ( 1000 * 60 * 61 ) );
            a.save();
        } );
    }

    /**
     * Ensures that nothing done in this test affects the rest of the system or
     * tests.
     */
    @After
    public void cleanUp () {
        DomainObject.deleteAll( LoginAttempt.class );
        DomainObject.deleteAll( LoginLockout.class );
        DomainObject.deleteAll( LoginBan.class );
    }

}
