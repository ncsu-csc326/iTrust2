
package edu.ncsu.csc.itrust2.forms.admin;

import edu.ncsu.csc.itrust2.models.persistent.Drug;

/**
 * A form for REST API communication. Contains fields for constructing Drug
 * objects.
 *
 * @author Connor
 */
public class DrugForm {

    private Long   id;
    private String name;
    private String code;
    private String description;

    /**
     * Empty constructor for filling in fields without a Drug object.
     */
    public DrugForm () {
    }

    /**
     * Constructs a new form with information from the given drug.
     *
     * @param drug
     *            the drug object
     */
    public DrugForm ( final Drug drug ) {
        setId( drug.getId() );
        setName( drug.getName() );
        setCode( drug.getCode() );
        setDescription( drug.getDescription() );
    }

    /**
     * Sets the drug's id to the given value. All saved drugs must have unique
     * ids.
     *
     * @return the drug id
     */
    public Long getId () {
        return id;
    }

    /**
     * Returns the drug's NDC
     *
     * @return the NDC
     */
    public String getCode () {
        return code;
    }

    /**
     * The name of the drug.
     *
     * @return the drug's name
     */
    public String getName () {
        return name;
    }

    /**
     * Gets this drug's description.
     *
     * @return this description
     */
    public String getDescription () {
        return description;
    }

    /**
     * Sets the id associated with this drug.
     *
     * @param id
     *            the drug's id
     */
    public void setId ( Long id ) {
        this.id = id;
    }

    /**
     * Sets the NDC to the given string. Must be in the format "####-####-##".
     *
     * @param code
     *            the NDC
     */
    public void setCode ( String code ) {
        this.code = code;
    }

    /**
     * Sets the drug name.
     *
     * @param name
     *            the name of the drug
     */
    public void setName ( String name ) {
        this.name = name;
    }

    /**
     * Sets this drug's description to the given value.
     *
     * @param description
     *            the description
     */
    public void setDescription ( String description ) {
        this.description = description;
    }
}
