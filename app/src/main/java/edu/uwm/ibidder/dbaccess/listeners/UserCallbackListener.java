package edu.uwm.ibidder.dbaccess.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.uwm.ibidder.dbaccess.models.UserModel;
import edu.uwm.ibidder.dbaccess.models.UserModel;

import static android.content.ContentValues.TAG;

/**
 * Listener that handles callbacks from the UserAccessor.  Needs the dataUpdate method implemented.
 */
public abstract class UserCallbackListener implements ValueEventListener {

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        UserModel um = dataSnapshot.getValue(UserModel.class);
        dataUpdate(um);
    }

    /**
     * Gets passed UserModel whenever the data updates within firebase.  You can do whatever you want with it by implementing this method
     * @param um The updated UserModel object
     */
    public abstract void dataUpdate(UserModel um);

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "loadUserModel:onCancelled", databaseError.toException());
    }

}
