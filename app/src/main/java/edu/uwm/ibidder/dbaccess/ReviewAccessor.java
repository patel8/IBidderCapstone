package edu.uwm.ibidder.dbaccess;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

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
