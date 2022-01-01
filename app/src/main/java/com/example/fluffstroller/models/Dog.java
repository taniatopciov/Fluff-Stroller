package com.example.fluffstroller.models;

import java.io.Serializable;

public class Dog implements Serializable {
    private String name;
    private String breed;
    private int age;
    private String description;
    private String imageURL;

    public Dog() {
    }

    public Dog(String name, String breed, int age, String description) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.description = description;
    }

    public Dog(String name, String breed, int age, String description, String imageURL) {
        this.name = name;
        this.breed = breed;
        this.age = age;
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

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }
}
