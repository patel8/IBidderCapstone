package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import edu.uwm.ibidder.dbaccess.listeners.AggregatedReviewCallbackListener;
import edu.uwm.ibidder.dbaccess.listeners.ReviewCallbackListener;
import edu.uwm.ibidder.dbaccess.models.ReviewModel;

/**
 * Used to create and access individual user reviews
 */
public class ReviewAccessor extends BaseAccessor {

    /**
     * Returns the ref to a aggregated review set for a user
     *
     * @param userId The user to get the aggregated review for
     * @return The user's aggregated review ref
     */
    public DatabaseReference getAggregatedReviewByUserIdRef(String userId) {
        return database.getReference("aggregatedReviews/" + userId);
    }

    public void getAggregatedReviewByUserIdOnce(String userId, final AggregatedReviewCallbackListener aggregatedReviewCallback) {
        DatabaseReference ref = getAggregatedReviewByUserIdRef(userId);
        ref.addListenerForSingleValueEvent(aggregatedReviewCallback);
    }


    /**
     * Returns a query for the reviews for a user
     *
     * @param userId the user to get the reviews for
     * @return the user's reviews query
     */
    public Query getReviewsByUserIdQuery(String userId) {
        return database.getReference("reviews").orderByChild("userReviewedId").equalTo(userId);
    }

    /**
     * Gets all the reviews for a user, sends them to the reviewCallbackListener
     *
     * @param userId                 The user id to get the reviews for
     * @param reviewCallbackListener The callback to send the reviews to
     */
    public void getReviewsByUserIdOnce(String userId, ReviewCallbackListener reviewCallbackListener) {
        getReviewsByUserIdQuery(userId).addListenerForSingleValueEvent(reviewCallbackListener);
    }

    /**
     * Returns a reference to a single review
     *
     * @param reviewId The id of the review to retrieve
     * @return The reference to the specified review
     */
    public DatabaseReference getReviewByReviewIdRef(String reviewId) {
        return database.getReference("reviews/" + reviewId);
    }

    /**
     * Gets a single review once, sends it to reviewCallbackListener
     *
     * @param reviewId               The id of the review to get
     * @param reviewCallbackListener The listener to send the review to
     */
    public void getReviewByReviewIdOnce(String reviewId, ReviewCallbackListener reviewCallbackListener) {
        getReviewByReviewIdRef(reviewId).addListenerForSingleValueEvent(reviewCallbackListener);
    }

    /**
     * Creates an individual review for a user.  Automatically sets the reviewId.
     *
     * @param rm The review model to create
     */
    public void createReview(ReviewModel rm) {
        DatabaseReference ref = database.getReference("reviews");
        DatabaseReference pushedRef = ref.push();
        rm.setReviewId(pushedRef.getKey());
        pushedRef.setValue(rm);
    }
}
