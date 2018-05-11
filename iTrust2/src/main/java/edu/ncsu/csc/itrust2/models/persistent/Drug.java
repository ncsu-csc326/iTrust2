package edu.ncsu.csc.itrust2.models.persistent;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.criterion.Criterion;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import edu.ncsu.csc.itrust2.forms.admin.DrugForm;

/**
 * Represents a drug in the NDC format.
 *
 * @author Connor
 * @author Kai Presler-Marshall
 */
@Entity
@Table ( name = "Drugs" )
public class Drug extends DomainObject<Drug> {

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
    @Override
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

    /**
     * Gets a list of drugs that match the given query.
     *
     * @param where
     *            List of Criterion to and together and search for records by
     * @return the collection of matching drugs
     */
    @SuppressWarnings ( "unchecked" )
    private static List<Drug> getWhere ( final List<Criterion> where ) {
        return (List<Drug>) getWhere( Drug.class, where );
    }

    /**
     * Returns the drug whose id matches the given value.
     *
     * @param id
     *            the id to search for
     * @return the matching drug or null if none is found
     */
    public static Drug getById ( final Long id ) {
        try {
            return getWhere( createCriterionAsList( ID, id ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Gets the drug with the code matching the given value. Returns null if
     * none found.
     *
     * @param code
     *            the code to search for
     * @return the matching drug
     */
    public static Drug getByCode ( final String code ) {
        try {
            return getWhere( createCriterionAsList( "code", code ) ).get( 0 );
        }
        catch ( final Exception e ) {
            return null;
        }
    }

    /**
     * Collects and returns all drugs in the system
     *
     * @return all saved drugs
     */
    @SuppressWarnings ( "unchecked" )
    public static List<Drug> getAll () {
        return (List<Drug>) DomainObject.getAll( Drug.class );
    }

}
