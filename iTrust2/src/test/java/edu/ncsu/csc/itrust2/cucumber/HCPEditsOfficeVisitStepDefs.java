package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;

/**
 * Step defs that define the tests for editing an office visit
 * 
 * @author jltalare
 *
 */
public class HCPEditsOfficeVisitStepDefs extends CucumberTest {

	private final String baseUrl = "http://localhost:8080/iTrust2";
	private final String ophHcpString = "bobbyOPH";
	private final String patientString = "bobby";

	/**
	 * Asserts that the text is on the page
	 *
	 * @param text
	 *            text to check
	 */
	public void assertTextPresent(final String text) {
		try {
			assertTrue(driver.getPageSource().contains(text));
		} catch (final Exception e) {
			fail();
		}
	}

	/**
	 * Logins in as an HCP and navigates to the Edit Office Visit page
	 */
	@Then("^The HCP logs in and navigates to the Edit Office Visit page$")
	public void editPageLogin() {
		attemptLogout();

		driver.get(baseUrl);

		final WebElement username = driver.findElement(By.name("username"));
		username.clear();
		username.sendKeys(ophHcpString);
		final WebElement password = driver.findElement(By.name("password"));
		password.clear();
		password.sendKeys("123456");
		final WebElement submit = driver.findElement(By.className("btn"));
		submit.click();

		assertEquals("iTrust2: HCP Home", driver.getTitle());

		((JavascriptExecutor) driver).executeScript("document.getElementById('editOfficeVisit').click();");

		assertEquals("iTrust2: Edit Office Visit", driver.getTitle());
	}

	/**
	 * Selects an office visit on the editing office visit page
	 */
	@When("^The HCP selects the existing office visit$")
	public void hcpSelectOfficeVisit() {
		List<OfficeVisit> visits = OfficeVisit.getOfficeVisits();
	    long targetId = 0;

	    for (int i = 0; i < visits.size(); i++) {
	      if (visits.get(i).getType().equals(AppointmentType.GENERAL_OPHTHALMOLOGY) && visits.get(i).getPatient().getUsername().equals(patientString) ) {
	        targetId = visits.get(i).getId();
	      }
	    }

	    final WebElement elem = driver.findElement(By.cssSelector("input[value=\"" + targetId + "\"]"));
	    elem.click();
	}

	/**
	 * Modifies the date and the height of the office visit
	 * @param date the new date of the visit
	 * @param height the new height of the patient
	 */
	@And("^The HCP modifies the date to be (.+), height (.+), and the left eye visual acuity (.+)$")
	public void modifyingTheDate(final String date, final String height, final String visualAcuityOS ) {
		waitForAngular();
		
		final WebElement dateElement = driver.findElement(By.name("date"));
		dateElement.sendKeys(date.replace("/", ""));

		driver.findElement(By.name("height")).clear();
		driver.findElement(By.name("height")).sendKeys(height);

		driver.findElement( By.name( "VAL" ) ).clear();
		driver.findElement( By.name( "VAL" ) ).sendKeys( visualAcuityOS );
	}

	/**
	 * Simulates clicking the submit button on the edit office visit page
	 */
	@And("^The HCP saves the office visit$")
	public void hcpSavesOfficeVisit() {
		driver.findElement(By.name("submit")).click();
	}

	/**
	 * Checks if the changes were allowed to be made
	 */
	@Then("^The ophthalmology office visit is updated successfully$")
	public void hcpSuccessfulOfficeVisit() {
		// confirm that the message is displayed
		try {
			driver.findElement(By.name("success")).getText().contains("Office visit edited successfully");
		} catch (final Exception e) {
			fail();
		}
	}

	/**
	 * Checks if the changes were not allowed to be made
	 */
	@Then("^The ophthalmology office visit is not updated successfully$")
	public void hcpUnsuccessfulOfficeVisit() {
		// confirm that the error message is displayed
		try {
			final String temp = driver.findElement(By.name("errorMsg")).getText();
			if (temp.equals("")) {
				fail();
			}
		} catch (final Exception e) {
		}
	}
}
