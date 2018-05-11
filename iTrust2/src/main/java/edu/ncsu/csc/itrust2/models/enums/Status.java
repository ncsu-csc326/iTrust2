package edu.ncsu.csc.itrust2.models.enums;

/**
 * Status enum that should be used for various different statuses rather than
 * just using Strings or Integers. "Positive" statuses should be odd; "negative"
 * statuses should be even.
 *
 * @author Kai Presler-Marshall
 *
 */
public enum Status {

    /* Positive statuses */

    /**
     * Pending approval by staff
     */
    PENDING ( 1 ),
    /**
     * Approved by staff
     */
    APPROVED ( 3 ),

    /* Negative statuses */
    /**
     * Rejected by staff
     */
    REJECTED ( 2 ),

    ;

    /**
     * Code of the status
     */
    private int code;

    /**
     * Create a Status from the numerical code.
     *
     * @param code
     *            Code of the Status
     */
    private Status ( final int code ) {
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

}
