package edu.ncsu.csc.itrust2.cucumber;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith ( Cucumber.class )
@CucumberOptions ( features = "src/test/resources/edu/ncsu/csc/itrust/cucumber/" )
public class ITRunner {

    @BeforeClass
    public static void setUp () {
        CucumberTest.setup();
    }

    @AfterClass
    public static void tearDown () {
        CucumberTest.tearDown();
    }

}
