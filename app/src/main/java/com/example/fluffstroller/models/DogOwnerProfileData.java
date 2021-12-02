package com.example.fluffstroller.models;

import java.util.List;

public class DogOwnerProfileData extends ProfileData {
    private DogWalk currentWalk;
    private List<Dog> dogs;

    public DogWalk getCurrentWalk() {
        return currentWalk;
    }

    public List<Dog> getDogs() {
        return dogs;
    }
}
