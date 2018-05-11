package edu.ncsu.csc.itrust2.models.enums;

import java.util.HashMap;
import java.util.Map;

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
    NotSpecified ( "Not Specified" );

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
     * Returns a map from field name to value, which is more easily serialized
     * for sending to front-end.
     *
     * @return map from field name to value for each of the fields in this enum
     */
    public Map<String, Object> getInfo () {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put( "id", name() );
        map.put( "name", getName() );
        return map;
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
