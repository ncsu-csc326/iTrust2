package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

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

import edu.ncsu.csc.iTrust2.forms.DrugForm;
import edu.ncsu.csc.iTrust2.models.Drug;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.DrugService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Provides REST endpoints that deal with drugs. Exposes functionality to add,
 * edit, fetch, and delete drug.
 *
 * @author Connor
 * @author Kai Presler-Marshall
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIDrugController extends APIController {

    @Autowired
    private DrugService service;

    @Autowired
    private LoggerUtil  loggerUtil;

    /**
     * Adds a new drug to the system. Requires admin permissions. Returns an
     * error message if something goes wrong.
     *
     * @param form
     *            the drug form
     * @return the created drug
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PostMapping ( BASE_PATH + "/drugs" )
    public ResponseEntity addDrug ( @RequestBody final DrugForm form ) {
        try {
            final Drug drug = new Drug( form );

            // Make sure code does not conflict with existing drugs
            if ( service.existsByCode( drug.getCode() ) ) {
                loggerUtil.log( TransactionType.DRUG_CREATE, LoggerUtil.currentUser(),
                        "Conflict: drug with code " + drug.getCode() + " already exists" );
                return new ResponseEntity( errorResponse( "Drug with code " + drug.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            service.save( drug );
            loggerUtil.log( TransactionType.DRUG_CREATE, LoggerUtil.currentUser(),
                    "Drug " + drug.getCode() + " created" );
            return new ResponseEntity( drug, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.DRUG_CREATE, LoggerUtil.currentUser(), "Failed to create drug" );
            return new ResponseEntity( errorResponse( "Could not add drug: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Edits a drug in the system. The id stored in the form must match an
     * existing drug, and changes to NDCs cannot conflict with existing NDCs.
     * Requires admin permissions.
     *
     * @param form
     *            the edited drug form
     * @return the edited drug or an error message
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @PutMapping ( BASE_PATH + "/drugs" )
    public ResponseEntity editDrug ( @RequestBody final DrugForm form ) {
        try {
            // Check for existing drug in database
            final Drug savedDrug = (Drug) service.findById( form.getId() );
            if ( savedDrug == null ) {
                return new ResponseEntity( errorResponse( "No drug found with code " + form.getCode() ),
                        HttpStatus.NOT_FOUND );
            }

            final Drug drug = new Drug( form );

            // If the code was changed, make sure it is unique
            final Drug sameCode = service.findByCode( drug.getCode() );
            if ( sameCode != null && !sameCode.getId().equals( savedDrug.getId() ) ) {
                return new ResponseEntity( errorResponse( "Drug with code " + drug.getCode() + " already exists" ),
                        HttpStatus.CONFLICT );
            }

            service.save( drug ); /* Overwrite existing drug */

            loggerUtil.log( TransactionType.DRUG_EDIT, LoggerUtil.currentUser(),
                    "Drug with id " + drug.getId() + " edited" );
            return new ResponseEntity( drug, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.DRUG_EDIT, LoggerUtil.currentUser(), "Failed to edit drug" );
            return new ResponseEntity( errorResponse( "Could not update drug: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the drug with the id matching the given id. Requires admin
     * permissions.
     *
     * @param id
     *            the id of the drug to delete
     * @return the id of the deleted drug
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @DeleteMapping ( BASE_PATH + "/drugs/{id}" )
    public ResponseEntity deleteDrug ( @PathVariable final String id ) {
        try {
            final Drug drug = (Drug) service.findById( Long.parseLong( id ) );
            if ( drug == null ) {
                loggerUtil.log( TransactionType.DRUG_DELETE, LoggerUtil.currentUser(),
                        "Could not find drug with id " + id );
                return new ResponseEntity( errorResponse( "No drug found with id " + id ), HttpStatus.NOT_FOUND );
            }
            service.delete( drug );
            loggerUtil.log( TransactionType.DRUG_DELETE, LoggerUtil.currentUser(),
                    "Deleted drug with id " + drug.getId() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            loggerUtil.log( TransactionType.DRUG_DELETE, LoggerUtil.currentUser(), "Failed to delete drug" );
            return new ResponseEntity( errorResponse( "Could not delete drug: " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets a list of all the drugs in the system.
     *
     * @return a list of drugs
     */
    @GetMapping ( BASE_PATH + "/drugs" )
    public List<Drug> getDrugs () {
        loggerUtil.log( TransactionType.DRUG_VIEW, LoggerUtil.currentUser(), "Fetched list of drugs" );
        return (List<Drug>) service.findAll();
    }

}
