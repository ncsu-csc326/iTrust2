package edu.ncsu.csc.itrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.itrust2.analytics.PassengerStatistics;
import edu.ncsu.csc.itrust2.models.persistent.Passenger;

/**
 * Class to test the Passenger Statistics plots and R0 value
 *
 * @author Leon Li
 * @author Christian Provenzano
 */
public class PassengerStatisticsTest {

    /**
     * Setup method to run before test methods - reset the Passenger table in
     * the DB
     */
    @Before
    public void setup () {
        Passenger.deleteAll();
    }

    /**
     * Loads Passengers from the given file in the "zipdata" directory
     *
     * @param fileName
     *            name of the file to parse
     */
    private void loadFile ( final String fileName ) {
        Passenger.parse( PassengerTest.getFileContents( fileName ) );
    }

    /**
     * Loads Passengers from the given file in the "zipdata" directory then
     * returns the PassengerStatistics instance for it
     *
     * @param fileName
     *            name of the file to parse
     * @return the PassengerStatistics for the parsed data
     */
    private PassengerStatistics getStatisticsForFile ( final String fileName ) {
        loadFile( fileName );
        return new PassengerStatistics();
    }

    /**
     * Tests the mapping of dates to the number of new infections on that day
     */
    @Test
    public void testNewInfectionsPerDay () {
        final PassengerStatistics stats = getStatisticsForFile( "rnaught_equalto_one.csv" );
        final Map<LocalDate, Integer> newInfectionsMap = stats.getNewInfectionsPerDay();

        assertEquals( 3, newInfectionsMap.size() );
        assertTrue( newInfectionsMap.get( LocalDate.parse( "2020-02-13" ) ) == 4 );
        assertTrue( newInfectionsMap.get( LocalDate.parse( "2020-02-14" ) ) == 4 );
        assertTrue( newInfectionsMap.get( LocalDate.parse( "2020-02-15" ) ) == 4 );
    }

    /**
     * Tests the mapping of dates to the number of new infections on that day,
     * when passengers are in the system but none are symptomatic
     */
    @Test
    public void testNewInfectionsPerDayNoneInfected () {
        final PassengerStatistics stats = getStatisticsForFile( "passenger-data-none_infected.csv" );
        final Map<LocalDate, Integer> newInfectionsMap = stats.getNewInfectionsPerDay();

        assertEquals( 0, newInfectionsMap.size() );
    }

    /**
     * Tests the mapping of dates to the number of new infections on that day,
     * when there are no passengers in the system
     */
    @Test
    public void testNewInfectionsPerDayNoPassengers () {
        final PassengerStatistics stats = new PassengerStatistics();
        final Map<LocalDate, Integer> newInfectionsMap = stats.getNewInfectionsPerDay();

        assertEquals( 0, newInfectionsMap.size() );
    }

    /**
     * Tests the mapping of dates to the number of total infections on that day
     */
    @Test
    public void testTotalInfectionsPerDay () {
        final PassengerStatistics stats = getStatisticsForFile( "rnaught_equalto_one.csv" );
        final Map<LocalDate, Integer> totalInfectionsMap = stats.getTotalInfectionsPerDay();

        assertEquals( 3, totalInfectionsMap.size() );
        assertTrue( totalInfectionsMap.get( LocalDate.parse( "2020-02-13" ) ) == 4 );
        assertTrue( totalInfectionsMap.get( LocalDate.parse( "2020-02-14" ) ) == 8 );
        assertTrue( totalInfectionsMap.get( LocalDate.parse( "2020-02-15" ) ) == 12 );
    }

    /**
     * Tests the mapping of dates to the number of total infections on that day,
     * when passengers are in the system but none are symptomatic
     */
    @Test
    public void testTotalInfectionsPerDayNoneInfected () {
        final PassengerStatistics stats = getStatisticsForFile( "passenger-data-none_infected.csv" );
        final Map<LocalDate, Integer> totalInfectionsMap = stats.getTotalInfectionsPerDay();

        assertEquals( 0, totalInfectionsMap.size() );
    }

    /**
     * Tests the mapping of dates to the number of total infections on that day,
     * when there are no passengers in the system
     */
    @Test
    public void testTotalInfectionsPerDayNoPassengers () {
        final PassengerStatistics stats = new PassengerStatistics();
        final Map<LocalDate, Integer> totalInfectionsMap = stats.getTotalInfectionsPerDay();

        assertEquals( 0, totalInfectionsMap.size() );
    }

