package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.fail;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.forms.admin.ICDCodeForm;
import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;
import edu.ncsu.csc.itrust2.forms.admin.LOINCForm.ResultEntry;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.LabResultScale;
import edu.ncsu.csc.itrust2.models.enums.LabStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Priority;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Diagnosis;
import edu.ncsu.csc.itrust2.models.persistent.GeneralCheckup;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.ICDCode;
import edu.ncsu.csc.itrust2.models.persistent.LOINC;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Class provides step definitions for HCPDiabetesTesting.feature Provides all
 * the needed backend setup to confirm HCP editOfficeVisit View
 * 
 * @author Nathan Seamon
 *
 */
public class HCPDiabetesTestingStepDefs extends CucumberTest {

	/*
	 * Base url for iTrust2
	 */
	private final String baseUrl = "http://localhost:8080/iTrust2";

	/**
	 * Logs into system as HCP
	 */
	@Given("^I am logged in as a general HCP$")
	public void loginHCP() {
		attemptLogout();
		driver.get(baseUrl);
		final WebElement username = driver.findElement(By.name("username"));
		username.clear();
		username.sendKeys("hcp");
		final WebElement password = driver.findElement(By.name("password"));
		password.clear();
		password.sendKeys("123456");
		final WebElement submit = driver.findElement(By.className("btn"));
		submit.click();
	}

	/**
	 * Creates a new office visit with an OGTT test
	 */
	@Given("^I have documented an Office Visit that includes an Oral Glucose Tolerance Test$")
	public void createOfficeVisitAndProcedure() {
		GeneralCheckup.deleteAll();
		Diagnosis.deleteAll(Diagnosis.class);
		LOINC.deleteAll();

		makeOfficeVisit();
	}

	/**
	 * Labtech updates procedure with results
	 * 
	 * @param value value of the test
	 */
	@Then("^a lab tech carries out the procedure and documents the results of (\\d+)$")
	public void labtechDocumentsProcedure(int value) {
		List<LabProcedure> labProcedureList = LabProcedure.getForTechAndPatient("labtech", "AliceThirteen");
		LabProcedure labProcedure = labProcedureList.get(0);
		labProcedure.setStatus(LabStatus.COMPLETED);
		labProcedure.setResult(Integer.toString(value));
		labProcedure.save();
	}

	/**
	 * HCP navigates to editOffice visit page and then selects the office visit
	 * vreated for our test
	 */
	@When("^I navigate to the editOfficeVisit page and select the correct office visit$")
	public void navigateToEditOfficeVisitAndSelectVisit() {
		((JavascriptExecutor) driver).executeScript("document.getElementById('editOfficeVisit').click();");
		waitForAngular();

		List<OfficeVisit> visits = OfficeVisit.getOfficeVisits();
		long targetId = 0;

		for (int i = 0; i < visits.size(); i++) {
			if (visits.get(i).getPatient().getUsername().equals("AliceThirteen")) {
				targetId = visits.get(i).getId();
			}
		}
		final WebElement visit_list_item = driver.findElement(By.cssSelector("input[value=\"" + targetId + "\"]"));
		visit_list_item.click();

	}

	/**
	 * HCP can see and confirm the suggested diagnosis
	 * 
	 * @param diagnosis expected diagnosis code
	 */
	@Then("^I can see and confirm the suggested diagnosis of \"([^\"]*)\"$")
	public void hcpConfirmsSuggestDiagnosis(String diagnosis) {

		waitForAngular();
		
		// make sure lab procedure info is displayed
		String tableText = driver.findElement(By.tagName("body")).getText();
		assert (tableText.contains("Do this test ASAP"));
	
		assert (tableText.contains("20436-2"));
		if ( diagnosis.equals("")) {
			return;
		}
		assert (tableText.contains(diagnosis));
		// find and click confirm button to add diagnosis
		WebElement submit = driver.findElement(By.name("confirmButton"));
		assert (submit.isEnabled());
		submit.click();
		assert (!submit.isEnabled());
		driver.findElement(By.name("submit")).click();

		// confirm that the message is displayed
		try {
			driver.findElement(By.name("success")).getText().contains("Office visit edited successfully");
		} catch (final Exception e) {
			fail();
		}
	}

	/**
	 * Makes an OfficeVisit for testing
	 *
	 * @return The newly created OfficeVisit
	 */
	private OfficeVisit makeOfficeVisit() {

		final Hospital hosp = new Hospital("Dr. Jenkins' Insane Asylum", "123 Main St", "12345", "NC");
		hosp.save();
		final GeneralCheckup visit = new GeneralCheckup();
		final BasicHealthMetrics bhm = new BasicHealthMetrics();

		// Make BasicHealthMetric object
		bhm.setDiastolic(150);
		bhm.setSystolic(100);
		bhm.setWeight(150f);
		bhm.setHcp(User.getByName("hcp"));
		bhm.setPatient(User.getByName("AliceThirteen"));
		bhm.setHdl(75);
		bhm.setLdl(90);
		bhm.setTri(200);
		bhm.setHeight(75f);
		bhm.setPatientSmokingStatus(PatientSmokingStatus.NEVER);
		bhm.setHouseSmokingStatus(HouseholdSmokingStatus.NONSMOKING);
		bhm.save();

		// set up basic information
		visit.setBasicHealthMetrics(bhm);
		visit.setType(AppointmentType.GENERAL_CHECKUP);
		visit.setHospital(hosp);
		visit.setPatient(User.getByName("AliceThirteen"));
		visit.setHcp(User.getByName("AliceThirteen"));
		visit.setDate(ZonedDateTime.now());
		visit.save();

		final LOINC loinc = makeLOINC();

		final LabProcedure pro = new LabProcedure();

		// create and add lab procedure
		final List<LabProcedure> procs = new Vector<LabProcedure>();
		pro.setLoinc(loinc);
		pro.setPriority(Priority.HIGH);
		pro.setStatus(LabStatus.ASSIGNED);
		pro.setPatient(User.getByName("AliceThirteen"));
		pro.setAssignedTech(User.getByName("labtech"));
		pro.setComments("Do this test ASAP");
		pro.setVisit(visit);
		pro.save();
		procs.add(pro);
		visit.setLabProcedures(procs);

		// Save and return object
		visit.save();
		return visit;
	}

	/**
	 * Method makes a LOINC custom to this test class
	 * 
	 * @return generated LOINC
	 */
	private LOINC makeLOINC() {

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

		return code;
	}
}
