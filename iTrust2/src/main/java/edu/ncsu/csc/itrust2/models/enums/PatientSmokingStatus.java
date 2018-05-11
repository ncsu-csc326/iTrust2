package edu.ncsu.csc.itrust2.models.enums;

/**
 * Enum representing the status of patient as a smoker.
 *
 * @author mrgray4
 */
public enum PatientSmokingStatus {

    /**
     * This field is unrelated
     */
    NONAPPLICABLE ( 0 ),
    /**
     * Non-smoking
     */
    NEVER ( 1 ),
    /**
     * Former smoker
     */
    FORMER ( 2 ),
    /**
     * Smokes somedays
     */
    SOMEDAYS ( 3 ),
    /**
     * Everyday smoker
     */
    EVERYDAY ( 4 ),
    /**
     * Current smoker but status unknown
     */
    CURRENT_BUT_UNKNOWN ( 5 ),
    /**
     * Unknown if ever smoked
     */
    UNKNOWN ( 9 ),

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
    private PatientSmokingStatus ( final int code ) {
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
     * Converts a code to a named smoking status.
     *
     * @param code
     *            The smoking code.
     * @return The string represented by the code.
     */
    public static String getName ( final int code ) {
        return PatientSmokingStatus.parseValue( code ).toString();
    }

    /**
     * Returns the PatientSmokingStatus enum that matches the given code.
     *
     * @param code
     *            The code to match
     * @return Corresponding PatientSmokingStatus object.
     */
    public static PatientSmokingStatus parseValue ( final int code ) {
        for ( final PatientSmokingStatus status : values() ) {
            if ( status.getCode() == code ) {
                return status;
            }
        }
        return PatientSmokingStatus.NONAPPLICABLE;
    }

}
