package edu.uwm.ibidder.dbaccess.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.uwm.ibidder.dbaccess.models.TaskModel;

import static android.content.ContentValues.TAG;

/**
 * Listener that handles callbacks from the DataAccessor.  Needs the dataUpdate method implemented.
 */
public abstract class TaskCallbackListener implements ValueEventListener {

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        TaskModel tm = dataSnapshot.getValue(TaskModel.class);
        dataUpdate(tm);
    }

    /**
     * Gets passed TaskModel whenever the data updates within firebase.  You can do whatever you want with it by implementing this method
     * @param tm The updated TaskModel object
     */
    public abstract void dataUpdate(TaskModel tm);

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "loadTaskModel:onCancelled", databaseError.toException());
    }

}
