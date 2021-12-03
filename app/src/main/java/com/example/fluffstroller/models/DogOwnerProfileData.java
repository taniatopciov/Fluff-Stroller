package com.example.fluffstroller.models;

import java.util.List;

public class DogOwnerProfileData extends ProfileData {
    private List<Dog> dogs;
    private DogWalkPreview currentWalkPreview;

    public List<Dog> getDogs() {
        return dogs;
    }

    public DogWalkPreview getCurrentWalkPreview() {
        return currentWalkPreview;
    }
}
