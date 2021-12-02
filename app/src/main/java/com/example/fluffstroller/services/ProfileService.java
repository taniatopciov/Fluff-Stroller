package com.example.fluffstroller.services;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.List;

public interface ProfileService {
    Subject<Boolean> setCurrentDogWalk(DogWalk dogWalk);

    Subject<ProfileData> getProfileData(String userId);

    Subject<ProfileData> createProfile(String uid, String name, String email, UserType userType);
}
