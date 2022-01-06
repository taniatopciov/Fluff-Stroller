package com.example.fluffstroller.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalkInProgressModel {
    private String walkId;
    private Map<String, Location> coordinates;

    public WalkInProgressModel() {
    }

    public WalkInProgressModel(String walkId, Location location) {
        this.walkId = walkId;
        coordinates = new HashMap<>();
        coordinates.put("start", location);
    }

    public String getWalkId() {
        return walkId;
    }

    public List<Location> getCoordinates() {
        return new ArrayList<>(coordinates.values());
    }
}