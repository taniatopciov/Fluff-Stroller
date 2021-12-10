package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.utils.observer.Subject;

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
    public Subject<Boolean> updateDogWalk(DogWalk dogWalk) {
        Map<String, Object> values = new HashMap<>();
        values.put("id", dogWalk.getId());
        values.put("requests", dogWalk.getRequests());
        values.put("status", dogWalk.getStatus());

        return firebaseRepository.updateDocument(WALKS_PATH + "/" + dogWalk.getId(), values);
    }

    @Override
    public Subject<DogWalk> getDogWalk(String id) {
        return firebaseRepository.getDocument(WALKS_PATH + "/" + id, DogWalk.class);
    }

    @Override
    public Subject<DogWalk> listenForDogWalkChanges(String walkId) {
        return firebaseRepository.listenForDocumentChanges(WALKS_PATH + "/" + walkId, DogWalk.class);
    }

    @Override
    public Subject<Boolean> removeCurrentWalk(String walkId) {
        return firebaseRepository.deleteDocument(WALKS_PATH, walkId);
    }

    @Override
    public Subject<Boolean> setWalkInProgress(String walkId) {
        Map<String, Object> values = new HashMap<>();
        values.put("status", WalkStatus.IN_PROGRESS);

        return firebaseRepository.updateDocument(WALKS_PATH + "/" + walkId, values);
    }
}
