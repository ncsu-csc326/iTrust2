package edu.ncsu.csc.itrust2.models.enums;

/**
 * Enum of all possible blood types.
 *
 * @author Kai Presler-Marshall
 *
 */
public enum BloodType {

    /**
     * A Positive
     */
    APos ( "A+" ),
    /**
     * A Negative
     */
    ANeg ( "A-" ),
    /**
     * B Positive
     */
    BPos ( "B+" ),
    /**
     * B Negative
     */
    BNeg ( "B-" ),
    /**
     * AB Positive
     */
    ABPos ( "AB+" ),
    /**
     * AB Negative
     */
    ABNeg ( "AB-" ),
    /**
     * O Positive
     */
    OPos ( "O+" ),
    /**
     * O Negative
     */
    ONeg ( "O-" ),
    /**
     * Not Specified / Unknown
     */
    NotSpecified ( "NotSpecified" );

    /**
     * Name of the BloodType
     */
    private String name;

    /**
     * Constructor for Enum.
     *
     * @param name
     *            Name of the BloodType
     */
    private BloodType ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the Name of this BloodType
     *
     * @return Name of the BloodType
     */
    public String getName () {
        return name;
    }

    /**
     * Converts the BloodType to a String
     */
    @Override
    public String toString () {
        return getName();
    }

    /**
     * Finds the matching BloodType Enum from the type provided
     *
     * @param typeStr
     *            Name of the BloodType to find an Enum record for
     * @return The BloodType found from the string, or Not Specified if none
     *         could be found
     */
    public static BloodType parse ( final String typeStr ) {
        for ( final BloodType type : values() ) {
            if ( type.getName().equals( typeStr ) ) {
                return type;
            }
        }
        return NotSpecified;
    }
}
