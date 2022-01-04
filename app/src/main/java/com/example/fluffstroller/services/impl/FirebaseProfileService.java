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
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseProfileService implements ProfileService {
    private final FirebaseRepository firebaseRepository;
    private static final String PROFILES_COLLECTION_PATH = "profiles";

    public FirebaseProfileService(FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
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

        return firebaseRepository.getDocument(PROFILES_COLLECTION_PATH + "/" + userId, "userType", possibleTypes);
    }

    @Override
    public Subject<ProfileData> listenForProfileData(String userId) {
        HashMap<String, Class<? extends ProfileData>> possibleTypes = new HashMap<>();
        possibleTypes.put(UserType.STROLLER.toString(), StrollerProfileData.class);
        possibleTypes.put(UserType.DOG_OWNER.toString(), DogOwnerProfileData.class);

        return firebaseRepository.listenForDocumentChanges(PROFILES_COLLECTION_PATH + "/" + userId, "userType", possibleTypes);
    }

    @Override
    public Subject<ProfileData> createProfile(String uid, String name, String email, UserType userType) {
        ProfileData profileData;

        if (userType.equals(UserType.DOG_OWNER)) {
            profileData = new DogOwnerProfileData(uid, name, email, userType);
        } else {
            profileData = new StrollerProfileData(uid, name, email, userType);
        }

        return firebaseRepository.setDocument(PROFILES_COLLECTION_PATH, uid, profileData);
    }

    public Subject<Boolean> updateDogsArray(String id, ArrayList<Dog> dogs) {
        Map<String, Object> values = new HashMap<>();
        values.put("dogs", dogs);
        return firebaseRepository.updateDocument(PROFILES_COLLECTION_PATH + "/" + id, values);
    }

    public Subject<Boolean> updateReviewsArray(String id, ArrayList<Review> reviews) {
        Map<String, Object> values = new HashMap<>();
        values.put("reviews", reviews);
        return firebaseRepository.updateDocument(PROFILES_COLLECTION_PATH + "/" + id, values);
    }

    public Subject<Boolean> updateDogOwnerProfile(String id, String name, String phoneNumber, List<Dog> dogs) {
        Map<String, Object> values = new HashMap<>();

        values.put("name", name);
        values.put("phoneNumber", phoneNumber);
        values.put("dogs", dogs);

        return firebaseRepository.updateDocument(PROFILES_COLLECTION_PATH + "/" + id, values);
    }

    public Subject<Boolean> updateDogStrollerProfile(String id, String name, String phoneNumber, String description) {
        Map<String, Object> values = new HashMap<>();

        values.put("name", name);
        values.put("phoneNumber", phoneNumber);
        values.put("description", description);

        return firebaseRepository.updateDocument(PROFILES_COLLECTION_PATH + "/" + id, values);
    }
}
