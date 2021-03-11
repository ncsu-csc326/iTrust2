package edu.ncsu.csc.iTrust2.cucumber;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * TestRunner class for the Cucumber tests. Adjust the "features" parameter
 * above as necessary to run just a subset of the tests. The body of the class
 * should be blank -- the annotations are all that is required
 *
 * @author Kai Presler-Marshall
 * @author Sarah Elder
 *
 */
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
