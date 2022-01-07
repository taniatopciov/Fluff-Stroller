package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.List;

public class StrollerProfileData extends ProfileData {
    private WalkRequest currentRequest;
    private List<Review> reviews;
    private String description;

    public StrollerProfileData() {
        reviews = new ArrayList<>();
    }

    public StrollerProfileData(String id, String name, String email, UserType userType) {
        super(id, name, email, userType);
        reviews = new ArrayList<>();
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

    public void setCurrentRequest(WalkRequest currentRequest) {
        this.currentRequest = currentRequest;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        if (reviews == null || reviews.size() == 0) {
            return 0.0;
        }

        Double totalScore = 0.0;
        int reviewCount = 0;

        for (Review review : reviews) {
            if (review.getGivenStars() != null) {
                totalScore += review.getGivenStars();
            }
            reviewCount++;
        }

        return totalScore / reviewCount;
    }
}
