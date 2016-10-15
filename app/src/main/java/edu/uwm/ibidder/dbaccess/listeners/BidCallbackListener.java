package edu.uwm.ibidder.dbaccess.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import edu.uwm.ibidder.dbaccess.models.BidModel;

import static android.content.ContentValues.TAG;

/**
 * Listener that handles callbacks from the BidAccessor.  Needs the dataUpdate method implemented.
 */
public abstract class BidCallbackListener implements ValueEventListener {

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();

        while (it.hasNext()) {
            BidModel bidModel = it.next().getValue(BidModel.class);
            if (bidModel != null)
                dataUpdate(bidModel);
        }
    }

    /**
     * Gets passed BidModel whenever the data updates within firebase.  You can do whatever you want with it by implementing this method
     *
     * @param bm The updated BidModel object
     */
    public abstract void dataUpdate(BidModel bm);

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "loadBidModel:onCancelled", databaseError.toException());
    }

}
