package edu.uwm.ibidder.dbaccess.models;


/**
 * Represents a bid
 */
public class BidModel {

    private String Description;
    private double value;

    /**
     * Default Constructor.  Required for firebase serialization.
     */
    BidModel() {

    }

    public String getDescription() {
        return Description;
    }

    public double getValue() {
        return value;
    }
}
