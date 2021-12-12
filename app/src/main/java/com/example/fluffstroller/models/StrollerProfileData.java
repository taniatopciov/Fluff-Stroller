package com.example.fluffstroller.models;

public class StrollerProfileData extends ProfileData {
    private WalkRequest currentRequest;
    private Double rating;
    private Integer reviewCount;

    public StrollerProfileData() {
    }

    public StrollerProfileData(String id, String name, String email, UserType userType) {
        super(id, name, email, userType);
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
    }
}
