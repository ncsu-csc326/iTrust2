package edu.ncsu.csc.itrust2.models.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * A SymptomSeverity represents the level of symptoms a patient is experiencing.
 */
public enum SymptomSeverity {

    /**
     * Not infected or not experiencing any symptoms
     */
    NOT_INFECTED ( "Not Infected", "N" ),

    /**
     * Experiencing mild symptoms
     */
    MILD ( "Mild", "M" ),

    /**
     * Experiencing severe symptoms
     */
    SEVERE ( "Severe", "S" ),

    /**
     * Experiencing critical symptoms
     */
    CRITICAL ( "Critical", "C" );

    /**
     * Creates a SymptomSeverity
     *
     * @param code
     *            Code of the severity
     * @param name
     *            Name or description of the severity
     */
    private SymptomSeverity ( final String name, final String abbreviation ) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    /**
     * Name of the SymptomSeverity
     */
    private String name;

    /**
     * An abbreviation of the SymptomSeverity's name
     */
    private String abbreviation;

    /**
     * Gets SymptomSeverity name
     *
     * @return the name
     */
    public String getName () {
        return this.name;
    }

    /**
     * Gets SymptomSeverity abbreviation
     *
     * @return the abbreviation
     */
    public String getAbbreviation () {
        return this.abbreviation;
    }

    /**
     * Match a string with a SymptomSeverity Enum
     *
     * @param severity
     *            the symptom severity to parse
     * @return SymptomSeverity or null
     */
    public static SymptomSeverity parse ( final String severity ) {
        for ( final SymptomSeverity mySeverity : values() ) {
            if ( mySeverity.getName().equals( severity ) || mySeverity.getAbbreviation().equals( severity ) ) {
                return mySeverity;
            }
        }
        return null;
    }

    /**
     * Get a list of SymptomSeverity Enum names
     *
     * @return List<String> all enum names
     */
    public static List<String> getAllNames () {
        final ArrayList<String> list = new ArrayList<String>();
        for ( final SymptomSeverity mySeverity : values() ) {
            if ( !mySeverity.getName().equals( "Not Specified" ) ) {
                list.add( mySeverity.getName() );
            }
        }
        return list;
    }
}
