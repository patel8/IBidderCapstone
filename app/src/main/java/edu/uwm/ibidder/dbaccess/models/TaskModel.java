package edu.uwm.ibidder.dbaccess.models;


import java.util.Date;
import java.util.Map;

/**
 * Represents a task
 */
public class TaskModel {

    private String description;
    private String status; //The status of this task- use the status enum
    private double maxPrice;
    private Date expirationTime;
    private String ownerId;
    private String title;
    private boolean isTaskItNow;
    private boolean isLocalTask; //True if the task requires someone to be in a physical location.
    private Map<String, Boolean> bidIds; //The ids for bids on this task.  Keep this as a new dictionary when modifying or creating tasks

    /**
     * Default constructor.  Needed for firebase serialization.
     */
    public TaskModel() {

    }

    public boolean isLocalTask() {
        return isLocalTask;
    }

    public void setLocalTask(boolean localTask) {
        isLocalTask = localTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTaskItNow() {
        return isTaskItNow;
    }

    public void setTaskItNow(boolean taskItNow) {
        isTaskItNow = taskItNow;
    }

    public Map<String, Boolean> getBidIds() {
        return bidIds;
    }

    public void setBidIds(Map<String, Boolean> bidIds) {
        this.bidIds = bidIds;
    }
}
