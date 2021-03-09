package edu.ncsu.csc.itrust2.controllers.hcp;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller that enables Disease Control Functionality for Virologists
 *
 * @author nrshah4
 *
 */
@Controller
public class VirologistController {

    /**
     * Returns the page allowing Virologists to upload passenger data
     *
     * @return The page to display
     */
    @GetMapping ( "/diseasecontrol/uploadPassengerData" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public String uploadPassengerData () {
        return "/diseasecontrol/uploadPassengerData";
    }

    /**
     * Returns the page allowing Virologists to view statistics
     *
     * @return The page to display
     */
    @GetMapping ( "/diseasecontrol/viewStatistics" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public String viewStatistics () {
        return "/diseasecontrol/viewStatistics";
    }

    /**
     * Returns the page allowing Virologists to upload passenger data
     *
     * @return The page to display
     */
    @GetMapping ( "/diseasecontrol/searchPassengerContacts" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public String searchPassengerContacts () {
        return "/diseasecontrol/searchPassengerContacts";
    }

}
