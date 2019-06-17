package edu.ncsu.csc.itrust2.controllers.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.patient.FoodDiaryEntryForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.FoodDiaryEntry;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the DiaryEntry model. Uses a
 * patient's username to create and retrieve entries.
 *
 * @author Brendan Boss (blboss)
 * @author Matt Dzwonczyk (mgdzwonc)
 */
@SuppressWarnings ( { "rawtypes", "unchecked" } )
@RestController
public class APIFoodDiaryController extends APIController {

    /**
     * Creates a new DiaryEntry object and saves it to the DB
     *
     * @param entry
     *            the form being used to create a DiaryEntry object
     * @return a response containing results of creating a new entry
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @PostMapping ( BASE_PATH + "/diary" )
    public ResponseEntity addEntry ( @RequestBody final FoodDiaryEntryForm entry ) {
        try {
            final FoodDiaryEntry dEntry = new FoodDiaryEntry( entry );
            dEntry.setPatient( LoggerUtil.currentUser() );
            dEntry.save();

            LoggerUtil.log( TransactionType.CREATE_FOOD_DIARY_ENTRY, LoggerUtil.currentUser() );
            return new ResponseEntity( dEntry, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not create Diary Entry provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Retrieves a list of patient DiaryEntries, either for the current patient
     * if the user has role PATIENT, or all of the patients if the user is an
     * HCP.
     *
     * @return a list of patient's food diary entries
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "diary" )
    public ResponseEntity getEntriesPatient () {
        LoggerUtil.log( TransactionType.PATIENT_VIEW_FOOD_DIARY_ENTRY, LoggerUtil.currentUser() );
        return new ResponseEntity( FoodDiaryEntry.getByPatient( LoggerUtil.currentUser() ), HttpStatus.OK );
    }

    /**
     * Retrieves a list of patient DiaryEntries for the specified patient.
     *
     * @param patient
     *            The username of the patient for which to get entries
     * @return a list of patient's food diary entries
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH')" )
    @GetMapping ( BASE_PATH + "diary/{patient}" )
    public ResponseEntity getEntriesHCP ( @PathVariable final String patient ) {
        if ( null == Patient.getByName( patient ) ) {
            return new ResponseEntity( errorResponse( "No patients found with username " + patient ),
                    HttpStatus.NOT_FOUND );
        }
        LoggerUtil.log( TransactionType.HCP_VIEW_FOOD_DIARY_ENTRY, User.getByName( LoggerUtil.currentUser() ),
                User.getByName( patient ) );
        return new ResponseEntity( FoodDiaryEntry.getByPatient( patient ), HttpStatus.OK );
    }

}
