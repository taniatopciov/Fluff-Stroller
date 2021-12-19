package com.example.fluffstroller.models;

public class DogWalkPreview {
    private String walkId;
    private WalkStatus status;

    public DogWalkPreview() {
    }

    public DogWalkPreview(String walkId, WalkStatus status) {
        this.walkId = walkId;
        this.status = status;
    }

    public String getWalkId() {
        return walkId;
    }

    public WalkStatus getStatus() {
        return status;
    }

    public void setStatus(WalkStatus status) {
        this.status = status;
    }
}
