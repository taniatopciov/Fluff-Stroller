package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.List;

public class DogOwnerProfileData extends ProfileData {
    private DogWalk currentWalk;
    private List<Dog> dogs;

    public DogOwnerProfileData() {
    }

    public DogOwnerProfileData(String id, String name, String email, UserType userType) {
        super(id, name, email, userType);
        dogs = new ArrayList<>();
    }

    public DogWalk getCurrentWalk() {
        return currentWalk;
    }

    public List<Dog> getDogs() {
        return dogs;
    }
}
