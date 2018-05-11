package edu.ncsu.csc.itrust2.forms.admin;

import edu.ncsu.csc.itrust2.models.persistent.ICDCode;

/**
 * Intermediate form for adding or editing ICDCodes. Used to create and
 * serialize ICDCodes.
 *
 * @author Thomas
 *
 */
public class ICDCodeForm {

    /** The code of the Diagnosis */
    private String code;
    /** The description of the diagnosis */
    private String description;
    private Long   id;

    /**
     * Empty constructor for GSON
     */
    public ICDCodeForm () {

    }

    /**
     * Construct a form off an existing ICDCode object
     *
     * @param code
     *            The object to fill this form with
     */
    public ICDCodeForm ( final ICDCode code ) {
        setCode( code.getCode() );
        setDescription( code.getDescription() );
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
     * Sets the description of this code
     *
     * @param d
     *            The new description
     */
    public void setDescription ( final String d ) {
        description = d;
    }

    /**
     * Returns the description of the code
     *
     * @return The description
     */
    public String getDescription () {
        return description;
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
     * Returns the ID of the Diagnosis
     * 
     * @return The diagnosis ID
     */
    public Long getId () {
        return id;
    }

    @Override
    public boolean equals ( final Object o ) {
        if ( o instanceof ICDCodeForm ) {
            final ICDCodeForm f = (ICDCodeForm) o;
            return code.equals( f.getCode() ) && id.equals( f.getId() ) && description.equals( f.getDescription() );
        }
        return false;
    }

}
