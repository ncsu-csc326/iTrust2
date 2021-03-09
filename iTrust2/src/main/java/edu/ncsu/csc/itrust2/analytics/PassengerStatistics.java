package edu.ncsu.csc.itrust2.analytics;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.ncsu.csc.itrust2.models.enums.SymptomSeverity;
import edu.ncsu.csc.itrust2.models.persistent.Passenger;

/**
 * Class encompassing all the statistical analysis or breakdown of Passenger
 * data in the system.
 */
public class PassengerStatistics {

    /**
     * Mapping of dates to how many Passengers have each symptom severity level
     * on that day
     */
    private SeverityData            passengersBySeverity;

    /**
     * Mapping of dates to how many new infections occurred on that day
     */
    private Map<LocalDate, Integer> newInfectionsPerDay;

    /**
     * Mapping of dates to how many total infections there were by that point
     */
    private Map<LocalDate, Integer> totalInfectionsPerDay;

    /**
     * Constructs the PassengerStatistics, calculating all statistics and
     * storing them in this class's fields
     */
    public PassengerStatistics () {
        calculateNewInfectionsPerDay();
        calculateTotalInfectionsPerDay();
        calculateInfectedPassengersBySeverity();
    }

    /**
     * Calculates and stores the mapping of dates to total infections by that
     * point
     */
    private void calculateTotalInfectionsPerDay () {

        // initiate map
        this.totalInfectionsPerDay = new TreeMap<LocalDate, Integer>();

        // total count
        int total = 0;

        // If new Infections Per Day is null, calculate it
        if ( this.newInfectionsPerDay == null ) {
            this.calculateNewInfectionsPerDay();
        }

        // iterate through entries, and add totals to map
        for ( final Entry<LocalDate, Integer> entry : newInfectionsPerDay.entrySet() ) {
            total += entry.getValue();
            totalInfectionsPerDay.put( entry.getKey(), total );
        }

    }

    /**
     * Calculates and stores the mapping of dates to number of Passengers with
     * each symptom severity level
     */
    private void calculateInfectedPassengersBySeverity () {
        this.passengersBySeverity = new SeverityData();
    }

    /**
     * Calculates and stores the mapping of dates to new infections on that day
     */
    private void calculateNewInfectionsPerDay () {
        // initiate map
        this.newInfectionsPerDay = new TreeMap<LocalDate, Integer>();

        // get list of all passengers
        final List<Passenger> passengers = Passenger.getPassengers();

        // iterate through list of passengers
        for ( final Passenger p : passengers ) {

            // if passenger is not infected, continue to next
            if ( p.getSymptomSeverity() == SymptomSeverity.NOT_INFECTED ) {
                continue;
            }

            // get date of infection and update map
            final LocalDate date = p.getInitialSymptomArrival().toLocalDate();
            final Integer currCount = newInfectionsPerDay.get( date );

            // update map
            newInfectionsPerDay.put( date, currCount == null ? 1 : currCount + 1 );

        }

        // Get a sorted set of the dates
        final SortedSet<LocalDate> keys = new TreeSet<>( this.newInfectionsPerDay.keySet() );

        LocalDate before = null;
        for ( final LocalDate date : keys ) {
            if ( before == null ) {
                before = date;
                continue;
            }

            // get distance between date and date before
            long days = ChronoUnit.DAYS.between( before, date );

            while ( days > 1 ) {
                newInfectionsPerDay.put( before.plusDays( days - 1 ), 0 );
                days--;
            }
            before = date;

        }

    }

    /**
     * Getter method for the mapping between dates and the number of new
     * infections
     *
     * @return the Map of the new infections per day
     */
    public Map<LocalDate, Integer> getNewInfectionsPerDay () {
        return this.newInfectionsPerDay;
    }

    /**
     * Getter method for the mapping between dates and the number of total
     * infections
     *
     * @return the Map of the total infections per day
     */
    public Map<LocalDate, Integer> getTotalInfectionsPerDay () {
        return this.totalInfectionsPerDay;
    }

