package edu.ncsu.csc.itrust2.controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Default controller that handles redirecting the logged-in user to one of the
 * appropriate landing screens based on their user roles. If a new role is added
 * to the system, add to the edu.ncsu.csc.itrust.roles.Role class.
 *
 * Other functionality should (generally) not be added to this class and instead
 * go in an appropriate controller for the user type. See the sub-packages for
 * location of each controller type.
 *
 * @author Kai Presler-Marshall
 *
 */

@Controller
public class DefaultController {

    /**
     * This controller is used to redirect the authenticated user to the
     * appropriate landing screen based on their role.
     *
     * @param model
     *            The data from the front end
     * @return The page to be displayed to the user
     */
    @RequestMapping ( value = "/" )
    public RedirectView index ( final Model model ) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final List< ? extends GrantedAuthority> auths = (List< ? extends GrantedAuthority>) auth.getAuthorities();
        final GrantedAuthority ga = auths.get( 0 );
        return new RedirectView( edu.ncsu.csc.itrust2.models.enums.Role.valueOf( ga.toString() ).getLanding() );
    }
}
