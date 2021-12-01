package com.example.flusffstroller.services;

import com.example.flusffstroller.models.DogWalk;
import com.example.flusffstroller.utils.observer.Subject;

public interface DogWalksService {
    Subject<DogWalk> createDogWalk(DogWalk dogWalk);

    Subject<Boolean> updateDogWalkId(String walkId);
}
