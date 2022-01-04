package com.example.fluffstroller.models;

public class Review {

    private String reviewerName;
    private String reviewText;
    private Integer givenStars;

    public Review(String reviewerName, String reviewText, Integer givenStars) {
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

    public Integer getGivenStars() {
        return givenStars;
    }
}
