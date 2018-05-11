package edu.ncsu.csc.itrust2.forms.admin;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form used for an Admin to delete a Hospital stored in the system
 *
 * @author Kai Presler-Marshall
 *
 */
public class DeleteHospitalForm {

    /**
     * Name of the Hospital to delete
     */
    private String name;

    /**
     * Whether the user selected to confirm their action
     */
    @NotEmpty
    private String confirm;

    /**
     * Retrieve the Name from the form
     *
     * @return The Name of the hospital form
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the Name of the Hospital to delete
     *
     * @param name
     *            The Name of the hospital to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Retrieve whether the user confirmed the delete action
     *
     * @return Confirmation
     */
    public String getConfirm () {
        return confirm;
    }

    /**
     * Whether or not the user confirmed the action
     *
     * @param confirm
     *            Confirmation
     */
    public void setConfirm ( final String confirm ) {
        this.confirm = confirm;
    }
}
