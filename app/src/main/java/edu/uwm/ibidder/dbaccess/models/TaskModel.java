package edu.uwm.ibidder.dbaccess.models;


import java.util.HashMap;

/**
 * Represents a task
 */
public class TaskModel {

    private String description;
    private String status; //The status of this task- use the status enum
    private float maxPrice;
    private long expirationTime;
    private String ownerId;
    private String title;
    private boolean isTaskItNow;
    private boolean isLocalTask; //True if the task requires someone to be in a physical location.
    private HashMap<String, String> tags; //stores the tags in format <Tag, True>
    private int reportCount = 0; //Used by node server, not user editable
    private boolean wasDeleted = false; //Used by node server, false by default
    private boolean wasReviewedByBidder = false; //Used by node server, false by default
    private String taskId;

    /**
     * Default constructor.  Needed for firebase serialization.
     */
    public TaskModel() {

    }

    public TaskModel(String taskName, String taskDescr, float maxPrice, String ownId, long exprTime, boolean isLocal, boolean isTaskNow, HashMap<String, String> tags) {
        this.title = taskName;
        this.description = taskDescr;
        this.maxPrice = maxPrice;
        this.ownerId = ownId;
        this.expirationTime = exprTime;
        this.isLocalTask = isLocal;
        this.isTaskItNow = isTaskNow;
        this.tags = tags;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, String> tags) {
        this.tags = tags;
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

    public int getReportCount() {
        return reportCount;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
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

    public boolean getIsTaskItNow() {
        return isTaskItNow;
    }

    public void setIsTaskItNow(boolean isTaskItNow) {
        this.isTaskItNow = isTaskItNow;
    }

    public boolean getIsLocalTask() {
        return isLocalTask;
    }

    public void setIsLocalTask(boolean isLocalTask) {
        this.isLocalTask = isLocalTask;
    }

    public boolean getWasDeleted() {
        return wasDeleted;
    }

    public void setWasDeleted(boolean wasDeleted) {
        this.wasDeleted = wasDeleted;
    }

    public boolean getWasReviewedByBidder() {
        return wasReviewedByBidder;
    }

    public enum TaskStatusType {
        READY,
        ACCEPTED,
        FINISHED,
        TIMED_OUT
    }
}
