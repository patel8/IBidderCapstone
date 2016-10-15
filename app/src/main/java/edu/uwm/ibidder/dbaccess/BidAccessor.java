package edu.uwm.ibidder.dbaccess;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;

import edu.uwm.ibidder.dbaccess.listeners.BidCallbackListener;
import edu.uwm.ibidder.dbaccess.models.BidModel;

/**
 * Handles database interactions with bid objects
 */
public class BidAccessor extends BaseAccessor {

    /**
     * Default constructor- starts with no listeners
     */
    public BidAccessor() {
        super();
    }

    /**
     * Createa a bid and returns its id
     *
     * @param bidToCreate The bid to create
     * @return The new bid's id
     */
    public String createBid(BidModel bidToCreate) {
        DatabaseReference ref = database.getReference("bids");

        DatabaseReference pushedRef = ref.push();
        pushedRef.setValue(bidToCreate);

        return pushedRef.getKey();
    }

    /**
     * Gets a Bid in its current form and sends it to the passed-in BidCallbackListener.  This does not update constantly.
     *
     * @param bidId               The id of the bid
     * @param bidCallbackListener The BidCallbackListener that will get the BidModel
     */
    public void getBidOnce(String bidId,final BidCallbackListener bidCallbackListener) {
        DatabaseReference ref = database.getReference("bids/" + bidId);
        ref.addListenerForSingleValueEvent(bidCallbackListener);
    }

    /**
     * Gets a Bid in its current form and sends it to the passed-in BidCallbackListener.  This is kept up-to-date in realtime.
     *
     * @param bidId               The id of the bid
     * @param bidCallbackListener The bidCallbackListener that will get the bidModel
     */
    public void getBid(String bidId,final BidCallbackListener bidCallbackListener) {
        DatabaseReference ref = database.getReference("bids/" + bidId);
        storedValueEventListeners.push(ref.addValueEventListener(bidCallbackListener));
        storedDatabaseRefs.push(ref);
    }

    /**
     * Deletes the bid with the specified id
     *
     * @param bidId The id of the bid to delete
     */
    public void removeBid(String bidId) {
        DatabaseReference ref = database.getReference("bids/" + bidId);
        ref.removeValue();
    }

    /**
     * Updates a bid with the specified id
     *
     * @param bidId  The id of the bid to modify
     * @param newBid The bid's new data
     */
    public void updateBid(String bidId, BidModel newBid) {
        DatabaseReference ref = database.getReference("bids/" + bidId);
        ref.setValue(newBid);
    }

    /**
     * Gets all bids made by the user with the given user id.  Send to the passed-in callback listener once.  The tasks are passed to the callback one-by-one.
     *
     * @param userId the uid to get the bids for
     */
    public void getUserBids(String userId,final BidCallbackListener bidCallbackListener) {
        DatabaseReference ref = database.getReference("bids");
        ref.orderByChild("bidderId").equalTo(userId).addListenerForSingleValueEvent(bidCallbackListener);
    }

    /**
     * Gets all bids made for a task with the given id.  Send to the passed-in callback listener once.  The tasks are passed to the callback one-by-one.
     */
    public void getTaskBids(String taskId,final BidCallbackListener bidCallbackListener) {
        DatabaseReference ref = database.getReference("bids");
        ref.orderByChild("taskId").equalTo(taskId).addListenerForSingleValueEvent(bidCallbackListener);
    }

}
