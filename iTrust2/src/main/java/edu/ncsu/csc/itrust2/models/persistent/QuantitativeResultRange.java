package edu.ncsu.csc.itrust2.models.persistent;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;

/**
 * Represents a single range of possible values for a Quantitative lab result. A
 * range has a minimum and maximum value, and stores an
 * {@link edu.ncsu.csc.itrust2.models.persistent.ICDCode} that represents the
 * suggested diagnosis for a patient with a lab result in this range.
 *
 * @author Sam Fields
 *
 */
@Embeddable
public class QuantitativeResultRange {

    /**
     * The suggested diagnosis for a result in this range.
     */
    @OneToOne
    private ICDCode icd;

    /**
     * The minimum value of the range
     */
    private Float   min;

    /**
     * The maximum value of the range
     */
    private Float   max;

    /**
     * Empty constructor for Thymeleaf
     */
    public QuantitativeResultRange () {

    }

    /**
     * Creates a new QuantitativeResultRange from a ResultEntry form. The form
     * contains extra fields used for creating
     * {@link edu.ncsu.csc.itrust2.models.persistent.QualitativeLOINCResult}
     * that are ignored.
     *
     * @param range
     *            the values for the range
     */
    public QuantitativeResultRange ( LOINCForm.ResultEntry range ) {
        setIcd( range.getIcd() );
        setMin( range.getMin() );
        setMax( range.getMax() );
        validateRange();
    }

    /**
     * Validates that the range minimum and maximum are valid.
     */
    private void validateRange () {
        if ( this.min >= this.max ) {
            throw new IllegalArgumentException( "Range minimum must be less than the range maximum." );
        }
    }

    /**
     * Returns whether or not the provided value is in this range
     *
     * @param value
     *            the value to check for in the range
     * @return whether or not the value is in the range
     */
    public boolean contains ( Float value ) {
        return value >= min && value <= max;
    }

    /**
     * Gets the suggested diagnosis for this range
     *
     * @return the suggested diagnosis for this range
     */
    public ICDCode getIcd () {
        return icd;
    }

    /**
     * Gets the minimum value of the range
     *
     * @return the minimum value of the range
     */
    public Float getMin () {
        return min;
    }

    /**
     * Gets the maximum value of the range
     *
     * @return the maximum value of the range
     */
    public Float getMax () {
        return max;
    }

    /**
     * Sets the suggested diagnosis for this range
     *
     * @param icd
     *            the suggested diagnosis for this range
     */
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
     * Sets the minimum value for this range
     *
     * @param min
     *            the minimum value for this range
     */
    private void setMin ( String min ) {
        try {
            final Float minValue = Float.valueOf( min );
            this.min = minValue;
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Invalid minimum for result range: " + min );
        }
        catch ( final NullPointerException e ) {
            throw new IllegalArgumentException( "Range minimum cannot be null" );
        }
    }

    /**
     * Sets the maximum value for this range
     *
     * @param max
     *            the maximum value for this range
     */
    private void setMax ( String max ) {
        try {
            final Float maxValue = Float.valueOf( max );
            this.max = maxValue;
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Invalid maximum for result range: " + max );
        }
        catch ( final NullPointerException e ) {
            throw new IllegalArgumentException( "Range maximum cannot be null" );
        }
    }
}
