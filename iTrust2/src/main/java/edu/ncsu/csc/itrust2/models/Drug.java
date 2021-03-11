package edu.ncsu.csc.iTrust2.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.iTrust2.forms.DrugForm;

/**
 * Represents a drug in the NDC format.
 *
 * @author Connor
 * @author Kai Presler-Marshall
 */
@Entity
public class Drug extends DomainObject {

    /** For Hibernate/Thymeleaf _must_ be an empty constructor */
    public Drug () {
    }

    /**
     * Constructs a new form from the details in the given form
     *
     * @param form
     *            the form to base the new drug on
     */
    public Drug ( final DrugForm form ) {
        setId( form.getId() );
        setCode( form.getCode() );
        setName( form.getName() );
        setDescription( form.getDescription() );
    }

    @Id
    @GeneratedValue ( strategy = GenerationType.AUTO )
    private Long   id;

    @Pattern ( regexp = "^\\d{4}-\\d{4}-\\d{2}$" )
    private String code;

    @NotEmpty
    @Length ( max = 64 )
    private String name;

    @NotNull
    @Length ( max = 1024 )
    private String description;

    /**
     * Sets the drug's id to the given value. All saved drugs must have unique
     * ids.
     *
     * @param id
     *            the new id
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns the id associated with this drug.
     *
     * @return the id
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
     * Sets the NDC to the given string. Must be in the format "####-####-##".
     *
     * @param code
     *            the NDC
     */
    public void setCode ( final String code ) {
        this.code = code;
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
     * Sets the drug name.
     *
     * @param name
     *            the name of the drug
     */
    public void setName ( final String name ) {
        this.name = name;
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
     * Sets this drug's description to the given value.
     *
     * @param description
     *            the description
     */
    public void setDescription ( final String description ) {
        this.description = description;
    }

}
