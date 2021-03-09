package edu.ncsu.csc.itrust2.models.persistent;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entry in the Zip Code Database
 *
 * @author nrshah4
 *
 */
@Entity
@Table ( name = "ZipCodes" )
public class ZipCodeEntry extends DomainObject<ZipCodeEntry> {

    /**
     * Get entry by Zip code
     *
     * @param zip
     *            the zip code
     * @return zip code entry
     */
    public static ZipCodeEntry getByZip ( final String zip ) {
        return (ZipCodeEntry) ZipCodeEntry.getById( ZipCodeEntry.class, zip );
    }

    /**
     * Calculate distance between two zips. Source: zips.sourceforge.net
     *
     * @param zipA
     *            zip code 1
     * @param zipB
     *            zip code 2
     * @return distance
     */
    public static double calculateDistance ( final ZipCodeEntry zipA, final ZipCodeEntry zipB ) {
        final double latA = zipA.getLatitude();
        final double latB = zipB.getLatitude();
        final double longA = zipA.getLongitude();
        final double longB = zipB.getLongitude();
        final double theDistance = ( Math.sin( Math.toRadians( latA ) ) * Math.sin( Math.toRadians( latB ) )
                + Math.cos( Math.toRadians( latA ) ) * Math.cos( Math.toRadians( latB ) )
                        * Math.cos( Math.toRadians( longA - longB ) ) );

        return Math.round( 10 * ( Math.toDegrees( Math.acos( theDistance ) ) ) * 69.09 ) / 10.0;
    }

    /**
     * zip code
     */
    @Id
    private String zip;

    /**
     * Latitude coordinate
     */

    private double latitude;

    /**
     * Longitude coordinate
     */

    private double longitude;

    /**
     * Empty constructor.
     */
    public ZipCodeEntry () {

    }

    /**
     * Constructor.
     *
     * @param zip
     *            zip code
     * @param lat
     *            latitude coord.
     * @param longitude
     *            longitude coord.
     */
    public ZipCodeEntry ( final String zip, final double lat, final double longitude ) {
        this.zip = zip;
        this.latitude = lat;
        this.longitude = longitude;
    }

    /**
     * Get id
     *
     * @return zip code.
     */
    @Override
    public Serializable getId () {
        return zip;
    }

    /**
     * set zip
     *
     * @param zip
     *            the zip code to set
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * get zip
     *
     * @return zip code
     */
    public String getZip () {
        return this.zip;
    }

    /**
     * set lat
     *
     * @param latitude
     *            the latitude coordinate
     */
    public void setLatitude ( final double latitude ) {
        this.latitude = latitude;
    }

    /**
     * Get lat.
     *
     * @return latitude
     */
    public double getLatitude () {

        return this.latitude;
    }

    /**
     * set long
     *
     * @param longitude
     *            the longitude coordinate
     */
    public void setLongitude ( final double longitude ) {
        this.longitude = longitude;
    }

    /**
     * Get long.
     *
     * @return longitude
     */
    public double getLongitude () {

        return this.longitude;
    }

    /**
     * Get all the zip codes
     *
     * @return list of zip code entries
     */
    @SuppressWarnings ( "unchecked" )
    public static List<ZipCodeEntry> getAll () {
        return (List<ZipCodeEntry>) DomainObject.getAll( ZipCodeEntry.class );
    }

}
