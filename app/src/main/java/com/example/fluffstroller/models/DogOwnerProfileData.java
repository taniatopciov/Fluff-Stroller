package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.List;

public class DogOwnerProfileData extends ProfileData {
    private List<Dog> dogs;
    private DogWalkPreview currentWalkPreview;

    public DogOwnerProfileData() {
    }

    public DogOwnerProfileData(String id, String name, String email, UserType userType) {
        super(id, name, email, userType);
        dogs = new ArrayList<>();
    }

    public List<Dog> getDogs() {
        return dogs;
    }

    public DogWalkPreview getCurrentWalkPreview() {
        return currentWalkPreview;
    }

    public void setCurrentWalkPreview(DogWalkPreview currentWalkPreview) {
        this.currentWalkPreview = currentWalkPreview;
    }
}
