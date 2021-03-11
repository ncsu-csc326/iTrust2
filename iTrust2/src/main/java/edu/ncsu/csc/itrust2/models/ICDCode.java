package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.ncsu.csc.iTrust2.forms.ICDCodeForm;

/**
 * Class for Diagnosis codes. These codes themselves are stored as a String,
 * along with a description and an ID.
 *
 * @author Thomas
 * @author Kai Presler-Marshall
 *
 */
@Entity
public class ICDCode extends DomainObject {

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    /**
     * The ICD Code string
     */
    private String code;
    /**
     * Description of the diagnosis
     */
    private String description;

    /**
     * Empty constructor for Hibernate
     */
    public ICDCode () {

    }

    /**
     * Construct from a form
     *
     * @param form
     *            The form that validates and sanitizes input
     */
    public ICDCode ( final ICDCodeForm form ) {
        setCode( form.getCode() );
        setDescription( form.getDescription() );
        setId( form.getId() );

        // validate
        if ( description.length() > 250 ) {
            throw new IllegalArgumentException( "Description too long (250 characters max): " + description );
        }
        // code XYY where X is a capital alphabetic letter and Y is a number
        // from 0-9. Code can also contain up to three decimal places
        final char[] c = code.toCharArray();
        if ( c.length < 3 ) {
            throw new IllegalArgumentException( "Code must be at least three characters: " + code );
        }
        if ( !Character.isLetter( c[0] ) ) {
            throw new IllegalArgumentException( "Code must begin with a capital letter: " + code );
        }
        // check its a valid number
        if ( !Character.isDigit( c[1] ) ) {
            throw new IllegalArgumentException( "Second character of code must be a digit: " + code );
        }
        if ( !Character.isLetter( c[2] ) && !Character.isDigit( c[2] ) ) {
            throw new IllegalArgumentException( "Third character of code must be alphanumeric: " + code );
        }
        if ( c.length > 3 ) {
            if ( c[3] != '.' ) {
                throw new IllegalArgumentException( "Fourth character of code must be decimal: " + code );
            }
        }
        if ( c.length > 8 ) {
            throw new IllegalArgumentException( "Code too long! Max 8 characters including decimal: " + code );
        }
        for ( int i = 4; i < c.length; i++ ) {
            if ( !Character.isLetter( c[i] ) && !Character.isDigit( c[i] ) ) {
                throw new IllegalArgumentException( "Characters after decimal must be alphanumeric: " + code );
            }
        }
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
        this.code = code;
    }

    /**
     * Returns the description of the code
     *
     * @return The description
     */
    public String getDescription () {
        return this.description;
    }

    /**
     * Sets the description of this code
     *
     * @param d
     *            The new description
     */
    public void setDescription ( final String d ) {
        description = d;
    }

    @Override
    public boolean equals ( final Object o ) {
        if ( o instanceof ICDCode ) {
            final ICDCode c = (ICDCode) o;
            return id.equals( c.getId() ) && description.equals( c.getDescription() ) && code.equals( c.getCode() );
        }
        return false;
    }

    public Long getId () {
        return id;
    }

}
