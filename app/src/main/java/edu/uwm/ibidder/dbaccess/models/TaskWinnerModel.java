package edu.uwm.ibidder.dbaccess.models;

/**
 * Represents a task winner in the database
 */
public class TaskWinnerModel {

    private String winnerId;
    private boolean wasNotified = false; //used by node backend
    private String taskOwnerId;
    private String taskId;

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public boolean getWasNotified() {
        return wasNotified;
    }

    public String getTaskOwnerId() {
        return taskOwnerId;
    }

    public void setTaskOwnerId(String taskOwnerId) {
        this.taskOwnerId = taskOwnerId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
