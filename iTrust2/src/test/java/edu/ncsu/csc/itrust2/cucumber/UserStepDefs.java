package edu.ncsu.csc.iTrust2.cucumber;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class UserStepDefs extends CucumberTest {

    private final String jenkinsUsername = "drJenkins";

    /**
     * Navigate to add user page
     */
    @When ( "I navigate to the Add User page" )
    public void addUserPage () {
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('users').click();" );
    }

    /**
     * Fill in add user values
     */
    @When ( "I fill in the values in the Add User form" )
    public void fillFields () {
        final WebElement username = driver.findElement( By.id( "username" ) );
        username.clear();
        username.sendKeys( jenkinsUsername );

        final WebElement password = driver.findElement( By.id( "password" ) );
        password.clear();
        password.sendKeys( "123456" );

        final WebElement password2 = driver.findElement( By.id( "password2" ) );
        password2.clear();
        password2.sendKeys( "123456" );

        final WebElement role = driver.findElement( By.id( "ROLE_HCP" ) );
        role.click();

        final WebElement enabled = driver.findElement( By.name( "enabled" ) );
        enabled.click();

        driver.findElement( By.id( "submit" ) ).click();

    }

    @When ( "I fill in invalid roles in the Add User form" )
    public void fillInInvalid () {
        final WebElement username = driver.findElement( By.id( "username" ) );
        username.clear();
        username.sendKeys( jenkinsUsername );

        final WebElement password = driver.findElement( By.id( "password" ) );
        password.clear();
        password.sendKeys( "123456" );

        final WebElement password2 = driver.findElement( By.id( "password2" ) );
        password2.clear();
        password2.sendKeys( "123456" );

        final WebElement role = driver.findElement( By.id( "ROLE_HCP" ) );
        role.click();

        final WebElement role2 = driver.findElement( By.id( "ROLE_PATIENT" ) );
        role2.click();

        final WebElement enabled = driver.findElement( By.name( "enabled" ) );
        enabled.click();

        driver.findElement( By.id( "submit" ) ).click();
    }

    @When ( "I fill in incomplete values in the Add User form" )
    public void fillInIncomplete () {
        final WebElement username = driver.findElement( By.id( "username" ) );
        username.clear();
        username.sendKeys( jenkinsUsername );

        final WebElement password = driver.findElement( By.id( "password" ) );
        password.clear();
        password.sendKeys( "123456" );

        final WebElement password2 = driver.findElement( By.id( "password2" ) );
        password2.clear();
        password2.sendKeys( "123456" );

        final WebElement enabled = driver.findElement( By.name( "enabled" ) );
        enabled.click();

        driver.findElement( By.id( "submit" ) ).click();
    }

    @When ( "I fill in the values in the Add User form with two valid roles" )
    public void twoValidRoles () {
        final WebElement username = driver.findElement( By.id( "username" ) );
        username.clear();
        username.sendKeys( jenkinsUsername );

        final WebElement password = driver.findElement( By.id( "password" ) );
        password.clear();
        password.sendKeys( "123456" );

        final WebElement password2 = driver.findElement( By.id( "password2" ) );
        password2.clear();
        password2.sendKeys( "123456" );

        final WebElement role = driver.findElement( By.id( "ROLE_HCP" ) );
        role.click();

        final WebElement role2 = driver.findElement( By.id( "ROLE_ER" ) );
        role2.click();

        final WebElement enabled = driver.findElement( By.name( "enabled" ) );
        enabled.click();

        driver.findElement( By.id( "submit" ) ).click();
    }

    @Then ( "The user is not created successfully" )
    public void userNotCreated () {
        waitForAngular();

        final WebElement error = driver.findElement( By.id( "error" ) );

        Assert.assertEquals( "Could not add User.", error.getText() );

        final WebElement success = driver.findElement( By.id( "success" ) );

        Assert.assertEquals( "", success.getText() );

        Assert.assertNull( userService.findByName( jenkinsUsername ) );

    }

    /**
     * Create user
     */
    @Then ( "The user is created successfully" )
    public void createdSuccessfully () {
        assertTrue( driver.getPageSource().contains( "User added successfully" ) );
    }

    /**
     * User login
     */
    @Then ( "The new user can login" )
    public void tryLogin () {
        driver.findElement( By.id( "logout" ) ).click();

        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( jenkinsUsername );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        waitForAngular();

        /**
         * Not an assert statement in the typical sense, but we know that we can
         * log in if we can find the "iTrust" button in the top-left after
         * attempting to do so.
         */
        try {
            waitForAngular();
            driver.findElement( By.linkText( "iTrust2" ) );
        }
        catch ( final Exception e ) {
            fail();
        }

        Assert.assertNotNull( userService.findByName( jenkinsUsername ) );
    }

    @Then ( "The new user has both roles" )
    public void bothRoles () {
        final User u = userService.findByName( jenkinsUsername );

        Assert.assertTrue( u.getRoles().contains( Role.ROLE_HCP ) );
        Assert.assertTrue( u.getRoles().contains( Role.ROLE_ER ) );

        Assert.assertEquals( 2, u.getRoles().size() );
    }

}
