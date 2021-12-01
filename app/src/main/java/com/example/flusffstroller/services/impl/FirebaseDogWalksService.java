package com.example.flusffstroller.services.impl;

import com.example.flusffstroller.models.DogWalk;
import com.example.flusffstroller.repository.FirebaseRepository;
import com.example.flusffstroller.services.DogWalksService;
import com.example.flusffstroller.utils.observer.Subject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDogWalksService implements DogWalksService {

    public static final String WALKS_PATH = "walks";
    private final FirebaseRepository firebaseRepository;

    public FirebaseDogWalksService(FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    @Override
    public Subject<DogWalk> createDogWalk(DogWalk dogWalk) {
        return firebaseRepository.addDocument(WALKS_PATH, dogWalk);
    }


    @Override
    public Subject<Boolean> updateDogWalkId(String walkId) {
        Map<String, Object> values = new HashMap<>();
        values.put("id", walkId);
        return firebaseRepository.updateDocument(WALKS_PATH + "/" + walkId, values);
    }
}
