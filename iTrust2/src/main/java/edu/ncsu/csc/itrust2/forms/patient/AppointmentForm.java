package edu.ncsu.csc.itrust2.forms.patient;

/**
 * Used for selecting Appointments from list to take further action on them
 *
 * @author Kai Presler-Marshall
 *
 */
public class AppointmentForm {

    /** The appointment of the form **/
    private String appointment;

    /**
     * Get the appointment of the form
     * 
     * @return the appointment of the form
     */
    public String getAppointment () {
        return appointment;
    }

    /**
     * Set the appointment of the form
     * 
     * @param appointment
     *            the appointment of the form
     */
    public void setAppointment ( final String appointment ) {
        this.appointment = appointment;
    }

    /**
     * Get the action of the appointment
     * 
     * @return the action of the appointment
     */
    public String getAction () {
        return action;
    }

    /**
     * Set the action of the appointment
     * 
     * @param action
     *            the action of the appointment
     */
    public void setAction ( final String action ) {
        this.action = action;
    }

    /** The action of the form **/
    private String action;

}
