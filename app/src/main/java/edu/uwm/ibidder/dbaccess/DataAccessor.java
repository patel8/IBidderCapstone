package edu.uwm.ibidder.dbaccess;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * Handles firebase data access and manipulation
 */
public class DataAccessor {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * Takes a task model and puts it in firebase.  Returns the id of the created task.
     * @param taskToCreate the model of the task to create
     */
    public String CreateTask(TaskModel taskToCreate){
        DatabaseReference ref = database.getReference("tasks");

        DatabaseReference pushedRef = ref.push();
        pushedRef.setValue(taskToCreate);

        return pushedRef.getKey();
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
