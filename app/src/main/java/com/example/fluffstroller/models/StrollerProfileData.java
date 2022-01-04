package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.List;

public class StrollerProfileData extends ProfileData {
    private WalkRequest currentRequest;
    private List<Review> reviews;
    private String description;
    private String walkId;
    private Double totalScore;
    private Integer reviewCount;

    public StrollerProfileData() {
        reviews = new ArrayList<>();
    }

    public StrollerProfileData(String id, String name, String email, UserType userType) {
        super(id, name, email, userType);
        reviews = new ArrayList<>();
        totalScore = 0.0;
        reviewCount = 0;
    }

    public String getDescription() {
        return description;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public WalkRequest getCurrentRequest() {
        return currentRequest;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setCurrentRequest(WalkRequest currentRequest) {
        this.currentRequest = currentRequest;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWalkId() {
        return walkId;
    }

    public Double getRating() {
        if (totalScore == null || reviewCount == null || reviewCount == 0) {
            return 0.0;
        }

        return totalScore / reviewCount;
    }
}