    /**
     * Getter for the date list in SeverityData
     *
     * @return the date list
     */
    public List<LocalDate> getDates () {
        return this.passengersBySeverity.dates;
    }

    /**
     * Getter for the not infected list in SeverityData
     *
     * @return the not infected list list
     */
    public List<Integer> getNotInfected () {
        return this.passengersBySeverity.notInfected;
    }

    /**
     * Getter for the mild list in SeverityData
     *
     * @return the mild list
     */
    public List<Integer> getMild () {
        return this.passengersBySeverity.mild;
    }

    /**
     * Getter for the severe list in SeverityData
     *
     * @return the severe list
     */
    public List<Integer> getSevere () {
        return this.passengersBySeverity.severe;
    }

    /**
     * Getter for the critical list in SeverityData
     *
     * @return the critical list
     */
    public List<Integer> getCritical () {
        return this.passengersBySeverity.critical;
    }

    /**
     * Calculates and stores the R0 value for the current Passenger data in the
     * system
     *
     * R0 = average of the daily increase from the first day of infection until
     * the peak day (the day with the most infections)
     *
     * @return double representing the R-Naught Value.
     */
    public static double calculateRNaught () {
        final Map<LocalDate, Integer> newInfectionsByDay = calculateRNaughtHelper();

        // find the date with the most infections (max value in the map)
        LocalDate dateWithMostInfections = null;
        int maxNewInfections = -1;
        for ( final Entry<LocalDate, Integer> entry : newInfectionsByDay.entrySet() ) {
            if ( dateWithMostInfections == null ) {
                dateWithMostInfections = entry.getKey();
                continue;
            }
            if ( entry.getValue() >= maxNewInfections ) {
                dateWithMostInfections = entry.getKey();
                maxNewInfections = entry.getValue();
            }
        }

        // iterate over dates, summing the ratio between days (used for average)
        double totalRatio = 0;
        int numDays = 0;
        Entry<LocalDate, Integer> prevEntry = null;
        for ( final Entry<LocalDate, Integer> entry : newInfectionsByDay.entrySet() ) {
            numDays++;

            // ratio = new / existing
            if ( prevEntry != null ) {
                final int prevInfections = prevEntry.getValue() == 0 ? 1 : prevEntry.getValue();
                final double ratio = (double) entry.getValue() / prevInfections;
                totalRatio += ratio;
            }
            prevEntry = entry;

            // stop once we hit the date with the most infections
            if ( entry.getKey().compareTo( dateWithMostInfections ) == 0 ) {
                break;
            }
        }

        // calculate r0
        double rNaught = 0;
        if ( numDays < 2 ) {
            rNaught = -1;
        }
        else {
            rNaught = totalRatio / ( numDays - 1 );
        }
        return rNaught;
    }

    /**
     * Helper method for calculating R0. Calculates and returns a map of the new
     * infections per day (same as calculateNewInfectionsPerDay())
     *
     * @return map of dates to the total number of infections at that point
     */
    private static Map<LocalDate, Integer> calculateRNaughtHelper () {
        final Map<LocalDate, Integer> map = new TreeMap<LocalDate, Integer>();

        // get list of all passengers
        final List<Passenger> passengers = Passenger.getPassengers();

        // iterate through list of passengers
        for ( final Passenger p : passengers ) {

            // if passenger is not infected, continue to next
            if ( p.getSymptomSeverity() == SymptomSeverity.NOT_INFECTED ) {
                continue;
            }

            // get date of infection and update map
            final LocalDate date = p.getInitialSymptomArrival().toLocalDate();
            final Integer currCount = map.get( date );

            // update map
            map.put( date, currCount == null ? 1 : currCount + 1 );

        }

        // Get a sorted set of the dates
        final SortedSet<LocalDate> keys = new TreeSet<>( map.keySet() );

        LocalDate before = null;
        for ( final LocalDate date : keys ) {
            if ( before == null ) {
                before = date;
                continue;
            }

            // get distance between date and date before
            long days = ChronoUnit.DAYS.between( before, date );

            while ( days > 1 ) {
                map.put( before.plusDays( days - 1 ), 0 );
                days--;
            }
            before = date;
        }

        return map;
    }

