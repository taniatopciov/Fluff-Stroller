package com.example.fluffstroller.models;

public class WalkRequest {
    private String id;
    private String strollerId;
    private String strollerName;
    private String strollerPhoneNumber;
    private Double strollerRating;
    private WalkStatus walkStatus;

    public WalkRequest() {
    }

    public WalkRequest(String id, String strollerId, String strollerName, String strollerPhoneNumber, Double strollerRating, WalkStatus walkStatus) {
        this.id = id;
        this.strollerId = strollerId;
        this.strollerName = strollerName;
        this.strollerPhoneNumber = strollerPhoneNumber;
        this.strollerRating = strollerRating;
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

    public String getStrollerPhoneNumber() {
        return strollerPhoneNumber;
    }

    public Double getStrollerRating() {
        return strollerRating;
    }

    public WalkStatus getWalkStatus() {
        return walkStatus;
    }
}
