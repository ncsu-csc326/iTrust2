package edu.ncsu.csc.itrust2.models.persistent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import edu.ncsu.csc.itrust2.forms.admin.LOINCForm;

/**
 * Represents the format of a qualitative result for a LOINC code. This
 * specifies the range of values that a lab tech can enter when entering the
 * results of a LabProcedure. A qualitative result is anything that can be
 * specified using words, and can be nominal or ordinal.
 *
 * @author Sam Fields
 *
 */
@Entity
public class QualitativeLOINCResult extends LOINCResult {

    /**
     * Stores all possible values a qualitative result can have. A valid result
     * must be one of the specified values. Each result has a name and a
     * suggested diagnosis for patients with that result.
     */
    @ElementCollection ( fetch = FetchType.EAGER )
    @Fetch ( value = FetchMode.SUBSELECT )
    private List<QualitativeResultEntry> resultEntries;

    /**
     * Empty constructor for Thymeleaf
     */
    public QualitativeLOINCResult () {

    }

    /**
     * Creates a new QualitativeLOINCResult from a list of entries. Each entry
     * represents a single value.
     *
     * @param formEntries
     *            the possible values the result can have
     */
    public QualitativeLOINCResult ( List<LOINCForm.ResultEntry> formEntries ) {

        if ( formEntries == null || formEntries.size() == 0 ) {
            throw new IllegalArgumentException( "Result must have at least one entry." );
        }

        resultEntries = new ArrayList<QualitativeResultEntry>();

        // We're using this to efficiently check for duplicate entries
        final Set<QualitativeResultEntry> entrySet = new HashSet<QualitativeResultEntry>();

        for ( final LOINCForm.ResultEntry formEntry : formEntries ) {
            final QualitativeResultEntry entry = new QualitativeResultEntry( formEntry );

            if ( !entrySet.contains( entry ) ) {
                resultEntries.add( entry );
                entrySet.add( entry );
            }
            else {
                throw new IllegalArgumentException( "Cannot add multiple values with duplicate names." );
            }

        }
    }

    @Override
    public ICDCode getDiagnosisForResult ( String result ) {
        for ( final QualitativeResultEntry entry : resultEntries ) {
            if ( entry.getName().equals( result ) ) {
                return entry.getIcd();
            }
        }

        // No matching result found
        throw new IllegalArgumentException( "Invalid result value" );
    }
}
