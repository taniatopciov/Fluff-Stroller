package com.example.flusffstroller.models;

public class ProfileData {
    private String userId;
    private UserType userType;
    private WalkRequest walkRequest;

    public ProfileData() {
    }

    public ProfileData(String userId, UserType userType, WalkRequest walkRequest) {
        this.userId = userId;
        this.userType = userType;
        this.walkRequest = walkRequest;
    }

    public String getUserId() {
        return userId;
    }

    public UserType getUserType() {
        return userType;
    }

    public WalkRequest getWalkRequest() {
        return walkRequest;
    }
}
