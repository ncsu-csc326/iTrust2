package edu.ncsu.csc.itrust2.models.enums;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Priority enum that should be used to indicate the priority of a Lab
 * Procedure. A Lab Procedure must be assigned a priority between 1 and 4 with 1
 * being the highest priority and 4 being the lowest.
 *
 * @author Alex Phelps
 *
 */
public enum Priority {

    /**
     * Highest priority
     */
    CRITICAL ( 1 ),
    /**
     * Second highest priority
     */
    HIGH ( 2 ),

    /**
     * Second lowest priority
     */
    MEDIUM ( 3 ),

    /**
     * Lowest priority
     */
    LOW ( 4 )

    ;

    /**
     * Code of the status
     */
    @Enumerated ( EnumType.STRING )
    private int code;

    /**
     * Create a Status from the numerical code.
     *
     * @param code
     *            Code of the Priority
     */
    private Priority ( final int code ) {
        this.code = code;
    }

    /**
     * Gets the numerical Code of the Status
     *
     * @return Code of the Status
     */
    public int getCode () {
        return code;
    }

    /**
     * Returns the Priority enum that matches the given code.
     *
     * @param code
     *            The code to match
     * @return Corresponding Priority object.
     */
    public static Priority parseIntValue ( final int code ) {
        for ( final Priority status : values() ) {
            if ( status.getCode() == code ) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns the Priority enum that matches the given string.
     *
     * @param name
     *            name of enum
     * @return corresponding Priority object
     */
    public static Priority parseStrValue ( final String name ) {
        for ( final Priority priority : values() ) {
            if ( priority.name().equals( name ) ) {
                return priority;
            }
        }
        throw new IllegalArgumentException();
    }
}
