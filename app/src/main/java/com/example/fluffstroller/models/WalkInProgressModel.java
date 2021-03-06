package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkInProgressModel {
    private String walkId;
    private String strollerId;
    private String ownerId;
    private Map<String, Location> coordinates;

    private transient List<Location> sortedCoordinates = null;

    public WalkInProgressModel() {
    }

    public WalkInProgressModel(String walkId, String strollerId, String ownerId, Location location) {
        this.walkId = walkId;
        this.strollerId = strollerId;
        this.ownerId = ownerId;
        coordinates = new HashMap<>();
        coordinates.put("start", location);
    }

    public String getWalkId() {
        return walkId;
    }

    public List<Location> getCoordinates() {
        if (sortedCoordinates != null) {
            return sortedCoordinates;
        }
        return new ArrayList<>(coordinates.values());
    }

    public String getStrollerId() {
        return strollerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setSortedCoordinates(List<Location> sortedCoordinates) {
        this.sortedCoordinates = sortedCoordinates;
    }
}