package edu.ncsu.csc.itrust2.controllers.personnel;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import edu.ncsu.csc.itrust2.forms.personnel.PersonnelForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
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
     *            information about the vidw
     * @return response
     */
    @GetMapping ( value = "personnel/editDemographics" )
    @PreAuthorize ( "hasRole('ROLE_HCP') or hasRole('ROLE_ADMIN')" )
    public String viewDemographics ( final Model model ) {
        final User self = User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() );
        final PersonnelForm form = new PersonnelForm( Personnel.getByName( self.getUsername() ) );
        model.addAttribute( "PersonnelForm", form );
        LoggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, self );
        return "/personnel/editDemographics";
    }

    /**
     * Controller for iTrust2 personnel to edit their demographics. This is the
     * submit action.
     *
     * @param form
     *            The PersonnelForm form
     * @param result
     *            The validation result
     * @param model
     *            Data from front end
     * @return The page to display for the user
     */
    @PostMapping ( "/personnel/editDemographics" )
    @PreAuthorize ( "hasRole('ROLE_HCP') or hasRole('ROLE_ADMIN')" )
    public String demographicsSubmit ( @Valid @ModelAttribute ( "PersonnelForm" ) final PersonnelForm form,
            final BindingResult result, final Model model ) {
        Personnel p = null;
        try {
            p = new Personnel( form );
            p.setSelf( User.getByName( SecurityContextHolder.getContext().getAuthentication().getName() ) );
        }
        catch ( final Exception e ) {
            e.printStackTrace( System.out );
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "PersonnelForm", form );
            return "/personnel/editDemographics";
        }
        else {
            p.save();
            LoggerUtil.log( TransactionType.ENTER_EDIT_DEMOGRAPHICS,
                    SecurityContextHolder.getContext().getAuthentication().getName() );
            return "personnel/editDemographicsResult";
        }
    }

}
