package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

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

import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.LOINC;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides the REST endpoints for handling LOINC Codes. They can be
 * retrieved individually based on id, or all in a list. An Admin can add,
 * remove, or edit them.
 *
 * @author Thomas Dickerson
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APILOINCController extends APIController {

    /**
     * Returns a list of Codes in the system
     *
     * @return All the codes in the system
     */
    @GetMapping ( BASE_PATH + "/loinccodes" )
    public List<LOINC> getCodes () {
        return LOINC.getAll();
    }

    /**
     * Returns the code with the given ID
     *
     * @param id
     *            The ID of the code to retrieve
     * @return The requested Code
     */
    @GetMapping ( BASE_PATH + "/loinccode/{id}" )
    public ResponseEntity getCode ( @PathVariable ( "id" ) final Long id ) {
        final LOINC code = LOINC.getById( id );
        if ( code == null ) {
            return new ResponseEntity( errorResponse( "No code with id " + id ), HttpStatus.NOT_FOUND );
        }
        return new ResponseEntity( code, HttpStatus.OK );
    }

    /**
     * Updates the code with the specified ID to the value supplied.
     *
     * @param id
     *            The ID of the code to edit
     * @param form
     *            The new values for the Code
     * @return The Response of the action
     */
    @PutMapping ( BASE_PATH + "/loinccode/{id}" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity updateLOINC ( @PathVariable ( "id" ) final Long id, @RequestBody final LOINCForm form ) {
        final LOINC code = LOINC.getById( id );
        if ( code == null ) {
            return new ResponseEntity( errorResponse( "No code with id " + id ), HttpStatus.NOT_FOUND );
        }
        form.setId( id );
        final LOINC updatedCode = new LOINC( form );
        updatedCode.save();
        final User user = User.getByName( LoggerUtil.currentUser() );
        LoggerUtil.log( TransactionType.LOINC_EDIT, user.getUsername(), user.getUsername() + " edited a LOINC Code" );

        return new ResponseEntity( updatedCode, HttpStatus.OK );
    }

    /**
     * Adds a new code to the system
     *
     * @param form
     *            The data for the new Code
     * @return The result of the action
     */
    @PostMapping ( BASE_PATH + "/loinccodes" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity addCode ( @RequestBody final LOINCForm form ) {
        final LOINC code = new LOINC( form );
        code.save();
        final User user = User.getByName( LoggerUtil.currentUser() );
        LoggerUtil.log( TransactionType.LOINC_CREATE, user.getUsername(),
                user.getUsername() + " created a LOINC Code" );

        return new ResponseEntity( code, HttpStatus.OK );
    }

    /**
     * Deletes a code from the system.
     *
     * @param id
     *            The ID of the code to delete
     * @return The result of the action.
     */
    @DeleteMapping ( BASE_PATH + "/loinccode/{id}" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public ResponseEntity deleteCode ( @PathVariable ( "id" ) final Long id ) {
        final LOINC code = LOINC.getById( id );
        if ( code == null ) {
            return new ResponseEntity( errorResponse( "No code with id " + id ), HttpStatus.NOT_FOUND );
        }
        code.delete();
        final User user = User.getByName( LoggerUtil.currentUser() );
        LoggerUtil.log( TransactionType.LOINC_DELETE, user.getUsername(),
                user.getUsername() + " deleted a LOINC Code" );

        return new ResponseEntity( HttpStatus.OK );
    }

}
