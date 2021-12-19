package com.example.fluffstroller.services.impl;

import android.app.Activity;

import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.services.LocationService;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationServiceImpl implements LocationService {
    private final FusedLocationProviderClient fusedLocationClient;

    public LocationServiceImpl(Activity activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    @Override
    public Subject<Location> getCurrentLocation() {
        Subject<Location> subject = new Subject<>();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        // if is null, the Location is turned off
                        subject.notifyObservers((Location) null);
                        return;
                    }
                    subject.notifyObservers(new Location(location.getLatitude(), location.getLongitude()));
                })
                .addOnFailureListener(subject::notifyObservers);

        return subject;
    }
}
