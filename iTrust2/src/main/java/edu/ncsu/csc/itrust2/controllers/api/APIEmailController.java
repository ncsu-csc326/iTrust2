package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.models.Email;
import edu.ncsu.csc.iTrust2.services.EmailService;

@RestController
public class APIEmailController extends APIController {

    @Autowired
    private EmailService service;

    @GetMapping ( BASE_PATH + "emails" )
    public List<Email> getEmails () {
        return (List<Email>) service.findAll();
    }

}
