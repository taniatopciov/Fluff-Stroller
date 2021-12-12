package com.example.fluffstroller.services;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.models.WalkRequest;

import java.util.List;

public interface LoggedUserDataService {

    boolean isUserLogged();

    void setLoggedUserData(ProfileData profileData);

    void setDogWalkPreview(DogWalkPreview walkPreview);

    void setCurrentRequest(WalkRequest request);

    String getLoggedUserEmail();

    String getLoggedUserName();

    String getLoggedUserId();

    String getLoggedUserPhoneNumber();

    List<Dog> getLoggedUserDogs();

    WalkRequest getLoggedUserCurrentWalkRequest();

    DogWalkPreview getLoggedUserWalkPreview();

    UserType getLogUserType();

    Double getLoggedUserRating();
}
