package com.example.fluffstroller.services;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.models.WalkInProgressModel;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.List;

public interface WalkInProgressService {
    void startWalk(DogWalk dogWalk, String strollerId);

    void addLocation(String walkId, double latitude, double longitude);

    Subject<WalkInProgressModel> getWalkInProgressModel(String walkId);

    Subject<List<Location>> getLocationsInRealTime(String walkId);
}
