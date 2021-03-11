package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.PersonnelForm;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.PersonnelService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

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

    @Autowired
    private LoggerUtil       loggerUtil;

    @Autowired
    private PersonnelService service;

    /**
     * Retrieves and returns a list of all Personnel stored in the system
     *
     * @return list of personnel
     */
    @GetMapping ( BASE_PATH + "/personnel" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN')" )
    public List<Personnel> getPersonnel () {
        return (List<Personnel>) service.findAll();
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
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN')" )
    public ResponseEntity getPersonnel ( @PathVariable ( "id" ) final String id ) {
        final Personnel personnel = (Personnel) service.findByName( id );
        if ( null == personnel ) {
            return new ResponseEntity( errorResponse( "No personnel found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( personnel, HttpStatus.OK );
        }
    }

    /**
     * If you are logged in as a personnel, then you can use this convenience
     * lookup to find your own information without remembering your id. This
     * allows you the shorthand of not having to look up the id in between.
     *
     * @return The personnel object for the currently authenticated user.
     */
    @GetMapping ( BASE_PATH + "/curPersonnel" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN')" )
    public ResponseEntity getCurrentPersonnel () {
        final String username = LoggerUtil.currentUser();
        final Personnel personnel = (Personnel) service.findByName( username );
        if ( personnel == null ) {
            return new ResponseEntity( errorResponse( "Could not find a personnel entry for you, " + username ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, username,
                    "Retrieved demographics for user " + username );
            return new ResponseEntity( personnel, HttpStatus.OK );
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
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN')" )
    public ResponseEntity updatePersonnel ( @PathVariable final String id,
            @RequestBody final PersonnelForm personnelF ) {

        final Personnel fromDb = (Personnel) service.findByName( id );

        if ( null == fromDb ) {
            return new ResponseEntity( errorResponse( "Could not find a personnel entry for you, " + id ),
                    HttpStatus.NOT_FOUND );
        }

        fromDb.update( personnelF );
        if ( ( null != fromDb.getUsername() && !id.equals( fromDb.getUsername() ) ) ) {
            return new ResponseEntity(
                    errorResponse( "The ID provided does not match the ID of the Personnel provided" ),
                    HttpStatus.CONFLICT );
        }
        try {
            service.save( fromDb );
            loggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( fromDb, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not update " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns only personnel of a specific role, based on what the user wants.
     *
     * @param role
     *            the role to filter out personnel by
     * @return response and list of personnel matching query
     */
    @GetMapping ( BASE_PATH + "/personnel/getbyroles/{role}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN', 'ROLE_PATIENT')" )
    public ResponseEntity getPersonnelByRole ( @PathVariable ( "role" ) final String role ) {
        final List<Personnel> allPersonnel = (List<Personnel>) service.findAll();

        try {
            final Role desired = Role.valueOf( role );

            allPersonnel.removeIf( e -> !e.getRoles().contains( desired ) );

            return new ResponseEntity( allPersonnel, HttpStatus.OK );
        }
        catch ( final IllegalArgumentException iae ) {
            return new ResponseEntity( errorResponse( "Invalid role" ), HttpStatus.BAD_REQUEST );
        }

    }

}
