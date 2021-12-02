package com.example.flusffstroller.services.impl;

import com.example.flusffstroller.models.DogWalk;
import com.example.flusffstroller.models.ProfileData;
import com.example.flusffstroller.models.UserType;
import com.example.flusffstroller.repository.FirebaseRepository;
import com.example.flusffstroller.services.ProfileService;
import com.example.flusffstroller.utils.observer.Subject;

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
    public Subject<ProfileData> getLoggedUser() {
        Subject<ProfileData> subject = new Subject<>();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        ProfileData profileData = new ProfileData("userId1", UserType.STROLLER, null);

                        subject.notifyObservers(profileData);

                        cancel();
                    }
                },
                1000
        );

        return subject;
    }
}
