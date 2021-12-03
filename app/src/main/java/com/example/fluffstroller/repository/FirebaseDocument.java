package com.example.fluffstroller.repository;

public abstract class FirebaseDocument {
    private transient String id;

    public FirebaseDocument() {
    }

    public FirebaseDocument(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
