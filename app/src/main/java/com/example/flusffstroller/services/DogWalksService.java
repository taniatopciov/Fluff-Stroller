package com.example.flusffstroller.services;

import com.example.flusffstroller.models.DogWalk;
import com.example.flusffstroller.utils.observer.Subject;

public interface DogWalksService {
    Subject<String> createDogWalk(DogWalk dogWalk);

    Subject<DogWalk> getCurrentDogWalk(String userId);
}
