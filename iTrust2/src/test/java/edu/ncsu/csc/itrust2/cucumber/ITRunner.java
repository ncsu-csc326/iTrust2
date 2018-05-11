package edu.ncsu.csc.itrust2.cucumber;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith ( Cucumber.class )
@CucumberOptions ( features = "src/test/resources/edu/ncsu/csc/itrust/cucumber/" )
public class ITRunner {

}
