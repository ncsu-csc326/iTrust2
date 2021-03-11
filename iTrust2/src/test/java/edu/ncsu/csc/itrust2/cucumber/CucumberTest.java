package edu.ncsu.csc.iTrust2.cucumber;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;

import com.paulhammant.ngwebdriver.NgWebDriver;

import edu.ncsu.csc.iTrust2.services.UserService;
import io.github.bonigarcia.wdm.ChromeDriverManager;

public abstract class CucumberTest {

    @Autowired
    protected UserService userService;

    /* Common steps */

    protected void setTextField ( final By byVal, final Object value ) {
        final WebElement elem = driver.findElement( byVal );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    protected void enterValue ( final String name, final String value ) {
        setTextField( By.name( name ), value );
    }

    protected void selectName ( final String name ) {
        final WebElement element = driver.findElement( By.cssSelector( "input[name='" + name + "']" ) );
        element.click();
    }

    /**
     * Fills in the date and time fields with the specified date and time.
     *
     * @param date
     *            The date to enter.
     * @param time
     *            The time to enter.
     */
    protected void fillInDateTime ( final String dateField, final String date, final String timeField,
            final String time ) {
        fillInDate( dateField, date );
        fillInTime( timeField, time );
    }

    /**
     * Fills in the date field with the specified date.
     *
     * @param date
     *            The date to enter.
     */
    protected void fillInDate ( final String dateField, final String date ) {
        driver.findElement( By.name( dateField ) ).clear();
        final WebElement dateElement = driver.findElement( By.name( dateField ) );
        dateElement.sendKeys( date.replace( "/", "" ) );
    }

    /**
     * Fills in the time field with the specified time.
     *
     * @param time
     *            The time to enter.
     */
    private void fillInTime ( final String timeField, String time ) {
        // Zero-pad the time for entry
        if ( time.length() == 7 ) {
            time = "0" + time;
        }

        driver.findElement( By.name( timeField ) ).clear();
        final WebElement timeElement = driver.findElement( By.name( timeField ) );
        timeElement.sendKeys( time.replace( ":", "" ).replace( " ", "" ) );
    }

    /* Selenium setup stuff */

    static {
        ChromeDriverManager.chromedriver().setup();
    }

    final static protected String BASE_URL = "http://localhost:8080/iTrust2/";

    final static private String   OS       = System.getProperty( "os.name" );

    static protected ChromeDriver driver;

    protected void attemptLogout () {
        try {
            driver.get( BASE_URL );
            driver.findElement( By.id( "logout" ) ).click();
        }
        catch ( final Exception e ) {
            ;
        }
    }

    static public void setup () {

        final ChromeOptions options = new ChromeOptions();
        options.addArguments( "headless" );
        options.setExperimentalOption( "useAutomationExtension", false );
        options.addArguments( "window-size=2000x1000" );
        options.addArguments( "blink-settings=imagesEnabled=false" );
        options.addArguments( "--no-sandbox" );
        options.addArguments( "--disable-dev-shm-usage" );

        if ( Linux() ) {
            options.setBinary( "/usr/bin/google-chrome" );
        }
        else if ( Windows() ) {
            options.setBinary( "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe" );
        }
        else if ( Mac() ) {
            options.setBinary( "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" );
        }

        driver = new ChromeDriver( options );

    }

    static private boolean Mac () {
        return OS.contains( "Mac OS X" );
    }

    static private boolean Linux () {
        return OS.contains( "Linux" );
    }

    static private boolean Windows () {
        return OS.contains( "Windows" );
    }

    static public void tearDown () {
        driver.close();
        driver.quit();

        if ( Windows() ) {
            windowsKill();
        }
        else if ( Linux() || Mac() ) {
            unixKill();
        }

    }

    protected void waitForAngular () {
        new NgWebDriver( driver ).waitForAngularRequestsToFinish();
    }

    static private void windowsKill () {
        try {
            Runtime.getRuntime().exec( "taskkill /f /im chrome.exe" );
            Runtime.getRuntime().exec( "taskkill /f /im chromedriver.exe" );
        }
        catch ( final Exception e ) {
        }
    }

    static private void unixKill () {
        try {
            Runtime.getRuntime().exec( "pkill -f chromium-browser" );
            Runtime.getRuntime().exec( "pkill -f chrome" );
            Runtime.getRuntime().exec( "pkill -f chromedriver" );
        }
        catch ( final Exception e ) {
        }

    }

    /**
     * Asserts that the text is on the page
     *
     * @param text
     *            text to check
     */
    protected void assertTextPresent ( final String text ) {
        try {
            assertTrue( driver.getPageSource().contains( text ) );
        }
        catch ( final Exception e ) {
            fail();
        }
    }

}
