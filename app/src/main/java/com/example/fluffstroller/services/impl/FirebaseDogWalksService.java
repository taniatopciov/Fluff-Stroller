package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Subject<Boolean> setWalkInProgress(String walkId) {
        Map<String, Object> values = new HashMap<>();
        values.put("status", WalkStatus.IN_PROGRESS);

        return firebaseRepository.updateDocument(WALKS_PATH + "/" + walkId, values);
    }

    @Override
    public Subject<Boolean> removeWalk(String walkId) {
        return firebaseRepository.deleteDocument(WALKS_PATH, walkId);
    }

    @Override
    public Subject<List<DogWalk>> getAvailableDogWalks() {
        Subject<List<DogWalk>> subject = new Subject<>();

        firebaseRepository.getAllDocuments(WALKS_PATH, DogWalk.class).subscribe(response -> {
            if (response.hasErrors() || response.data == null) {
                subject.notifyObservers(response.exception);
                return;
            }

            subject.notifyObservers(response.data.stream()
                    .filter(dogWalk -> dogWalk.getStatus() == WalkStatus.PENDING)
                    .collect(Collectors.toList()));
        });

        return subject;
    }

    @Override
    public Subject<Boolean> requestWalk(WalkRequest walkRequest) {
        return firebaseRepository.addItemToArray(WALKS_PATH + "/" + walkRequest.getWalkId(), "requests", walkRequest);
    }
}
