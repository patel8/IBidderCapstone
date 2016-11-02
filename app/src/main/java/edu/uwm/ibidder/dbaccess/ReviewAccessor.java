package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;

import edu.uwm.ibidder.dbaccess.models.ReviewModel;

/**
 * Used to create and access individual user reviews
 */
public class ReviewAccessor extends BaseAccessor {

    /**
     * Returns the aggregated review set for a user
     *
     * @param userId The user to get the aggregated review for
     * @return The user's aggregated review object
     */
    public DatabaseReference getAggregatedReviewByUserIdRef(String userId) {
        return database.getReference("aggregatedReviews/" + userId);
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
