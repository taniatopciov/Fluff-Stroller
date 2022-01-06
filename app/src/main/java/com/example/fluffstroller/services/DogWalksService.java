package com.example.fluffstroller.services;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.List;

public interface DogWalksService {
    Subject<DogWalk> createDogWalk(DogWalk dogWalk);

    Subject<DogWalkPreview> updateDogWalk(String ownerId, String id, WalkStatus walkStatus, List<WalkRequest> requests);

    Subject<DogWalk> getDogWalk(String id);

    Subject<DogWalk> listenForDogWalkChanges(String walkId);

    Subject<Boolean> removeWalk(String walkId);

    Subject<List<DogWalk>> getNearbyAvailableDogWalks(String id, Location currentLocation, Double radius);

    Subject<Boolean> requestWalk(WalkRequest walkRequest);
}
