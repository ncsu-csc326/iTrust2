package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import javax.mail.MessagingException;

import org.junit.Test;

import edu.ncsu.csc.itrust2.utils.EmailUtil;

/**
 * Test the EmailUtil class
 *
 * @author Kevin Li
 * @author Kai Presler-Marshall
 *
 */
public class EmailUtilTest {

    @Test
    public void testEmail () {
        final String email = "kli11@ncsu.edu";

        assertNotEquals( email, EmailUtil.getEmailByUsername( "admin" ) );
    }

    @Test
    public void testSendEmail () throws MessagingException {
        final String a = "Your password has been changed successfully";
        EmailUtil.sendEmail( EmailUtil.getSystemEmail(), "iTrust2: Password Changed", a );

        assertNotNull( a );
    }

}
