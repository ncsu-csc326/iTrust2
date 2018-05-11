package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides multiple API endpoints for interacting with the Users
 * model.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIUserController extends APIController {

    /** constant for admin role */
    private static final String ROLE_ADMIN   = "ROLE_ADMIN";

    /** constant for patient role */
    private static final String ROLE_PATIENT = "ROLE_PATIENT";

    /** constant for hcp role */
    private static final String ROLE_HCP     = "ROLE_HCP";

    /**
     * Retrieves and returns a list of all Users in the system, regardless of
     * their classification (including all Patients, all Personnel, and all
     * users who do not have a further status specified)
     *
     * @return list of users
     */
    @GetMapping ( BASE_PATH + "/users" )
    public List<User> getUsers () {
        LoggerUtil.log( TransactionType.VIEW_USERS, LoggerUtil.currentUser() );
        return User.getUsers();
    }

    /**
     * Retrieves and returns the user with the username provided
     *
     * @param id
     *            The username of the user to be retrieved
     * @return reponse
     */
    @GetMapping ( BASE_PATH + "/users/{id}" )
    public ResponseEntity getUser ( @PathVariable ( "id" ) final String id ) {
        final User user = User.getByName( id );
        LoggerUtil.log( TransactionType.VIEW_USER, id );
        return null == user ? new ResponseEntity( errorResponse( "No User found for id " + id ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( user, HttpStatus.OK );
    }

    /**
     * Creates a new user from the RequestBody provided, validates it, and saves
     * it to the database.
     *
     * @param userF
     *            The user to be saved
     * @return response
     */
    @PostMapping ( BASE_PATH + "/users" )
    public ResponseEntity createUser ( @RequestBody final UserForm userF ) {
        final User user = new User( userF );
        if ( null != User.getByName( user.getUsername() ) ) {
            return new ResponseEntity( errorResponse( "User with the id " + user.getUsername() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        try {
            user.save();
            LoggerUtil.log( TransactionType.CREATE_USER, LoggerUtil.currentUser() );
            return new ResponseEntity( user, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not create " + user.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Updates the User with the id provided by overwriting it with the new User
     * record that is provided. If the ID provided does not match the ID set in
     * the User provided, the update will not take place
     *
     * @param id
     *            The ID of the User to be updated
     * @param userF
     *            The updated User to save in place of the existing one
     * @return response
     */
    @PutMapping ( BASE_PATH + "/users/{id}" )
    public ResponseEntity updateUser ( @PathVariable final String id, @RequestBody final UserForm userF ) {
        final User user = new User( userF );
        if ( null != user.getId() && !id.equals( user.getId() ) ) {
            return new ResponseEntity( errorResponse( "The ID provided does not match the ID of the User provided" ),
                    HttpStatus.CONFLICT );
        }
        final User dbUser = User.getByName( id );
        if ( null == dbUser ) {
            return new ResponseEntity( errorResponse( "No user found for id " + id ), HttpStatus.NOT_FOUND );
        }
        try {
            user.save(); /* Will overwrite existing user */
            LoggerUtil.log( TransactionType.UPDATE_USER, LoggerUtil.currentUser() );
            return new ResponseEntity( user, HttpStatus.OK );
        }

        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update " + user.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Gets the current logged in role.
     *
     * @return role of the currently logged in user.
     */
    @GetMapping ( BASE_PATH + "/role" )
    public ResponseEntity getRole () {
        if ( hasRole( ROLE_HCP ) ) {
            return new ResponseEntity( successResponse( ROLE_HCP ), HttpStatus.OK );
        }
        else if ( hasRole( ROLE_PATIENT ) ) {
            return new ResponseEntity( successResponse( ROLE_PATIENT ), HttpStatus.OK );
        }
        else if ( hasRole( ROLE_ADMIN ) ) {
            return new ResponseEntity( successResponse( ROLE_ADMIN ), HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "UNAUTHORIZED" ), HttpStatus.UNAUTHORIZED );
        }
    }

    /**
     * Checks if the current user has a `role`.
     *
     * @param role
     *            role to check for the user to have.
     * @return true if the user has `role`, false otherwise.
     */
    protected boolean hasRole ( final String role ) {
        // get security context from thread local
        final SecurityContext context = SecurityContextHolder.getContext();
        if ( context == null ) {
            return false;
        }

        final Authentication authentication = context.getAuthentication();
        if ( authentication == null ) {
            return false;
        }

        for ( final GrantedAuthority auth : authentication.getAuthorities() ) {
            if ( role.equals( auth.getAuthority() ) ) {
                return true;
            }
        }
        return false;
    }
}
