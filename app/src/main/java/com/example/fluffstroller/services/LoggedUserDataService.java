package com.example.fluffstroller.services;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.Review;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.models.WalkRequest;

import java.util.List;

public interface LoggedUserDataService {

    boolean isUserLogged();

    void updateStrollerData(String name, String phoneNumber, String description);

    void updateDogOwnerData(String name, String phoneNumber, List<Dog> dogs);

    void setLoggedUserData(ProfileData profileData);

    void setDogWalkPreview(DogWalkPreview walkPreview);

    void setCurrentRequest(WalkRequest request);

    String getLoggedUserEmail();

    String getLoggedUserName();

    String getLoggedUserId();

    String getLoggedUserPhoneNumber();

    String getLoggedUserDescription();

    List<Dog> getLoggedUserDogs();

    List<Review> getLoggedUserReviews();

    WalkRequest getLoggedUserCurrentWalkRequest();

    DogWalkPreview getLoggedUserWalkPreview();

    UserType getLogUserType();

    Double getLoggedUserRating();

    String getCurrentWalkId();
}
