package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkInProgressModel {
    private String walkId;
    private String ownerId;
    private String strollerId;
    private Long creationTimeMillis;
    private Map<String, Double> latitude;
    private Map<String, Double> longitude;

    public WalkInProgressModel() {
    }

    public WalkInProgressModel(String walkId, String ownerId, String strollerId, Long creationTimeMillis) {
        this.walkId = walkId;
        this.ownerId = ownerId;
        this.strollerId = strollerId;
        this.creationTimeMillis = creationTimeMillis;
        latitude = new HashMap<>();
        longitude = new HashMap<>();
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
        return new ArrayList<>(latitude.values());
    }

    public List<Double> getLongitude() {
        return new ArrayList<>(longitude.values());
    }
}