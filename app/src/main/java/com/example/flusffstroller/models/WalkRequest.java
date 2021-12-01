package com.example.flusffstroller.models;

public class WalkRequest {
    private String id;
    private String strollerId;
    private String strollerName;
    private String phoneNumber;
    private Double rating;
    private WalkStatus walkStatus;

    public WalkRequest() {
    }

    public WalkRequest(String id, String strollerId, String strollerName, String phoneNumber, Double rating, WalkStatus walkStatus) {
        this.id = id;
        this.strollerId = strollerId;
        this.strollerName = strollerName;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.walkStatus = walkStatus;
    }

    public String getId() {
        return id;
    }

    public String getStrollerId() {
        return strollerId;
    }

    public String getStrollerName() {
        return strollerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Double getRating() {
        return rating;
    }

    public WalkStatus getWalkStatus() {
        return walkStatus;
    }
}
