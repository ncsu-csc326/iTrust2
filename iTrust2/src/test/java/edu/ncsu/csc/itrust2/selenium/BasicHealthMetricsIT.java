package edu.ncsu.csc.itrust2.selenium;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.itrust2.config.RootConfiguration;
import edu.ncsu.csc.itrust2.models.enums.HouseholdSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.PatientSmokingStatus;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.State;
import edu.ncsu.csc.itrust2.models.persistent.BasicHealthMetrics;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.mvc.config.WebMvcConfiguration;
import edu.ncsu.csc.itrust2.utils.HibernateDataGenerator;

@ContextConfiguration ( classes = { RootConfiguration.class, WebMvcConfiguration.class } )
@WebAppConfiguration
public class BasicHealthMetricsIT {

    @Autowired
    private WebApplicationContext context;

    private final WebDriver       driver       = new HtmlUnitDriver( true );
    private final String          baseUrl      = "http://localhost:8080/iTrust2";
    // Hash for 123456 Password
    private final String          passwordHash = "$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.";
    private final String          hospitalName = "Central Hospital";
    WebDriverWait                 wait         = new WebDriverWait( driver, 20 );

    private void createHcp ( final String firstName, final String lastName, final String username,
            final String password ) {
        final User user = new User( username, password, Role.ROLE_HCP, 1 );
        user.save();
    }

    private void createPatient ( final String firstName, final String lastName, final String username,
            final String password, final String dateOfBirth ) throws ParseException {
        final User user = new User( username, password, Role.ROLE_PATIENT, 1 );
        user.save();

        final Patient patient = new Patient();
        final SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyyy", Locale.ENGLISH );
        final Date parsedDate = sdf.parse( dateOfBirth );
        final Calendar c = Calendar.getInstance();
        c.setTime( parsedDate );
        patient.setDateOfBirth( c );
        patient.setFirstName( firstName );
        patient.setLastName( lastName );
        patient.setSelf( user );
        patient.save();
    }

    private void createHospital ( final String name ) {
        final Hospital hospital = new Hospital();
        hospital.setName( name );
        hospital.setAddress( "1234 Bialystok Road" );
        hospital.setState( State.NC );
        hospital.setZip( String.valueOf( 27516 ) );
        hospital.save();
    }

    @Before
    public void setUp () throws ParseException, InterruptedException {
        HibernateDataGenerator.refreshDB();
        createHcp( "Shelly", "Vang", "svang", passwordHash );
        createHcp( "John", "Smith", "jsmith", passwordHash );
        createPatient( "Brynn", "McClain", "bmcclain", passwordHash, "05/01/2017" );
        createPatient( "Caldwell", "Hudson", "chudson", passwordHash, "09/29/2015" );
        createPatient( "Fulton", "Gray", "fgray", passwordHash, "10/10/2012" );
        createPatient( "Daria", "Griffin", "dgriffin", passwordHash, "10/25/1997" );
        createPatient( "Thane", "Ross", "tross", passwordHash, "01/03/1993" );
        createHospital( hospitalName );
    }

    private void login ( final String usernameString, final String passwordString ) {
        driver.get( baseUrl );
        final WebElement username = driver.findElement( By.name( "username" ) );
        username.clear();
        username.sendKeys( usernameString );
        final WebElement password = driver.findElement( By.name( "password" ) );
        password.clear();
        password.sendKeys( passwordString );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();
    }

