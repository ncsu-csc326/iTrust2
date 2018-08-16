package edu.ncsu.csc.itrust2.controllers.hcp;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class responsible for managing the behavior for the HCP Landing
 * Screen
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class HCPController {

    /**
     * Returns the Landing screen for the HCP
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @RequestMapping ( value = "hcp/index" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_HCP.getLanding();
    }

    /**
     * Returns the page allowing HCPs to edit patient demographics
     *
     * @return The page to display
     */
    @GetMapping ( "/hcp/editPatientDemographics" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String editPatientDemographics () {
        return "/hcp/editPatientDemographics";
    }

    /**
     * Returns the page allowing HCPs to edit prescriptions
     *
     * @return The page to display
     */
    @GetMapping ( "/hcp/editPrescriptions" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String editPrescriptions () {
        return "/hcp/editPrescriptions";
    }

    /**
     * Returns the page allowing HCPs to view and add reps for a patient
     *
     * @return The page to display
     */
    @GetMapping ( "/hcp/personalRepresentatives" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String personalReps () {
        return "/hcp/personalRepresentatives";
    }
    
    /**
     * Returns the ER for the given model
     * 
     * @param model
     *            model to check
     * @return role
     */
    @RequestMapping ( value = "hcp/records" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String emergencyRecords ( final Model model ) {
        return "personnel/records";
    }
    
    /**
     * Method responsible for HCP's Accept/Reject requested appointment
     * functionality. This prepares the page.
     *
     * @param model
     *            Data for the front end
     * @return The page to display to the user
     */
    @GetMapping ( "/hcp/appointmentRequests" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String requestAppointmentForm ( final Model model ) {
        return "hcp/appointmentRequests";
    }

}
