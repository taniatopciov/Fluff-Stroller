package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.models.DogOwnerProfileData;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.StrollerProfileData;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseProfileService implements ProfileService {
    private final FirebaseRepository firebaseRepository;
    private static final String PROFILES_COLLECTION_PATH = "profiles";

    public FirebaseProfileService(FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    @Override
    @Deprecated
    public Subject<Boolean> setCurrentDogWalk(DogWalk dogWalk) {
        Map<String, Object> values = new HashMap<>();
        values.put("currentWalk", dogWalk);
        return firebaseRepository.updateDocument(PROFILES_COLLECTION_PATH + "/userId1", values);
    }

    @Override
    public Subject<ProfileData> getProfileData(String userId) {
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
}
