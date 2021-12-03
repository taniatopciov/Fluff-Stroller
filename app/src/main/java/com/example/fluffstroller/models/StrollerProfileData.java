package com.example.fluffstroller.models;

public class StrollerProfileData extends ProfileData {
    private WalkRequest currentRequest;
    private String walkId;

    public StrollerProfileData() {
    }

    public StrollerProfileData(String id, String name, String email, UserType userType) {
        super(id, name, email, userType);
    }

    public WalkRequest getCurrentRequest() {
        return currentRequest;
    }

    public String getWalkId() {
        return walkId;
    }
}
