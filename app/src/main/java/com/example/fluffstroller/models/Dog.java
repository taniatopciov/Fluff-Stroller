package com.example.fluffstroller.models;

import java.io.Serializable;

public class Dog implements Serializable {
    private final String name;
    private final String breed;
    private final String description;
    private String imageURL;

    public Dog(String name, String breed, String description) {
        this.name = name;
        this.breed = breed;
        this.description = description;
    }

    public Dog(String name, String breed, String description, String imageURL) {
        this.name = name;
        this.breed = breed;
        this.description = description;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }
}
