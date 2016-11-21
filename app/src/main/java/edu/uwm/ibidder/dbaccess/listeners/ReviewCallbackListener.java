package edu.uwm.ibidder.dbaccess.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import edu.uwm.ibidder.dbaccess.models.ReviewModel;

import static android.content.ContentValues.TAG;

/**
 * Listener that handles callbacks from the ReviewAccessor.  Needs the dataUpdate method implemented.
 */
public abstract class ReviewCallbackListener implements ValueEventListener {

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try {
            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
            while (it.hasNext()) {
                dataUpdate(it.next().getValue(ReviewModel.class));
            }
        } catch (Exception e) {
            dataUpdate(dataSnapshot.getValue(ReviewModel.class));
        }
    }

    /**
     * Gets passed ReviewModel whenever the data updates within firebase.  You can do whatever you want with it by implementing this method
     *
     * @param um The updated ReviewModel object
     */
    public abstract void dataUpdate(ReviewModel um);

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "loadReviewModel:onCancelled", databaseError.toException());
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
