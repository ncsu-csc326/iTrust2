package edu.ncsu.csc.itrust2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;

/**
 * Class for sending email. Used for the Password Reset emails.
 *
 * @author Kai Presler-Marshall
 *
 */
public class EmailUtil {

    /**
     * Static function to retrieve the email from a givent user
     *
     * @param username
     *            username of user to get email of
     * @return email of user or null if doesnt exist
     */
    public static String getEmailByUsername ( final String username ) {
        final User user = User.getByName( username );
        if ( user == null ) {
            return null;
        }
        String email = null;
        if ( user.getRole() == Role.ROLE_PATIENT ) {
            final Patient patient = Patient.getByName( username );
            if ( patient != null ) {
                email = patient.getEmail();
            }
        }
        else {
            final Personnel pers = Personnel.getByName( user );
            if ( pers != null ) {
                email = pers.getEmail();
            }
        }

        return null == email || email.equals( "" ) || email.equals( " " ) ? null : email;
    }

    private static Properties getEmailProperties () {
        InputStream input = null;
        final Properties properties = new Properties();

        final String filename = "email.properties";

        // ClassLoader approach doesn't work for Jetty
        try {
            input = new FileInputStream( new File( "src/main/java/" + filename ) );

        }
        catch ( final Exception e ) {
            // deliberately ignoring this to try the ClassLoader below (for
            // Tomcat)
        }
        if ( null == input ) {
            input = DBUtil.class.getClassLoader().getResourceAsStream( filename );
        }

        if ( null != input ) {
            try {
                properties.load( input );
            }
            catch ( final IOException e ) {
                e.printStackTrace();
            }
        }
        else {
            throw new NullPointerException( "Cannot read input file" );
        }
        return properties;
    }

    /**
     * Retrieves the System Email address. This can be used as a known-valid
     * address to send to rather than hardcoding one. This address is pulled
     * from the email.properties file
     *
     * @return Address from the email.properties file
     */
    static public final String getSystemEmail () {
        final String email = getEmailProperties().getProperty( "username" );
        return email.contains( "gmail" ) ? email : email + "@gmail.com";
    }

    /**
     * Send an email from the email account in the system's `email.properties`
     * file
     *
     * @param addr
     *            Address to send to
     * @param subject
     *            Subject of the email
     * @param body
     *            Body of the message to send
     * @throws MessagingException
     *             Results from JavaX Mail functionality
     */
    public static void sendEmail ( final String addr, final String subject, final String body )
            throws MessagingException {

        final Properties properties = getEmailProperties();

        final String to = addr;
        final String from;
        final String username;
        final String password;
        final String host;

        from = properties.getProperty( "from" );
        username = properties.getProperty( "username" );
        password = properties.getProperty( "password" );
        host = properties.getProperty( "host" );

        /*
         * Source for java mail code:
         * https://www.tutorialspoint.com/javamail_api/
         * javamail_api_gmail_smtp_server.htm
         */

        final Properties props = new Properties();
        props.put( "mail.smtp.auth", "true" );
        props.put( "mail.smtp.starttls.enable", "true" );
        props.put( "mail.smtp.host", host );
        props.put( "mail.smtp.port", "587" );

        final Session session = Session.getInstance( props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication () {
                return new PasswordAuthentication( username, password );
            }
        } );

        try {
            final Message message = new MimeMessage( session );
            message.setFrom( new InternetAddress( from ) );
            message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( to ) );
            message.setSubject( subject );
            message.setText( body );
            Transport.send( message );
        }
        catch ( final MessagingException e ) {
            e.printStackTrace();
            throw e;
        }
    }

}
