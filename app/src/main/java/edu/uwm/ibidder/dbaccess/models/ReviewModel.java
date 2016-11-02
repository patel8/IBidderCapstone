package edu.uwm.ibidder.dbaccess.models;

/**
 * Represents an individual review for a user
 */
public class ReviewModel {

    private String reviewId;
    private String reviewWriterId;
    private String userReviewedId;
    private float reviewScore;
    private String reviewText;
    private boolean wasRead = false; //used by backend

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public float getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(float reviewScore) {
        this.reviewScore = reviewScore;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public boolean getWasRead() {
        return wasRead;
    }

    public String getReviewWriterId() {
        return reviewWriterId;
    }

    public void setReviewWriterId(String reviewWriterId) {
        this.reviewWriterId = reviewWriterId;
    }

    public String getUserReviewedId() {
        return userReviewedId;
    }

    public void setUserReviewedId(String userReviewedId) {
        this.userReviewedId = userReviewedId;
    }
}
