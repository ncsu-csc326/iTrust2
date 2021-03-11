package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import edu.ncsu.csc.iTrust2.forms.AppointmentRequestForm;
import edu.ncsu.csc.iTrust2.models.AppointmentRequest;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.Status;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.AppointmentRequestService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the AppointmentRequest model. In
 * all requests made to this controller, the {id} provided is a numeric ID that
 * is the primary key of the appointment request in question
 *
 * @author Kai Presler-Marshall
 * @author Matt Dzwonczyk
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIAppointmentRequestController extends APIController {

    @Autowired
    private AppointmentRequestService service;

    @Autowired
    private LoggerUtil                loggerUtil;

    @Autowired
    private UserService               userService;

    /**
     * Retrieves a list of all AppointmentRequests in the database
     *
     * @return list of appointment requests
     */
    @GetMapping ( BASE_PATH + "/appointmentrequests" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public List<AppointmentRequest> getAppointmentRequests () {
        final List<AppointmentRequest> requests = (List<AppointmentRequest>) service.findAll();

        requests.stream().map( AppointmentRequest::getPatient ).distinct().forEach( e -> loggerUtil
                .log( TransactionType.APPOINTMENT_REQUEST_VIEWED, LoggerUtil.currentUser(), e.getUsername() ) );

        return requests;
    }

    /**
     * Retrieves the AppointmentRequest specified by the username provided
     *
     * @return list of appointment requests for the logged in patient
     */
    @GetMapping ( BASE_PATH + "/appointmentrequest" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public List<AppointmentRequest> getAppointmentRequestsForPatient () {
        final User patient = userService.findByName( LoggerUtil.currentUser() );
        return service.findByPatient( patient ).stream().filter( e -> e.getStatus().equals( Status.PENDING ) )
                .collect( Collectors.toList() );
    }

    /**
     * Retrieves the AppointmentRequest specified by the username provided
     *
     * @return list of appointment requests for the logged in hcp
     */
    @GetMapping ( BASE_PATH + "/appointmentrequestForHCP" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public List<AppointmentRequest> getAppointmentRequestsForHCP () {

        final User hcp = userService.findByName( LoggerUtil.currentUser() );

        return service.findByHcp( hcp ).stream().filter( e -> e.getStatus().equals( Status.PENDING ) )
                .collect( Collectors.toList() );

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
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT')" )
    public ResponseEntity getAppointmentRequest ( @PathVariable ( "id" ) final Long id ) {
        final AppointmentRequest request = (AppointmentRequest) service.findById( id );
        if ( null != request ) {
            loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_VIEWED, request.getPatient(), request.getHcp() );

            /* Patient can't look at anyone else's requests */
            final User self = userService.findByName( LoggerUtil.currentUser() );
            if ( self.getRoles().contains( Role.ROLE_PATIENT ) && !request.getPatient().equals( self ) ) {
                return new ResponseEntity( HttpStatus.UNAUTHORIZED );
            }
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
     * @param requestForm
     *            The AppointmentRequestForm to be parsed into an
     *            AppointmentRequest and stored
     * @return The parsed and validated AppointmentRequest created from the Form
     *         provided, HttpStatus.CONFLICT if a Request already exists with
     *         the ID of the provided request, or HttpStatus.BAD_REQUEST if
     *         another error occurred while parsing or saving the Request
     *         provided
     */
    @PostMapping ( BASE_PATH + "/appointmentrequests" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity createAppointmentRequest ( @RequestBody final AppointmentRequestForm requestForm ) {
        try {
            final AppointmentRequest request = service.build( requestForm );
            if ( null != service.findById( request.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "AppointmentRequest with the id " + request.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            service.save( request );
            loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_SUBMITTED, request.getPatient(), request.getHcp() );
            return new ResponseEntity( request, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Error occurred while validating or saving "
                    + requestForm.toString() + " because of " + e.getMessage() ), HttpStatus.BAD_REQUEST );
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
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT')" )
    public ResponseEntity deleteAppointmentRequest ( @PathVariable final Long id ) {
        final AppointmentRequest request = (AppointmentRequest) service.findById( id );
        if ( null == request ) {
            return new ResponseEntity( errorResponse( "No AppointmentRequest found for id " + id ),
                    HttpStatus.NOT_FOUND );
        }

        /* Patient can't look at anyone else's requests */
        final User self = userService.findByName( LoggerUtil.currentUser() );
        if ( self.getRoles().contains( Role.ROLE_PATIENT ) && !request.getPatient().equals( self ) ) {
            return new ResponseEntity( HttpStatus.UNAUTHORIZED );
        }
        try {
            service.delete( request );
            loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_DELETED, request.getPatient(), request.getHcp() );
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
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT')" )
    public ResponseEntity updateAppointmentRequest ( @PathVariable final Long id,
            @RequestBody final AppointmentRequestForm requestF ) {
        try {
            final AppointmentRequest request = service.build( requestF );
            request.setId( id );

            if ( null != request.getId() && !id.equals( request.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "The ID provided does not match the ID of the AppointmentRequest provided" ),
                        HttpStatus.CONFLICT );
            }

            /* Patient can't look at anyone else's requests */
            final User self = userService.findByName( LoggerUtil.currentUser() );
            if ( self.getRoles().contains( Role.ROLE_PATIENT ) && !request.getPatient().equals( self ) ) {
                return new ResponseEntity( HttpStatus.UNAUTHORIZED );
            }

            final AppointmentRequest dbRequest = (AppointmentRequest) service.findById( id );
            if ( null == dbRequest ) {
                return new ResponseEntity( errorResponse( "No AppointmentRequest found for id " + id ),
                        HttpStatus.NOT_FOUND );
            }

            service.save( request );
            loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_UPDATED, request.getPatient(), request.getHcp() );
            if ( request.getStatus().getCode() == Status.APPROVED.getCode() ) {
                loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_APPROVED, request.getPatient(), request.getHcp() );
            }
            else {
                loggerUtil.log( TransactionType.APPOINTMENT_REQUEST_DENIED, request.getPatient(), request.getHcp() );
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
     * View Appointments will retrieve and display all appointments for the
     * logged-in HCP that are in "approved" status
     *
     *
     * @return The page to display for the user
     */
    @GetMapping ( BASE_PATH + "/viewAppointments" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public List<AppointmentRequest> upcomingAppointments () {
        final User hcp = userService.findByName( LoggerUtil.currentUser() );

        final List<AppointmentRequest> appointment = service.findByHcp( hcp ).stream()
                .filter( e -> e.getStatus().equals( Status.APPROVED ) ).collect( Collectors.toList() );
        /* Log the event */
        appointment.stream().map( AppointmentRequest::getPatient ).distinct().forEach( e -> loggerUtil
                .log( TransactionType.APPOINTMENT_REQUEST_VIEWED, LoggerUtil.currentUser(), e.getUsername() ) );
        return appointment;
    }

}
