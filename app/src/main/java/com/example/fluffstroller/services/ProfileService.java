package com.example.fluffstroller.services;

import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.utils.observer.Subject;

public interface ProfileService {
    Subject<Boolean> updateDogWalkPreview(String userId, DogWalkPreview walkPreview);

    Subject<Boolean> updateCurrentRequest(String userId, WalkRequest request);

    Subject<ProfileData> getProfileData(String userId);

    Subject<ProfileData> listenForProfileData(String userId);

    Subject<ProfileData> createProfile(String uid, String name, String email, UserType userType);
}
