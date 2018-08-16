package edu.ncsu.csc.itrust2.cucumber;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import edu.ncsu.csc.itrust2.models.persistent.PasswordResetToken;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;

public class PasswordChangeStepDefs extends CucumberTest {

    static {
        java.util.logging.Logger.getLogger( "com.gargoylesoftware" ).setLevel( Level.OFF );
    }

    private final String       baseUrl = "http://localhost:8080/iTrust2";

    // Token for testing
    private PasswordResetToken token   = null;

    private void setTextField ( final By byval, final Object value ) {
        final WebElement elem = driver.findElement( byval );
        elem.clear();
        elem.sendKeys( value.toString() );
    }

    @Given ( "I can log in to iTrust as (.+) with password (.+)" )
    public void login ( final String username, final String password ) {
        attemptLogout();

        driver.get( baseUrl );
        setTextField( By.name( "username" ), username );
        setTextField( By.name( "password" ), password );
        final WebElement submit = driver.findElement( By.className( "btn" ) );
        submit.click();

        waitForAngular();
    }

    @When ( "I navigate to the change password page" )
    public void navigateChange () {
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('changePassword').click();" );
        waitForAngular();
    }

    @When ( "I fill out the form with current password (.+) and new password (.+)" )
    public void fillChangeForm ( final String password, final String newPassword ) {
        // Wait until page loads
        waitForAngular();

        setTextField( By.name( "currentPW" ), password );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @When ( "I fill out the form with current password (.+), new password (.+), and re-entry (.+)" )
    public void fillChangeForm ( final String currentPassword, final String newPassword, final String newPassword2 ) {
        // Wait until page loads
        waitForAngular();

        setTextField( By.name( "currentPW" ), currentPassword );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword2 );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @Then ( "My password is updated sucessfully" )
    public void verifyUpdate () {
        waitForAngular();

        try {
            assertTrue(
                    driver.findElement( By.name( "message" ) ).getText().contains( "Password changed successfully" ) );

        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "message" ) ).getText() + "\n" + token.getId() + "\n"
                    + token.getTempPasswordPlaintext() );
        }

        // set password back when done for repeat testing
        fillChangeForm( "654321", "123456", "123456" );

    }

    @Then ( "My password is not updated because (.*)" )
    public void verifyNoUpdate ( final String message ) {
        waitForAngular();

        try {
            assertTrue( driver.findElement( By.name( "message" ) ).getText().contains( message ) );
        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "message" ) ).getText() + "\n"
                    + ( null == token ? "" : ( token.getId() + "\n" + token.getTempPasswordPlaintext() ) ) );
        }
    }

    @Given ( "The user (.+) exists with email (.+)" )
    public void userExistsWithEmail ( final String username, final String email ) throws InterruptedException {
        attemptLogout();
        waitForAngular();

        final User user = User.getByName( username );
        switch ( user.getRole() ) {
            case ROLE_PATIENT:
                Patient dbPatient = Patient.getByName( username );
                if ( null == dbPatient ) {
                    dbPatient = new Patient();
                }
                dbPatient.setSelf( user );
                dbPatient.setFirstName( "Test" );
                dbPatient.setLastName( "User" );
                dbPatient.setEmail( email );
                dbPatient.setAddress1( "1234 Street Dr." );
                dbPatient.setCity( "city" );
                dbPatient.setZip( "12345" );
                dbPatient.setPhone( "123-456-7890" );
                dbPatient.save();
                break;

            default:
                Personnel dbPersonnel = Personnel.getByName( username );
                if ( null == dbPersonnel ) {
                    dbPersonnel = new Personnel();
                }
                dbPersonnel.setSelf( user );
                dbPersonnel.setFirstName( "Test" );
                dbPersonnel.setLastName( "User" );
                dbPersonnel.setEmail( email );
                dbPersonnel.setAddress1( "1234 Street Dr." );
                dbPersonnel.setCity( "city" );
                dbPersonnel.setZip( "12345" );
                dbPersonnel.setPhone( "123-456-7890" );
                dbPersonnel.setEnabled( true );
                dbPersonnel.save();
                break;
        }

    }

    @When ( "I navigate to the Forgot Password page" )
    public void navigateForgot () {
        driver.get( baseUrl );
        waitForAngular();
        ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('passwordResetRequest').click();" );
        waitForAngular();
    }

    @When ( "I enter the temporary password wrong, new password (.+) and reentry (.+)" )
    public void wrongTemp ( final String newPassword, final String newPassword2 ) {
        // Wait until page loads
        waitForAngular();

        setTextField( By.name( "tempPW" ), "this is wrong" );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword2 );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @When ( "I fill out the request for with the username (.+)" )
    public void fillResetRequest ( final String username ) throws InterruptedException {
        waitForAngular();
        final WebElement un = driver.findElement( By.name( "username" ) );
        un.clear();
        un.sendKeys( username );
        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @When ( "I receive an email with a link and temporary password" )
    public void getEmail () throws InterruptedException {
        waitForAngular();

        // wait for the email to be sent
        try {
            assertTrue( driver.findElement( By.name( "message" ) ).getText()
                    .contains( "Password reset request successfully sent" ) );

        }
        catch ( final Exception e ) {
            fail( e.getMessage() + "\n" + driver.findElement( By.name( "message" ) ).getText() );
        }

        // wait for the email to be delivered
        waitForAngular();
        token = getTokenFromEmail();
        if ( token == null ) {
            fail( "Failed to receive email" );
        }
    }

    @When ( "I follow the link to the password reset page" )
    public void followLink () {
        waitForAngular();
        // NOTE: can host be localhost always?
        // Token should not be null at this point
        final String link = "http://localhost:8080/iTrust2/resetPassword?tkid=" + token.getId();
        driver.get( link );
    }

    @When ( "I enter the temporary password and new password (.+)" )
    public void fillResetForm ( final String newPassword ) {
        // Wait until page loads
        waitForAngular();

        setTextField( By.name( "tempPW" ), token.getTempPasswordPlaintext() );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    @Given ( "The user (.+) does not exist in the system" )
    public void noUser ( final String username ) {
        attemptLogout();
        waitForAngular();

        final User user = User.getByName( username );
        if ( null != user ) {
            try {
                user.delete();
            }
            catch ( final Exception e ) {
                ;
            }
        }
    }

    @Then ( "I see an error message on the password page" )
    public void resetError () {
        waitForAngular();

        try {
            assertTrue( driver.findElement( By.name( "message" ) ).getText()
                    .contains( "Password reset request could not be sent" ) );

        }
        catch ( final Exception e ) {
            fail( driver.findElement( By.name( "message" ) ).getText() );
        }
    }

    @When ( "I enter the temporary password, new password (.+) and reentry (.+)" )
    public void fillResetForm ( final String newPassword, final String newPassword2 ) {
        // Wait until page loads
        waitForAngular();

        setTextField( By.name( "tempPW" ), token.getTempPasswordPlaintext() );
        setTextField( By.name( "newPW" ), newPassword );
        setTextField( By.name( "confirmPW" ), newPassword2 );

        final WebElement submit = driver.findElement( By.name( "submitButton" ) );
        submit.click();
    }

    /*
     * Credit for checking email:
     * https://www.tutorialspoint.com/javamail_api/javamail_api_checking_emails.
     * htm
     */
    private PasswordResetToken getTokenFromEmail () {
        final String username = "csc326.201.1@gmail.com";
        final String password = "iTrust2Admin123456";
        final String host = "pop.gmail.com";
        PasswordResetToken token = null;
        try {
            // create properties field
            final Properties properties = new Properties();
            properties.put( "mail.store.protocol", "pop3" );
            properties.put( "mail.pop3.host", host );
            properties.put( "mail.pop3.port", "995" );
            properties.put( "mail.pop3.starttls.enable", "true" );
            final Session emailSession = Session.getDefaultInstance( properties );
            // emailSession.setDebug(true);

            // create the POP3 store object and connect with the pop server
            final Store store = emailSession.getStore( "pop3s" );

            store.connect( host, username, password );

            // create the folder object and open it
            final Folder emailFolder = store.getFolder( "INBOX" );
            emailFolder.open( Folder.READ_ONLY );

            // retrieve the messages from the folder in an array and print it
            final Message[] messages = emailFolder.getMessages();
            Arrays.sort( messages, ( x, y ) -> {
                try {
                    return y.getSentDate().compareTo( x.getSentDate() );
                }
                catch ( final MessagingException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return 0;
            } );
            for ( final Message message : messages ) {
                // SUBJECT
                if ( message.getSubject() != null && message.getSubject().contains( "iTrust2 Password Reset" ) ) {
                    String content = (String) message.getContent();
                    content = content.replaceAll( "\r", "" ); // Windows
                    content = content.substring( content.indexOf( "?tkid=" ) );

                    final Scanner scan = new Scanner( content.substring( 6, content.indexOf( '\n' ) ) );
                    System.err.println( "token(" + content.substring( 6, content.indexOf( '\n' ) ) + ")end" );
                    final long tokenId = scan.nextLong();
                    scan.close();

                    content = content.substring( content.indexOf( "temporary password: " ) );
                    content = content.substring( 20, content.indexOf( "\n" ) );
                    content.trim();

                    if ( content.endsWith( "\n" ) ) {
                        content = content.substring( content.length() - 1 );
                    }

                    token = new PasswordResetToken();
                    token.setId( tokenId );
                    token.setTempPasswordPlaintext( content );
                    break;
                }
            }

            // close the store and folder objects
            emailFolder.close( false );
            store.close();
            return token;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
}
