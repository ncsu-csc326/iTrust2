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

import edu.ncsu.csc.itrust2.forms.hcp.PrescriptionForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Prescription;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with prescriptions. Exposes functionality
 * to add, edit, fetch, and delete prescriptions.
 *
 * @author Connor
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPrescriptionController extends APIController {

    /**
     * Adds a new prescription to the system. Requires HCP permissions.
     *
     * @param form
     *            details of the new prescription
     * @return the created prescription
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @PostMapping ( BASE_PATH + "/prescriptions" )
    public ResponseEntity addPrescription ( @RequestBody final PrescriptionForm form ) {
        try {
            final Prescription p = new Prescription( form );
            p.save();
            LoggerUtil.log( TransactionType.PRESCRIPTION_CREATE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Created prescription with id " + p.getId() );
            return new ResponseEntity( p, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_CREATE, LoggerUtil.currentUser(),
                    "Failed to create prescription" );
            return new ResponseEntity( errorResponse( "Could not save the prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits an existing prescription in the system. Matches prescriptions by
     * ids. Requires HCP permissions.
     *
     * @param form
     *            the form containing the details of the new prescription
     * @return the edited prescription
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @PutMapping ( BASE_PATH + "/prescriptions" )
    public ResponseEntity editPrescription ( @RequestBody final PrescriptionForm form ) {
        try {
            final Prescription p = new Prescription( form );
            final Prescription saved = Prescription.getById( p.getId() );
            if ( saved == null ) {
                LoggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(),
                        "No prescription found with id " + p.getId() );
                return new ResponseEntity( errorResponse( "No prescription found with id " + p.getId() ),
                        HttpStatus.NOT_FOUND );
            }
            p.save(); /* Overwrite existing */
            LoggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Edited prescription with id " + p.getId() );
            return new ResponseEntity( p, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_EDIT, LoggerUtil.currentUser(),
                    "Failed to edit prescription" );
            return new ResponseEntity( errorResponse( "Failed to update prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the prescription with the given id.
     *
     * @param id
     *            the id
     * @return the id of the deleted prescription
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @DeleteMapping ( BASE_PATH + "/prescriptions/{id}" )
    public ResponseEntity deletePrescription ( @PathVariable final Long id ) {
        final Prescription p = Prescription.getById( id );
        if ( p == null ) {
            return new ResponseEntity( errorResponse( "No prescription found with id " + id ), HttpStatus.NOT_FOUND );
        }
        try {
            p.delete();
            LoggerUtil.log( TransactionType.PRESCRIPTION_DELETE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Deleted prescription with id " + p.getId() );
            return new ResponseEntity( p.getId(), HttpStatus.OK );
        }
        catch ( final Exception e ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_DELETE, LoggerUtil.currentUser(), p.getPatient().getUsername(),
                    "Failed to delete prescription" );
            return new ResponseEntity( errorResponse( "Failed to delete prescription: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns a collection of all the prescriptions in the system.
     *
     * @return all saved prescriptions
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "/prescriptions" )
    public List<Prescription> getPrescriptions () {
        final boolean isHCP = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains( new SimpleGrantedAuthority( "ROLE_HCP" ) );
        if ( isHCP ) {
            // Return all prescriptions in system
            LoggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(),
                    "HCP viewed a list of all prescriptions" );
            return Prescription.getPrescriptions();
        }
        else {
            // Return only prescriptions assigned to the patient
            return Prescription.getForPatient( LoggerUtil.currentUser() );
        }
    }

    /**
     * Returns a single prescription using the given id.
     *
     * @param id
     *            the id of the desired prescription
     * @return the requested prescription
     */
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    @GetMapping ( BASE_PATH + "/prescriptions/{id}" )
    public ResponseEntity getPrescription ( @PathVariable final Long id ) {
        final Prescription p = Prescription.getById( id );
        if ( p == null ) {
            LoggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(),
                    "Failed to find prescription with id " + id );
            return new ResponseEntity( errorResponse( "No prescription found for " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.PRESCRIPTION_VIEW, LoggerUtil.currentUser(), "Viewed prescription  " + id );
            return new ResponseEntity( p, HttpStatus.OK );
        }
    }

}
