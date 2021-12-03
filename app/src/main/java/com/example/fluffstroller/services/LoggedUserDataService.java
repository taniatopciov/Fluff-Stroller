package com.example.fluffstroller.services;

import android.util.Pair;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.WalkRequest;

import java.util.List;

public interface LoggedUserDataService {

    void setLoggedUserData(ProfileData profileData);

    void setDogWalkPreview(DogWalkPreview walkPreview);

    String getLoggedUserEmail();

    String getLoggedUserName();

    String getLoggedUserId();

    String getLoggedUserPhoneNumber();

    List<Dog> getLoggedUserDogs();

    String getLoggedUserCurrentWalkId();

    Pair<String, WalkRequest> getLoggedUserCurrentWalkRequest();
}
