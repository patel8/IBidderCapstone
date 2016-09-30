package edu.uwm.ibidder;

/**
 * Created by Sagar on 9/29/2016.
 * This class will Represent any Task
 */

enum TaskType{
    BIDTASK,
    QUICKTASK;
}

public class Task {
    private String taskDescription;
    private double taskMaxBid;
    private TaskType taskType;

    // Blank Contructor is to keep Firebase Connection Happy.
    public Task(){

    }

    public Task(String taskDescription, double taskMaxBid, TaskType taskType){
        this.taskDescription = taskDescription;
        this.taskMaxBid = taskMaxBid;
        this.taskType = taskType;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public double getTaskMaxBid() {
        return taskMaxBid;
    }

    public TaskType getTaskType() {
        return taskType;
    }
}
