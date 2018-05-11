package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.patient.AppointmentRequestForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.AppointmentRequest;
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
        return null == request ? new ResponseEntity( "No AppointmentRequest found for id " + id, HttpStatus.NOT_FOUND )
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
                return new ResponseEntity( "AppointmentRequest with the id " + request.getId() + " already exists",
                        HttpStatus.CONFLICT );
            }
            request.save();
            LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_SUBMITTED, request.getPatient(), request.getHcp() );
            return new ResponseEntity( request, HttpStatus.OK );

        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    "Error occured while validating or saving " + requestF.toString() + " because of " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
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
            return new ResponseEntity( "No appointmentrequest found for id " + id, HttpStatus.NOT_FOUND );
        }
        try {
            request.delete();
            LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_DELETED, request.getPatient(), request.getHcp() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( "Could not delete " + request.toString() + " because of " + e.getMessage(),
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

            if ( null != request.getId() && !id.equals( request.getId() ) ) {
                return new ResponseEntity( "The ID provided does not match the ID of the AppointmentRequest provided",
                        HttpStatus.CONFLICT );
            }
            final AppointmentRequest dbRequest = AppointmentRequest.getById( id );
            if ( null == dbRequest ) {
                return new ResponseEntity( "No appointmentrequest found for id " + id, HttpStatus.NOT_FOUND );
            }

            request.save();
            LoggerUtil.log( TransactionType.APPOINTMENT_REQUEST_UPDATED, request.getPatient(), request.getHcp() );
            return new ResponseEntity( request, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( "Could not update " + requestF.toString() + " because of " + e.getMessage(),
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
            AppointmentRequest.deleteAll( AppointmentRequest.class );
            return new ResponseEntity( "Successfully deleted all AppointmentRequests", HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( "Could not delete one or more AppointmentRequests " + e.getMessage(),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
