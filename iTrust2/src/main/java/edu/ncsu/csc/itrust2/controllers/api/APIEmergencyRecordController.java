package edu.ncsu.csc.itrust2.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.personnel.EmergencyRecordForm;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the EmergencyRecordForm. Allows
 * for the fetching of a specific patients EmergencyRecordForm and for searching
 * for patients that contain a user string in their first name, last name, or
 * username.
 *
 * @author Alexander Phelps
 *
 */
@RestController
public class APIEmergencyRecordController extends APIController {
    /**
     * Returns the EmergencyRecordForm for a patient.
     *
     * @param username
     *            The username of the patient that an EmergencyRecordForm should
     *            be generated for.
     * @return emergRec The EmergencyRecordForm for the patient requested.
     */
    @SuppressWarnings ( { "unchecked", "rawtypes" } )
    @PreAuthorize ( "hasRole('ROLE_ER') or hasRole('ROLE_HCP')" )
    @GetMapping ( BASE_PATH + "/emergencyrecord/{patientName}" )
    public ResponseEntity getEmergencyRecord ( @PathVariable ( "patientName" ) final String username ) {
        final User expectedPatient = User.getByName( username );
        final boolean isHCP = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains( new SimpleGrantedAuthority( "ROLE_HCP" ) );
        
        TransactionType logCode = isHCP ? TransactionType.HCP_VIEW_ER : TransactionType.ER_VIEW_ER;
        
        if ( expectedPatient != null && expectedPatient.getRole().equals( Role.ROLE_PATIENT ) ) {
            final EmergencyRecordForm emergRec = new EmergencyRecordForm( expectedPatient.getUsername() );
            LoggerUtil.log( logCode, LoggerUtil.currentUser(), expectedPatient.getUsername(),
                    "Fetched Emergency Record for user " + expectedPatient.getUsername() );
            return new ResponseEntity( emergRec, HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for " + username ),
                    HttpStatus.NOT_FOUND );
        }
    }

    /**
     * Returns the EmergencyRecordForm for a patient.
     *
     * @param username
     *            The username of the patient that an EmergencyRecordForm should
     *            be generated for.
     * @return emergRec The EmergencyRecordForm for the patient requested.
     */
    @SuppressWarnings ( { "unchecked", "rawtypes" } )
    @GetMapping ( BASE_PATH + "/emergencyrecord/hcp/{patientName}" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public ResponseEntity getEmergencyRecordHCP ( @PathVariable ( "patientName" ) final String username ) {
        final User expectedPatient = User.getByName( username );
        
        if ( expectedPatient != null && expectedPatient.getRole().equals( Role.ROLE_PATIENT ) ) {
            final EmergencyRecordForm emergRec = new EmergencyRecordForm( expectedPatient.getUsername() );
            LoggerUtil.log( TransactionType.HCP_VIEW_ER, LoggerUtil.currentUser(), expectedPatient.getUsername(),
                    "Fetched Emergency Record for user " + expectedPatient.getUsername() );
            return new ResponseEntity( emergRec, HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Could not find a patient entry for " + username ),
                    HttpStatus.NOT_FOUND );
        }
    }
}
