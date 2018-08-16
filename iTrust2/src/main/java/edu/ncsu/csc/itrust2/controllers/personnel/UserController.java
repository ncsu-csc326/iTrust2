package edu.ncsu.csc.itrust2.controllers.personnel;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Controller for Personnel to edit their information
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class UserController {

    /**
     * Controller for iTrust2 personnel to modify their demographics.
     * The @PreAuthorize tag will have to be extended if new classes of users
     * are added to the system
     *
     * @param model
     *            information about the vdw
     * @return response
     */
    @GetMapping ( value = "personnel/editDemographics" )
    @PreAuthorize ( "hasRole('ROLE_HCP') or hasRole('ROLE_ADMIN') or hasRole('ROLE_ER') or hasRole('ROLE_LABTECH')" )
    public String viewDemographics ( final Model model ) {
        LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, LoggerUtil.currentUser() );
        return "/personnel/editDemographics";
    }

    /**
     * Returns the image for Dr. Jenkins
     *
     * @return the image for Dr. Jenkins
     */
    @GetMapping ( "/resources/img/DrJenkins.jpg" )
    public String viewDrJenkins () {
        return "../resources/img/DrJekins.jpg";
    }

}
