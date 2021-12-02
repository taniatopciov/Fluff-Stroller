package com.example.flusffstroller.services;

import com.example.flusffstroller.models.DogWalk;
import com.example.flusffstroller.models.ProfileData;
import com.example.flusffstroller.utils.observer.Subject;

import java.util.List;

public interface ProfileService {
    String getLoggedUserId();

    Subject<List<String>> getLoggedUserDogs();

    Subject<Boolean> setCurrentDogWalk(DogWalk dogWalk);

    Subject<ProfileData> getLoggedUser();
}
