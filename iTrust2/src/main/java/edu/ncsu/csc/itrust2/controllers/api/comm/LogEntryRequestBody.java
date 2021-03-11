package edu.ncsu.csc.iTrust2.controllers.api.comm;

/**
 * A POJO class that represents the request body of a GET request for log
 * entries Spring automatically converts the JSON into this object.
 *
 * @author Bryce
 *
 */
public class LogEntryRequestBody {

    /** Start date to retrieve log entries from */
    public String startDate;

    /** End date to retrieve log entries to */
    public String endDate;

    /** Current page number */
    public int    page;
    /** Number of items per page */
    public int    pageLength;

    /**
     * Empty Constructor required for spring to use this as a RequestBody
     */
    public LogEntryRequestBody () {
        // Empty Constructor required for spring to use this
    }

    /**
     * Gets the start date to retrieve log entries from.
     *
     * @return start date for log entry search
     */
    public String getStartDate () {
        return startDate;
    }

    /**
     * Sets the start date to retrieve log entries from.
     *
     * @param startDate
     *            for log entry search
     */
    public void setStartDate ( final String startDate ) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date to retrieve log entries from.
     *
     * @return end date for log entry search
     */
    public String getEndDate () {
        return endDate;
    }

    /**
     * Sets the end date to retrieve log entries from.
     *
     * @param endDate
     *            for log entry search
     */
    public void setEndDate ( final String endDate ) {
        this.endDate = endDate;
    }

    /**
     * Gets the page number on the list of log entries.
     *
     * @return page number integer
     */
    public int getPage () {
        return page;
    }

    /**
     * Sets the page number on the list of log entries.
     *
     * @param page
     *            number integer
     */
    public void setPage ( final int page ) {
        this.page = page;
    }

    /**
     * Gets the length of the page.
     *
     * @return page length
     */
    public int getPageLength () {
        return pageLength;
    }

    /**
     * Sets the length of the page.
     *
     * @param pageLength
     *            length of page
     */
    public void setPageLength ( final int pageLength ) {
        this.pageLength = pageLength;
    }

}
