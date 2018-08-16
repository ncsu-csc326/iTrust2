package edu.ncsu.csc.itrust2.models.persistent;

import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.criterion.Criterion;

import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;

/**
 * Class for Lab Procedure codes. These codes themselves are stored as a String,
 * along with three descriptive strings and an ID.
 *
 * @author Thomas Dickerson
 *
 */
@Entity
@Table ( name = "LOINCCodes" )
public class LOINC extends DomainObject<LOINC> {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    /**
     * The LOINC Code string. Formatted as [1 to 5 digits]-[1 digit]
     */
    private String code;
    /**
     * The commonly-used name for the lab procedure
     */
    private String commonName;
    /**
     * The substance or entity being measured or observed
     */
    private String component;
    /**
     * The characteristic or attribute of the analyte
     */
    private String property;

    @Override
    public Long getId () {
        return id;
    }

    /**
     * Empty constructor for Hibernate
     */
    public LOINC () {

    }

    /**
     * Construct from a form
     *
     * @param form
     *            The form that validates and sanitizes input
     */
    public LOINC ( final LOINCForm form ) {
        setCode( form.getCode() );
        setId( form.getId() );
        setCommonName( form.getCommonName() );
        setComponent( form.getComponent() );
        setProperty( form.getProperty() );
    }

    /**
     * Sets the ID of the Code
     *
     * @param id
     *            The new ID of the Code. For Hibernate.
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the String representation of the code
     *
     * @return The code
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the String representation of the code
     *
     * @param code
     *            The new code
     */
    public void setCode ( final String code ) {
        if ( code != null && Pattern.matches( "^[0-9]{1,5}-[0-9]$", code ) ) {
            this.code = code;
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Sets the common name of this code
     *
     * @param n
     *            The new common name
     */
    public void setCommonName ( final String n ) {
        if ( n == null || n.length() > 250 ) {
            throw new IllegalArgumentException();
        }
        this.commonName = n;
    }

    /**
     * Returns the common name of the code
     *
     * @return The common name
     */
    public String getCommonName () {
        return commonName;
    }

    /**
     * Sets the component of this code
     *
     * @param c
     *            The new component
     */
    public void setComponent ( final String c ) {
        if ( c == null || c.length() > 250 ) {
            throw new IllegalArgumentException();
        }
        this.component = c;
    }

    /**
     * Returns the component of the code
     *
     * @return The component
     */
    public String getComponent () {
        return component;
    }

    /**
     * Sets the property of this code
     *
     * @param p
     *            The new property
     */
    public void setProperty ( final String p ) {
        if ( p == null || p.length() > 250 ) {
            throw new IllegalArgumentException();
        }
        this.property = p;
    }

    /**
     * Returns the property of the code
     *
     * @return The property
     */
    public String getProperty () {
        return property;
    }

    @Override
    public boolean equals ( final Object o ) {
        if ( o instanceof LOINC ) {
            final LOINC c = (LOINC) o;
            return id.equals( c.getId() ) && commonName.equals( c.getCommonName() ) && code.equals( c.getCode() )
                    && component.equals( c.getComponent() ) && property.equals( c.getProperty() );
        }
        return false;
    }

    /**
     * Returns a List of LOINC codes that meet the given WHERE clause
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return The list of Codes selected
     */
    @SuppressWarnings ( "unchecked" )
    private static List<LOINC> getWhere ( final List<Criterion> where ) {
        return (List<LOINC>) getWhere( LOINC.class, where );
    }

    /**
     * Returns the Code with the given ID
     *
     * @param id
     *            The ID to retrieve
     * @return The LOINC requested if it exists
     */
    public static LOINC getById ( final Long id ) {
        try {
            return getWhere( eqList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }

    }

    /**
     * Returns a list of all LOINC codes in the system
     *
     * @return The list of Codes
     */
    @SuppressWarnings ( "unchecked" )
    public static List<LOINC> getAll () {
        return (List<LOINC>) DomainObject.getAll( LOINC.class );
    }

}
