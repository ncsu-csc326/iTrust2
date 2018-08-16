package edu.ncsu.csc.itrust2.forms.admin;

import edu.ncsu.csc.itrust2.models.persistent.LOINC;

/**
 * Intermediate form for adding or editing LOINC codes. Used to create and
 * serialize LOINC codes.
 *
 * @author Thomas Dickerson
 *
 */
public class LOINCForm {

    /** The code of the Lab Procedure */
    private String code;
    /** The auto-generated id of the LOINC code */
    private Long   id;
    /** The commonly-used name for the lab procedure */
    private String commonName;
    /** The substance or entity being measured or observed */
    private String component;
    /** The characteristic or attribute of the analyte */
    private String property;

    /**
     * Empty constructor for GSON
     */
    public LOINCForm () {

    }

    /**
     * Construct a form off an existing LOINC object
     *
     * @param code
     *            The object to fill this form with
     */
    public LOINCForm ( final LOINC code ) {
        setCode( code.getCode() );
        setCommonName( code.getCommonName() );
        setComponent( code.getComponent() );
        setProperty( code.getProperty() );
        setId( code.getId() );
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
     * Returns the String representation of the code
     *
     * @return The code
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the common name of this code
     *
     * @param n
     *            The new common name
     */
    public void setCommonName ( final String n ) {
        commonName = n;
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
        component = c;
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
        property = p;
    }

    /**
     * Returns the property of the code
     *
     * @return The property
     */
    public String getProperty () {
        return property;
    }

    /**
     * Sets the ID of the Code
     *
     * @param l
     *            The new ID of the Code. For Hibernate.
     */
    public void setId ( final Long l ) {
        id = l;
    }

    /**
     * Returns the ID of the Code
     *
     * @return The Lab Procedure ID
     */
    public Long getId () {
        return id;
    }

    @Override
    public boolean equals ( final Object o ) {
        if ( o instanceof LOINCForm ) {
            final LOINCForm f = (LOINCForm) o;
            return code.equals( f.getCode() ) && id.equals( f.getId() ) && commonName.equals( f.getCommonName() )
                    && component.equals( f.getComponent() ) && property.equals( f.getProperty() );
        }
        return false;
    }

}
