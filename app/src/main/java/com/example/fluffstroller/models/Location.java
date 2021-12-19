package com.example.fluffstroller.models;

public final class Location {
    public final double latitude;
    public final double longitude;

    public Location() {
        latitude = 0;
        longitude = 0;
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
