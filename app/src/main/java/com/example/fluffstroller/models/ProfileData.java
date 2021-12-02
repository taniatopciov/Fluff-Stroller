package com.example.fluffstroller.models;

import com.example.fluffstroller.repository.FirebaseDocument;

public class ProfileData extends FirebaseDocument {
    private UserType userType;

    public UserType getUserType() {
        return userType;
    }
}
