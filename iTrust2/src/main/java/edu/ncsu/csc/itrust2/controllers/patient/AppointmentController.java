package edu.ncsu.csc.itrust2.controllers.patient;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import edu.ncsu.csc.itrust2.forms.patient.AppointmentForm;
import edu.ncsu.csc.itrust2.forms.patient.AppointmentRequestForm;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller for various appointment-related actions for a Patient in the
 * system.
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class AppointmentController {

    /**
     * Retrieves the page for the Patient to request an Appointment
     *
     * @param model
     *            Data for the front end
     * @return The page the patient should see
     */
    @GetMapping ( "/patient/requestAppointment" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String requestAppointmentForm ( final Model model ) {
        model.addAttribute( "AppointmentRequestForm", new AppointmentRequestForm() );
        model.addAttribute( "hcps", User.getHCPs() );
        return "/patient/requestAppointment";
    }

    /**
     * Handles processing the AppointmentRequestForm from the Patient
     *
     * @param form
     *            With all of the details of the Request created
     * @param result
     *            Validation results
     * @param model
     *            Data from the front end
     * @return Page to display to the user
     */
    @PostMapping ( "/patient/requestAppointment" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String inventorySubmit (
            @Valid @ModelAttribute ( "AppointmentRequestForm" ) final AppointmentRequestForm form,
            final BindingResult result, final Model model ) {
        form.setPatient( SecurityContextHolder.getContext().getAuthentication().getName() );
        AppointmentRequest req = null;
        try {
            req = new AppointmentRequest( form );
        }
        catch ( final Exception e ) {
            result.reject( "Error occurred while parsing form" );
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "hcps", User.getHCPs() );
            return "patient/requestAppointment";
        }
        else {
            req.save();
            LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_SUBMITTED, form.getPatient() );
            return "patient/requestAppointmentResult";
        }
    }

    /**
     * Creates a page of all AppointmentRequests so the patient can view them
     * 
     * @param model
     *            Data from the front end
     * @return The page for the patient to view their appointment requests
     */
    @GetMapping ( "/patient/viewAppointmentRequests" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewAppointmentRequests ( final Model model ) {
        /* Default to showing only the Pending events */
        final List<AppointmentRequest> appointment = AppointmentRequest
                .getAppointmentRequestsForPatient( SecurityContextHolder.getContext().getAuthentication().getName() )
                .stream().filter( e -> e.getStatus().equals( Status.PENDING ) ).collect( Collectors.toList() );
        final List<AppointmentRequestForm> appointments = new Vector<AppointmentRequestForm>();
        for ( final AppointmentRequest ar : appointment ) {
            appointments.add( new AppointmentRequestForm( ar ) );
        }
        model.addAttribute( "appointmentForm", new AppointmentForm() );
        model.addAttribute( "appointments", appointments );
        LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_VIEWED,
                SecurityContextHolder.getContext().getAuthentication().getName() );
        return "patient/viewAppointmentRequests";
    }

    /**
     * Delete Appointment request mapping
     *
     * @param form
     *            AppointmentForm with the appointment to delete
     * @return response
     */
    @PostMapping ( "/patient/viewAppointmentRequests" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewRequestsSubmit ( @ModelAttribute final AppointmentForm form ) {
        final int id = Integer.parseInt( form.getAppointment() );
        AppointmentRequest.getById( Long.valueOf( id ) ).delete();
        LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_SUBMITTED,
                SecurityContextHolder.getContext().getAuthentication().getName() );
        return "patient/viewAppointmentRequestsResult";
    }

}
