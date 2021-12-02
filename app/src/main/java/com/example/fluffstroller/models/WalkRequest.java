package com.example.fluffstroller.models;

import com.example.fluffstroller.repository.FirebaseDocument;

public class WalkRequest extends FirebaseDocument {
    private String strollerId;
    private String strollerName;
    private String strollerPhoneNumber;
    private Double strollerRating;
    private WalkRequestStatus status;

    public WalkRequest() {
    }

    public WalkRequest(String strollerId, String strollerName, String strollerPhoneNumber, Double strollerRating) {
        this.strollerId = strollerId;
        this.strollerName = strollerName;
        this.strollerPhoneNumber = strollerPhoneNumber;
        this.strollerRating = strollerRating;
        this.status = WalkRequestStatus.PENDING;
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

    public WalkRequestStatus getStatus() {
        return status;
    }
}
