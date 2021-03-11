package edu.ncsu.csc.iTrust2.controllers.api.comm;

/**
 * A class representing the 4 string values in a row of the Log Entry view.
 *
 * @author Bryce
 *
 */
public class LogEntryTableRow {

    /** Name of primary User */
    private String  primary;
    /** Name of secondary User */
    private String  secondary;
    /** Role of accessor */
    private String  role;
    /** Date and time of transaction */
    private String  dateTime;
    /** Transaction type of log event */
    private String  transactionType;
    /** boolean for whether or not being viewed by patient */
    private boolean isPatient = false;
    /** total number of pages in the table */
    private int     numPages  = 1;

    /**
     * Empty constructor so that Spring is able to use this class for
     * communicating over http
     */
    public LogEntryTableRow () {
        // Empty Constructor
    }

    /**
     * Returns the number of pages in the table
     *
     * @return number of pages
     */
    public int getNumPages () {
        return numPages;
    }

    /**
     * Sets the number of pages in the table
     *
     * @param numPages
     *            number of pages
     */
    public void setNumPages ( final int numPages ) {
        this.numPages = numPages;
    }

    /**
     * Returns whether or not this table row is being viewed by a patient
     *
     * @return is/isnt viewed by patient
     */
    public boolean isPatient () {
        return isPatient;
    }

    /**
     * Sets whether or not this table row is being viewed by a patient
     *
     * @param isPatient
     *            is/isnt viewed by patient
     *
     */
    public void setPatient ( final boolean isPatient ) {
        this.isPatient = isPatient;
    }

    /**
     * Returns the name of the primary user
     *
     * @return name of primary user
     */
    public String getPrimary () {
        return primary;
    }

    /**
     * Sets the name of the primary user row
     *
     * @param primary
     *            name of primary user
     */
    public void setPrimary ( final String primary ) {
        this.primary = primary;
    }

    /**
     * Returns the name of the secondary user
     *
     * @return name of secondary user
     */
    public String getSecondary () {
        return secondary;
    }

    /**
     * Sets the name of the secondary user
     *
     * @param secondary
     *            name of secondary user
     */
    public void setSecondary ( final String secondary ) {
        this.secondary = secondary;
    }

    /**
     * Returns the role of the non-patient user
     *
     * @return role of user
     */
    public String getRole () {
        return role;
    }

    /**
     * Sets the role of the non-patient user
     *
     * @param role
     *            name of role
     */
    public void setRole ( final String role ) {
        this.role = role;
    }

    /**
     * Gets the date/time of the transaction in the log entry.
     *
     * @return date/time of transaction
     */
    public String getDateTime () {
        return dateTime;
    }

    /**
     * Sets the date/time of the transaction in the log entry.
     *
     * @param dateTime
     *            date/time of transaction
     */
    public void setDateTime ( final String dateTime ) {
        this.dateTime = dateTime;
    }

    /**
     * Gets the type of the transaction in the log entry.
     *
     * @return type of transaction
     */
    public String getTransactionType () {
        return transactionType;
    }

    /**
     * Sets the type of the transaction in the log entry.
     *
     * @param transactionType
     *            type of transaction
     */
    public void setTransactionType ( final String transactionType ) {
        this.transactionType = transactionType;
    }

}
