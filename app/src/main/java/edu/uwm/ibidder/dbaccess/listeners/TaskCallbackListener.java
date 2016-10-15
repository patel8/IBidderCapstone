package edu.uwm.ibidder.dbaccess.listeners;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import edu.uwm.ibidder.dbaccess.models.TaskModel;

import static android.content.ContentValues.TAG;

/**
 * Listener that handles callbacks from the TaskAccessor.  Needs the dataUpdate method implemented.
 */
public abstract class TaskCallbackListener implements ValueEventListener {

    private boolean statusRestricted;
    private TaskModel.TaskStatusType statusRestrictionType;

    /**
     * Creates a TaskCallbackListener with NO restrictions on what tasks are returned.
     */
    public TaskCallbackListener() {
        statusRestricted = false;
    }

    /**
     * Creates a TaskCallbackListener that only returns tasks with the status of the passed-in restriction enum
     *
     * @param restriction The TaskStatusType that should be returned by this TaskCallbackListener.
     */
    public TaskCallbackListener(TaskModel.TaskStatusType restriction) {
        statusRestricted = true;
        statusRestrictionType = restriction;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();

        while (it.hasNext()) {
            TaskModel taskModel = it.next().getValue(TaskModel.class);
            if (taskModel != null && (!statusRestricted || statusRestrictionType.toString().equals(taskModel.getStatus())))
                dataUpdate(taskModel);
        }
    }

    /**
     * Gets passed TaskModel whenever the data updates within firebase.  You can do whatever you want with it by implementing this method
     *
     * @param tm The updated TaskModel object
     */
    public abstract void dataUpdate(TaskModel tm);

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "loadTaskModel:onCancelled", databaseError.toException());
    }

}
