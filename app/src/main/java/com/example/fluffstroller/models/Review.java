package com.example.fluffstroller.models;

public class Review {

    private final String reviewerName;
    private final String reviewText;
    private final Double givenStars;

    public Review() {
        reviewerName = "";
        reviewText = "";
        givenStars = 0.0;
    }

    public Review(String reviewerName, String reviewText, Double givenStars) {
        this.reviewerName = reviewerName;
        this.reviewText = reviewText;
        this.givenStars = givenStars;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public Double getGivenStars() {
        return givenStars;
    }
}
