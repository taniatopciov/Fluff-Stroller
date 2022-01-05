package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.List;

public class WalkInProgressModel {
    private String walkId;
    private String ownerId;
    private String strollerId;
    private Long creationTimeMillis;
    private List<Double> latitude;
    private List<Double> longitude;

    public WalkInProgressModel() {
    }

    public WalkInProgressModel(String walkId, String ownerId, String strollerId, Long creationTimeMillis) {
        this.walkId = walkId;
        this.ownerId = ownerId;
        this.strollerId = strollerId;
        this.creationTimeMillis = creationTimeMillis;
        latitude = new ArrayList<>();
        longitude = new ArrayList<>();
    }

    public String getWalkId() {
        return walkId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getStrollerId() {
        return strollerId;
    }

    public Long getCreationTimeMillis() {
        return creationTimeMillis;
    }

    public List<Double> getLatitude() {
        return latitude;
    }

    public List<Double> getLongitude() {
        return longitude;
    }
}