package edu.uwm.ibidder.dbaccess.models;

/**
 * Represents a user's aggregated reviews
 */
public class AggregatedReviewModel {

    private float reviewScore;
    private String userId;
    private float totalReviews;

    public float getReviewScore() {
        return reviewScore;
    }

    public String getUserId() {
        return userId;
    }

    public float getTotalReviews() {
        return totalReviews;
    }
}