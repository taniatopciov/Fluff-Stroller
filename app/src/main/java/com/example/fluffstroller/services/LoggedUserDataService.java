package com.example.fluffstroller.services;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.Review;

import java.util.List;

public interface LoggedUserDataService {

    void setLoggedUserData(ProfileData profileData);

    String getLoggedUserEmail();

    String getLoggedUserName();

    String getLoggedUserId();

    String getLoggedUserPhoneNumber();

    String getLoggedUserDescription();

    List<Dog> getLoggedUserDogs();

    List<Review> getLoggedUserReviews();
}