    /**
     * Tests the mapping of dates to the breakdown of symptom severities the
     * total number of infections on that day
     */
    @Test
    public void testInfectionsBySeverity () {
        final PassengerStatistics stats = getStatisticsForFile( "rnaught_equalto_one.csv" );
        final List<LocalDate> dates = stats.getDates();
        final List<Integer> notInfected = stats.getNotInfected();
        final List<Integer> mild = stats.getMild();
        final List<Integer> severe = stats.getSevere();
        final List<Integer> critical = stats.getCritical();

        // assert that the dates are correct
        assertEquals( 3, dates.size() );
        assertEquals( LocalDate.parse( "2020-02-13" ), dates.get( 0 ) );
        assertEquals( LocalDate.parse( "2020-02-14" ), dates.get( 1 ) );
        assertEquals( LocalDate.parse( "2020-02-15" ), dates.get( 2 ) );

        // assert that the count of not infected passengers is correct
        assertEquals( 3, notInfected.size() );
        assertTrue( notInfected.get( 0 ) == 8 );
        assertTrue( notInfected.get( 1 ) == 4 );
        assertTrue( notInfected.get( 2 ) == 0 );

        // assert that the count of mild symptom passengers is correct
        assertEquals( 3, mild.size() );
        assertTrue( mild.get( 0 ) == 2 );
        assertTrue( mild.get( 1 ) == 6 );
        assertTrue( mild.get( 2 ) == 8 );

        // assert that the count of severe symptom passengers is correct
        assertEquals( 3, severe.size() );
        assertTrue( severe.get( 0 ) == 2 );
        assertTrue( severe.get( 1 ) == 2 );
        assertTrue( severe.get( 2 ) == 3 );

        // assert that the count of critical symptom passengers is correct
        assertEquals( 3, critical.size() );
        assertTrue( critical.get( 0 ) == 0 );
        assertTrue( critical.get( 1 ) == 0 );
        assertTrue( critical.get( 2 ) == 1 );
    }

    /**
     * Tests the mapping of dates to the breakdown of symptom severities the
     * total number of infections on that day, when passengers are in the system
     * but none are symptomatic
     */
    @Test
    public void testInfectionsBySeverityNoneInfected () {
        final PassengerStatistics stats = getStatisticsForFile( "passenger-data-none_infected.csv" );
        final List<LocalDate> dates = stats.getDates();
        final List<Integer> notInfected = stats.getNotInfected();
        final List<Integer> mild = stats.getMild();
        final List<Integer> severe = stats.getSevere();
        final List<Integer> critical = stats.getCritical();

        assertEquals( 0, dates.size() );
        assertEquals( 0, notInfected.size() );
        assertEquals( 0, mild.size() );
        assertEquals( 0, severe.size() );
        assertEquals( 0, critical.size() );
    }

    /**
     * Tests the mapping of dates to the breakdown of symptom severities the
     * total number of infections on that day, when there are no passengers in
     * the system
     */
    @Test
    public void testInfectionsBySeverityNoPassengers () {
        final PassengerStatistics stats = new PassengerStatistics();
        final List<LocalDate> dates = stats.getDates();
        final List<Integer> notInfected = stats.getNotInfected();
        final List<Integer> mild = stats.getMild();
        final List<Integer> severe = stats.getSevere();
        final List<Integer> critical = stats.getCritical();

        assertEquals( 0, dates.size() );
        assertEquals( 0, notInfected.size() );
        assertEquals( 0, mild.size() );
        assertEquals( 0, severe.size() );
        assertEquals( 0, critical.size() );
    }

    /**
     * Tests the calculated R0 value, a reading of how quickly the infection is
     * spreading
     */
    @Test
    public void testRNaught () {
        // test a file with an R0 value = 1
        Passenger.deleteAll();
        loadFile( "rnaught_equalto_one.csv" );
        assertEquals( 1.0, PassengerStatistics.calculateRNaught(), 0.01 );

        // test a file with an R0 value > 1
        Passenger.deleteAll();
        loadFile( "rnaught_greaterthan_one.csv" );
        assertEquals( 2.0, PassengerStatistics.calculateRNaught(), 0.01 );

        // test a file with a single date - cannot calculate R0
        Passenger.deleteAll();
        loadFile( "rnaught_invalid.csv" );
        assertEquals( -1.0, PassengerStatistics.calculateRNaught(), 0.01 );

        // test a file with no date - cannot calculate R0
        Passenger.deleteAll();
        loadFile( "passenger-data-none_infected.csv" );
        assertEquals( -1.0, PassengerStatistics.calculateRNaught(), 0.01 );
    }

}
