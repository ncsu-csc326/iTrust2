package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class LoginStepDefs extends CucumberTest {
	
	@Given("All user types exist in the system")
	public void allUsers() {
		attemptLogout();
		
		final User hcp = new User( "hcp", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_HCP,
                1 );
        hcp.save();
        
        final User patient = new User( "patient", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_PATIENT, 1 );
        patient.save();

        final User admin = new User( "admin", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.",
                Role.ROLE_ADMIN, 1 );
        admin.save();

        final User er = new User( "er", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_ER,
                1 );
        er.save();
        
        final User lt  = new User( "lt", "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.", Role.ROLE_LABTECH,
                1 );
        lt.save();


	}
	
	@When ( "I log into iTrust2 as a: (.+)" )
    public void patientExists ( final String user ) throws ParseException {
		final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( user );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }
	
	@Then("I should be able to view my homepage")
	public void confirmLogin() {
		assertFalse(driver.getCurrentUrl().contains("login"));
	}
	
	@Then("I should not be logged in")
	public void confirmNotLogin() {
		assertTrue(driver.getCurrentUrl().contains("login"));
	}

}
