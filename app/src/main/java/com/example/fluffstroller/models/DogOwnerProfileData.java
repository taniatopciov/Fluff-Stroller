package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.List;

public class DogOwnerProfileData extends ProfileData {
    private List<Dog> dogs;
    private DogWalkPreview currentWalkPreview;

    public DogOwnerProfileData() {
        dogs = new ArrayList<>();
    }

    public DogOwnerProfileData(String id, String name, String email, UserType userType) {
        super(id, name, email, userType);
        dogs = new ArrayList<>();
    }

    public List<Dog> getDogs() {
        return dogs;
    }

    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
    }

    public DogWalkPreview getCurrentWalkPreview() {
        return currentWalkPreview;
    }

    public void setCurrentWalkPreview(DogWalkPreview currentWalkPreview) {
        this.currentWalkPreview = currentWalkPreview;
    }
}
