package edu.ncsu.csc.itrust2.controllers.patient;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This controller enables Patients to view their current and previous
 * diagnoses.
 *
 * @author Tam Le
 *
 */

@Controller

public class DiagnosesController {
    /**
     * Create a page for the patient to view all diagnoses
     *
     * @param model
     *            data for front end
     * @return The page for the patient to view their diagnoses
     */
    @GetMapping ( value = "patient/viewDiagnoses" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewDiagnoses ( final Model model ) {
        return "/patient/viewDiagnoses";
    }

}
