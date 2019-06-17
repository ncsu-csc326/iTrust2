package edu.ncsu.csc.itrust2.controllers.api.officevisit;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.itrust2.controllers.api.APIController;
import edu.ncsu.csc.itrust2.models.enums.AppointmentType;
import edu.ncsu.csc.itrust2.models.enums.Role;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.DomainObject;
import edu.ncsu.csc.itrust2.models.persistent.GeneralCheckup;
import edu.ncsu.csc.itrust2.models.persistent.GeneralOphthalmology;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.OphthalmologySurgery;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that provides REST API endpoints for the OfficeVisit model. In all
 * requests made to this controller, the {id} provided is a Long that is the
 * primary key id of the office visit requested.
 *
 * @author Kai Presler-Marshall
 * @author Jack MacDonald
 */
@RestController
public class APIOfficeVisitController extends APIController {

    /**
     * Retrieves a list of all OfficeVisits in the database
     *
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/officevisits" )
    @PreAuthorize ( "hasRole('ROLE_HCP') or hasRole('ROLE_OD') or hasRole('ROLE_OPH')" )
    public List<OfficeVisit> getOfficeVisits () {
        // append all other office visits here.
        return OfficeVisit.getOfficeVisits();
    }

    /**
     * Retrieves all of the office visits for the current HCP.
     *
     * @return all of the office visits for the current HCP.
     */
    @GetMapping ( BASE_PATH + "/officevisits/HCP" )
    public List<OfficeVisit> getOfficeVisitsForHCP () {
        final User self = User.getByName( LoggerUtil.currentUser() );
        final List<OfficeVisit> visits = OfficeVisit.getForType( AppointmentType.GENERAL_CHECKUP );
        if ( self.getRole() == Role.ROLE_OPH ) {
            visits.addAll( OfficeVisit.getForType( AppointmentType.GENERAL_OPHTHALMOLOGY ) );
            visits.addAll( OfficeVisit.getForType( AppointmentType.OPHTHALMOLOGY_SURGERY ) );
        }
        else if ( self.getRole() == Role.ROLE_OD ) {
            visits.addAll( OfficeVisit.getForType( AppointmentType.GENERAL_OPHTHALMOLOGY ) );
        }
        return visits;
    }

    /**
     * Retrieves a list of all OfficeVisits in the database
     *
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/officevisits/myofficevisits" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public List<OfficeVisit> getMyOfficeVisits () {
        final User self = User.getByName( LoggerUtil.currentUser() );
        LoggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, self );
        return OfficeVisit.getForPatient( self.getId() );
    }

    /**
     * Deletes all OfficeVisits in the system. This cannot be reversed; exercise
     * caution before calling it
     */
    @DeleteMapping ( BASE_PATH + "/officevisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH')" )
    public void deleteOfficeVisits () {
        LoggerUtil.log( TransactionType.DELETE_ALL_OFFICE_VISITS, LoggerUtil.currentUser() );
        GeneralCheckup.deleteAll();
        DomainObject.deleteAll( OphthalmologySurgery.class );
        DomainObject.deleteAll( GeneralOphthalmology.class );
    }

}
