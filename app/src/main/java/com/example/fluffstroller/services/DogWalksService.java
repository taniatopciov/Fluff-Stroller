package com.example.fluffstroller.services;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.utils.observer.Subject;

public interface DogWalksService {
    Subject<DogWalk> createDogWalk(DogWalk dogWalk);

    Subject<Boolean> updateDogWalk(DogWalk dogWalk);

    Subject<DogWalk> getDogWalk(String id);

    Subject<DogWalk> listenForDogWalkChanges(String walkId);

    Subject<Boolean> removeCurrentWalk(String walkId);
}
