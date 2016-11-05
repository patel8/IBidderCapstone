package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;
import edu.uwm.ibidder.dbaccess.models.TaskCompletedModel;

/**
 * This is only used to mark tasks completed.
 */
public class TaskCompletedAccessor extends BaseAccessor {

    /**
     * Default constructor
     */
    public TaskCompletedAccessor() {
        super();
    }

    /**
     * Marks a task as completed
     *
     * @param taskId The id of the task to mark as completed
     */
    public void markTaskCompleted(String taskId) {
        DatabaseReference ref = database.getReference("tasksCompleted/" + taskId);
        TaskCompletedModel tcm = new TaskCompletedModel();

        tcm.setTaskId(taskId);
        ref.setValue(tcm);
    }

}
