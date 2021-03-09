package edu.ncsu.csc.itrust2.controllers.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.analytics.PassengerStatistics;
import edu.ncsu.csc.itrust2.forms.passenger.PassengerForm;
import edu.ncsu.csc.itrust2.forms.virologist.ContactsByDepthForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Passenger;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * Passenger model.
 *
 * @author caproven, nrshah4
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPassengerController extends APIController {

    /**
     * Retrieves a grouping of statistical data for the Passengers stored in the
     * system
     *
     * @return response
     */
    @GetMapping ( BASE_PATH + "/diseasecontrol/statistics" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public ResponseEntity getStatistics () {
        // First check if there is any passenger data in the system
        if ( Passenger.getPassengers().size() == 0 ) {
            return new ResponseEntity( errorResponse( "No passengers in database." ), HttpStatus.BAD_REQUEST );
        }
        final PassengerStatistics stats = new PassengerStatistics();
        LoggerUtil.log( TransactionType.VIROLOGIST_VIEW_PASSENGER_STATS, LoggerUtil.currentUser()  );
        return new ResponseEntity( stats, HttpStatus.OK );
    }

    /**
     * Returns only rnaught for the Passengers stored in the system
     *
     * @return response entity
     */
    @GetMapping ( BASE_PATH + "/diseasecontrol/rnaught" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public ResponseEntity viewRNaught () {
        if ( Passenger.getPassengers().size() == 0 ) {
            return new ResponseEntity( errorResponse( "No passengers in database." ), HttpStatus.BAD_REQUEST );
        }
        final double rnaught = PassengerStatistics.calculateRNaught();
        LoggerUtil.log( TransactionType.VIROLOGIST_VIEW_RNAUGHT, User.getByName( LoggerUtil.currentUser() ) );
        return new ResponseEntity( rnaught, HttpStatus.OK );
    }

    /**
     * Creates multiple new Passenger objects and adds them to the data base
     *
     * @param info
     *            string representation of the csv file that is parsed for
     *            information on the Passengers
     * @return a response that is OK if all the entries were added, BAD REQUEST
     *         if the csv file has incorrect format.
     */
    @PostMapping ( BASE_PATH + "/diseasecontrol/passengers/csv" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public ResponseEntity uploadPassengerData ( @RequestBody final String info ) {
        final int[] response = Passenger.parse( info );

        /* If there were 0 successful uploads, return an error response */
        if ( response[0] == 0 && response[1] > 0 ) {
            LoggerUtil.log( TransactionType.VIROLOGIST_UPLOAD_PASSENGERS_ERR,
                    User.getByName( LoggerUtil.currentUser() ) );
            return new ResponseEntity( errorResponse( "All passengers in this file are already in the system" ),
                    HttpStatus.BAD_REQUEST );
        }
        else if ( response[0] == 0 ) {
            LoggerUtil.log( TransactionType.VIROLOGIST_UPLOAD_PASSENGERS_ERR,
                    User.getByName( LoggerUtil.currentUser() ) );
            return new ResponseEntity( errorResponse( "No successful uploads, check file formatting and content" ),
                    HttpStatus.BAD_REQUEST );
        }
        /* Else just return the array */
        LoggerUtil.log( TransactionType.VIROLOGIST_UPLOAD_PASSENGERS, User.getByName( LoggerUtil.currentUser() ) );
        return new ResponseEntity( response, HttpStatus.OK );
    }

    /**
     * Updates Passenger objects by adding contact lists to the database
     *
     * @param info
     *            string representation of the csv file that is parsed for
     *            information on the Passenger contact lists
     * @return a response that is OK if all the entries were added, BAD REQUEST
     *         if the csv file has incorrect format.
     */
    @PostMapping ( BASE_PATH + "/diseasecontrol/contacts/csv" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public ResponseEntity uploadContacts ( @RequestBody final String info ) {
        if ( info == null || info.trim().equals( "" ) ) {
            return new ResponseEntity( errorResponse( "Empty or Invalid file" ), HttpStatus.BAD_REQUEST );
        }
        final boolean response = Passenger.parseContacts( info );
        if ( !response ) {
            return new ResponseEntity( errorResponse( "Could not upload contacts" ), HttpStatus.CONFLICT );
        }
        LoggerUtil.log( TransactionType.VIROLOGIST_UPLOAD_CONTACTS, User.getByName( LoggerUtil.currentUser() ) );
        return new ResponseEntity( response, HttpStatus.OK );
    }

    /**
     * Searches for the n-depth Passengers that a source Passenger has come in
     * contact with
     *
     * @param form
     *            Form containing all query parameters
     * @return response containing lists of Passengers for each depth of the
     *         search
     */
    @PostMapping ( BASE_PATH + "diseasecontrol/contacts" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public ResponseEntity getContactsByDepth ( @RequestBody final ContactsByDepthForm form ) {
        final String passengerId = form.getPassengerId();
        if ( passengerId.trim().equals( "" ) || Passenger.getById( passengerId ) == null ) {
            return new ResponseEntity( errorResponse( "Invalid passenger Id." ), HttpStatus.BAD_REQUEST );
        }
        final int depth = form.getDepth();
        if ( depth <= 0 || depth > Passenger.getPassengers().size() ) {
            return new ResponseEntity( errorResponse( "Invalid depth" ), HttpStatus.BAD_REQUEST );
        }

        final Map<Integer, List<PassengerForm>> contacts = Passenger.getContactsByDepth( passengerId, depth );
        LoggerUtil.log( TransactionType.VIROLOGIST_SEARCH_BY_DEPTH, User.getByName( LoggerUtil.currentUser() ) );
        return new ResponseEntity( contacts, HttpStatus.OK );
    }

    /**
     * Delete all passengers in the database.
     *
     * @return status
     */
    @DeleteMapping ( BASE_PATH + "diseasecontrol/passengers" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public ResponseEntity deletePassengers () {
        Passenger.deleteAll();
        LoggerUtil.log( TransactionType.VIROLOGIST_DELETE_ALL_PASSENGERS, User.getByName( LoggerUtil.currentUser() ) );
        return new ResponseEntity( HttpStatus.OK );
    }

    /**
     * Gets a map of passenger ids and names for UC29
     *
     * @return status
     */
    @GetMapping ( BASE_PATH + "diseasecontrol/passengers" )
    @PreAuthorize ( "hasRole('ROLE_VIROLOGIST')" )
    public ResponseEntity getPassengers () {
        final List<Passenger> passengers = Passenger.getPassengers();
        if ( passengers.size() == 0 ) {
            return new ResponseEntity( errorResponse( "No passengers in the database." ), HttpStatus.BAD_REQUEST );
        }
        final Map<String, String> idToName = new HashMap<String, String>();
        for ( final Passenger p : passengers ) {
            final String name = p.getFirstName() + " " + p.getLastName();
            idToName.put( p.getPassengerId(), name );
        }
        return new ResponseEntity( idToName, HttpStatus.OK );
    }

}
