package edu.ncsu.csc.itrust2.managers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc.itrust2.models.persistent.ZipCodeEntry;

/**
 * Manager that holds zip code database and calculates distances.
 *
 * @author nrshah4
 *
 */
public class ZipCodeManager {

    /** singleton instance of manager */
    private static ZipCodeManager manager;

    /**
     * Private constructor
     */
    private ZipCodeManager () {

    }

    /**
     * Get Instance of manager
     *
     * @return manager
     */
    public static ZipCodeManager getInstance () {
        if ( manager == null ) {
            manager = new ZipCodeManager();
        }
        return manager;

    }

    /**
     * Load database from csv and save to hibernate.
     *
     * @param pathname
     *            path to csv file.
     *
     * @throws IOException
     */
    public void loadDatabase ( final String pathname ) throws IOException {
        final BufferedReader csv = new BufferedReader( new FileReader( pathname ) );

        final List<ZipCodeEntry> newEntries = new ArrayList<>();

        String row = csv.readLine(); // read titles
        row = csv.readLine();
        while ( row != null ) {
            final String[] data = row.split( "," );
            for ( int i = 0; i < data.length; i++ ) {
                final StringBuilder d = new StringBuilder( data[i] );
                int j = 0;
                while ( j < d.length() ) {
                    final char c = d.charAt( j );
                    if ( c == '"' ) {

                        d.deleteCharAt( j );
                        j--;
                    }

                    j++;
                }
                data[i] = d.toString();
            }
            final String zip = data[0];
            final double latitude = Double.parseDouble( data[2] );
            final double longitude = Double.parseDouble( data[3] );
            final ZipCodeEntry entry = new ZipCodeEntry( zip, latitude, longitude );
            newEntries.add( entry );

            // read next line
            row = csv.readLine();

        }
        csv.close();

        // save entries in database
        ZipCodeEntry.saveAll( newEntries );
    }

    /**
     * Check if a database has been loaded
     *
     * @return database
     */
    public boolean checkDatabase () {
        return ZipCodeEntry.getAll().size() > 0;
    }

    /**
     * Clear the database
     */
    public void clearDatabase () {
        ZipCodeEntry.deleteAll( ZipCodeEntry.class );
    }

    /**
     * Get the database
     *
     * @return zip code entries
     */
    public List<ZipCodeEntry> getDatabase () {
        return ZipCodeEntry.getAll();
    }

}
