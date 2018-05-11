package edu.ncsu.csc.itrust2.models.enums;

/**
 * Enum of all of the types of appointments that are recognized by the system.
 *
 * @author Kai Presler-Marshall
 *
 */
public enum AppointmentType {

    /**
     * General Checkup
     */
    GENERAL_CHECKUP ( 1 ),

    ;

    /**
     * Numerical code of the AppointmentType
     */
    private int code;

    /**
     * Creates the AppointmentType from its code.
     *
     * @param code
     *            Code of the AppointmentType
     */
    private AppointmentType ( final int code ) {
        this.code = code;
    }

    /**
     * Gets the numerical code of the AppointmentType
     *
     * @return Code of the AppointmentType
     */
    public int getCode () {
        return code;
    }

}
