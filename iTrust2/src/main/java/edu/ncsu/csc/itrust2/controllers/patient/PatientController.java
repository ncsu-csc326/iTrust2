package edu.ncsu.csc.itrust2.controllers.patient;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller for the Patient landing screen and basic patient information
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class PatientController {
	
    /**
     * Retrieves the page for the Patient to request an Appointment
     *
     * @param model
     *            Data for the front end
     * @return The page the patient should see
     */
    @GetMapping ( "/patient/appointmentRequest/manageAppointmentRequest" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String requestAppointmentForm ( final Model model ) {
        return "/patient/appointmentRequest/manageAppointmentRequest";
    }

    /**
     * Returns the form page for a patient to view all OfficeVisits
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/patient/officeVisit/viewOfficeVisits" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewOfficeVisits ( final Model model ) {
        return "/patient/officeVisit/viewOfficeVisits";
    }

    /**
     * Returns the form page for a patient to view all prescriptions
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/patient/officeVisit/viewPrescriptions" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewPrescriptions ( final Model model ) {
        return "/patient/officeVisit/viewPrescriptions";
    }

    /**
     * Landing screen for a Patient when they log in
     *
     * @param model
     *            The data from the front end
     * @return The page to show to the user
     */
    @RequestMapping ( value = "patient/index" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_PATIENT.getLanding();
    }

    /**
     * Provides the page for a User to view and edit their demographics
     *
     * @param model
     *            The data for the front end
     * @return The page to show the user so they can edit demographics
     */
    @GetMapping ( value = "patient/demographics/editDemographics" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewDemographics ( final Model model ) {
        final User self = User.getByName( LoggerUtil.currentUser() );
        LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, self );
        return "/patient/demographics/editDemographics";
    }


    /**
     * Returns the page for a patient to view Personal
     * Representatives
     *
     * @return Page to display to the user
     */
    @GetMapping ( value = "/patient/personalRepresentative/personalRepresentatives" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewPersonalReps () {
        return "/patient/personalRepresentative/personalRepresentatives";
    }
    
    /**
     * Create a page for the patient to view all diagnoses
     *
     * @param model
     *            data for front end
     * @return The page for the patient to view their diagnoses
     */
    @GetMapping ( value = "patient/officeVisit/viewDiagnoses" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewDiagnoses ( final Model model ) {
        return "/patient/officeVisit/viewDiagnoses";
    }


}
