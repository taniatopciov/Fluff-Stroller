package com.example.flusffstroller.services.impl;

import com.example.flusffstroller.models.DogWalk;
import com.example.flusffstroller.repository.FirebaseRepository;
import com.example.flusffstroller.services.DogWalksService;
import com.example.flusffstroller.utils.observer.Subject;

public class FirebaseDogWalksService implements DogWalksService {

    public static final String WALKS_PATH = "walks";
    private final FirebaseRepository firebaseRepository;

    public FirebaseDogWalksService(FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    @Override
    public Subject<String> createDogWalk(DogWalk dogWalk) {
        return firebaseRepository.addDocument(WALKS_PATH, dogWalk);
    }

    @Override
    public Subject<DogWalk> getCurrentDogWalk(String userId) {
        return null;
    }
}
