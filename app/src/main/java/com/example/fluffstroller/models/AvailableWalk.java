package com.example.fluffstroller.models;

import java.util.List;

public class AvailableWalk {
    private String id;
    private String dogOwnerId;
    private String dogOwnerName;
    private List<String> dogNames;
    private Integer walkingTimeMinutes;
    private Integer price;

    public AvailableWalk() {
    }

    public AvailableWalk(String id, String dogOwnerId, String dogOwnerName, List<String> dogNames, Integer walkingTimeMinutes, Integer price) {
        this.id = id;
        this.dogOwnerId = dogOwnerId;
        this.dogOwnerName = dogOwnerName;
        this.dogNames = dogNames;
        this.walkingTimeMinutes = walkingTimeMinutes;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getDogOwnerId() {
        return dogOwnerId;
    }

    public String getDogOwnerName() {
        return dogOwnerName;
    }

    public List<String> getDogNames() {
        return dogNames;
    }

    public Integer getWalkingTimeMinutes() {
        return walkingTimeMinutes;
    }

    public Integer getPrice() {
        return price;
    }
}
