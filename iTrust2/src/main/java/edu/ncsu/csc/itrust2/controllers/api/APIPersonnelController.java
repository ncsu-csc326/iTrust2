package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * Personnel model.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPersonnelController extends APIController {

    /**
     * Retrieves and returns a list of all Personnel stored in the system
     *
     * @return list of personnel
     */
    @GetMapping ( BASE_PATH + "/personnel" )
    public List<Personnel> getPersonnel () {
        return Personnel.getPersonnel();
    }

    /**
     * Retrieves and returns the Personnel with the username provided
     *
     * @param id
     *            The username of the Personnel to be retrieved, as stored in
     *            the Users table
     * @return response
     */
    @GetMapping ( BASE_PATH + "/personnel/{id}" )
    public ResponseEntity getPersonnel ( @PathVariable ( "id" ) final String id ) {
        final Personnel personnel = Personnel.getByName( id );
        if ( null == personnel ) {
            return new ResponseEntity( errorResponse( "No personnel found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( personnel, HttpStatus.OK );
        }
    }

    /**
     * Creates a new Personnel record for a User from the RequestBody provided.
     *
     * @param personnelF
     *            the Personnel to be validated and saved to the database
     * @return response
     */
    @PostMapping ( BASE_PATH + "/personnel" )
    public ResponseEntity createPersonnel ( @RequestBody final PersonnelForm personnelF ) {
        final Personnel personnel = new Personnel( personnelF );
        if ( null != Personnel.getByName( personnel.getSelf() ) ) {
            return new ResponseEntity(
                    errorResponse( "Personnel with the id " + personnel.getSelf() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        try {
            personnel.save();
            LoggerUtil.log( TransactionType.CREATE_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( personnel, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not create " + personnel.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Updates the Personnel with the id provided by overwriting it with the new
     * Personnel record that is provided. If the ID provided does not match the
     * ID set in the Patient provided, the update will not take place
     *
     * @param id
     *            The username of the Personnel to be updated
     * @param personnelF
     *            The updated Personnel to save
     * @return response
     */
    @PutMapping ( BASE_PATH + "/personnel/{id}" )
    public ResponseEntity updatePersonnel ( @PathVariable final String id,
            @RequestBody final PersonnelForm personnelF ) {
        final Personnel personnel = new Personnel( personnelF );
        if ( null != personnel.getSelf() && null != personnel.getSelf().getUsername()
                && !id.equals( personnel.getSelf().getUsername() ) ) {
            return new ResponseEntity(
                    errorResponse( "The ID provided does not match the ID of the Personnel provided" ),
                    HttpStatus.CONFLICT );
        }
        final Personnel dbPersonnel = Personnel.getByName( id );
        if ( null == dbPersonnel ) {
            return new ResponseEntity( errorResponse( "No personnel found for id " + id ), HttpStatus.NOT_FOUND );
        }
        try {
            personnel.save();
            LoggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( personnel, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update " + personnel.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
