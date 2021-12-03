package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.List;

public class StrollerProfileData extends ProfileData {
    private WalkRequest currentRequest;
    private List<Review> reviews;
    private String description;
    private Double averageRating;

    public StrollerProfileData() {
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

    public Double getAverageRating() {
        return averageRating;
    }

    public WalkRequest getCurrentRequest() {
        return currentRequest;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
