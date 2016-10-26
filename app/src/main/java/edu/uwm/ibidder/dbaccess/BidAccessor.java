package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

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
     * Createa a bid and returns its id.  Automatically sets the bidId property.
     *
     * @param bidToCreate The bid to create
     * @return The new bid's id
     */
    public String createBid(BidModel bidToCreate) {
        DatabaseReference ref = database.getReference("bids");

        DatabaseReference pushedRef = ref.push();
        bidToCreate.setBidId(pushedRef.getKey());
        pushedRef.setValue(bidToCreate);

        return pushedRef.getKey();
    }

    /**
     * Gets a Bid in its current form and sends it to the passed-in BidCallbackListener.  This does not update constantly.
     *
     * @param bidId               The id of the bid
     * @param bidCallbackListener The BidCallbackListener that will get the BidModel
     */
    public void getBidOnce(String bidId, final BidCallbackListener bidCallbackListener) {
        DatabaseReference ref = getBidRef(bidId);
        ref.addListenerForSingleValueEvent(bidCallbackListener);
    }

    /**
     * Returns a ref to a specific bid
     *
     * @param bidId The bid's id
     * @return The bid's ref
     */
    public DatabaseReference getBidRef(String bidId) {
        return database.getReference("bids/" + bidId);
    }

    /**
     * Gets a Bid in its current form and sends it to the passed-in BidCallbackListener.  This is kept up-to-date in realtime.
     *
     * @param bidId               The id of the bid
     * @param bidCallbackListener The bidCallbackListener that will get the bidModel
     */
    public void getBid(String bidId, final BidCallbackListener bidCallbackListener) {
        DatabaseReference ref = getBidRef(bidId);
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
     * Returns a query for some user's bids
     *
     * @param userId The userId to make the query for
     * @return The query for some user's bids
     */
    public Query getUserBidsQuery(String userId) {
        DatabaseReference ref = database.getReference("bids");
        return ref.orderByChild("bidderId").equalTo(userId);
    }

    /**
     * Gets all bids made by the user with the given user id.  Send to the passed-in callback listener once.  The tasks are passed to the callback one-by-one.
     *
     * @param userId the uid to get the bids for
     */
    public void getUserBids(String userId, final BidCallbackListener bidCallbackListener) {
        getUserBidsQuery(userId).addListenerForSingleValueEvent(bidCallbackListener);
    }

    /**
     * Returns the query for bids from some task
     *
     * @param taskId The taskId to get the bids for
     * @return The query to get bids by taskId
     */
    public Query getTaskBidsQuery(String taskId) {
        DatabaseReference ref = database.getReference("bids");
        return ref.orderByChild("taskId").equalTo(taskId);
    }

    /**
     * Gets all bids made for a task with the given id.  Send to the passed-in callback listener once.  The tasks are passed to the callback one-by-one.
     */
    public void getTaskBids(String taskId, final BidCallbackListener bidCallbackListener) {
        getTaskBidsQuery(taskId).addListenerForSingleValueEvent(bidCallbackListener);
    }

}
