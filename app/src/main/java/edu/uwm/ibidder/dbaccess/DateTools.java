package edu.uwm.ibidder.dbaccess;


import java.util.Date;

/**
 * Tools used to convert dates to epoch and epochs to dates.
 * The epochs must be in SECONDS.  NOT the standard Java Milliseconds.
 */
public class DateTools {

    /**
     * Converts a date to the epoch time and returns it.
     *
     * @param date The date to convert to epoch
     * @return The date in epoch form.
     */
    public static long dateToEpoch(Date date) {
        return date.getTime() / 1000;
    }

    /**
     * Converts an epoch to a date.
     *
     * @param epoch The epoch.
     * @return The date.
     */
    public static Date epochToDate(long epoch) {
        return new Date(epoch * 1000);
    }
}
