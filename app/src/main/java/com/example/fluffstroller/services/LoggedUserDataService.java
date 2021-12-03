package com.example.fluffstroller.services;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.ProfileData;

import java.util.List;

public interface LoggedUserDataService {

    void setLoggedUserData(ProfileData profileData);

    String getLoggedUserEmail();

    String getLoggedUserName();

    String getLoggedUserId();

    String getLoggedUserPhoneNumber();

    List<Dog> getLoggedUserDogs();
}
