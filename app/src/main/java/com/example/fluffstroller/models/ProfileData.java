package com.example.fluffstroller.models;

import com.example.fluffstroller.repository.FirebaseDocument;

public class ProfileData extends FirebaseDocument {
    private String name;
    private String email;
    private String phoneNumber;
    private UserType userType;

    public ProfileData() {
    }

    public ProfileData(String id, String name, String email, UserType userType) {
        super(id);
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

    public UserType getUserType() {
        return userType;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
