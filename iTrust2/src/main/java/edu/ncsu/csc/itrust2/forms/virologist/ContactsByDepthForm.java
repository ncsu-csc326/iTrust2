package edu.ncsu.csc.itrust2.forms.virologist;

/**
 * Form for Virologist to perform n-depth searches of who a Passenger has come
 * in contact with
 *
 * @author caproven
 */
public class ContactsByDepthForm {

    /**
     * Passenger ID of the source Passenger for the search
     */
    private String passengerId;

    /**
     * How many levels deep to search when going through Passenger contacts
     */
    private int    depth;

    /**
     * Constructs the form
     *
     * @param passengerId
     *            the source Passenger's Passenger ID
     * @param depth
     *            Depth of the contact search to perform
     */
    public ContactsByDepthForm ( final String passengerId, final int depth ) {
        this.passengerId = passengerId;
        this.depth = depth;
    }

    /**
     * Empty constructor
     */
    public ContactsByDepthForm () {
    }

    /**
     * Retrieves the form's Passenger ID
     *
     * @return the Passenger ID
     */
    public String getPassengerId () {
        return passengerId;
    }

    /**
     * Sets the form's Passenger ID
     *
     * @param passengerId
     *            the Passenger ID
     */
    public void setPassengerId ( final String passengerId ) {
        this.passengerId = passengerId;
    }

    /**
     * Retrieves the form's given depth
     *
     * @return the depth
     */
    public int getDepth () {
        return depth;
    }

    /**
     * Sets the form's given depth
     *
     * @param depth
     *            the depth
     */
    public void setDepth ( final int depth ) {
        this.depth = depth;
    }
}
