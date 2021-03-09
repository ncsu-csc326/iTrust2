package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;
import edu.ncsu.csc.itrust2.forms.admin.LOINCForm.ResultEntry;
import edu.ncsu.csc.itrust2.models.enums.LabResultScale;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.LOINC;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;

/**
 * Step defs for the admin adding a loinc
 *
 * @author Sam Fields
 *
 */
public class AdminAddLOINCStepDefs extends CucumberTest {

	private final String baseUrl = "http://localhost:8080/iTrust2";

	/**
	 * Add required ICD codes
	 */
	@Given("^the required ICD codes exist$")
	public void generateICDCodes() {
		Diagnosis.deleteAll(Diagnosis.class);
		LabProcedure.deleteAll();
		LOINC.deleteAll();
		ICDCode.deleteAll(ICDCode.class);
		ICDCode newCode = new ICDCode();
		newCode.setCode("R31.9");
		newCode.setDescription("Hematuria");
		newCode.save();

		newCode = new ICDCode();
		newCode.setCode("R73.03");
		newCode.setDescription("Prediabetes");
		newCode.save();

		newCode = new ICDCode();
		newCode.setCode("E11.9");
		newCode.setDescription("Type 2 Diabetes");
		newCode.save();
	}

	/**
	 * Login as admin
	 *
	 */
	@Given("^I login as the Admin$")
	public void loginAdmin() {
		attemptLogout();
		driver.get(baseUrl);
		final WebElement username = driver.findElement(By.name("username"));
		username.clear();
		username.sendKeys("admin");
		final WebElement password = driver.findElement(By.name("password"));
		password.clear();
		password.sendKeys("123456");
		final WebElement submit = driver.findElement(By.className("btn"));
		submit.click();
	}

	/**
	 * Navigate to add LOINC page
	 *
	 */
	@When("^I navigate to the Add LOINC page$")
	public void navigateToPage() {
		((JavascriptExecutor) driver).executeScript("document.getElementById('manageLOINCCodes').click();");
		waitForAngular();
	}

