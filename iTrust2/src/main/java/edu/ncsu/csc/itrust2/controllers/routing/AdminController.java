package edu.ncsu.csc.iTrust2.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Controller to manage basic abilities for Admin roles
 *
 * @author Kai Presler-Marshall
 *
 */

@Controller
public class AdminController {

    /**
     * Returns the admin for the given model
     *
     * @param model
     *            model to check
     * @return role
     */
    @RequestMapping ( value = "admin/index" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String index ( final Model model ) {
        return Role.ROLE_ADMIN.getLanding();
    }

    /**
     * Add or delete user
     *
     * @param model
     *            data for front end
     * @return mapping
     */
    @RequestMapping ( value = "admin/users" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String manageUser ( final Model model ) {
        return "/admin/users";
    }

    /**
     * Creates the form page for the Add Hospital page
     *
     * @param model
     *            Data for the front end
     * @return Page to show to the user
     */
    @RequestMapping ( value = "admin/hospitals" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String manageHospital ( final Model model ) {
        return "/admin/hospitals";
    }

    /**
     * Retrieves the form for the Drugs action
     *
     * @param model
     *            Data for front end
     * @return The page to display
     */
    @RequestMapping ( value = "admin/drugs" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String drugs ( final Model model ) {
        return "admin/drugs";
    }

    /**
     * Add code
     *
     * @param model
     *            data for front end
     * @return mapping
     */
    @RequestMapping ( value = "admin/manageICDCodes" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String addCode ( final Model model ) {
        return "/admin/manageICDCodes";
    }
}
