package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

public class ViewAccessLogDefs {

    private final WebDriver driver  = new HtmlUnitDriver( true );
    private final String    baseUrl = "http://localhost:8080/iTrust2";
    WebDriverWait           wait    = new WebDriverWait( driver, 2 );

    @Before
    public void setup () {

        HibernateDataGenerator.generateUsers();

    }

    private void setTextField ( final By byval, final Object value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    @After
    public void tearDown () {
        driver.close();
    }

    @When ( "(.+) has logged in with password and chosen to view the access log" )
    public void goToLogPage ( final String user ) {
        driver.get( baseUrl );
        setTextField( By.name( "username" ), "svang" );
        setTextField( By.name( "password" ), "123456" );
        driver.findElement( By.className( "btn" ) ).click();

        wait.until( ExpectedConditions.not( ExpectedConditions.titleIs( "iTrust2 :: Login" ) ) );

    }

    @Then ( "The first ten record should appear on the screen" )
    public void goToTheLogPage () {
        driver.findElement( By.className( "navbar-brand" ) ).click();

        assertTrue( driver.getPageSource().contains( "svang" ) );
    }

    @When ( "She selects the start date and end date" )
    public void correctTime () {

        final String pattern = "mm-dd-yyyy";
        final String dateInString = new SimpleDateFormat( pattern ).format( new Date() );
        setTextField( By.name( "startDate" ), dateInString );
        setTextField( By.name( "endDate" ), dateInString );
        driver.findElement( By.name( "submit" ) ).click();

        assertTrue( driver.getPageSource().contains( "Successful login" ) );

    }

    @Then ( "She sees the access log within this time frame." )
    public void checkLog () {
        assertTrue( driver.getPageSource().contains( "Successful login" ) );
    }

    @And ( "She enter the date in the wrong text box" )
    public void startDateLaterThanEndDate () {

        setTextField( By.name( "startDate" ), "02/21/2018" );
        setTextField( By.name( "endDate" ), "02/10/2018" );

    }

    @Then ( "The Search By Date button is disabled" )
    public void disableBtn () {
        assertTrue( Boolean.parseBoolean( driver.findElement( By.name( "submit" ) ).getAttribute( "disabled" ) ) );
    }

    @And ( "She didn't enter the end date" )
    public void noEndDate () {
        setTextField( By.name( "startDate" ), "01/01/2018" );
        driver.findElement( By.name( "endDate" ) ).clear();

    }

    @And ( "She didn't enter the start date" )
    public void noStartDate () {
        setTextField( By.name( "endDate" ), "12/31/2018" );
        driver.findElement( By.name( "startDate" ) ).clear();

    }

}
