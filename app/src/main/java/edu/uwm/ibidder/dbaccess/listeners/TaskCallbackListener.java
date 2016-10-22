package edu.uwm.ibidder.dbaccess.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.Iterator;

import edu.uwm.ibidder.dbaccess.models.TaskModel;

import static android.content.ContentValues.TAG;

/**
 * Listener that handles callbacks from the TaskAccessor.  Needs the dataUpdate method implemented.
 */
public abstract class TaskCallbackListener implements ValueEventListener {

    private TaskModel.TaskStatusType statusRestrictionType;
    private boolean isTagRestricted;
    private Collection<String> tagRestrictions;

    /**
     * Creates a TaskCallbackListener that only returns tasks with the status of the passed-in restriction enum
     *
     * @param restriction The TaskStatusType that should be returned by this TaskCallbackListener.
     */
    public TaskCallbackListener(TaskModel.TaskStatusType restriction) {
        statusRestrictionType = restriction;
    }

    /**
     * Creates a TaskCallbackListener that only returns tasks that match one or more of the tags and have the proper status restriction.
     *
     * @param restriction The status of tasks to return
     * @param tags        The collection of tags to look for
     */
    public TaskCallbackListener(TaskModel.TaskStatusType restriction, Collection<String> tags) {
        statusRestrictionType = restriction;
        isTagRestricted = true;
        tagRestrictions = tags;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
        while (it.hasNext()) {

            TaskModel taskModel = it.next().getValue(TaskModel.class);
            boolean canUpdateData = true;

            //check that at least one tag matches when needed
            if (isTagRestricted) {
                canUpdateData = false;

                for (String key : taskModel.getTags().keySet()) {
                    if (tagRestrictions.contains(key)) {
                        canUpdateData = true;
                        break;
                    }
                }
            }

            if (canUpdateData)
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
        dataError(databaseError);
    }

    /**
     * This is an overridable method to handle DB errors.  It's not necessary to implement, but you can use it to do special things on error when needed.  It does nothing by default.
     *
     * @param databaseError The database's error object
     */
    public void dataError(DatabaseError databaseError) {
        // do nothing
    }

    /**
     * Gets the status restriction type of this task
     *
     * @return The status restriction type of this task in lowercase string form.
     */
    public String getStatusRestrictionType() {
        return this.statusRestrictionType.toString().toLowerCase();
    }

}
