package edu.ncsu.csc.itrust2.forms.admin;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form used for an Admin to delete a user (any type) from the system.
 *
 * @author Kai Presler-Marshall
 *
 */
public class DeleteUserForm {

    /**
     * Name of the User to delete
     */
    private String name;

    /**
     * Whether the Admin confirmed the action or not.
     */
    @NotEmpty
    private String confirm;

    /**
     * Retrieves the Name of the user to delete
     *
     * @return The Name of the User
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the Name of the User to delete
     *
     * @param name
     *            The new Name
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Retrieves whether the Admin confirmed the action
     *
     * @return Whether the event was confirmed
     */
    public String getConfirm () {
        return confirm;
    }

    /**
     * Mark the action as confirmed or not
     *
     * @param confirm
     *            New confirm setting
     */
    public void setConfirm ( final String confirm ) {
        this.confirm = confirm;
    }
}
