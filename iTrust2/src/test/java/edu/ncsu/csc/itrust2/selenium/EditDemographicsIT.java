package edu.ncsu.csc.itrust2.selenium;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import edu.ncsu.csc.itrust2.models.persistent.Patient;

/**
 * Verifies that editing demographics doesn't create a new patient.
 *
 * @author jshore
 *
 */
public class EditDemographicsIT {

    private final WebDriver     driver   = new HtmlUnitDriver();
    private final String        baseUrl  = "http://localhost:8080/iTrust2";
    private static final String HOME_URL = "http://localhost:8080/iTrust2/ROLE/index";

    WebDriverWait               wait     = new WebDriverWait( driver, 10 );

    /**
     * Verify that editing demographics doesn't create a second Patient.
     */
    @Test
    public void testEditDemographics () {
        attemptLogout();
        driver.get( baseUrl + "/login" );
        System.out.println( driver.getCurrentUrl() );
        System.out.println( driver.getPageSource() );
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "username" ) ) );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( "patient" );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        driver.get( baseUrl + "/patient/editDemographics" );
        try {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "firstName" ) ) );
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

            final WebElement submit2 = driver.findElement( By.className( "btn" ) );
            submit2.click();

        }
        catch ( final Exception e ) {
            /*  */
        }
        finally {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "logout" ) ) );
            driver.findElement( By.id( "logout" ) ).click();
        }

        final List<Patient> patientList = Patient.getWhere( "self_id = 'patient'" );
        assertEquals( 1, patientList.size() );
    }

    private void attemptLogout () {
        try {
            driver.get( baseUrl );
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.id( "logout" ) ) );
            driver.findElement( By.id( "logout" ) ).click();
        }
        catch ( final Exception e ) {
            /*
             * Deliberately ignore exceptions from logout element not being
             * found
             */
        }
    }
}
