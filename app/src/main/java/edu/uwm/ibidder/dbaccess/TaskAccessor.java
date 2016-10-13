package edu.uwm.ibidder.dbaccess;

import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

import static android.content.ContentValues.TAG;

/**
 * Handles task data access and manipulation
 */
public class TaskAccessor {

    final private FirebaseDatabase database = FirebaseDatabase.getInstance();
    final private FirebaseAuth auth = FirebaseAuth.getInstance();
    final private GeoFire geoFire = new GeoFire(database.getReference("geofire"));

    private Stack<DatabaseReference> storedDatabaseRefs;
    private Stack<ValueEventListener> storedValueEventListeners;

    /**
     * Creates a TaskAccessor with its own pool of listeners
     */
    public TaskAccessor() {
        storedDatabaseRefs = new Stack<DatabaseReference>();
        storedValueEventListeners = new Stack<ValueEventListener>();
    }

    /**
     * Unhooks all persistent listeners.
     */
    public void stopListening() {
        while (!storedDatabaseRefs.empty() && !storedValueEventListeners.empty()) {
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
     * Takes a task model and puts it in firebase.  Returns the id of the created task.
     *
     * @param taskToCreate the model of the task to create
     * @param latitude     The latitude of where this task is
     * @param longitude    The longitude of where this task is
     */
    public String createTask(TaskModel taskToCreate, double latitude, double longitude) {
        DatabaseReference ref = database.getReference("tasks");

        DatabaseReference pushedRef = ref.push();
        pushedRef.setValue(taskToCreate);

        geoFire.setLocation(pushedRef.getKey(), new GeoLocation(latitude, longitude));

        return pushedRef.getKey();
    }

    /**
     * Updates a task with the ID of taskKey to be equal to the TaskModel taskToUpdate
     *
     * @param taskKey      The task to update's string key
     * @param taskToUpdate The model to update the task to
     */
    public void updateTask(String taskKey, TaskModel taskToUpdate) {
        DatabaseReference ref = database.getReference("tasks/" + taskKey);
        ref.setValue(taskToUpdate);
    }

    /**
     * Deletes a task with the ID of taskKey.  This is done asynchronously.
     *
     * @param taskKey The ID of the task to delete
     */
    public void removeTask(String taskKey) {
        DatabaseReference ref = database.getReference("tasks/" + taskKey);
        ref.removeValue();
    }

    /**
     * Deletes a task asynchronously with the ID of taskKey.  The completion listener is hooked up to the task if you want to do something on completion.
     *
     * @param taskKey            The key of the task to delete
     * @param completionListener The completion listener to hook up to the delete task
     */
    public void removeTask(String taskKey, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference ref = database.getReference("tasks/" + taskKey);
        ref.removeValue(completionListener);
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
     * Gets tasks within a circle.  These tasks are only returned once to save battery life.
     *
     * @param callback  The TaskCallbackListener that the tasks are sent to (one by one)
     * @param latitude  The lattitude of the center of the circle
     * @param longitude The longitude of the center of the circle
     * @param radius    The radius of the circle in kilometers
     */
    public void getTasksOnce(final TaskCallbackListener callback, double latitude, double longitude, double radius) {
        final GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longitude), radius);
        final List<String> keysInZone = new LinkedList<String>();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                keysInZone.add(key);
            }

            @Override
            public void onKeyExited(String key) {
                //do nothing
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //do nothing
            }

            @Override
            public void onGeoQueryReady() {
                geoQuery.removeGeoQueryEventListener(this);

                for (String key : keysInZone) {
                    getTaskOnce(key, callback);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.w(TAG, "loadGeoQuery:onCancelled", error.toException());
            }
        });
    }

}
