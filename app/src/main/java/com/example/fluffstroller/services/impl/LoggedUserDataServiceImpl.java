package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogOwnerProfileData;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.services.LoggedUserDataService;

import java.util.ArrayList;
import java.util.List;

public class LoggedUserDataServiceImpl implements LoggedUserDataService {
    private ProfileData profileData = null;

    @Override
    public void setLoggedUserData(ProfileData profileData) {
        this.profileData = profileData;
    }

    @Override
    public String getLoggedUserEmail() {
        if (profileData != null) {
            return profileData.getEmail();
        }

        return "";
    }

    @Override
    public String getLoggedUserName() {
        if (profileData != null) {
            return profileData.getName();
        }

        return "";
    }

    @Override
    public String getLoggedUserId() {
        if (profileData != null) {
            return profileData.getId();
        }

        return "";
    }

    @Override
    public String getLoggedUserPhoneNumber() {
        if (profileData != null) {
            return profileData.getPhoneNumber();
        }

        return "";
    }

    @Override
    public List<Dog> getLoggedUserDogs() {
        if (profileData != null && profileData instanceof DogOwnerProfileData) {
            return ((DogOwnerProfileData) profileData).getDogs();
        }

        return new ArrayList<>();
    }
}
