package edu.ncsu.csc.iTrust2.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.iTrust2.models.Email;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.services.EmailService;

@Component
public class EmailUtil {

    @Autowired
    private EmailService service;

    public void sendEmail ( final User receiver, final String subject, final String messageBody ) {
        sendEmail( "iTrust2 System", receiver, subject, messageBody );
    }

    public void sendEmail ( final String sender, final User receiver, final String subject, final String messageBody ) {
        final Email email = new Email( sender, receiver, subject, messageBody );
        service.save( email );
    }
}
