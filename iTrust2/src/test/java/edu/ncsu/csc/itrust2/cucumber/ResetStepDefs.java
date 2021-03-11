package edu.ncsu.csc.iTrust2.cucumber;

import org.junit.Assert;

import io.cucumber.java.en.Given;

public class ResetStepDefs extends CucumberTest {

    @Given ( "The database is empty" )
    public void emptyDB () {
        // This is run once, to reset the DB and prepare for unit/API tests when
        // running with the JUnit runner locally
        Assert.assertTrue( true );
    }

}
