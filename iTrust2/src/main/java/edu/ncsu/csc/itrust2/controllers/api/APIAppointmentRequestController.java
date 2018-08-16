package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.patient.AppointmentRequestForm;
import edu.ncsu.csc.itrust2.models.enums.Status;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.utils.EmailUtil;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the AppointmentRequest model. In
 * all requests made to this controller, the {id} provided is a numeric ID that
 * is the primary key of the appointment request in question
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIAppointmentRequestController extends APIController {

    /**
     * Retrieves a list of all AppointmentRequests in the database
     *
     * @return list of appointment requests
     */
    @GetMapping ( BASE_PATH + "/appointmentrequests" )
    public List<AppointmentRequest> getAppointmentRequests () {
        return AppointmentRequest.getAppointmentRequests();
    }

    /**
     * Retrieves the AppointmentRequest specified by the username provided
     *
     * @return list of appointment requests for the logged in patient
     */
    @GetMapping ( BASE_PATH + "/appointmentrequest" )
    public List<AppointmentRequest> getAppointmentRequestsForPatient () {
        return AppointmentRequest.getAppointmentRequestsForPatient( LoggerUtil.currentUser() ).stream()
                .filter( e -> e.getStatus().equals( Status.PENDING ) ).collect( Collectors.toList() );
    }

    /**
     * Retrieves the AppointmentRequest specified by the username provided
     *
     * @return list of appointment requests for the logged in hcp
     */
    @GetMapping ( BASE_PATH + "/appointmentrequestForHCP" )
    public List<AppointmentRequest> getAppointmentRequestsForHCP () {

        return AppointmentRequest.getAppointmentRequestsForHCP( LoggerUtil.currentUser() ).stream()
                .filter( e -> e.getStatus().equals( Status.PENDING ) ).collect( Collectors.toList() );

    }

    /**
     * Retrieves the AppointmentRequest specified by the ID provided
     *
     * @param id
     *            The (numeric) ID of the AppointmentRequest desired
     * @return The AppointmentRequest corresponding to the ID provided or
     *         HttpStatus.NOT_FOUND if no such AppointmentRequest could be found
     */
    @GetMapping ( BASE_PATH + "/appointmentrequests/{id}" )
    public ResponseEntity getAppointmentRequest ( @PathVariable ( "id" ) final Long id ) {
        final AppointmentRequest request = AppointmentRequest.getById( id );
        if ( null != request ) {
            LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_VIEWED, request.getPatient(), request.getHcp() );
        }
        return null == request
                ? new ResponseEntity( errorResponse( "No AppointmentRequest found for id " + id ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( request, HttpStatus.OK );
    }

    /**
     * Creates an AppointmentRequest from the RequestBody provided. Record is
     * automatically saved in the database.
     *
     * @param requestF
     *            The AppointmentRequestForm to be parsed into an
     *            AppointmentRequest and stored
     * @return The parsed and validated AppointmentRequest created from the Form
     *         provided, HttpStatus.CONFLICT if a Request already exists with
     *         the ID of the provided request, or HttpStatus.BAD_REQUEST if
     *         another error occurred while parsing or saving the Request
     *         provided
     */
    @PostMapping ( BASE_PATH + "/appointmentrequests" )
    public ResponseEntity createAppointmentRequest ( @RequestBody final AppointmentRequestForm requestF ) {
        try {
            final AppointmentRequest request = new AppointmentRequest( requestF );
            if ( null != AppointmentRequest.getById( request.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "AppointmentRequest with the id " + request.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            request.save();
            LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_SUBMITTED, request.getPatient(), request.getHcp() );
            return new ResponseEntity( request, HttpStatus.OK );

        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Error occured while validating or saving " + requestF.toString()
                    + " because of " + e.getMessage() ), HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Deletes the AppointmentRequest with the id provided. This will remove all
     * traces from the system and cannot be reversed.
     *
     * @param id
     *            The id of the AppointmentRequest to delete
     * @return response
     */
    @DeleteMapping ( BASE_PATH + "/appointmentrequests/{id}" )
    public ResponseEntity deleteAppointmentRequest ( @PathVariable final Long id ) {
        final AppointmentRequest request = AppointmentRequest.getById( id );
        if ( null == request ) {
            return new ResponseEntity( errorResponse( "No appointmentrequest found for id " + id ),
                    HttpStatus.NOT_FOUND );
        }
        try {
            request.delete();
            LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_DELETED, request.getPatient(), request.getHcp() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not delete " + request.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Updates the AppointmentRequest with the id provided by overwriting it
     * with the new AppointmentRequest that is provided. If the ID provided does
     * not match the ID set in the AppointmentRequest provided, the update will
     * not take place
     *
     * @param id
     *            The ID of the AppointmentRequest to be updated
     * @param requestF
     *            The updated AppointmentRequestForm to parse, validate, and
     *            save
     * @return The AppointmentRequest that is created from the Form that is
     *         provided
     */
    @PutMapping ( BASE_PATH + "/appointmentrequests/{id}" )
    public ResponseEntity updateAppointmentRequest ( @PathVariable final Long id,
            @RequestBody final AppointmentRequestForm requestF ) {

        try {
            final AppointmentRequest request = new AppointmentRequest( requestF );
            request.setId( id );

            if ( null != request.getId() && !id.equals( request.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "The ID provided does not match the ID of the AppointmentRequest provided" ),
                        HttpStatus.CONFLICT );
            }
            final AppointmentRequest dbRequest = AppointmentRequest.getById( id );
            if ( null == dbRequest ) {
                return new ResponseEntity( errorResponse( "No appointmentrequest found for id " + id ),
                        HttpStatus.NOT_FOUND );
            }

            request.save();
            LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_UPDATED, request.getHcp(), request.getPatient() );
            if ( request.getStatus().getCode() == Status.APPROVED.getCode() ) {
                LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_APPROVED, request.getHcp(), request.getPatient() );
            }
            else {
                LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_DENIED, request.getHcp(), request.getPatient() );
            }

            if ( dbRequest.getStatus() != request.getStatus() ) {
                final String name = request.getPatient().getUsername();
                final String email = EmailUtil.getEmailByUsername( name );
                if ( email != null ) {
                    try {
                        EmailUtil.sendEmail( email, "iTrust2: Appointment Status Updated",
                                "The status of one of your appointments has been updated." );
                        LoggerUtil.log( TransactionType.CREATE_APPOINTMENT_REQUEST_EMAIL, name );
                    }
                    catch ( final MessagingException e ) {
                        e.printStackTrace();
                    }
                }
                else {
                    LoggerUtil.log( TransactionType.CREATE_MISSING_EMAIL_LOG, name );
                }
            }

            return new ResponseEntity( request, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update " + requestF.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Deletes _all_ of the AppointmentRequests stored in the system. Exercise
     * caution before calling this method.
     *
     * @return reponse
     */
    @DeleteMapping ( BASE_PATH + "/appointmentrequests" )
    public ResponseEntity deleteAppointmentRequests () {
        try {
            DomainObject.deleteAll( AppointmentRequest.class );
            return new ResponseEntity( successResponse( "Successfully deleted all AppointmentRequests" ),
                    HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not delete one or more AppointmentRequests " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * View Appointments will retrieve and display all appointments for the
     * logged-in HCP that are in "approved" status
     *
     *
     * @return The page to display for the user
     */
    @GetMapping ( BASE_PATH + "/viewAppointments" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public List<AppointmentRequest> upcomingAppointments () {
        final List<AppointmentRequest> appointment = AppointmentRequest
                .getAppointmentRequestsForHCP( LoggerUtil.currentUser() ).stream()
                .filter( e -> e.getStatus().equals( Status.APPROVED ) ).collect( Collectors.toList() );
        /* Log the event */
        appointment.stream().map( AppointmentRequest::getPatient ).forEach( e -> LoggerUtil
                .log( TransactionType.APPOINTMENT_REQUEST_VIEWED, LoggerUtil.currentUser(), e.getUsername() ) );
        return appointment;
    }

}
