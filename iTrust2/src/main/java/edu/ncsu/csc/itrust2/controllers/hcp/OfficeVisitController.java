package edu.ncsu.csc.itrust2.controllers.hcp;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * Controller that enables a HCP to document an office visit into the system
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class OfficeVisitController {

    /**
     * Returns the form page for a HCP to document an OfficeVisit
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/hcp/documentOfficeVisit" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH')" )
    public String documentOfficeVisit ( final Model model ) {
        return "/hcp/documentOfficeVisit";
    }

    /**
     * Returns the form page for a HCP to edit an OfficeVisit
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/hcp/editOfficeVisit" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH')" )
    public String getAllOfficeVisits ( final Model model ) {
        return "/hcp/editOfficeVisit";
    }

    /**
     * Returns the form page for a HCP to view patient Office Visits
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/hcp/viewPatientOfficeVisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_OD', 'ROLE_OPH')" )
    public String getPatientsOfficeVisits ( final Model model ) {
        return "/hcp/viewPatientOfficeVisits";
    }
}
