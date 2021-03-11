package edu.ncsu.csc.iTrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ManageDrugsStepDefs extends CucumberTest {

    private static final String BASE_URL = "http://localhost:8080/iTrust2/";
    private static final String DRUG_URL = BASE_URL + "admin/drugs";

    @When ( "I choose to add a new drug" )
    public void addDrug () {
        driver.get( DRUG_URL );
    }

    @When ( "^submit the values for NDC (.+), name (.+), and description (.*)$" )
    public void submitDrug ( final String ndc, final String name, final String description )
            throws InterruptedException {

        waitForAngular();
        assertEquals( "Admin Manage Drugs", driver.findElement( By.tagName( "h3" ) ).getText() );

        waitForAngular();
        enterValue( "drug", name );
        enterValue( "code", ndc );
        enterValue( "description", description );
        driver.findElement( By.name( "submit" ) ).click();
    }

    @Then ( "^the drug (.+) is successfully added to the system$" )
    public void drugSuccessful ( final String drug ) throws InterruptedException {
        waitForAngular();
        assertEquals( "", driver.findElement( By.id( "errP" ) ).getText() );

        for ( final WebElement r : driver.findElements( By.name( "drugTableRow" ) ) ) {
            if ( r.getText().contains( drug ) ) {
                r.findElement( By.name( "deleteDrug" ) ).click();
            }
        }
        waitForAngular();

        try {
            assertFalse( driver.findElement( By.tagName( "body" ) ).getText().contains( drug ) );

        }
        catch ( final Exception e ) {
            fail();
        }
    }

}
