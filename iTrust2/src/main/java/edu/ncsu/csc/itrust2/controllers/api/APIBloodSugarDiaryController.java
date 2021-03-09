package edu.ncsu.csc.itrust2.controllers.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.patient.BloodSugarDiaryForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.BloodSugarDiaryEntry;
import edu.ncsu.csc.itrust2.models.persistent.BloodSugarLimit;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the BloodSugarDiary model. Uses a
 * patient's username to create and retrieve entries.
 *
 * @author Thomas Landsberg
 * @author Eric Starner
 *
 */
@SuppressWarnings ( { "rawtypes", "unchecked" } )
@RestController
public class APIBloodSugarDiaryController extends APIController {

    /**
     * Creates a new DiaryEntry object and saves it to the DB
     *
     * @param entry
     *            the form being used to create a DiaryEntry object
     * @return a response containing results of creating a new entry
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @PostMapping ( BASE_PATH + "bloodSugarDiaries/patient" )
    public ResponseEntity addEntry ( @RequestBody final BloodSugarDiaryForm entry ) {
        String date;
        if ( entry.getDate() != null ) {
            date = entry.getDate();
        }
        else {
            date = LocalDate.now().toString();
        }

        try {
            final Patient patient = Patient.getByName( LoggerUtil.currentUser() );
            final BloodSugarDiaryEntry currentEntry = BloodSugarDiaryEntry.getByDateAndPatient( date, patient );
            if ( currentEntry == null ) {
                final BloodSugarDiaryEntry dEntry = new BloodSugarDiaryEntry( entry );
                dEntry.setPatient( patient );
                dEntry.save();

                LoggerUtil.log( TransactionType.CREATE_BLOOD_SUGAR_DIARY, LoggerUtil.currentUser() );
                return new ResponseEntity( dEntry, HttpStatus.OK );
            }
            else {
                final BloodSugarDiaryEntry update = new BloodSugarDiaryEntry( entry );
                update.setPatient( currentEntry.getPatient() );
                update.setId( currentEntry.getId() );
                update.save();
                LoggerUtil.log( TransactionType.EDIT_BLOOD_SUGAR_DIARY, LoggerUtil.currentUser() );

                return new ResponseEntity( update, HttpStatus.OK );
            }
        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not create Blood Sugar Diary Entry provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Creates multiple new Blood Sugar Diary Entry objects and adds them to the
     * data base.
     *
     * @param info
     *            string representation of the csv file that is parsed for
     *            information on the diary entries
     * @return a response that is OK if all the entries were added, BAD REQUEST
     *         if the csv file has incorrect format.
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @PostMapping ( BASE_PATH + "bloodSugarDiaries/patient/csv" )
    public ResponseEntity addEntries ( @RequestBody final String info ) {
        final String copy = info.replace( "\"", "" );
        final String lineSplit = "\n";
        final String split = "[,]";
        final String[] lines = copy.split( lineSplit );
        final String header = lines[0];
        final String[] headerCheck = header.split( split );

        if ( headerCheck.length != 5 ) { // Check length of header
            return new ResponseEntity( errorResponse( "CSV file header is incorrect." ), HttpStatus.BAD_REQUEST );
        }
        else if ( !headerCheck[0].trim().equals( "date" ) || !headerCheck[1].trim().equals( "fasting" )
                || !headerCheck[2].trim().equals( "meal_1" ) // Check to make
                                                             // sure the headers
                                                             // are named
                                                             // correctly
                || !headerCheck[3].trim().equals( "meal_2" ) || !headerCheck[4].trim().equals( "meal_3" ) ) {
            return new ResponseEntity( errorResponse( "CSV file header is incorrect." ), HttpStatus.BAD_REQUEST );
        }

        final List<BloodSugarDiaryEntry> list = new ArrayList<BloodSugarDiaryEntry>();
        String[] current = null;
        // Now to parse the string
        for ( int i = 1; i < lines.length; i++ ) { // Parse through each line
            current = lines[i].split( split );
            if ( current.length != 5 ) {
                return new ResponseEntity( errorResponse( "Incorrect csv file format" ), HttpStatus.BAD_REQUEST );
            }
            try { // Try to parse each int and the date and add the entry to the
                  // list. If one fails, we dont make any
                final BloodSugarDiaryEntry temp = new BloodSugarDiaryEntry();
                temp.setPatient( Patient.getByName( LoggerUtil.currentUser() ) ); // always
                                                                                  // set
                                                                                  // the
                // patient
                temp.setDate( LocalDate.parse( current[0].trim() ) );
                if ( current[1].trim().length() != 0 ) { // Check for no value
                                                         // specified
                    temp.setFastingLevel( Integer.parseInt( current[1].trim() ) );
                }
                if ( current[2].trim().length() != 0 ) { // Check for no value
                                                         // specified
                    temp.setFirstLevel( Integer.parseInt( current[2].trim() ) );
                }
                if ( current[3].trim().length() != 0 ) { // Check for no value
                                                         // specified
                    temp.setSecondLevel( Integer.parseInt( current[3].trim() ) );
                }
                if ( current[4].trim().length() != 0 ) { // Check for no value
                                                         // specified
                    temp.setThirdLevel( Integer.parseInt( current[4].trim() ) );
                }
                list.add( temp ); // Add it to the list to save later
            }
            catch ( final Exception e ) {
                return new ResponseEntity( errorResponse( "Incorrect csv file format" ), HttpStatus.BAD_REQUEST );
            }
        }

        final Patient patient = Patient.getByName( LoggerUtil.currentUser() );

        for ( int i = 0; i < list.size(); i++ ) { // Save each of the entries in
                                                  // the list now that we know
                                                  // it is a valid file.
            final BloodSugarDiaryEntry temp = BloodSugarDiaryEntry
                    .getByDateAndPatient( list.get( i ).getDate().toString(), patient );
            if ( temp == null ) { // No date found so make a new one
                list.get( i ).save();
                LoggerUtil.log( TransactionType.CREATE_BLOOD_SUGAR_DIARY, LoggerUtil.currentUser() );
            }
            else { // An entry with the given date exists so just edit it.
                temp.copyFrom( list.get( i ), false );
                temp.save();
                LoggerUtil.log( TransactionType.EDIT_BLOOD_SUGAR_DIARY, LoggerUtil.currentUser() );
            }

        }
        return new ResponseEntity( HttpStatus.OK );
    }

    /**
     * Retrieves a list of patient Blood Sugar Diary entries for the specified
     * patient.
     *
     * @param patientName
     *            The username of the patient for which to get entries
     * @return a list of patient's blood sugar diary entries
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_VIROLOGIST')" )
    @GetMapping ( BASE_PATH + "bloodSugarDiaries/hcp/{patient}" )
    public ResponseEntity getEntriesHCP ( @PathVariable ( "patient" ) final String patientName ) {
        final Patient patient = Patient.getByName( patientName );
        if ( patient == null ) {
            return new ResponseEntity( errorResponse( "No patients found with username " + patientName ),
                    HttpStatus.NOT_FOUND );
        }
        LoggerUtil.log( TransactionType.VIEW_BLOOD_SUGAR_DIARY_HCP, User.getByName( LoggerUtil.currentUser() ),
                User.getByName( patientName ) );
        return new ResponseEntity( BloodSugarDiaryEntry.getByPatient( patient ), HttpStatus.OK );
    }

    /**
     * Retrieves a list of patient blood sugar diary for the current patient if
     * the user has role PATIENT
     *
     * @return a list of patient's blood sugar diary entries
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "bloodSugarDiaries/patient" )
    public ResponseEntity getEntriesPatient () {
        final Patient patient = Patient.getByName( LoggerUtil.currentUser() );
        LoggerUtil.log( TransactionType.VIEW_BLOOD_SUGAR_DIARY_PATIENT, LoggerUtil.currentUser() );
        return new ResponseEntity( BloodSugarDiaryEntry.getByPatient( patient ), HttpStatus.OK );
    }

    /**
     * Retrieves a blood sugar diary for the current day of the patient if the
     * user has role PATIENT
     *
     * @return patient's blood sugar diary entry
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "bloodSugarDiaries/patient/currentDay" )
    public ResponseEntity getPatientEntry () {
        final Patient patient = Patient.getByName( LoggerUtil.currentUser() );
        final BloodSugarDiaryEntry temp = BloodSugarDiaryEntry.getByDateAndPatient( LocalDate.now().toString(),
                patient );
        if ( temp != null ) {
            LoggerUtil.log( TransactionType.VIEW_BLOOD_SUGAR_DIARY_PATIENT, LoggerUtil.currentUser() );
            return new ResponseEntity( temp, HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Could not find a Blood Sugar Diary Entry for the current date" ),
                    HttpStatus.NOT_FOUND );
        }
    }

    /**
     * Retrieves the patients blood sugar limits imposed by the HCP if the user
     * has role PATIENT
     *
     * @return patient's blood sugar limits
     */
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    @GetMapping ( BASE_PATH + "bloodSugarDiaries/patient/limits" )
    public ResponseEntity getPatientBloodSugarLimits () {
        final Patient current = Patient.getByName( LoggerUtil.currentUser() );
        final BloodSugarLimit patientLimits = BloodSugarLimit.getByPatient( current );

        if ( current != null && patientLimits != null ) {
            final Integer limits[] = { patientLimits.getFastingLimit(), patientLimits.getMealLimit() };
            LoggerUtil.log( TransactionType.VIEW_BLOOD_SUGAR_LIMITS_PATIENT, LoggerUtil.currentUser() );
            return new ResponseEntity( limits, HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Could not find blood sugar limits for current patient" ),
                    HttpStatus.NOT_FOUND );
        }
    }

