package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

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
    private static final String       ROLE_ADMIN      = "ROLE_ADMIN";

    /** constant for patient role */
    private static final String       ROLE_PATIENT    = "ROLE_PATIENT";

    /** constant for hcp role */
    private static final String       ROLE_HCP        = "ROLE_HCP";

    /** constant for ER role */
    private static final String       ROLE_ER         = "ROLE_ER";

    /** constant for lab role */
    private static final String       ROLE_LABTECH    = "ROLE_LABTECH";

    /** constant for virologist role */
    private static final String       ROLE_VIROLOGIST = "ROLE_VIROLOGIST";

    /** constant for lab role */
    private static final String       ROLE_OD         = "ROLE_OD";

    /** constant for lab role */
    private static final String       ROLE_OPH        = "ROLE_OPH";

    private static final List<String> ALL_ROLES        = List.of( ROLE_ADMIN, ROLE_PATIENT, ROLE_HCP, ROLE_ER,
            ROLE_LABTECH, ROLE_VIROLOGIST, ROLE_OD, ROLE_OPH );

    @Autowired
    private LoggerUtil                loggerUtil;

    @Autowired
    private UserService               userService;

    /**
     * Retrieves and returns a list of all Users in the system, regardless of
     * their classification (including all Patients, all Personnel, and all
     * users who do not have a further status specified)
     *
     * @return list of users
     */
    @GetMapping ( BASE_PATH + "/users" )
    public List<User> getUsers () {
        loggerUtil.log( TransactionType.VIEW_USERS, LoggerUtil.currentUser() );
        return (List<User>) userService.findAll();
    }

    /**
     * Retrieves and returns the user with the username provided
     *
     * @param id
     *            The username of the user to be retrieved
     * @return response
     */
    @GetMapping ( BASE_PATH + "/users/{id}" )
    public ResponseEntity getUser ( @PathVariable ( "id" ) final String id ) {
        final User user = userService.findByName( id );
        loggerUtil.log( TransactionType.VIEW_USER, id );
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
        if ( null != userService.findByName( userF.getUsername() ) ) {
            return new ResponseEntity( errorResponse( "User with the id " + userF.getUsername() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        User user = null;
        final List<Role> rolesOnUser = userF.getRoles().stream().map( Role::valueOf ).collect( Collectors.toList() );

        try {
            if ( rolesOnUser.contains( Role.ROLE_PATIENT ) ) {
                user = new Patient( userF );
            }

            else {
                user = new Personnel( userF );
            }

            userService.save( user );
            loggerUtil.log( TransactionType.CREATE_USER, LoggerUtil.currentUser(), user.getUsername(), null );
            return new ResponseEntity( user, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not create " + userF.getUsername() + " because of " + e.getMessage() ),
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
        User user = null;
        final List<Role> rolesOnUser = userF.getRoles().stream().map( Role::valueOf ).collect( Collectors.toList() );

        try {
            if ( rolesOnUser.contains( Role.ROLE_PATIENT ) ) {
                user = new Patient( userF );
            }
            else {
                user = new Personnel( userF );
            }

            if ( null != user.getId() && !id.equals( user.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "The ID provided does not match the ID of the User provided" ),
                        HttpStatus.CONFLICT );
            }
            final User dbUser = userService.findByName( id );
            if ( null == dbUser ) {
                return new ResponseEntity( errorResponse( "No user found for id " + id ), HttpStatus.NOT_FOUND );
            }

            userService.save( user ); /* Will overwrite existing user */
            loggerUtil.log( TransactionType.UPDATE_USER, LoggerUtil.currentUser() );
            return new ResponseEntity( user, HttpStatus.OK );
        }

        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not update " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Deletes the user with the id matching the given id. Requires admin
     * permissions.
     *
     * @param id
     *            the id of the user to delete
     * @return the id of the deleted user
     */
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    @DeleteMapping ( BASE_PATH + "/users/{id}" )
    public ResponseEntity deleteUser ( @PathVariable final String id ) {
        final User user = userService.findByName( id );
        try {
            if ( null == user ) {
                return new ResponseEntity( errorResponse( "No user found for id " + id ), HttpStatus.NOT_FOUND );
            }
            userService.delete( user );
            loggerUtil.log( TransactionType.DELETE_USER, LoggerUtil.currentUser() );
            return new ResponseEntity( id, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not delete " + id + " because of " + e.getMessage() ),
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
        final List<String> matchingRoles = ALL_ROLES.stream().filter( role -> hasRole( role ) )
                .collect( Collectors.toList() );

        if ( matchingRoles.isEmpty() ) {
            return new ResponseEntity( errorResponse( "UNAUTHORIZED" ), HttpStatus.UNAUTHORIZED );
        }
        final String joinedRoles = String.join( ",", matchingRoles );

        return new ResponseEntity( successResponse( joinedRoles ), HttpStatus.OK );

    }

    @PostMapping ( BASE_PATH + "generateUsers" )
    public ResponseEntity generateUsers () {
        final User admin = new Personnel( new UserForm( "admin", "123456", Role.ROLE_ADMIN, 1 ) );

        final User doc = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        userService.save( admin );

        userService.save( doc );

        final User multiRoleDoc = new Personnel( new UserForm( "er", "123456", Role.ROLE_HCP, 1 ) );
        multiRoleDoc.addRole( Role.ROLE_ER );

        userService.save( multiRoleDoc );

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        userService.save( patient );

        loggerUtil.log( TransactionType.USERS_GENERATED, "" );

        return new ResponseEntity( HttpStatus.OK );
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
