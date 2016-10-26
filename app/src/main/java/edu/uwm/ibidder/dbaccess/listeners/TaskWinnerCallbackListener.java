package edu.uwm.ibidder.dbaccess.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.uwm.ibidder.dbaccess.models.TaskWinnerModel;

import static android.content.ContentValues.TAG;

/**
 * Listener that handles callbacks from the TaskWinnerAccessor.  Needs the dataUpdate method implemented.
 */
public abstract class TaskWinnerCallbackListener implements ValueEventListener {

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        dataUpdate(dataSnapshot.getValue(TaskWinnerModel.class));
    }

    /**
     * Gets passed TaskWinnerModel whenever the data updates within firebase.  You can do whatever you want with it by implementing this method
     *
     * @param um The updated TaskWinnerModel object
     */
    public abstract void dataUpdate(TaskWinnerModel um);

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "loadTaskWinnerModel:onCancelled", databaseError.toException());
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

}
