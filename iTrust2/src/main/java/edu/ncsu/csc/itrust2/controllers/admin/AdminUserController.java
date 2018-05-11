package edu.ncsu.csc.itrust2.controllers.admin;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.itrust2.forms.admin.DeleteUserForm;
import edu.ncsu.csc.itrust2.forms.admin.UserForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Patient;
import edu.ncsu.csc.itrust2.models.persistent.Personnel;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * This controller enables Admins to add new users to the system or (eventually)
 * modify existing ones
 *
 * @author Kai Presler-Marshall
 *
 */

@Controller
public class AdminUserController {

    /**
     * Add user
     *
     * @param model
     *            data for front end
     * @return mapping
     */
    @RequestMapping ( value = "admin/addUser" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String addUser ( final Model model ) {
        model.addAttribute( "UserForm", new UserForm() );
        return "/admin/addUser";
    }

    /**
     * Add user submission
     *
     * @param form
     *            form information
     * @param result
     *            result
     * @param model
     *            data for front end
     * @return mapping
     */
    @PostMapping ( "/admin/addUser" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String addUserSubmit ( @Valid @ModelAttribute ( "UserForm" ) final UserForm form, final BindingResult result,
            final Model model ) {
        User u = null;
        try {
            u = new User( form );
            if ( User.getByName( u.getUsername() ) != null ) {
                result.rejectValue( "username", "username.notvalid", "Username is already in use" );
            }
        }
        catch ( final Exception e ) {
            result.rejectValue( "password", "password.notvalid", "Passwords invalid or do not match" );
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "UserForm", form );
            return "/admin/addUser";
        }
        else {
            u.save();
            switch ( u.getRole() ) {
                // user is created, lets create the specific table entries
                case ROLE_ADMIN:
                case ROLE_HCP:
                    final Personnel per = new Personnel();
                    per.setSelf( u );
                    per.save();
                    break;
                case ROLE_PATIENT:
                    final Patient pat = new Patient();
                    pat.setSelf( u );
                    pat.save();
                    break;
                default:
                    // shouldn't reach
                    break;
            }
            LoggerUtil.log( TransactionType.CREATE_USER,
                    SecurityContextHolder.getContext().getAuthentication().getName(), u.getUsername() );
            return "admin/addUserResult";
        }
    }

    /**
     * Retrieves the form for the Delete User action
     *
     * @param model
     *            Data for front end
     * @return The page to display
     */
    @RequestMapping ( value = "admin/deleteUser" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String deleteUser ( final Model model ) {
        model.addAttribute( "users", User.getUsers() );
        return "admin/deleteUser";
    }

    /**
     * Submit action for the Delete User action.
     *
     * @param form
     *            Submitted form
     * @param result
     *            Results of the validation
     * @param model
     *            Model from front end
     * @return The page to redirect to
     */
    @PostMapping ( "/admin/deleteUser" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String deleteUserSubmit ( @Valid @ModelAttribute ( "DeleteUserForm" ) final DeleteUserForm form,
            final BindingResult result, final Model model ) {
        final User u = User.getByName( form.getName() );
        if ( null != form.getConfirm() && null != u ) {
            u.delete();
            return "admin/deleteUserResult";
        }
        else if ( null == u ) {
            result.rejectValue( "name", "name.notvalid", "User cannot be found" );
        }
        else {
            result.rejectValue( "confirm", "confirm.notvalid", "You must confirm that the user should be deleted" );
        }
        return "admin/deleteUser";

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
        model.addAttribute( "users", User.getUsers() );
        return "admin/drugs";
    }
}
