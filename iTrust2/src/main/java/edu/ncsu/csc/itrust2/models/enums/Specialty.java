package edu.ncsu.csc.itrust2.models.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enum for Specialty Type of a health care provider.
 *
 * @author nrshah4
 *
 */
public enum Specialty {

    /**
     * Cardiologist
     */
    CARDIOLOGIST ( "Cardiologist" ),

    /**
     * Dermatologist
     */
    DERMATOLOGIST ( "Dermatologist" ),

    /**
     * OPH
     */
    OPH ( "Opthalmologist" ),

    /**
     * Sleep Medicine
     */
    SLEEP_MEDICINE ( "Sleep Medicine" ),

    /**
     * MG
     */
    MEDICAL_GENETICIST ( "Medical Geneticist" ),

    /**
     * Urologist
     */
    UROLOGIST ( "Urologist" ),

    /**
     * Neurologist
     */
    NEUROLOGIST ( "Neurologist" ),

    /**
     * Pediatrician
     */
    PED ( "Pediatrician" ),

    /**
     * Psychiatrist
     */
    PSYCH ( "Psychiatrist" ),

    /**
     * Virologist
     */
    VIROLOGIST ( "Virologist" ),

    /**
     * Not Specified
     */
    NOT_SPECIFIED ( "Not Specified" );

    /** name of Specialty */
    private String name;

    /**
     * Constructor
     *
     * @param name
     *            of specialty
     */
    Specialty ( final String name ) {
        this.name = name;
    }

    /**
     * Get Specialty name.
     *
     * @return name
     */
    public String getName () {
        return this.name;
    }

    /**
     * Get info for all Specialties
     *
     * @return information for all specialties
     */
    public Map<String, Object> getInfo () {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put( "id", name() );
        map.put( "name", getName() );
        return map;
    }

    /**
     * Match a string with a Specialty Enum
     *
     * @param specialty
     *            the specialty to parse
     * @return Specialty or null
     */
    public static Specialty parse ( final String specialty ) {
        for ( final Specialty mySpecialty : values() ) {
            if ( mySpecialty.getName().equals( specialty ) ) {
                return mySpecialty;
            }
        }
        return null;

    }

    /**
     * Get a list of Specialty Enum names
     *
     * @return List<String> all enum names
     */
    public static List<String> getAllNames () {
        final ArrayList<String> list = new ArrayList<String>();
        for ( final Specialty mySpecialty : values() ) {
            if ( !mySpecialty.getName().equals( "Not Specified" ) ) {
                list.add( mySpecialty.getName() );
            }
        }
        return list;

    }

}