    /**
     * Data to send for the passenger severities by date is more complex, so
     * this class encapsulates all the required data.
     *
     * All list fields should be the same length. The indices into these lists
     * are the mappings between the number of passengers with each severity
     * level and the corresponding date.
     *
     * @author caproven
     */
    private class SeverityData {
        /**
         * List of dates for the Passenger data
         */
        private final List<LocalDate> dates;

        /**
         * List of Passenger counts per day that are not infected
         */
        private final List<Integer>   notInfected;

        /**
         * List of Passenger counts per day that have mild symptoms
         */
        private final List<Integer>   mild;

        /**
         * List of Passenger counts per day that have severe symptoms
         */
        private final List<Integer>   severe;

        /**
         * List of Passenger counts per day that have critical symptoms
         */
        private final List<Integer>   critical;

        /**
         * Constructs the SeverityData
         */
        public SeverityData () {
            // If new Infections Per Day is null, calculate it
            if ( newInfectionsPerDay == null ) {
                calculateNewInfectionsPerDay();
            }

            // Initialize fields. The symptom severity lists are initialized as
            // all zeros, with a length corresponding to the number of dates
            dates = new ArrayList<>( newInfectionsPerDay.keySet() );
            notInfected = new ArrayList<>( Collections.nCopies( dates.size(), 0 ) );
            mild = new ArrayList<>( Collections.nCopies( dates.size(), 0 ) );
            severe = new ArrayList<>( Collections.nCopies( dates.size(), 0 ) );
            critical = new ArrayList<>( Collections.nCopies( dates.size(), 0 ) );

            // map dates to their index in the list(s)
            final Map<LocalDate, Integer> dateIndices = new HashMap<>();
            for ( int i = 0; i < dates.size(); i++ ) {
                dateIndices.put( dates.get( i ), i );
            }

            final List<Passenger> passengers = Passenger.getPassengers();

            for ( final Passenger p : passengers ) {
                // dates not stored for passengers that aren't infected, so skip
                // over them
                if ( p.getSymptomSeverity() == SymptomSeverity.NOT_INFECTED ) {
                    continue;
                }

                final LocalDate infectionDate = p.getInitialSymptomArrival().toLocalDate();
                final int dateIndex = dateIndices.get( infectionDate );
                // increment the number of patients by severity per day
                switch ( p.getSymptomSeverity() ) {
                    case MILD:
                        mild.set( dateIndex, mild.get( dateIndex ) + 1 );
                        break;
                    case SEVERE:
                        severe.set( dateIndex, severe.get( dateIndex ) + 1 );
                        break;
                    case CRITICAL:
                        critical.set( dateIndex, critical.get( dateIndex ) + 1 );
                        break;
                    default:
                        break;
                }
            }

            // Severity counts should be a total of all afflicted passengers up
            // to that point, not just the new cases per day. Iterate over lists
            // and ensure they represent the totals
            //
            // Additionally, we must use this information to calculate the
            // number of passengers that are not infected on each day. These
            // passengers do not have dates associated with them, meaning we
            // must use the other totals to find this value
            int totalMild = 0;
            int totalSevere = 0;
            int totalCritical = 0;
            for ( int i = 0; i < dates.size(); i++ ) {

                totalMild += mild.get( i );
                mild.set( i, totalMild );

                totalSevere += severe.get( i );
                severe.set( i, totalSevere );

                totalCritical += critical.get( i );
                critical.set( i, totalCritical );

                final int totalNotInfected = passengers.size() - ( totalMild + totalSevere + totalCritical );
                notInfected.set( i, totalNotInfected );
            }
        }
    }
}
