package edu.uwm.ibidder.dbaccess.models;


/**
 * Represents a bid
 */
public class BidModel {

    private String bidderId;
    private String taskId;
    private double bidValue;
    private String bidId;

    /**
     * Default Constructor.  Required for firebase serialization.
     */
    public BidModel() {

    }

    public String getBidderId() {
        return bidderId;
    }

    public void setBidderId(String bidderId) {
        this.bidderId = bidderId;
    }

    public double getBidValue() {
        return bidValue;
    }

    public void setBidValue(double bidValue) {
        this.bidValue = bidValue;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }
}
