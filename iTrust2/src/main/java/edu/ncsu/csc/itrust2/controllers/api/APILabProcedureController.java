package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.personnel.LabProcedureForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LabProcedure;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the LabProcedure model. In all
 * requests made to this controller, the {id} provided is a Long that is the
 * primary key id of the LabProcedure requested.
 *
 * @author Alex Phelps
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APILabProcedureController extends APIController {

    /**
     * Retrieves a list of all LabProcedures in the database
     *
     * @return list of lab procedures
     */
    @PreAuthorize ( "hasRole('ROLE_HCP') or hasRole('ROLE_LABTECH')" )
    @GetMapping ( BASE_PATH + "/labprocedures" )
    public ResponseEntity getLabProcedures () {
        final List<LabProcedure> procs;
        final TransactionType logCode;
        final boolean isHCP = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains( new SimpleGrantedAuthority( "ROLE_HCP" ) );
        if ( isHCP ) {
            logCode = TransactionType.HCP_VIEW_PROCS;
            procs = LabProcedure.getLabProcedures();
            LoggerUtil.log( logCode, LoggerUtil.currentUser(), null,
                    "HCP " + LoggerUtil.currentUser() + " Views Lab Procedures" );
        }
        else {
            logCode = TransactionType.LABTECH_VIEW_PROCS;
            procs = LabProcedure.getForLabtech( LoggerUtil.currentUser() );
            LoggerUtil.log( logCode, LoggerUtil.currentUser(), null,
                    "LabTech " + LoggerUtil.currentUser() + " Views Their Lab Procedures" );
        }
        return new ResponseEntity( procs, HttpStatus.OK );
    }

    /**
     * Retrieves a list of LabProcedures for a specified LabTech.
     *
     * @param techId
     *            The id of the
     * @return Specified LabTechs LabProcedures
     */
    @GetMapping ( BASE_PATH + "/labprocedures/byUser/{techId}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public ResponseEntity getProceduresForTech ( @PathVariable ( "techId" ) final String techId ) {
        final User tech = User.getByName( techId );
        final TransactionType logCode;
        final List<LabProcedure> procs;
        if ( tech != null && tech.getRole().equals( Role.ROLE_LABTECH ) ) {
            logCode = TransactionType.HCP_VIEW_PROCS;
            procs = LabProcedure.getForLabtech( techId );
            LoggerUtil.log( logCode, LoggerUtil.currentUser(), techId,
                    "HCP " + LoggerUtil.currentUser() + " Views " + techId + "'s Lab Procedures" );
            return new ResponseEntity( procs, HttpStatus.OK );
        }
        return new ResponseEntity( errorResponse( "Could not find a LabTech with username " + techId ),
                HttpStatus.NOT_FOUND );
    }

    /**
     * Retrieves the LabProcedures for a specified OfficeVisit.
     *
     * @param id
     *            The id of the OfficeVisit to get LabProcedures from.
     *
     * @return LabProcedures associated with the specified OfficeVisit
     */
    @GetMapping ( BASE_PATH + "/labprocedures/byVisit/{id}" )
    @PreAuthorize ( "hasRole('ROLE_HCP') or hasRole('ROLE_PATIENT')" )
    public ResponseEntity getProceduresForOfficeVisit ( @PathVariable ( "id" ) final Long id ) {
        final OfficeVisit ov = OfficeVisit.getById( id );
        final TransactionType logCode;
        final List<LabProcedure> procs;
        final boolean isHCP = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains( new SimpleGrantedAuthority( "ROLE_HCP" ) );
        if ( isHCP ) {
            if ( ov != null ) {
                logCode = TransactionType.HCP_VIEW_PROCS;
                procs = LabProcedure.getForVisit( id );
                LoggerUtil.log( logCode, LoggerUtil.currentUser(), null,
                        "HCP " + LoggerUtil.currentUser() + " Views OfficeVisit " + id + " Lab Procedures" );
                return new ResponseEntity( procs, HttpStatus.OK );
            }
        }
        else { // Patient view lab procedures for office visit
            final User self = User.getByName( LoggerUtil.currentUser() );
            if ( self == null ) {
                return null;
            }
            if ( self.getUsername().equals( ov.getPatient().getUsername() ) ) {
                logCode = TransactionType.PATIENT_VIEW_PROCS;
                procs = LabProcedure.getForVisit( id );
                LoggerUtil.log( logCode, LoggerUtil.currentUser(), null,
                        "Patient " + LoggerUtil.currentUser() + " Views OfficeVisit " + id + " Lab Procedures" );
                return new ResponseEntity( procs, HttpStatus.OK );
            }
            else {
                return new ResponseEntity(
                        errorResponse( "Cannot view lab procedures for an office visit that is not yours." ),
                        HttpStatus.BAD_REQUEST );
            }
        }
        return new ResponseEntity( errorResponse( "Could not find an OfficeVisit with id " + id.toString() ),
                HttpStatus.NOT_FOUND );
    }

    /**
     * Retrieves a LabProcedure with a specified Id.
     *
     * @param id
     *            The id of the LabProcedue
     *
     * @return LabProcedure with the specified id.
     */
    @GetMapping ( BASE_PATH + "/labprocedures/{id}" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH') or hasRole('ROLE_HCP')" )
    public ResponseEntity getProcedureById ( @PathVariable ( "id" ) final Long id ) {
        final boolean isHCP = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains( new SimpleGrantedAuthority( "ROLE_HCP" ) );
        final TransactionType logCode;
        final LabProcedure proc = LabProcedure.getById( id );
        if ( proc != null ) {
            if ( isHCP ) {
                logCode = TransactionType.HCP_VIEW_PROCS;
                LoggerUtil.log( logCode, LoggerUtil.currentUser(), null,
                        "HCP " + LoggerUtil.currentUser() + " Views Lab Procedure " + id );
                return new ResponseEntity( proc, HttpStatus.OK );
            }
            else {
                logCode = TransactionType.LABTECH_VIEW_PROCS;
                LoggerUtil.log( logCode, LoggerUtil.currentUser(), null,
                        "LabTech " + LoggerUtil.currentUser() + " Views Lab Procedure " + id );
                return new ResponseEntity( proc, HttpStatus.OK );
            }
        }
        return new ResponseEntity( errorResponse( "Could not find a LabProcedure with id " + id.toString() ),
                HttpStatus.NOT_FOUND );
    }

    /**
     * Deletes all LabProcedures in the system. This cannot be reversed;
     * exercise caution before calling it
     */
    @DeleteMapping ( BASE_PATH + "/labprocedures" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public void deleteLaProcedures () {
        LoggerUtil.log( TransactionType.HCP_DELETE_PROC, LoggerUtil.currentUser() );
        LabProcedure.deleteAll();
    }

    /**
     * Creates and saves a new LabProcedure from the RequestBody provided.
     *
     * @param procF
     *            The LabProcedure to be validated and saved
     * @return response
     */
    @PostMapping ( BASE_PATH + "/labprocedures" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public ResponseEntity createLabProcedure ( @RequestBody final LabProcedureForm procF ) {
        try {
            final LabProcedure proc = new LabProcedure( procF );
            if ( LabProcedure.getById( proc.getId() ) != null ) {
                return new ResponseEntity(
                        errorResponse( "LabProcedure with the id " + proc.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            proc.save();
            LoggerUtil.log( TransactionType.HCP_CREATE_PROC, LoggerUtil.currentUser(), null );
            return new ResponseEntity( proc, HttpStatus.OK );

        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not validate or save the LabProcedure provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the LabProcedure with the id provided. This will remove all
     * traces from the system and cannot be reversed.
     *
     * @param id
     *            The id of the LabProcedure to delete
     * @return response
     */
    @DeleteMapping ( BASE_PATH + "/labprocedures/{id}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public ResponseEntity deleteLabProcedure ( @PathVariable final Long id ) {
        final LabProcedure proc = LabProcedure.getById( id );
        if ( proc == null ) {
            return new ResponseEntity( errorResponse( "No LabProcedure found for " + id ), HttpStatus.NOT_FOUND );
        }

        if ( !proc.getStatus().name().equals( "ASSIGNED" ) ) {
            return new ResponseEntity(
                    errorResponse( "Cannot delete a Lab Procedure that is in progress or completed" ),
                    HttpStatus.BAD_REQUEST );
        }

        try {
            proc.delete();
            LoggerUtil.log( TransactionType.HCP_DELETE_PROC, LoggerUtil.currentUser() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity( errorResponse( "Could not delete " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Updates the LabProcedure with the id provided by overwriting it with the
     * new LabProcedure that is provided. If the ID provided does not match the
     * ID set in the LabProcedure provided, the update will not take place
     *
     * @param id
     *            The ID of the LabProcedure to be updated
     * @param form
     *            The updated LabProcedure to save
     * @return response
     */
    @PutMapping ( BASE_PATH + "/labprocedures/{id}" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public ResponseEntity updateLabProcedure ( @PathVariable final Long id, @RequestBody final LabProcedureForm form ) {
        try {
            final LabProcedure original = LabProcedure.getById( id );
            form.setVisitId( original.getVisit().getId() );
            form.setLoincId( original.getLoinc().getId() );
            form.setPatient( original.getPatient().getUsername() );
            form.setPriority( Integer.toString( original.getPriority().getCode() ) );
            final LabProcedure lp = new LabProcedure( form );
            if ( lp.getId() != null && !id.equals( lp.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "The ID provided (" + lp.getId()
                                + ") does not match the ID of the LabProcedure provided (" + id + ")" ),
                        HttpStatus.CONFLICT );
            }
            final LabProcedure dbProcedure = LabProcedure.getById( id );
            if ( dbProcedure == null ) {
                return new ResponseEntity( errorResponse( "No LabProcedure found for id " + id ),
                        HttpStatus.NOT_FOUND );
            }
            if ( form.getAssignedTech().equals( original.getAssignedTech().getUsername() )
                    && form.getStatus().equals( original.getStatus().name() ) ) {
                return new ResponseEntity( errorResponse( "Cannot reassign to yourself" ), HttpStatus.BAD_REQUEST );
            }
            if ( form.getAssignedTech().equals( original.getAssignedTech().getUsername() )
                    && form.getStatus().equals( original.getStatus().name() )
                    && form.getComments().equals( original.getComments() ) ) {
                return new ResponseEntity( errorResponse( "You must change at least the status." ),
                        HttpStatus.BAD_REQUEST );
            }
            lp.save(); /* Will overwrite existing request */

            LoggerUtil.log( TransactionType.LABTECH_EDIT_PROC, LoggerUtil.currentUser() );
            if ( original.getAssignedTech() != lp.getAssignedTech() ) {
                LoggerUtil.log( TransactionType.LABTECH_REASSIGN_PROC, LoggerUtil.currentUser(),
                        lp.getAssignedTech().getUsername(), "LabTech " + LoggerUtil.currentUser()
                                + " Reassigns Procedure To " + lp.getAssignedTech().getUsername() );
            }
            return new ResponseEntity( lp, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update LabProcedure " + form.getId() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }
}
