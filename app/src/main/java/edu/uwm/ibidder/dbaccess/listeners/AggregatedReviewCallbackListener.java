package edu.uwm.ibidder.dbaccess.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import edu.uwm.ibidder.dbaccess.models.AggregatedReviewModel;

import static android.content.ContentValues.TAG;

/**
 * Listener that handles callbacks from the AggregatedReviewAccessor.  Needs the dataUpdate method implemented.
 */
public abstract class AggregatedReviewCallbackListener implements ValueEventListener {

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        AggregatedReviewModel singleAggregatedReview = dataSnapshot.getValue(AggregatedReviewModel.class);
        dataUpdate(singleAggregatedReview);
    }

    /**
     * Gets passed AggregatedReviewModel whenever the data updates within firebase.  You can do whatever you want with it by implementing this method
     *
     * @param ar The updated AggregatedReviewModel object
     */
    public abstract void dataUpdate(AggregatedReviewModel ar);

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "loadAggregatedReviewModel:onCancelled", databaseError.toException());
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
