package edu.ncsu.csc.iTrust2.cucumber;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class CommonStepDefs extends CucumberTest {

    @Autowired
    private HospitalService hospitalService;

    @Given ( "An Admin exists in iTrust2" )
    public void adminExists () {
        final User u = new Personnel( new UserForm( "admin", "123456", Role.ROLE_ADMIN, 1 ) );
        userService.save( u );

    }

    @Given ( "A Patient exists in iTrust2" )
    public void patientExists () {
        final User u = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );
        userService.save( u );
    }

    @Given ( "An HCP exists in iTrust2" )
    public void HCPExists () {
        final User u = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );
        userService.save( u );

    }

    @Given ( "A hospital exists in iTrust2" )
    public void hospitalExists () {
        final Hospital hospital = new Hospital( "iTrust Test Hospital 2", "Bialystok", "10101", State.NJ.toString() );
        hospitalService.save( hospital );
    }

    /**
     * Admin log in
     */
    @When ( "I log in as admin" )
    public void loginAdmin () {
        attemptLogout();

        driver.get( BASE_URL );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "admin" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * HCP log in
     */
    @When ( "I log in as hcp" )
    public void loginHCP () {
        attemptLogout();

        driver.get( BASE_URL );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "hcp" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    /**
     * Logs in as a patient.
     */
    @When ( "I log in as patient" )
    public void loginPatient () {
        attemptLogout();

        driver.get( BASE_URL );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "patient" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

    }

}