    private void documentOfficeVisit ( final String patientUsername, final String dateString, final String note,
            final String weight, final String heightLength, final String headCircumference, final String systolic,
            final String diastolic, final String hdl, final String ldl, final String tri,
            final HouseholdSmokingStatus householdSmoke, final PatientSmokingStatus patientSmoke ) {

        driver.get( baseUrl + "/hcp/documentOfficeVisit" );

        try {
            Thread.sleep( 5000 );
        }
        catch ( final InterruptedException e ) {
            /* Intentionally ignore this exception */
        }

        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[value=\"" + hospitalName + "\"]" ) ) );
        final WebElement hospital = driver.findElement( By.cssSelector( "input[value=\"" + hospitalName + "\"]" ) );
        hospital.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "type" ) ) );
        final WebElement type = driver.findElement( By.name( "type" ) );
        type.click();

        wait.until( ExpectedConditions
                .visibilityOfElementLocated( By.cssSelector( "input[value=\"" + patientUsername + "\"]" ) ) );
        final WebElement patient = driver.findElement( By.cssSelector( "input[value=\"" + patientUsername + "\"]" ) );
        patient.click();

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "time" ) ) );
        final WebElement time = driver.findElement( By.name( "time" ) );
        time.clear();
        time.sendKeys( "12:00 pm" );

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "date" ) ) );
        final WebElement date = driver.findElement( By.name( "date" ) );
        date.clear();
        date.sendKeys( dateString );

        if ( note != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "notes" ) ) );
            final WebElement notesElement = driver.findElement( By.name( "notes" ) );
            notesElement.clear();
            notesElement.sendKeys( note );
        }

        if ( weight != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "weight" ) ) );
            final WebElement weightElement = driver.findElement( By.name( "weight" ) );
            weightElement.clear();
            weightElement.sendKeys( weight );
        }

        if ( heightLength != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "height" ) ) );
            final WebElement heightElement = driver.findElement( By.name( "height" ) );
            heightElement.clear();
            heightElement.sendKeys( heightLength );
        }

        if ( headCircumference != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "head" ) ) );
            final WebElement headElement = driver.findElement( By.name( "head" ) );
            headElement.clear();
            headElement.sendKeys( headCircumference );
        }

        if ( systolic != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "systolic" ) ) );
            final WebElement systolicElement = driver.findElement( By.name( "systolic" ) );
            systolicElement.clear();
            systolicElement.sendKeys( systolic );
        }

        if ( diastolic != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "diastolic" ) ) );
            final WebElement diastolicElement = driver.findElement( By.name( "diastolic" ) );
            diastolicElement.clear();
            diastolicElement.sendKeys( diastolic );
        }

        if ( hdl != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "hdl" ) ) );
            final WebElement hdlElement = driver.findElement( By.name( "hdl" ) );
            hdlElement.clear();
            hdlElement.sendKeys( hdl );
        }

        if ( ldl != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "ldl" ) ) );
            final WebElement ldlElement = driver.findElement( By.name( "ldl" ) );
            ldlElement.clear();
            ldlElement.sendKeys( ldl );
        }

        if ( tri != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "tri" ) ) );
            final WebElement triElement = driver.findElement( By.name( "tri" ) );
            triElement.clear();
            triElement.sendKeys( tri );
        }

        if ( householdSmoke != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector( "input[value=\"" + householdSmoke.toString() + "\"]" ) ) );
            final WebElement houseSmokeElement = driver
                    .findElement( By.cssSelector( "input[value=\"" + householdSmoke.toString() + "\"]" ) );
            houseSmokeElement.click();
        }

        if ( patientSmoke != null ) {
            wait.until( ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector( "input[value=\"" + patientSmoke.toString() + "\"]" ) ) );
            final WebElement patientSmokeElement = driver
                    .findElement( By.cssSelector( "input[value=\"" + patientSmoke.toString() + "\"]" ) );
            patientSmokeElement.click();
        }

        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "submit" ) ) );
        final WebElement submit = driver.findElement( By.name( "submit" ) );
        submit.click();
        // Give the data time to save to the database
        try {
            Thread.sleep( 2000 );
        }
        catch ( final InterruptedException e ) {
            /* Intentionally ignore this exception */
        }
    }

    private void verifyTest ( final int existingSize ) {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "success" ) ) );
        final WebElement message = driver.findElement( By.name( "success" ) );

        assertFalse( message.getText().contains( "Error occurred creating office visit" ) );
        final List<BasicHealthMetrics> list = BasicHealthMetrics.getBasicHealthMetrics();
        assertEquals( 1, list.size() - existingSize );
    }

    private void verifyTestUnsuccessful ( final int existingSize ) {
        wait.until( ExpectedConditions.visibilityOfElementLocated( By.name( "success" ) ) );
        final WebElement message = driver.findElement( By.name( "success" ) );

        assertTrue( message.getText().contains( "Error occurred creating office visit" ) );
        final List<BasicHealthMetrics> list = BasicHealthMetrics.getBasicHealthMetrics();
        assertEquals( 0, list.size() - existingSize );
    }

    @Test
    public void testBasicHealthMetrics1 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "bmcclain", "10/01/2017",
                "Brynn can start eating rice cereal mixed with breast milk or formula once a day.", "16.5", "22.3",
                "16.1", null, null, null, null, null, HouseholdSmokingStatus.NONSMOKING, null );
        verifyTest( existingSize );
    }

    @Test
    public void testBasicHealthMetrics2 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "chudson", "10/28/2017",
                "Diagnosed with strep throat. Avoid contact with others for first 24 hours of antibiotics.", "30.2",
                "34.7", "19.4", null, null, null, null, null, HouseholdSmokingStatus.INDOOR, null );
        verifyTest( existingSize );
    }

    @Test
    public void testBasicHealthMetrics3 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "fgray", "10/13/2017",
                "Fulton has all required immunizations to start kindergarten next year.", "37.9", "42.9", null, "95",
                "65", null, null, null, HouseholdSmokingStatus.OUTDOOR, null );
        verifyTest( existingSize );
    }

    @Test
    public void testBasicHealthMetrics4 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "dgriffin", "10/25/2017", "Patient is healthy", "124.3", "62.3", null, "95", "65", "65",
                "102", "147", HouseholdSmokingStatus.NONSMOKING, PatientSmokingStatus.FORMER );
        verifyTest( existingSize );
    }

    @Test
    public void testBasicHealthMetrics5 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "tross", "10/25/2017",
                "Thane should consider modifying diet and exercise to avoid future heart disease", "210.1", "73.1",
                null, "160", "100", "37", "141", "162", HouseholdSmokingStatus.NONSMOKING, PatientSmokingStatus.NEVER );
        verifyTest( existingSize );
    }

    @Test
    public void testBasicHealthMetrics6 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "fgray", "10/13/2018", "Fulton had a good first year of school and is on chart for growth",
                "50", "47", null, "1000", "68", null, null, null, HouseholdSmokingStatus.OUTDOOR, null );
        verifyTestUnsuccessful( existingSize );
    }

    @Test
    public void testBasicHealthMetrics7 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "fgray", "10/13/2018", "Fulton had a good first year of school and is on chart for growth",
                "50", "47", null, "100", "68", null, null, null, HouseholdSmokingStatus.OUTDOOR, null );
        verifyTest( existingSize );
    }

    @Test
    public void testBasicHealthMetrics8 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "dgriffin", "05/18/2017", "Patient is showing signs of seasonal allergies", "125.4",
                "62.25", null, "112", "80", "62", "106", "157", HouseholdSmokingStatus.NONSMOKING,
                PatientSmokingStatus.FORMER );
        verifyTestUnsuccessful( existingSize );
    }

    @Test
    public void testBasicHealthMetrics9 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "dgriffin", "05/18/2017", "Patient is showing signs of seasonal allergies", "125.4",
                "62.2", null, "112", "80", "62", "106", "157", HouseholdSmokingStatus.NONSMOKING,
                PatientSmokingStatus.FORMER );
        verifyTest( existingSize );
    }

    @Test
    public void testBasicHealthMetrics10 () {
        final int existingSize = BasicHealthMetrics.getBasicHealthMetrics().size();
        login( "svang", "123456" );
        documentOfficeVisit( "bmcclain", "10/02/2017", "Brynn is on track for healthy growth!", "35.5", "30.3", "18.11",
                null, null, null, null, null, HouseholdSmokingStatus.NONSMOKING, null );
        verifyTestUnsuccessful( existingSize );
    }
}