	/**
	 * Creates a new LOINC
	 *
	 * @param code       the loinc code
	 * @param commonName the name of the code
	 * @param component  the component
	 * @param property   the property
	 */
	@When("^I add a LOINC with the values: \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", and ([^\"]*)\\.$")
	public void addLOINC(String code, String commonName, String component, String property, String scale) {
		waitForAngular();
		final WebElement codeField = driver.findElement(By.name("iCode"));
		codeField.clear();
		codeField.sendKeys(code);
		final WebElement commonNameField = driver.findElement(By.name("iComName"));
		commonNameField.clear();
		commonNameField.sendKeys(commonName);
		final WebElement componentField = driver.findElement(By.name("iComponent"));
		componentField.clear();
		componentField.sendKeys(component);
		final WebElement propertyField = driver.findElement(By.name("iProperty"));
		propertyField.clear();
		propertyField.sendKeys(property);
		if (!scale.equals("None")) {
			final WebElement scaleField = driver.findElement(By.name("iScale"));
			final Select dropdown = new Select(scaleField);
			dropdown.selectByVisibleText(scale);
		}
	}

	/**
	 * Adds qualitative values to the LOINC
	 *
	 * @param name the name of the value
	 * @param icd  the ICD code associated with the value
	 */
	@Then("^I add a new result value with name: \"([^\"]*)\" and ICD: \"([^\"]*)\"$")
	public void addResult(String name, String icd) {
		waitForAngular();
		final WebElement nameField = driver.findElement(By.name("val_name"));
		nameField.clear();
		nameField.sendKeys(name);
		if (!icd.equals("None")) {
			final WebElement icdField = driver.findElement(By.name("val_icd"));
			final Select dropdown = new Select(icdField);
			dropdown.selectByVisibleText(icd);
		}
		final WebElement addValue = driver.findElement(By.name("submitQual"));
		addValue.click();
	}

	/**
	 * Add a new quantitative range
	 *
	 * @param min the min of the range
	 * @param max the max of the range
	 * @param icd the icd for the range
	 */
	@Then("^I add a new range with min: \"([^\"]*)\", max: \"([^\"]*)\", and ICD: \"([^\"]*)\"$")
	public void addNewRange(String min, String max, String icd) {
		waitForAngular();
		final WebElement startField = driver.findElement(By.name("rangeStart"));
		startField.clear();
		startField.sendKeys(min);
		final WebElement endField = driver.findElement(By.name("rangeEnd"));
		endField.clear();
		endField.sendKeys(max);
		if (!icd.equals("None")) {
			final WebElement icdField = driver.findElement(By.name("rangeCode"));
			final Select dropdown = new Select(icdField);
			dropdown.selectByVisibleText(icd);
		}
		final WebElement addValue = driver.findElement(By.name("submitRange"));
		addValue.click();
	}

	/**
	 * Clicks the Add Code button
	 */
	@Then("^I click Add code$")
	public void addCode() {
		waitForAngular();
		final WebElement submit = driver.findElement(By.name("submit"));
		submit.click();
	}

	/**
	 * Verifies that the code was added
	 */
	@Then("^the code is added$")
	public void checkCodeAdded() {
		waitForAngular();
		assertTrue(driver.getPageSource().contains("Successfully added code"));
	}

	/**
	 * Deletes range value
	 * 
	 * @param icd the ICD code associated with range to remove
	 */
	@Then("^I delete the code with ICD: \"([^\"]*)\" and the table is empty$")
	public void deleteSingleQuantitativeResult(String icd) {
		String tableText = driver.findElement(By.name("QuantitativeRangeTable")).getText();
		assertTrue(tableText.contains(icd));

		final WebElement delete = driver.findElement(By.name("DeleteButton"));
		delete.click();

		waitForAngular();
		tableText = driver.findElement(By.name("QuantitativeRangeTable")).getText();
		assertTrue(!tableText.contains(icd));
	}

	/**
	 * Creates a new quantitative LOINC
	 */
	@Given("^there exists a quantitative LOINC$")
	public void makeQuantitativeLOINC() {

		LOINC.deleteAll();

		// New LOINC with Quantitative results
		final LOINCForm loincForm = new LOINCForm();
		loincForm.setCode("20436-2");
		loincForm.setCommonName("Glucose 2 Hr After Glucose, Blood");
		loincForm.setProperty("MCnc");
		loincForm.setComponent("Glucose^2H post dose glucose");
		loincForm.setScale(LabResultScale.QUANTITATIVE.getName());

		// make needed ICD codes
		final ICDCode prediabetesICD = (ICDCode) ICDCode.getBy(ICDCode.class, "code", "R73.03");
		final ICDCode diabetesICD = (ICDCode) ICDCode.getBy(ICDCode.class, "code", "E11.9");

		// make the result entry ranges
		final List<ResultEntry> resultEntries = new ArrayList<ResultEntry>();

		ResultEntry entry1 = loincForm.new ResultEntry();
		ResultEntry entry2 = loincForm.new ResultEntry();
		ResultEntry entry3 = loincForm.new ResultEntry();
		entry1.setIcd(null);
		entry1.setMin("0");
		entry1.setMax("139");
		resultEntries.add(entry1);

		entry2.setIcd(prediabetesICD.getCode());
		entry2.setMin("140");
		entry2.setMax("199");
		resultEntries.add(entry2);

		entry3.setIcd(diabetesICD.getCode());
		entry3.setMin("200");
		entry3.setMax("5000");
		resultEntries.add(entry3);

		// add ranges to form
		loincForm.setResultEntries(resultEntries);
		final LOINC code = new LOINC(loincForm);

		// save form
		code.save();
	}

	/**
	 * Selects LOINC code and switches scale to QUALITATIVE. Assumes only one LOINC in system
	 */
	@When("^I select the LOINC and change the scale to qualitative$")
	public void changeLOINC() {
		final WebElement edit = driver.findElement(By.name("editCode"));
		edit.click();
		final WebElement scaleField = driver.findElement(By.name("iScale"));
		final Select dropdown = new Select(scaleField);
		dropdown.selectByVisibleText("QUALITATIVE");
	}

	/**
	 * Selects edit code
	 */
	@Then("^I select edit code$")
	public void selectEditCode() {
		waitForAngular();
		final WebElement submit = driver.findElement(By.name("submit"));
		submit.click();
	}

	/**
	 * Checks that code edit success messages is displayed
	 */
	@Then("^code is edited$")
	public void codeIsEdited() {
		waitForAngular();
		String bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains("Successfully edited code"));
	
	}
	
	
	/**
	 * Checks for overlapping error message
	 */
	@Then("^I get an overlapping error message$")
	public void overlappingErrorMessage() {
		String bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains("Values in range already used."));
	}
	
	/**
	 * Checks for duplicate error message
	 */
	@Then("^I get a duplicate name error$")
	public void duplicateError() {
		String bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains("Name already used."));
	}
}
