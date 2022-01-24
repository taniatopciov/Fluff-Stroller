package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogOwnerProfileData;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.Review;
import com.example.fluffstroller.models.StrollerProfileData;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.PhotoService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.observer.Response;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseProfileService implements ProfileService {
    private final FirebaseRepository firebaseRepository;
    private final PhotoService photoService;
    private final LoggedUserDataService loggedUserDataService;

    private static final String PROFILES_COLLECTION_PATH = "profiles";

    public FirebaseProfileService(FirebaseRepository firebaseRepository, PhotoService photoService, LoggedUserDataService loggedUserDataService) {
        this.firebaseRepository = firebaseRepository;
        this.photoService = photoService;
        this.loggedUserDataService = loggedUserDataService;
    }

    @Override
    public Subject<Boolean> updateDogWalkPreview(String userId, DogWalkPreview walkPreview) {
        Map<String, Object> values = new HashMap<>();
        values.put("currentWalkPreview", walkPreview);
        return firebaseRepository.updateDocument(PROFILES_COLLECTION_PATH + "/" + userId, values);
    }

    @Override
    public Subject<Boolean> updateCurrentRequest(String userId, WalkRequest request) {
        Map<String, Object> values = new HashMap<>();
        values.put("currentRequest", request);
        return firebaseRepository.updateDocument(PROFILES_COLLECTION_PATH + "/" + userId, values);
    }

    @Override
    public Subject<ProfileData> getProfileData(String userId) {
        HashMap<String, Class<? extends ProfileData>> possibleTypes = new HashMap<>();
        possibleTypes.put(UserType.STROLLER.toString(), StrollerProfileData.class);
        possibleTypes.put(UserType.DOG_OWNER.toString(), DogOwnerProfileData.class);

        Subject<ProfileData> subject = new Subject<>();

        firebaseRepository.getDocument(PROFILES_COLLECTION_PATH + "/" + userId, "userType", possibleTypes)
                .subscribe(response -> updateProfilePhotos(response, subject));

        return subject;
    }

    @Override
    public Subject<ProfileData> listenForProfileData(String userId) {
        HashMap<String, Class<? extends ProfileData>> possibleTypes = new HashMap<>();
        possibleTypes.put(UserType.STROLLER.toString(), StrollerProfileData.class);
        possibleTypes.put(UserType.DOG_OWNER.toString(), DogOwnerProfileData.class);

        Subject<ProfileData> subject = new Subject<>();

        firebaseRepository.listenForDocumentChanges(PROFILES_COLLECTION_PATH + "/" + userId, "userType", possibleTypes)
                .subscribe(response -> updateProfilePhotos(response, subject));

        return subject;
    }

    @Override
    public Subject<ProfileData> createProfile(String uid, String name, String email, UserType userType) {
        ProfileData profileData;

        if (userType.equals(UserType.DOG_OWNER)) {
            profileData = new DogOwnerProfileData(uid, name, email, userType);
        } else {
            profileData = new StrollerProfileData(uid, name, email, userType);
        }

        return firebaseRepository.setDocument(PROFILES_COLLECTION_PATH, uid, profileData)
                .peek(loggedUserDataService::setLoggedUserData);
    }

    public Subject<Boolean> updateDogOwnerProfile(String id, String name, String phoneNumber, List<Dog> dogs) {
        Map<String, Object> values = new HashMap<>();

        values.put("name", name);
        values.put("phoneNumber", phoneNumber);
        values.put("dogs", dogs);

        return firebaseRepository.updateDocument(PROFILES_COLLECTION_PATH + "/" + id, values)
                .peek(ignored -> {
                    loggedUserDataService.updateDogOwnerData(name, phoneNumber, dogs);
                });
    }

    public Subject<Boolean> updateStrollerProfile(String id, String name, String phoneNumber, String description) {
        Map<String, Object> values = new HashMap<>();

        values.put("name", name);
        values.put("phoneNumber", phoneNumber);
        values.put("description", description);

        return firebaseRepository.updateDocument(PROFILES_COLLECTION_PATH + "/" + id, values)
                .peek(ignored -> {
                    loggedUserDataService.updateStrollerData(name, phoneNumber, description);
                });
    }

    @Override
    public Subject<Boolean> updateStrollerProfile(String strollerId, Review review) {
        return firebaseRepository.addItemToArray(PROFILES_COLLECTION_PATH + "/" + strollerId, "reviews", review);
    }

    private void updateProfilePhotos(Response<ProfileData> response, Subject<ProfileData> subject) {
        if (response.hasErrors()) {
            subject.notifyObservers(response.exception);
            return;
        }

        if (response.data == null) {
            subject.notifyObservers(response.data);
            return;
        }

        if (!(response.data instanceof DogOwnerProfileData)) {
            subject.notifyObservers(response.data);
            return;
        }

        DogOwnerProfileData dogOwnerProfileData = (DogOwnerProfileData) response.data;

        if (dogOwnerProfileData.getDogs() == null || dogOwnerProfileData.getDogs().isEmpty()) {
            subject.notifyObservers(response.data);
            return;
        }

        AtomicInteger atomicInteger = new AtomicInteger(0);

        for (Dog dog : dogOwnerProfileData.getDogs()) {
            if (dog.getImageURL() == null || dog.getImageURL().isEmpty()) {
                if (atomicInteger.incrementAndGet() == dogOwnerProfileData.getDogs().size()) {
                    subject.notifyObservers(dogOwnerProfileData);
                }
            } else {
                photoService.getPhoto(dog.getImageURL(), bitmap -> {
                    dog.bitmap = bitmap;
                    if (atomicInteger.incrementAndGet() == dogOwnerProfileData.getDogs().size()) {
                        subject.notifyObservers(dogOwnerProfileData);
                    }
                });
            }

        }
    }
}
