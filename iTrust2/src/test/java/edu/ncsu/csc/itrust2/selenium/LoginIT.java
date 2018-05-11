package edu.ncsu.csc.itrust2.selenium;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class LoginIT {

    static {
        java.util.logging.Logger.getLogger( "com.gargoylesoftware" ).setLevel( Level.OFF );
    }

    private final WebDriver     driver   = new HtmlUnitDriver( true );
    private final String        baseUrl  = "http://localhost:8080/iTrust2";
    private static final String HOME_URL = "http://localhost:8080/iTrust2/ROLE/index";

    private void testLogin ( final String role ) {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( role );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( "123456" );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
        assertEquals( HOME_URL.replace( "ROLE", role ), driver.getCurrentUrl() );
    }

    @Test
    public void hcpShouldLogIn () {
        testLogin( "hcp" );
    }

    @Test
    public void patientShouldLogIn () {
        testLogin( "patient" );
    }

    @Test
    public void adminShouldLogIn () {
        testLogin( "admin" );
    }
}
