package com.example.fluffstroller.models;

import com.example.fluffstroller.repository.FirebaseDocument;

import java.util.ArrayList;
import java.util.List;

public class DogWalk extends FirebaseDocument {
    private List<String> dogNames;
    private String ownerId;
    private String ownerName;
    private String ownerPhoneNumber;
    private Integer totalPrice;
    private Integer walkTime;
    private WalkStatus status;
    private List<WalkRequest> requests;
    private Long creationTimeMillis;

    public DogWalk() {
    }

    public DogWalk(List<String> dogNames, String ownerId, String ownerName, String ownerPhoneNumber, Integer totalPrice, Integer walkTime) {
        this.dogNames = dogNames;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.totalPrice = totalPrice;
        this.walkTime = walkTime;
        this.requests = new ArrayList<>();
        this.status = WalkStatus.PENDING;
        this.creationTimeMillis = System.currentTimeMillis();
    }

    public List<String> getDogNames() {
        return dogNames;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public Integer getWalkTime() {
        return walkTime;
    }

    public List<WalkRequest> getRequests() {
        return requests;
    }

    public WalkStatus getStatus() {
        return status;
    }

    public Long getCreationTimeMillis() {
        return creationTimeMillis;
    }
}
