package edu.ncsu.csc.itrust2.controllers.labtech;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to manage basic abilities for Lab tech role
 *
 * @author Natalie Landsberg
 * @author Kai Presler-Marshall
 *
 */

@Controller
public class LabTechController {

    /**
     * Returns the lab tech for the given model
     *
     * @param model
     *            model to check
     * @return role
     */
    @RequestMapping ( value = "labtech/index" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public String index ( final Model model ) {
        return edu.ncsu.csc.itrust2.models.enums.Role.ROLE_LABTECH.getLanding();
    }

    /**
     * Returns the procedures page
     *
     * @return role
     */
    @RequestMapping ( value = "labtech/procedures" )
    @PreAuthorize ( "hasRole('ROLE_LABTECH')" )
    public String labProcedures () {
        return "labtech/procedures";
    }

}
