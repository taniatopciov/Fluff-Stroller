package com.example.flusffstroller.models;

import java.util.List;

public class DogWalk {
    private String id;
    private String ownerId;
    private List<String> dogNames;
    private Integer totalPrice;
    private Integer walkTime;

    public DogWalk() {
    }

    public DogWalk(String ownerId, List<String> dogNames, Integer totalPrice, Integer walkTime) {
        this.ownerId = ownerId;
        this.dogNames = dogNames;
        this.totalPrice = totalPrice;
        this.walkTime = walkTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public List<String> getDogNames() {
        return dogNames;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public Integer getWalkTime() {
        return walkTime;
    }
}
