package edu.uwm.ibidder.dbaccess;

import android.location.Location;
import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.LinkedList;
import java.util.List;

import edu.uwm.ibidder.Location.LocationService;
import edu.uwm.ibidder.dbaccess.listeners.BidCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.TaskCallbackListener;
import edu.uwm.ibidder.dbaccess.models.BidModel;
import edu.uwm.ibidder.dbaccess.models.TaskModel;

import static android.content.ContentValues.TAG;

/**
 * Handles task data access and manipulation
 */
public class TaskAccessor extends BaseAccessor {

    /**
     * Default constructor, initializes with no listeners
     */
    public TaskAccessor() {
        super();
    }

    /**
     * Takes a task model and puts it in firebase.  Returns the id of the created task.
     * This also sets the taskId field and status field on the TaskModel automatically.
     *
     * @param taskToCreate the model of the task to create
     */
    public String createTask(TaskModel taskToCreate) {
        DatabaseReference ref = database.getReference("tasks/" + "ready");

        DatabaseReference pushedRef = ref.push();
        taskToCreate.setTaskId(pushedRef.getKey());
        taskToCreate.setStatus(TaskModel.TaskStatusType.READY.toString());
        pushedRef.setValue(taskToCreate);

        return pushedRef.getKey();
    }

    /**
     * Takes a task model and puts it in firebase.  Returns the id of the created task.
     * This also sets the taskId field and status field on the TaskModel automatically.
     *
     * @param taskToCreate the model of the task to create
     * @param latitude     The latitude of where this task is
     * @param longitude    The longitude of where this task is
     */
    public String createTask(TaskModel taskToCreate, double latitude, double longitude) {
        DatabaseReference ref = database.getReference("tasks/" + "ready");

        DatabaseReference pushedRef = ref.push();
        taskToCreate.setTaskId(pushedRef.getKey());
        taskToCreate.setStatus(TaskModel.TaskStatusType.READY.toString());
        pushedRef.setValue(taskToCreate);

        //Want to add location here - Austin
       // LocationService locSer = new LocationService(getContext()){
       //     @Override
        //    public Location getCoordinates(double lat, double longi) {
         //       return null;
         //   }
        //};
        geoFire.setLocation(pushedRef.getKey(), new GeoLocation(latitude, longitude));

        return pushedRef.getKey();
    }

    /**
     * Updates a task with the ID of taskKey to be equal to the TaskModel taskToUpdate.
     *
     * @param taskKey      The task to update's string key
     * @param taskToUpdate The model to update the task to
     */
    public void updateTask(String taskKey, TaskModel taskToUpdate) {
        DatabaseReference ref = database.getReference("tasks/" + taskToUpdate.getStatus().toLowerCase() + "/" + taskKey);
        ref.setValue(taskToUpdate);
    }

    /**
     * Deletes a task with the ID of taskKey.  This is done asynchronously.
     * Only tasks in the "READY" status can be deleted.
     *
     * @param taskKey The ID of the task to delete
     */
    public void removeTask(String taskKey) {
        DatabaseReference ref = database.getReference("tasks/" + "ready/" + taskKey);
        ref.removeValue();
        geoFire.removeLocation(taskKey);
    }

    /**
     * Deletes a task asynchronously with the ID of taskKey.  The completion listener is hooked up to the task if you want to do something on completion.
     * Only tasks in the "READY" status can be deleted.
     *
     * @param taskKey            The key of the task to delete
     * @param completionListener The completion listener to hook up to the delete task
     */
    public void removeTask(String taskKey, final DatabaseReference.CompletionListener completionListener) {
        DatabaseReference ref = database.getReference("tasks/" + "ready/" + taskKey);
        ref.removeValue(completionListener);
    }

    /**
     * Gets a Task in its current form and sends it to the passed-in TaskCallbackListener.  This does not update constantly.
     *
     * @param taskId               The id of the task
     * @param taskCallbackListener The TaskCallbackListener that will get the TaskModel
     */
    public void getTaskOnce(String taskId, final TaskCallbackListener taskCallbackListener) {
        DatabaseReference ref = getTaskRef(taskId, taskCallbackListener.getStatusRestrictionType());
        ref.addListenerForSingleValueEvent(taskCallbackListener);
    }

    /**
     * Gets the task ref to a specific task
     *
     * @param taskId     The task's id
     * @param taskStatus The task's current status
     * @return The task's ref
     */
    public DatabaseReference getTaskRef(String taskId, String taskStatus) {
        return database.getReference("tasks/" + taskStatus.toLowerCase() + "/" + taskId);
    }

    /**
     * Gets a Task in its current form and sends it to the passed-in TaskCallbackListener.  This is kept up-to-date in realtime.
     *
     * @param taskId               The id of the task
     * @param taskCallbackListener The TaskCallbackListener that will get the TaskModel
     */
    public void getTask(String taskId, final TaskCallbackListener taskCallbackListener) {
        DatabaseReference ref = getTaskRef(taskId, taskCallbackListener.getStatusRestrictionType());
        storedValueEventListeners.push(ref.addValueEventListener(taskCallbackListener));
        storedDatabaseRefs.push(ref);
    }

    //TODO: THIS IS A TEMP METHOD, ONLY USE THIS WHEN TESTING, IT GETS ALL TASKS THAT ARE IN EXISTENCE ONCE!!!
    public void getTasksOnce(TaskCallbackListener taskCallbackListener) {
        DatabaseReference ref = database.getReference("tasks/" + taskCallbackListener.getStatusRestrictionType());
        ref.addListenerForSingleValueEvent(taskCallbackListener);
    }

    /**
     * Gets a tasks query by ownerId and status.
     *
     * @param ownerId               The ownerId for the tasks
     * @param statusRestrictionType The toString of the TaskStatusTypes status of these tasks
     * @return The tasks query by ownerId and status
     */
    public Query getTasksByOwnerIdQuery(String ownerId, String statusRestrictionType) {
        DatabaseReference ref = database.getReference("tasks/" + statusRestrictionType.toLowerCase());
        return ref.orderByChild("ownerId").equalTo(ownerId);
    }

    /**
     * Gets all the tasks for some owner by their id.  They are passed once to the taskCallbackListener one-by-one.
     *
     * @param ownerId              The id of the owner we are getting the tasks for
     * @param taskCallbackListener The callback listener to pass the tasks to.
     */
    public void getTasksByOwnerId(String ownerId, final TaskCallbackListener taskCallbackListener) {
        getTasksByOwnerIdQuery(ownerId, taskCallbackListener.getStatusRestrictionType()).addListenerForSingleValueEvent(taskCallbackListener);
    }

    /**
     * Gets all the tasks that some user has bid on using their userId/bidderId.  The tasks are passed once to the taskCallbackListener one-by-one.
     * Only returns tasks of a certain status.
     *
     * @param bidderId             the bidder id to get all the tasks for
     * @param taskCallbackListener the taskCallback that the tasks will be sent to
     */
    public void getTasksByBidderId(String bidderId, final TaskCallbackListener taskCallbackListener) {
        BidAccessor ba = new BidAccessor();

        ba.getUserBids(bidderId, new BidCallbackListener() {
            @Override
            public void dataUpdate(BidModel bm) {
                getTaskOnce(bm.getTaskId(), taskCallbackListener);
            }
        });
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
