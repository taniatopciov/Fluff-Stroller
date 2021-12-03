package com.example.fluffstroller.models;

public class StrollerProfileData extends ProfileData {
    private WalkRequest currentRequest;

    public StrollerProfileData() {
    }

    public StrollerProfileData(String id, String name, String email, UserType userType) {
        super(id, name, email, userType);
    }

    public WalkRequest getCurrentRequest() {
        return currentRequest;
    }
}
