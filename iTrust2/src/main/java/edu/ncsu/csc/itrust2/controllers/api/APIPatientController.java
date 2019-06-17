package edu.ncsu.csc.itrust2.controllers.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.hcp_patient.PatientForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * Patient model.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPatientController extends APIController {

    /**
     * Retrieves and returns a list of all Patients stored in the system
     *
     * @return list of patients
     */
    @GetMapping ( BASE_PATH + "/patients" )
    public List<Patient> getPatients () {
        final List<Patient> patients = Patient.getPatients();
        for ( final Patient p : patients ) {
            p.setRepresentatives( null );
            p.setRepresented( null );
        }
        return patients;
    }

    /**
     * If you are logged in as a patient, then you can use this convenience
     * lookup to find your own information without remembering your id. This
     * allows you the shorthand of not having to look up the id in between.
     *
     * @return The patient object for the currently authenticated user.
     */
    @GetMapping ( BASE_PATH + "/patient" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public ResponseEntity getPatient () {
        final User self = User.getByName( LoggerUtil.currentUser() );
        final Patient patient = Patient.getByName( self.getUsername() );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for you, " + self.getUsername() ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, LoggerUtil.currentUser(),
                    "Retrieved demographics for user " + self.getUsername() );
            patient.setRepresentatives( null );
            patient.setRepresented( null );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
    }

    /**
     * Retrieves and returns the Patient with the username provided
     *
     * @param username
     *            The username of the Patient to be retrieved, as stored in the
     *            Users table
     * @return response
     */
    @GetMapping ( BASE_PATH + "/patients/{username}" )
    public ResponseEntity getPatient ( @PathVariable ( "username" ) final String username ) {
        final Patient patient = Patient.getByName( username );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "No Patient found for username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            LoggerUtil.log( TransactionType.PATIENT_DEMOGRAPHICS_VIEW, LoggerUtil.currentUser(), username,
                    "HCP retrieved demographics for patient with username " + username );
            patient.setRepresentatives( null );
            patient.setRepresented( null );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
    }

    /**
     * Creates a new Patient record for a User from the RequestBody provided.
     *
     * @param patientF
     *            the Patient to be validated and saved to the database
     * @return response
     */
    @PostMapping ( BASE_PATH + "/patients" )
    public ResponseEntity createPatient ( @RequestBody final PatientForm patientF ) {
        try {
            if ( patientF.getSelf() == null ) {
                final User self = User.getByName( LoggerUtil.currentUser() );
                patientF.setSelf( self.getUsername() );
            }
            final Patient patient = new Patient( patientF );
            if ( null != Patient.getPatient( patient.getSelf() ) ) {
                return new ResponseEntity(
                        errorResponse( "Patient with the id " + patient.getSelf().getUsername() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            patient.save();
            LoggerUtil.log( TransactionType.CREATE_DEMOGRAPHICS, LoggerUtil.currentUser() );
            patient.setRepresentatives( null );
            patient.setRepresented( null );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not create " + patientF.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Updates the Patient with the id provided by overwriting it with the new
     * Patient record that is provided. If the ID provided does not match the ID
     * set in the Patient provided, the update will not take place
     *
     * @param id
     *            The username of the Patient to be updated
     * @param patientF
     *            The updated Patient to save
     * @return response
     */
    @PutMapping ( BASE_PATH + "/patients/{id}" )
    public ResponseEntity updatePatient ( @PathVariable final String id, @RequestBody final PatientForm patientF ) {
        // check that the user is an HCP or a patient with username equal to id
        boolean userEdit = false; // true if user edits his or her own
                                  // demographics, false if hcp edits them
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            if ( ( !auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_HCP" ) )
                    && !auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_OD" ) )
                    && !auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_OPH" ) ) )
                    && ( !auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_PATIENT" ) )
                            || !auth.getName().equals( id ) ) ) {
                return new ResponseEntity( errorResponse( "You do not have permission to edit this record" ),
                        HttpStatus.UNAUTHORIZED );
            }

            userEdit = auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_HCP" ) )
                    || auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_OD" ) )
                    || auth.getAuthorities().contains( new SimpleGrantedAuthority( "ROLE_OPH" ) );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( HttpStatus.UNAUTHORIZED );
        }

        try {
            final Patient patient = new Patient( patientF );
            if ( null != patient.getSelf().getUsername() && !id.equals( patient.getSelf().getUsername() ) ) {
                return new ResponseEntity(
                        errorResponse( "The ID provided does not match the ID of the Patient provided" ),
                        HttpStatus.CONFLICT );
            }
            final Patient dbPatient = Patient.getByName( id );
            if ( null == dbPatient ) {
                return new ResponseEntity( errorResponse( "No Patient found for id " + id ), HttpStatus.NOT_FOUND );
            }
            patient.save();

            // Log based on whether user or hcp edited demographics
            if ( userEdit ) {
                LoggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser(),
                        "User with username " + patient.getSelf().getUsername() + "updated their demographics" );
            }
            else {
                LoggerUtil.log( TransactionType.PATIENT_DEMOGRAPHICS_EDIT, LoggerUtil.currentUser(),
                        patient.getSelf().getUsername(),
                        "HCP edited demographics for patient with username " + patient.getSelf().getUsername() );
            }
            patient.setRepresentatives( null );
            patient.setRepresented( null );
            return new ResponseEntity( patient, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity(
                    errorResponse( "Could not update " + patientF.toString() + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns all representatives for a given patient.
     *
     * @param username
     *            The patient's username
     * @return The patient objects for all the users representatives.
     */
    @GetMapping ( BASE_PATH + "/patient/representatives/{username}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_PATIENT')" )
    public ResponseEntity getRepresentatives ( @PathVariable final String username ) {
        final User me = User.getByName( LoggerUtil.currentUser() );
        if ( me.getRole() == Role.ROLE_PATIENT && !me.getUsername().equals( username ) ) {
            return new ResponseEntity( errorResponse( "Can only access your own representatives." ),
                    HttpStatus.FORBIDDEN );
        }
        final User self = User.getByName( username );
        if ( self == null ) {
            return new ResponseEntity( errorResponse( "Could not find a user entry for " + username ),
                    HttpStatus.NOT_FOUND );
        }
        final Patient patient = Patient.getPatient( self );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for " + username ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            final Patient x = patient;
            for ( final Patient p : x.getRepresentatives() ) {
                p.setRepresentatives( null );
                p.setRepresented( null );
            }
            return new ResponseEntity( x.getRepresentatives(), HttpStatus.OK );
        }
    }

    /**
     * Returns all patients represented by a given patient.
     *
     * @param username
     *            The patient's username
     * @return The patient objects for all the users representatives.
     */
    @GetMapping ( BASE_PATH + "/patient/representing/{username}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_PATIENT')" )
    public ResponseEntity getRepresenting ( @PathVariable final String username ) {
        final User me = User.getByName( LoggerUtil.currentUser() );
        if ( me.getRole() == Role.ROLE_PATIENT && !me.getUsername().equals( username ) ) {
            return new ResponseEntity( errorResponse( "Can only access your own representatives." ),
                    HttpStatus.FORBIDDEN );
        }
        final User self = User.getByName( username );
        if ( self == null ) {
            return new ResponseEntity( errorResponse( "Could not find a user entry for " + username ),
                    HttpStatus.NOT_FOUND );
        }
        final Patient patient = Patient.getPatient( self );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for " + username ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            final Patient x = patient;
            for ( final Patient p : x.getRepresented() ) {
                p.setRepresentatives( null );
                p.setRepresented( null );
            }
            return new ResponseEntity( x.getRepresented(), HttpStatus.OK );
        }
    }

    /**
     * Returns all patients represented by a given patient.
     *
     * @param patient
     *            The patient's username
     * @param representative
     *            The representatives username
     * @return The patient objects for all the users representatives.
     */
    @GetMapping ( BASE_PATH + "/patient/representatives/{patient}/{representative}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_PATIENT')" )
    public ResponseEntity addRepresentative ( @PathVariable final String patient,
            @PathVariable final String representative ) {
        final User me = User.getByName( LoggerUtil.currentUser() );
        if ( me.getRole() == Role.ROLE_PATIENT && !me.getUsername().equals( patient ) ) {
            return new ResponseEntity( errorResponse( "Cannot add representatives to other patients." ),
                    HttpStatus.FORBIDDEN );
        }
        if ( patient.equals( representative ) ) {
            return new ResponseEntity( errorResponse( "Cannot add yourself as a representative" ),
                    HttpStatus.BAD_REQUEST );
        }
        final User patUser = User.getByName( patient );
        if ( patUser == null ) {
            return new ResponseEntity( errorResponse( "Could not find a user entry for " + patient ),
                    HttpStatus.NOT_FOUND );
        }
        final Patient patPat = Patient.getPatient( patUser );
        if ( patPat == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for " + patient ),
                    HttpStatus.NOT_FOUND );
        }
        final User repUser = User.getByName( representative );
        if ( repUser == null ) {
            return new ResponseEntity( errorResponse( "Could not find a user entry for " + representative ),
                    HttpStatus.NOT_FOUND );
        }
        final Patient repPat = Patient.getPatient( repUser );
        if ( repPat == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for " + representative ),
                    HttpStatus.NOT_FOUND );
        }
        for ( final Patient pat : patPat.getRepresentatives() ) {
            if ( pat.getSelf().getUsername().equals( representative ) ) {
                return new ResponseEntity( errorResponse( representative + " is already a representative" ),
                        HttpStatus.BAD_REQUEST );
            }
        }

        patPat.addRepresentative( repPat );
        patPat.save();
        repPat.save();
        if ( me.getRole().equals( Role.ROLE_HCP ) ) {
            LoggerUtil.log( TransactionType.DECLARE_PR, me.getUsername(), repUser.getUsername(),
                    "HCP " + me.getUsername() + " has declared " + repUser.getUsername() + " as a representative for "
                            + patUser.getUsername() );
        }
        else {
            LoggerUtil.log( TransactionType.HCP_DECLARE_PR, patUser.getUsername(), repUser.getUsername(), "User "
                    + repUser.getUsername() + " has been added as a representative for user " + patUser.getUsername() );
        }
        final Patient x = patPat;
        x.setRepresentatives( null );
        x.setRepresented( null );
        return new ResponseEntity( x, HttpStatus.OK );

    }

    /**
     * Returns all patients represented by a given patient.
     *
     * @param patient
     *            The patient's username
     * @param representative
     *            The representatives username
     * @return The patient objects for all the users representatives.
     */
    @GetMapping ( BASE_PATH + "/patient/representatives/remove/{patient}/{representative}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_PATIENT')" )
    public ResponseEntity removeRepresentative ( @PathVariable final String patient,
            @PathVariable final String representative ) {
        final User me = User.getByName( LoggerUtil.currentUser() );
        final boolean isInvolved = ( me.getUsername().equals( patient ) || me.getUsername().equals( representative ) );
        if ( me.getRole() == Role.ROLE_PATIENT && !isInvolved ) {
            return new ResponseEntity( errorResponse( "Cannot remove representatives for other patients." ),
                    HttpStatus.FORBIDDEN );
        }
        final User patUser = User.getByName( patient );
        if ( patUser == null ) {
            return new ResponseEntity( errorResponse( "Could not find a user entry for " + patient ),
                    HttpStatus.NOT_FOUND );
        }
        final Patient patPat = Patient.getPatient( patUser );
        if ( patPat == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for " + patient ),
                    HttpStatus.NOT_FOUND );
        }
        final User repUser = User.getByName( representative );
        if ( repUser == null ) {
            return new ResponseEntity( errorResponse( "Could not find a user entry for " + representative ),
                    HttpStatus.NOT_FOUND );
        }
        final Patient repPat = Patient.getPatient( repUser );
        if ( repPat == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for " + representative ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            for ( final Patient xPat : patPat.getRepresentatives() ) {
                if ( xPat.getSelf().getUsername().equals( representative ) ) {
                    try {
                        patPat.removeRepresentative( xPat );
                    }
                    catch ( final IllegalArgumentException e ) {
                        return new ResponseEntity( errorResponse( "Relationship does not exist." ),
                                HttpStatus.NOT_FOUND );
                    }
                    patPat.save();
                    xPat.save();
                    if ( me.getUsername().equals( patient ) ) {
                        LoggerUtil.log( TransactionType.REMOVE_PR, patUser.getUsername(), repUser.getUsername(), "User "
                                + patUser.getUsername() + " has undeclared representative " + repUser.getUsername() );
                    }
                    else {
                        LoggerUtil.log( TransactionType.REMOVE_SELF_AS_PR, patUser.getUsername(), repUser.getUsername(),
                                "User " + repUser.getUsername() + " has undeclared self as representative for "
                                        + patUser.getUsername() );
                    }
                    final Patient x = patPat;
                    x.setRepresentatives( null );
                    x.setRepresented( null );
                    return new ResponseEntity( x, HttpStatus.OK );
                }
            }

        }
        return new ResponseEntity( errorResponse( "Relationship does not exist." ), HttpStatus.NOT_FOUND );
    }
}
