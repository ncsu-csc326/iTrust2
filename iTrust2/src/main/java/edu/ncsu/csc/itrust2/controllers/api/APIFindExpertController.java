package edu.ncsu.csc.itrust2.controllers.api;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.forms.findexperts.ExpertTable;
import edu.ncsu.csc.itrust2.forms.findexperts.ExpertTableEntry;
import edu.ncsu.csc.itrust2.forms.findexperts.FindExpertForm;
import edu.ncsu.csc.itrust2.managers.ZipCodeManager;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.Specialty;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.models.persistent.ZipCodeEntry;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class to provide REST API endpoints to the Find Expert Form
 *
 * @author nrshah4
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIFindExpertController extends APIController {

    /**
     * update the Find Expert table and return it.
     *
     * @param form
     *            the form with the user-entered values
     * @return ResponseEntity with error response or table.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_PATIENT', 'ROLE_VIROLOGIST')" )
    @PostMapping ( BASE_PATH + "/findexperts" )
    public ResponseEntity updateTable ( @RequestBody final FindExpertForm form ) {
        // get user role
        final User user = User.getByName( LoggerUtil.currentUser() );
        final Enum role = user.getRole();

        // get ZipCodeManager
        final ZipCodeManager manager = ZipCodeManager.getInstance();

        // if database not yet loaded return
        if ( !manager.checkDatabase() ) {

            return new ResponseEntity( errorResponse( "Database is empty." ), HttpStatus.BAD_REQUEST );
        }

        // Adapted pattern code from https://bit.ly/2SNm8z0
        final String regex = "^[0-9]{5}(?:-[0-9]{4})?$";

        final Pattern pattern = Pattern.compile( regex );
        final String formGetZip = form.getZip();

        final Matcher matcher = pattern.matcher( formGetZip );
        if ( !matcher.matches() ) {
            LoggerUtil.log( TransactionType.FIND_EXPERTS_ZIP_INVALID, LoggerUtil.currentUser() );
            return new ResponseEntity( errorResponse( "Invalid Zip Code." ), HttpStatus.NOT_FOUND );

        }

        // get entry (zip,lat,long) for the given zip
        ZipCodeEntry zip = ZipCodeEntry.getByZip( formGetZip );

        if ( zip == null ) {
            zip = ZipCodeEntry.getByZip( formGetZip.substring( 0, 5 ) );

            if ( zip == null ) {
                LoggerUtil.log( TransactionType.FIND_EXPERTS_ZIP_ERROR, LoggerUtil.currentUser() );
                return new ResponseEntity( errorResponse( "Zip code not found." ), HttpStatus.NOT_FOUND );
            }
        }

        // max distance should be size of radius
        final double maxDistance = form.getRadius();

        // get Specialty
        final Specialty specialty = Specialty.parse( form.getSpecialty() );

        // get all hospitals
        final List<Hospital> allHospitals = Hospital.getHospitals();

        if ( allHospitals == null ) {
            return new ResponseEntity( errorResponse( "No Hospitals found." ), HttpStatus.NOT_FOUND );

        }

        // new empty table
        final ExpertTable table = new ExpertTable();

        // add entries to table
        for ( int i = 0; i < allHospitals.size(); i++ ) {
            final Hospital h = allHospitals.get( i );
            final String hospitalZip = h.getZip();
            if ( hospitalZip == null || hospitalZip.equals( "" ) ) {
                continue;
            }
            ZipCodeEntry hZip = ZipCodeEntry.getByZip( hospitalZip );
            if ( hZip == null ) {
                hZip = ZipCodeEntry.getByZip( hospitalZip.substring( 0, 5 ) );
                if ( hZip == null ) {
                    continue;
                }
            }
            final double dist = ZipCodeEntry.calculateDistance( zip, hZip );
            if ( dist <= maxDistance ) {
                final List<Criterion> criterionList = new ArrayList<Criterion>();
                criterionList.add( Restrictions.eq( "hospitalId", h.getName() ) );
                criterionList.add( Restrictions.eq( "specialty", specialty ) );
                final List<Personnel> personnel = Personnel.getWhere( criterionList );
                if ( personnel == null || personnel.size() == 0 ) {
                    continue;
                }
                final ExpertTableEntry entry = new ExpertTableEntry( h, personnel, dist );
                table.addEntry( entry );
            }

        }

        // sort table by distances
        final List<ExpertTableEntry> sortedTable = table.sortTable();

        if ( role == Role.ROLE_HCP ) {
            LoggerUtil.log( TransactionType.FIND_EXPERTS_HCP, LoggerUtil.currentUser() );
        }
        else {
            LoggerUtil.log( TransactionType.FIND_EXPERTS_PATIENT, LoggerUtil.currentUser() );
        }

        if ( sortedTable.size() == 0 ) {
            return new ResponseEntity( errorResponse( "No experts found in your area." ), HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( sortedTable, HttpStatus.OK );

    }

}
