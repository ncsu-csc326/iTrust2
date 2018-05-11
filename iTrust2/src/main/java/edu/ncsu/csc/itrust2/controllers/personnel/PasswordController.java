package edu.ncsu.csc.itrust2.controllers.personnel;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Spring controller for the webpages associated with changing or resetting
 * password
 *
 * @author Thomas
 *
 */
@Controller
public class PasswordController {

    /**
     * Returns the html view for a request to reset password. This method is
     * accessed by anonymous user.
     *
     * @param model
     *            The Spring MVC Model
     * @return The html view
     */
    @GetMapping ( value = "/requestPasswordReset" )
    public String requestReset ( final Model model ) {
        return "personnel/passwordResetRequest";
    }

    /**
     * Returns the html view for the page to reset a forgotten password. This
     * page is accessed anonymously through the link sent in an email.
     *
     * @param model
     *            The Spring MVC Model
     * @param tkid
     *            The token ID
     * @return The html view of the reset password page.
     */
    @GetMapping ( value = "/resetPassword" )
    public String resetPassword ( final Model model, @RequestParam final long tkid ) {
        model.addAttribute( "tokenId", tkid );
        return "personnel/resetPassword";
    }

    /**
     * Returns the html view for the page to change a password. This is the page
     * used by a logged in user to change it.
     *
     * @param model
     *            The Spring MVC model
     * @return The html view to change a password.
     */

    @GetMapping ( value = "/changePassword" )
    public String changePassword ( final Model model ) {
        return "personnel/changePassword";
    }
}
