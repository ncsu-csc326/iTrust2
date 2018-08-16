package edu.ncsu.csc.itrust2.models.enums;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * LabStatus enum that should be used to indicate the status of Lab Procedures.
 * A Lab Procedure must be assigned to a Lab Tech upon creation. Once it is in
 * progress, the Lab Tech can update the status to reflect this. Once finished,
 * the LabStatus is completed.
 *
 * @author Alex Phelps
 * @author Natalie Landsberg
 */
public enum LabStatus {

    /**
     * Assigned to a LabTech
     */
    ASSIGNED ( 1 ),
    /**
     * Procedure is in progress.
     */
    IN_PROGRESS ( 2 ),
    /**
     * The procedure is finished.
     */
    COMPLETED ( 3 );

    /**
     * Code of the LabStatus
     */
    @Enumerated ( EnumType.STRING )
    private int code;

    /**
     * Create a LabStatus from the numerical code.
     *
     * @param code
     *            Code of the LabStatus
     */
    private LabStatus ( final int code ) {
        this.code = code;
    }

    /**
     * Gets the numerical Code of the LabStatus
     *
     * @return Code of the LabStatus
     */
    public int getCode () {
        return code;
    }

    /**
     * Returns the LabStatus enum that matches the given code.
     *
     * @param code
     *            The code to match
     * @return Corresponding labstatus object.
     */
    public static LabStatus parseValue ( final int code ) {
        for ( final LabStatus status : values() ) {
            if ( status.getCode() == code ) {
                return status;
            }
        }
        return null;
    }

    /**
     * Returns the LabStatus enum that matches the given name
     *
     * @param name
     *            the string value of the enum
     * @return corresponding labStatus object
     */
    public static LabStatus parseStrValue ( final String name ) {
        for ( final LabStatus status : values() ) {
            if ( status.name().equals( name ) ) {
                return status;
            }
        }
        throw new IllegalArgumentException();
    }
}
