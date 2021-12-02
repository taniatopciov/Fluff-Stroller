package com.example.fluffstroller.models;

import com.example.fluffstroller.repository.FirebaseDocument;

public class Dog extends FirebaseDocument {
    private String name;

    public String getName() {
        return name;
    }
}
