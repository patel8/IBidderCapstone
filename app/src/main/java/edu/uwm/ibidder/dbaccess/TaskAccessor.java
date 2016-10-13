package edu.uwm.ibidder.dbaccess;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Stack;

import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

/**
 * Handles task data access and manipulation
 */
public class TaskAccessor {

    final private FirebaseDatabase database = FirebaseDatabase.getInstance();
    final private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Stack<DatabaseReference> storedDatabaseRefs;
    private Stack<ValueEventListener> storedValueEventListeners;

    public TaskAccessor(){
        storedDatabaseRefs = new Stack<DatabaseReference>();
        storedValueEventListeners = new Stack<ValueEventListener>();
    }

    /**
     * Unhooks all persistent listeners.
     */
    public void stopListening(){
        while(!storedDatabaseRefs.empty() && !storedValueEventListeners.empty()){
            storedDatabaseRefs.pop().removeEventListener(storedValueEventListeners.pop());
        }
    }

    /**
     * Takes a task model and puts it in firebase.  Returns the id of the created task.
     *
     * @param taskToCreate the model of the task to create
     */
    public String createTask(TaskModel taskToCreate) {
        DatabaseReference ref = database.getReference("tasks");

        DatabaseReference pushedRef = ref.push();
        pushedRef.setValue(taskToCreate);

        return pushedRef.getKey();
    }

    /**
     * Gets a Task in its current form and sends it to the passed-in TaskCallbackListener.  This does not update constantly.
     *
     * @param taskId               The id of the task
     * @param taskCallbackListener The TaskCallbackListener that will get the TaskModel
     */
    public void getTaskOnce(String taskId, TaskCallbackListener taskCallbackListener) {
        DatabaseReference ref = database.getReference("tasks/" + taskId);
        ref.addListenerForSingleValueEvent(taskCallbackListener);
    }

    /**
     * Gets a Task in its current form and sends it to the passed-in TaskCallbackListener.  This is kept up-to-date in realtime.
     *
     * @param taskId               The id of the task
     * @param taskCallbackListener The TaskCallbackListener that will get the TaskModel
     */
    public void getTask(String taskId, TaskCallbackListener taskCallbackListener) {
        DatabaseReference ref = database.getReference("tasks/" + taskId);
        storedValueEventListeners.push(ref.addValueEventListener(taskCallbackListener));
        storedDatabaseRefs.push(ref);
    }

    /**
     * Gets a reference of all tasks.  This is a temporary method until we have a location service and filter.
     * TODO
     * @return The database reference for all tasks
     */
    public DatabaseReference getTasksOnce() {
        return database.getReference("tasks");
    }

}
