package edu.ncsu.csc.iTrust2.cucumber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import edu.ncsu.csc.iTrust2.models.BasicHealthMetrics;
import edu.ncsu.csc.iTrust2.models.Diagnosis;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.ICDCode;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import edu.ncsu.csc.iTrust2.services.ICDCodeService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DiagnosesStepDefs extends CucumberTest {

    @Autowired
    private ICDCodeService     icdCodeService;

    @Autowired
    private HospitalService    hospitalService;

    @Autowired
    private OfficeVisitService officeVisitService;

    @Then ( "^The (.+), and (.+), are correct$" )
    public void checkList ( final String description, final String note ) {
        final long time = System.currentTimeMillis();
        waitForAngular();
        while ( System.currentTimeMillis() - time < 5000 ) {
            for ( final WebElement diag : driver.findElements( By.name( "diagnosis" ) ) ) {
                final String text = diag.getText();
                if ( text.contains( description ) && text.contains( note ) ) {
                    // we found the right diganosis
                    return;
                }
            }
        }
        Assert.fail( "failed to find specified diagnosis" );
    }

    @When ( "^I navigate to the list of diagnoses$" )
    public void adminNavigate () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('manageICDCodes').click();" );

    }

    List<String> before;
    List<String> after;
    String       expectedCode;
    String       expectedDescription;

    @When ( "^I enter the info for a diagnosis with code: (.+), and description: (.+)$" )
    public void enterDiagnosisInfo ( final String code, final String description ) {

        expectedCode = code;
        expectedDescription = description;

        waitForAngular();

        before = driver.findElements( By.name( "codeRow" ) ).stream().map( x -> x.getAttribute( "codeid" ) )
                .collect( Collectors.toList() );

        try {
            setTextField( By.name( "code" ), code );
            setTextField( By.name( "description" ), description );
            driver.findElement( By.name( "submit" ) ).click();
            waitForAngular();
            try {
                new WebDriverWait( driver, 10 ).until( ExpectedConditions.or(
                        ExpectedConditions.numberOfElementsToBeMoreThan( By.name( "codeRow" ), before.size() ),
                        ExpectedConditions.textToBePresentInElementLocated( By.id( "errP" ),
                                "Code doesn't meet specifications" ) ) );
            }
            catch ( final Exception e ) {
                // ignore this ~ problems caught in next step
            }
        }
        catch ( final Exception e ) {
            fail( e.getMessage() );
        }
    }

    @Then ( "The diagnosis is added sucessfully" )
    public void checkDiagnosisAdd () {
        waitForAngular();
        after = driver.findElements( By.name( "codeRow" ) ).stream().map( x -> x.getAttribute( "codeid" ) )
                .collect( Collectors.toList() );
        after.removeAll( before );

        waitForAngular();

        assertEquals( 1, after.size() );
    }

    @Then ( "The diagnosis info is correct" )
    public void verifyAddedDiagnosis () {
        waitForAngular();
        // the one left in after is what we expect.
        final WebElement newRow = driver.findElements( By.name( "codeRow" ) ).stream()
                .filter( x -> x.getAttribute( "codeid" ).equals( after.get( 0 ) ) ).findFirst().get();
        assertEquals( expectedCode, newRow.findElement( By.name( "codeCell" ) ).getText() );
        assertEquals( expectedDescription, newRow.findElement( By.name( "descriptionCell" ) ).getText() );
    }

    @Then ( "The diagnosis is not added" )
    public void checkInvalidAdd () {
        waitForAngular();
        try {
            final WebElement err = driver.findElement( By.id( "errP" ) );
            assertTrue( err.getText().contains( "Code doesn't meet specifications" )
                    || err.getText().contains( "Description exceeds character limit of 250" ) );
            after = driver.findElements( By.name( "codeRow" ) ).stream().map( x -> x.getAttribute( "codeid" ) )
                    .collect( Collectors.toList() );
            after.removeAll( before );
            assertEquals( 0, after.size() );
        }
        catch ( final Exception e ) {
            fail( e.getMessage() );
        }
    }

    @When ( "I delete the new code" )
    public void deleteCode () {
        waitForAngular();
        // the one left in after is what we expect.
        final WebElement newRow = driver.findElements( By.name( "codeRow" ) ).stream()
                .filter( x -> x.getAttribute( "codeid" ).equals( after.get( 0 ) ) ).findFirst().get();
        newRow.findElement( By.tagName( "input" ) ).click();
        waitForAngular();
    }

    @When ( "I add a diagnosis without a code" )
    public void addDiagnosisNoCode () {
        waitForAngular();
        driver.findElement( By.name( "GENERAL_CHECKUP" ) ).click();
        setTextField( By.name( "notesEntry" ), "Fun note" );
        driver.findElement( By.name( "fillDiagnosis" ) ).click();
    }

    @Then ( "The code is deleted" )
    public void checkDelete () {
        waitForAngular();

        final List<String> current = driver.findElements( By.name( "codeRow" ) ).stream()
                .map( x -> x.getAttribute( "codeid" ) ).collect( Collectors.toList() );
        assertFalse( current.contains( after.get( 0 ) ) );
    }

    @Then ( "A message is shown that indicates that the code was invalid" )
    public void checkFailMessage () {
        waitForAngular();
        assertTrue( driver.getPageSource().contains( "Diagnosis must be associated with a diagnosis code" ) );
    }

    @Given ( "A diagnosis code exists in iTrust2" )
    public void codeAdded () {
        final ICDCode code = new ICDCode();
        code.setCode( "T16" );
        code.setDescription( "Pneumonia" );
        icdCodeService.save( code );
    }

    @Given ( "The patient has been diagnosed with the code" )
    public void diagCreated () {

        final ICDCode code = icdCodeService.findByCode( "T16" );

        final Hospital hospital = hospitalService.findByName( "iTrust Test Hospital 2" );

        final OfficeVisit visit = new OfficeVisit();
        final BasicHealthMetrics bhm = new BasicHealthMetrics();

        bhm.setDiastolic( 150 );
        bhm.setDiastolic( 100 );
        bhm.setHcp( userService.findByName( "hcp" ) );
        bhm.setPatient( userService.findByName( "patient" ) );
        bhm.setHdl( 75 );
        bhm.setHeight( 75f );
        bhm.setHouseSmokingStatus( HouseholdSmokingStatus.NONSMOKING );

        visit.setBasicHealthMetrics( bhm );
        visit.setType( AppointmentType.GENERAL_CHECKUP );
        visit.setHospital( hospital );
        visit.setPatient( userService.findByName( "patient" ) );
        visit.setHcp( userService.findByName( "hcp" ) );
        visit.setDate( ZonedDateTime.now() );

        final Diagnosis diag = new Diagnosis();
        diag.setCode( code );
        diag.setNote( "Patient should avoid contact with others for first 24 hours and take prescribed antibiotics" );
        diag.setVisit( visit );

        visit.setDiagnoses( List.of( diag ) );

        officeVisitService.save( visit );
    }

    @When ( "I navigate to my past diagnoses" )
    public void patientNavigate () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('viewDiagnoses').click();" );
        waitForAngular();
    }

    @Then ( "I see the list of my diagnoses" )
    public void seeList () {
        waitForAngular();

        try {
            driver.findElement( By.name( "diagnosis" ) );
        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "success" ) ).getText() );
        }
    }

    @When ( "^I fill in information on the diagnosis (.+), diagnosis note: (.+)$" )
    public void fillDiagnosis ( final String diagnosis, final String diagnosisNote ) {
        // add the diagnosis
        waitForAngular();
        driver.findElement( By.name( diagnosis ) ).click();
        setTextField( By.name( "notesEntry" ), diagnosisNote );
        driver.findElement( By.name( "fillDiagnosis" ) ).click();
    }

}