    /**
     * Retrieves the patients blood sugar limits if the user has role HCP
     *
     * @param patient
     *            The username of the patient for which to get entries
     * @return patients blood sugar limits
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_VIROLOGIST')" )
    @GetMapping ( BASE_PATH + "bloodSugarDiaries/hcp/limits/{patient}" )
    public ResponseEntity getBloodSugarLimitsHCP ( @PathVariable final String patient ) {
        final Patient current = Patient.getByName( patient );
        final BloodSugarLimit patientLimits = BloodSugarLimit.getByPatient( current );

        if ( current != null && patientLimits != null ) {
            final Integer limits[] = { patientLimits.getFastingLimit(), patientLimits.getMealLimit() };
            LoggerUtil.log( TransactionType.VIEW_BLOOD_SUGAR_LIMITS_HCP, LoggerUtil.currentUser() );
            return new ResponseEntity( limits, HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Could not find blood sugar limits for current patient" ),
                    HttpStatus.NOT_FOUND );
        }
    }

    /**
     * Updates the blood sugar limits for a patient
     *
     * @param patient
     *            the patient the limits are for
     *
     * @param limits
     *            the limits to be set
     * @return a response containing results of updating the limits
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH', 'ROLE_VIROLOGIST')" )
    @PostMapping ( BASE_PATH + "bloodSugarDiaries/hcp/limits/{patient}" )
    public ResponseEntity addLimits ( @PathVariable final String patient, @RequestBody final List<Integer> limits ) {
        final Patient current = Patient.getByName( patient );
        BloodSugarLimit currentLimits = BloodSugarLimit.getByPatient( current );

        if ( current != null ) {

            if ( currentLimits == null ) {
                currentLimits = new BloodSugarLimit( current );
            }

            currentLimits.setFastingLimit( limits.get( 0 ) );
            currentLimits.setMealLimit( limits.get( 1 ) );
            currentLimits.save();

            LoggerUtil.log( TransactionType.ADD_BLOOD_SUGAR_LIMITS, LoggerUtil.currentUser() );
            return new ResponseEntity( successResponse( "Limits successfully updated." ), HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Could not update limits." ), HttpStatus.BAD_REQUEST );
        }

    }
}
