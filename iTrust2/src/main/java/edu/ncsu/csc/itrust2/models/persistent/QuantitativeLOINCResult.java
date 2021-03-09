package edu.ncsu.csc.itrust2.models.persistent;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;

/**
 * Represents the format of a quantitative result for a LOINC code. This
 * specifies the range of values that a lab tech can enter when entering the
 * results of a LabProcedure. A quantitative result is anything that can be
 * specified using a range of numbers, and can be discrete or continuous.
 *
 * @author Sam Fields
 *
 */
@Entity
public class QuantitativeLOINCResult extends LOINCResult {

    /**
     * Stores the ranges the result value can fall into. A valid result must be
     * in one of the specified ranges. Each range has a minimum and maximum
     * value, and a ICD code that represents the suggested diagnosis for a
     * patient with a value in that range.
     */
    @ElementCollection ( fetch = FetchType.EAGER )
    @Fetch ( value = FetchMode.SUBSELECT )
    private List<QuantitativeResultRange> resultRanges;

    /**
     * Empty constructor for Thymeleaf
     */
    public QuantitativeLOINCResult () {

    }

    /**
     * Creates a new QuantitativeLOINCResult from a list of entries. Each entry
     * represents a single range.
     *
     * @param resultEntries
     *            the ranges a result can fall into
     */
    public QuantitativeLOINCResult ( List<LOINCForm.ResultEntry> resultEntries ) {

        if ( resultEntries == null || resultEntries.size() == 0 ) {
            throw new IllegalArgumentException( "Result must have at least one range." );
        }

        resultRanges = new ArrayList<QuantitativeResultRange>();

        for ( final LOINCForm.ResultEntry entry : resultEntries ) {
            final QuantitativeResultRange range = new QuantitativeResultRange( entry );

            // Check for duplicates
            if ( isDuplicateRange( range ) ) {
                throw new IllegalArgumentException( "Cannot add multiple ranges with overlapping values." );
            }

            resultRanges.add( range );
        }
    }

    @Override
    public ICDCode getDiagnosisForResult ( String result ) {
        try {
            final Float resultValue = Float.valueOf( result );

            for ( final QuantitativeResultRange range : resultRanges ) {
                if ( range.contains( resultValue ) ) {
                    return range.getIcd();
                }
            }

            // Value doesn't fit any range
            throw new IllegalArgumentException( "Invalid result value" );
        }
        catch ( final NumberFormatException e ) {
            throw new IllegalArgumentException( "Quantitative result must be parsable as a float." );
        }
    }

    /**
     * This is a horrible O(n^2) solution for looking for overlapping ranges,
     * but a more efficient solution would require implementing a new data
     * structure, namely a Interval Tree.
     *
     * @param range
     *            the range to check
     * @return whether or not the range is a duplicate
     */
    private boolean isDuplicateRange ( QuantitativeResultRange range ) {
        for ( final QuantitativeResultRange existingRange : resultRanges ) {
            if ( rangeOverlaps( range, existingRange ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean rangeOverlaps ( QuantitativeResultRange range1, QuantitativeResultRange range2 ) {
        return range1.getMin() <= range2.getMax() && range2.getMin() <= range1.getMax();
    }
}
