package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.List;

public class StrollerProfileData extends ProfileData {
    private WalkRequest currentRequest;
    private List<Review> reviews;
    private String description;
    private Double averageRating;
    private String walkId;
    private Double rating;
    private Integer reviewCount;

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
        rating = 0.0;
        reviewCount = 0;
    }

    public WalkRequest getCurrentRequest() {
        return currentRequest;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setCurrentRequest(WalkRequest currentRequest) {
        this.currentRequest = currentRequest;
        public void setDescription (String description){
            this.description = description;
        }

        public String getWalkId () {
            return walkId;
        }
    }
