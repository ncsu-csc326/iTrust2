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

import edu.ncsu.csc.itrust2.forms.admin.HospitalForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the Hospital model. In all
 * requests made to this controller, the {id} provided is a String that is the
 * name of the hospital desired.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIHospitalController extends APIController {

    /**
     * Retrieves a list of all Hospitals in the database
     *
     * @return list of hospitals
     */
    @GetMapping ( BASE_PATH + "/hospitals" )
    public List<Hospital> getHospitals () {
        return Hospital.getHospitals();
    }

    /**
     * Retrieves the Hospital specified by the name provided
     *
     * @param id
     *            The name of the hospital
     * @return response
     */
    @GetMapping ( BASE_PATH + "/hospitals/{id}" )
    public ResponseEntity getHospital ( @PathVariable ( "id" ) final String id ) {
        final Hospital hospital = Hospital.getByName( id );
        if ( null != hospital ) {
            LoggerUtil.log( TransactionType.VIEW_HOSPITAL, LoggerUtil.currentUser() );
        }
        return null == hospital
                ? new ResponseEntity( errorResponse( "No hospital found for name " + id ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( hospital, HttpStatus.OK );
    }

    /**
     * Creates a new Hospital from the RequestBody provided.
     *
     * @param hospitalF
     *            The Hospital to be validated and saved to the database.
     * @return response
     */
    @PostMapping ( BASE_PATH + "/hospitals" )
    public ResponseEntity createHospital ( @RequestBody final HospitalForm hospitalF ) {
        System.err.println( "HOSPITALS" );
        final Hospital hospital = new Hospital( hospitalF );
        if ( null != Hospital.getByName( hospital.getName() ) ) {
            return new ResponseEntity(
                    errorResponse( "Hospital with the name " + hospital.getName() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        try {
            hospital.save();
            LoggerUtil.log( TransactionType.CREATE_HOSPITAL, LoggerUtil.currentUser() );
            return new ResponseEntity( hospital, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Error occured while validating or saving " + hospital.toString()
                    + " because of " + e.getMessage() ), HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Updates the hospital with the name provided by overwriting it with the
     * new Hospital provided.
     *
     * @param id
     *            Name of the hospital to update
     * @param hospitalF
     *            The new hospital to save to this name
     * @return response
     */
    @PutMapping ( BASE_PATH + "/hospitals/{id}" )
    public ResponseEntity updateHospital ( @PathVariable final String id, @RequestBody final HospitalForm hospitalF ) {
        final Hospital hospital = new Hospital( hospitalF );
        final Hospital dbHospital = Hospital.getByName( id );
        if ( null == dbHospital ) {
            return new ResponseEntity( errorResponse( "No hospital found for name " + id ), HttpStatus.NOT_FOUND );
        }
        try {
            hospital.save(); /* Will overwrite existing request */
            if ( !hospital.getName().equals( id ) ) {
                // If we are editing the name, we have to delete the old record,
                // because name is used as the primary key in hibernate.
                dbHospital.delete();
            }
            LoggerUtil.log( TransactionType.EDIT_HOSPITAL, LoggerUtil.currentUser() );
            return new ResponseEntity( hospital, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not update " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
