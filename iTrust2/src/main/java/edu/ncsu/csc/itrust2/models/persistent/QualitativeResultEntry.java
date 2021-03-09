package edu.ncsu.csc.itrust2.models.persistent;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;

/**
 * Represents a single possible values for a Qualitative lab result. A range has
 * a name and stores an {@link edu.ncsu.csc.itrust2.models.persistent.ICDCode}
 * that represents the suggested diagnosis for a patient with this result value.
 *
 * @author Sam Fields
 *
 */
@Embeddable
public class QualitativeResultEntry {

    /**
     * The suggested diagnosis for a result with this value.
     */
    @OneToOne
    private ICDCode icd;

    /**
     * The name of this result
     */
    private String  name;

    /**
     * Empty constructor for Thymeleaf
     */
    public QualitativeResultEntry () {

    }

    /**
     * Creates a new QualitativeResultEntry from a ResultEntry form. The form
     * contains extra fields used for creating
     * {@link edu.ncsu.csc.itrust2.models.persistent.QuantitativeLOINCResult}
     * that are ignored.
     *
     * @param range
     *            the values for the range
     */
    public QualitativeResultEntry ( LOINCForm.ResultEntry range ) {
        setIcd( range.getIcd() );
        setName( range.getName() );
    }

    /**
     * Gets the suggested diagnosis for this value
     *
     * @return the suggested diagnosis for this value
     */
    public ICDCode getIcd () {
        return icd;
    }

    private void setIcd ( String icd ) {
        if ( icd == null ) {
            this.icd = null;
            return;
        }

        final ICDCode code = (ICDCode) ICDCode.getBy( ICDCode.class, "code", icd );

        if ( code == null ) {
            throw new IllegalArgumentException( icd + " is not a valid ICD-10 code." );
        }

        this.icd = code;
    }

    /**
     * Sets the name of this value
     *
     * @param name
     *            the name of this value
     */
    public void setName ( String name ) {
        this.name = name;
    }

    /**
     * Gets the name of this value
     *
     * @return the name of this value
     */
    public String getName () {
        return name;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null ) {
            return false;
        }
        if ( getClass() != o.getClass() ) {
            return false;
        }
        final QualitativeResultEntry other = (QualitativeResultEntry) o;
        if ( !getName().equals( other.getName() ) ) {
            return false;
        }
        return true;
    }

    /**
     * Overriding the hashCode function here allows us to efficiently check for
     * duplicate entries by checking the hashCode. We can't allow the user to
     * specify more than one entry with the same name.
     */
    @Override
    public int hashCode () {
        return name.hashCode();
    }

}
