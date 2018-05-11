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

import edu.ncsu.csc.itrust2.forms.admin.DeleteHospitalForm;
import edu.ncsu.csc.itrust2.forms.admin.HospitalForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 * Class that enables an Admin to add a Hospital to the system.
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class HospitalController {

    /**
     * Creates the form page for the Add Hospital page
     *
     * @param model
     *            Data for the front end
     * @return Page to show to the user
     */
    @RequestMapping ( value = "admin/addHospital" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String addHospital ( final Model model ) {
        model.addAttribute( "HospitalForm", new HospitalForm() );
        return "/admin/addHospital";
    }

    /**
     * Parses the HospitalForm from the User
     *
     * @param form
     *            HospitalForm to validate and save
     * @param result
     *            Validation result
     * @param model
     *            Data from the front end
     * @return The page to show to the user
     */
    @PostMapping ( "/admin/addHospital" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String addHospitalSubmit ( @Valid @ModelAttribute ( "HospitalForm" ) final HospitalForm form,
            final BindingResult result, final Model model ) {
        Hospital h = null;
        try {
            h = new Hospital( form );
            if ( Hospital.getByName( h.getName() ) != null ) {
                result.rejectValue( "name", "name.notvalid", "A hospital with this name already exists" );
            }
        }
        catch ( final Exception e ) {
            throw new IllegalArgumentException(
                    e ); /* This should never happen */
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "HospitalForm", form );
            return "/admin/addHospital";
        }
        else {
            h.save();
            LoggerUtil.log( TransactionType.CREATE_HOSPITAL,
                    SecurityContextHolder.getContext().getAuthentication().getName() );
            return "admin/addHospitalResult";
        }
    }

    /**
     * Displays the form for an Admin to delete a Hospital from the system
     *
     * @param model
     *            Data for the front end
     * @return The page to display to the user
     */
    @RequestMapping ( value = "admin/deleteHospital" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String deleteHospital ( final Model model ) {
        model.addAttribute( "hospitals", Hospital.getHospitals() );
        return "admin/deleteHospital";
    }

    /**
     * Processes the form for an Admin to delete a Hospital from the system
     *
     * @param form
     *            DeleteHospitalForm to use to delete the hospital
     * @param result
     *            Validation result
     * @param model
     *            Data from the front end
     * @return Page to show to the user
     */
    @PostMapping ( "/admin/deleteHospital" )
    @PreAuthorize ( "hasRole('ROLE_ADMIN')" )
    public String deleteHospitalSubmit ( @Valid @ModelAttribute ( "DeleteHospitalForm" ) final DeleteHospitalForm form,
            final BindingResult result, final Model model ) {
        final Hospital h = Hospital.getByName( form.getName() );
        if ( null != form.getConfirm() && null != h ) {
            h.delete();
            return "admin/deleteHospitalResult";
        }
        else if ( null == h ) {
            result.rejectValue( "name", "name.notvalid", "Hospital cannot be found" );
        }
        else {
            result.rejectValue( "confirm", "confirm.notvalid", "You must confirm that the hospital should be deleted" );
        }
        return "admin/deleteHospital";

    }

}
