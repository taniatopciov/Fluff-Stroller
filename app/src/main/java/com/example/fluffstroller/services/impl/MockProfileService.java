package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.models.DogOwnerProfileData;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.StrollerProfileData;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockProfileService implements ProfileService {
    private final FirebaseRepository firebaseRepository;

    public MockProfileService(FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
    }

    @Override
    public String getLoggedUserId() {
        return "userId1";
    }

    @Override
    public String getLoggedUserName() {
        return "TestName";
    }

    @Override
    public UserType getLoggedUserType() {
        return UserType.STROLLER;
    }

    @Override
    public Subject<Boolean> setCurrentDogWalk(DogWalk dogWalk) {
        Map<String, Object> values = new HashMap<>();
        values.put("currentWalk", dogWalk);
        return firebaseRepository.updateDocument("profiles/userId1", values);
    }

    @Override
    public Subject<List<String>> getLoggedUserDogs() {
        Subject<List<String>> subject = new Subject<>();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        List<String> names = new ArrayList<>();
                        names.add("John Dog");
                        names.add("Jane Dog");

                        subject.notifyObservers(names);

                        cancel();
                    }
                },
                1000
        );
        return subject;
    }

    @Override
    public Subject<ProfileData> getProfileData(String userId) {
        HashMap<String, Class<? extends ProfileData>> possibleTypes = new HashMap<>();
        possibleTypes.put(UserType.STROLLER.toString(), StrollerProfileData.class);
        possibleTypes.put(UserType.DOG_OWNER.toString(), DogOwnerProfileData.class);

        return firebaseRepository.listenForDocumentChanges("profiles/" + userId, "userType", possibleTypes);
    }
}
