package edu.uwm.ibidder.dbaccess;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
     * Gets a databaseReference for returns it.  This reference can be used for linking events up.
     * @param taskId The id of the task
     * @return The databaseReference for the task
     */
    public DatabaseReference GetTask(String taskId){
        return database.getReference("tasks/" + taskId);
    }

    /**
     * Gets a reference of all tasks.  This is a temporary method until we have a location service and filter.
     * @return The database reference for all tasks
     */
    public DatabaseReference GetTasks(){
        return database.getReference("tasks");
    }

}
