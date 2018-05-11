package edu.ncsu.csc.itrust2.models.enums;

/**
 * Enum representing possible ethnicities known by the system.
 *
 * @author Kai Presler-Marshall
 *
 */
public enum Ethnicity {

    /**
     * Caucasian
     */
    Caucasian ( "Caucasian" ),
    /**
     * African-American
     */
    AfricanAmerican ( "African American" ),
    /**
     * Hispanic
     */
    Hispanic ( "Hispanic" ),
    /**
     * Native American/First Nations People/American-Indian
     */
    NativeAmerican ( "Native American" ),
    /**
     * Asian
     */
    Asian ( "Asian" ),
    /**
     * Not Specified
     */
    NotSpecified ( "Not Specified" );

    /**
     * Name of the Ethnicity
     */
    private String name;

    /**
     * Constructor for Ethnicity.
     *
     * @param name
     *            Name of the Ethnicity to create
     */
    private Ethnicity ( final String name ) {
        this.name = name;
    }

    /**
     * Retrieve the Name of the Ethnicity
     *
     * @return Name of the Ethnicity
     */
    public String getName () {
        return this.name;
    }

    /**
     * Convert the Ethnicity to a String
     */
    @Override
    public String toString () {
        return getName();
    }

    /**
     * Find the matching Ethnicity for the string provided.
     *
     * @param ethnicityStr
     *            Ethnicity String to find an Ethnicity Enum for
     * @return The Ethnicity parsed or NotSpecified if not found
     */
    public static Ethnicity parse ( final String ethnicityStr ) {
        for ( final Ethnicity ethnicity : values() ) {
            if ( ethnicity.getName().equals( ethnicityStr ) ) {
                return ethnicity;
            }
        }
        return NotSpecified;
    }

}
