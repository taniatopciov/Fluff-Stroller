package com.example.fluffstroller.models;

import com.example.fluffstroller.repository.FirebaseDocument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DogWalk extends FirebaseDocument implements Serializable {
    private List<String> dogNames;
    private String ownerId;
    private String ownerName;
    private String ownerPhoneNumber;
    private Double totalPrice;
    private Integer walkTime;
    private WalkStatus status;
    private List<WalkRequest> requests;
    private Long creationTimeMillis;
    private Long walkStartedMillis;
    private Location location;

    public DogWalk() {
    }

    public DogWalk(List<String> dogNames, String ownerId, String ownerName, String ownerPhoneNumber, Double totalPrice, Integer walkTime, Location location) {
        this.dogNames = dogNames;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.totalPrice = totalPrice;
        this.walkTime = walkTime;
        this.location = location;
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

    public Double getTotalPrice() {
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

    public Location getLocation() {
        return location;
    }

    public WalkRequest getAcceptedRequest() {
        for (WalkRequest request : requests) {
            if (request.getStatus().equals(WalkRequestStatus.ACCEPTED)) {
                return request;
            }
        }
        return null;
    }

    public Long getWalkStartedMillis() {
        return walkStartedMillis;
    }

    public void setStatus(WalkStatus status) {
        this.status = status;
    }

    public void setRequests(List<WalkRequest> requests) {
        this.requests = requests;
    }

    public void setWalkStartedMillis(Long walkStartedMillis) {
        this.walkStartedMillis = walkStartedMillis;
    }
}
