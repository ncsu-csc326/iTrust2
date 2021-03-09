package edu.ncsu.csc.itrust2.forms.findexperts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Form that contains the Table of Experts from the Find Experts search.
 *
 * @author nrshah4
 *
 */
public class ExpertTable {

    /** list of entries in table */
    List<ExpertTableEntry> entries;

    /**
     * Constructor creates empty table.
     */
    public ExpertTable () {
        entries = new ArrayList<ExpertTableEntry>();
    }

    /**
     * Add an entry to the table
     *
     * @param entry
     *            the entry to add
     */
    public void addEntry ( final ExpertTableEntry entry ) {
        this.entries.add( entry );
    }

    /**
     * Sort entries by calculated distance
     *
     * @return sorted table
     */
    public List<ExpertTableEntry> sortTable () {

        Collections.sort( entries, new EntryComparator() );

        return entries;

    }

    /**
     * Getter for the list of entries
     *
     * @return entries
     */
    public List<ExpertTableEntry> getEntries () {
        return entries;
    }

    /**
     * Comparator that will be used to compare entries by distance.
     *
     * @author nrshah4
     *
     */
    public class EntryComparator implements Comparator<ExpertTableEntry> {

        /**
         * compare two entries by distance
         *
         * @param o1
         *            entry 1
         * @param o2
         *            entry 2
         * @return 1 if o1.distance is greater than o2.distance, -1 if
         *         o1.distance less than o2.distance, 0 otherwise
         */
        @Override
        public int compare ( final ExpertTableEntry o1, final ExpertTableEntry o2 ) {
            if ( o1.getDistance() > o2.getDistance() ) {
                return 1;
            }
            if ( o1.getDistance() < o2.getDistance() ) {
                return -1;
            }
            return 0;
        }
    }

}
