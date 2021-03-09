package edu.ncsu.csc.itrust2.models.enums;

/**
 * Enum of all the possible scales for measuring lab results.
 *
 * @author Sam Fields
 *
 */
public enum LabResultScale {

    /**
     * Indicates the LOINC has no results
     */
    NONE ( "None", 0 ),
    /**
     * Results with numerical values. Can be continuous or discrete.
     */
    QUANTITATIVE ( "Quantitative", 1 ),

    /**
     * Results with text values. Can be nominal or ordinal.
     */
    QUALITATIVE ( "Qualitative", 2 );

    /**
     * Numerical code of the LabResultScale
     */
    private int    code;

    /**
     * Name of the result scale
     */
    private String name;

    /**
     * Gets the name of the scale
     * 
     * @return the name of the scale
     */
    public String getName () {
        return name;
    }

    /**
     * Creates the LabResultScale from its code.
     *
     * @param code
     *            Code of the AppointmentType
     */
    private LabResultScale ( final String name, final int code ) {
        this.name = name;
        this.code = code;
    }

    /**
     * Gets the numerical code of the LabResultScale
     *
     * @return Code of the LabResultScale
     */
    public int getCode () {
        return code;
    }

    /**
     * Parses a lab result scale from a string
     *
     * @param scaleStr
     *            the name of the scale
     * @return a lab result scale from the string
     */
    public static LabResultScale parse ( String scaleStr ) {
        for ( final LabResultScale scale : values() ) {
            if ( scale.getName().equals( scaleStr ) ) {
                return scale;
            }
        }
        return NONE;
    }

}
