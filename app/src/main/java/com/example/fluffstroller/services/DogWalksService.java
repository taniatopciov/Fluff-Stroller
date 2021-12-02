package com.example.fluffstroller.services;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.utils.observer.Subject;

public interface DogWalksService {
    Subject<DogWalk> createDogWalk(DogWalk dogWalk);

    Subject<Boolean> updateDogWalkId(String walkId);
}
