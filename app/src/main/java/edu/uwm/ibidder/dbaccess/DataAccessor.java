package edu.uwm.ibidder.dbaccess;

import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * Handles firebase data access and manipulation
 */
public class DataAccessor {

    /**
     * Takes a task model and puts it in firebase.  Returns the id of the created task.
     * @param taskToCreate the model of the task to create
     */
    public String CreateTask(TaskModel taskToCreate){
        //TODO
        return "";
    }

    /**
     * Gets a task model from firebase using the provided taskid.
     * @param taskId The id of the task
     * @return The populated task model for the task
     */
    public TaskModel GetTask(String taskId){
        return new TaskModel();//TODO
    }

}
