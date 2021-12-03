package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.utils.observer.Subject;

public class FirebaseDogWalksService implements DogWalksService {

    public static final String WALKS_PATH = "walks";
    private static final String REQUESTS_COLLECTION_NAME = "requests";

    private final FirebaseRepository firebaseRepository;

    public FirebaseDogWalksService(FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    @Override
    public Subject<DogWalk> createDogWalk(DogWalk dogWalk) {
        return firebaseRepository.addDocument(WALKS_PATH, dogWalk);
    }

    @Override
    public Subject<DogWalk> getDogWalk(String id) {
        return firebaseRepository.getDocument(WALKS_PATH + "/" + id, DogWalk.class);
    }

    @Override
    public Subject<DogWalk> listenForDogWalkChanges(String walkId) {
        return firebaseRepository.listenForDocumentChanges(WALKS_PATH + "/" + walkId, DogWalk.class);
    }

}
