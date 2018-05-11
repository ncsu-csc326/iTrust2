package edu.ncsu.csc.itrust2.models.enums;

/**
 * Enum representing possible Genders for users of the system.
 *
 * @author Kai Presler-Marshall
 *
 */
public enum Gender {

    /**
     * Male
     */
    Male ( "Male" ),
    /**
     * Female
     */
    Female ( "Female" ),
    /**
     * Other
     */
    Other ( "Other" ),
    /**
     * Not Specified
     */
    NotSpecified ( "Not Specified" );

    /**
     * Name of this Gender
     */
    private String name;

    /**
     * Constructor for the Gender
     *
     * @param name
     *            Name of the Gender to create
     */
    private Gender ( final String name ) {
        this.name = name;
    }

    /**
     * Get the Name of the Gender
     *
     * @return Name of the Gender
     */
    public String getName () {
        return this.name;
    }

    /**
     * Convert the Gender to a String
     */
    @Override
    public String toString () {
        return getName();
    }

    /**
     * Parse the String provided into an actual Gender enum.
     * 
     * @param genderStr
     *            String representation of the Gender
     * @return Gender enum corresponding to the string, or NotSpecified if
     *         nothing could be matched
     */
    public static Gender parse ( final String genderStr ) {
        for ( final Gender gender : values() ) {
            if ( gender.getName().equals( genderStr ) ) {
                return gender;
            }
        }
        return NotSpecified;
    }

}
