package edu.uwm.ibidder.dbaccess.models;

/**
 * One of these is created for a task when a task is marked completed by the task poster.
 */
public class TaskCompletedModel {

    private String taskId;
    private boolean wasRead = false; //used by backend, not user editable

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean getWasRead() {
        return wasRead;
    }
}
