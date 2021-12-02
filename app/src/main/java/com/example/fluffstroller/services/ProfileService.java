package com.example.fluffstroller.services;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.List;

public interface ProfileService {
    String getLoggedUserId();

    Subject<List<String>> getLoggedUserDogs();

    Subject<Boolean> setCurrentDogWalk(DogWalk dogWalk);

    Subject<ProfileData> getLoggedUser();
}
