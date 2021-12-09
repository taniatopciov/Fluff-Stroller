package com.example.fluffstroller.services.impl;

import android.util.Pair;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogOwnerProfileData;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.StrollerProfileData;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.Review;
import com.example.fluffstroller.models.StrollerProfileData;
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
    public void setDogWalkPreview(DogWalkPreview walkPreview) {
        if (profileData != null && profileData instanceof DogOwnerProfileData) {
            ((DogOwnerProfileData) profileData).setCurrentWalkPreview(walkPreview);
        }
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
    public String getLoggedUserDescription() {
        if (profileData != null && profileData instanceof StrollerProfileData) {
            return ((StrollerProfileData) profileData).getDescription();
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

    @Override
    public List<Review> getLoggedUserReviews() {
        if (profileData != null && profileData instanceof StrollerProfileData) {
            return ((StrollerProfileData) profileData).getReviews();
        }

        return new ArrayList<>();
    }

    @Override
    public String getLoggedUserCurrentWalkId() {
        if (profileData != null && profileData instanceof DogOwnerProfileData) {
            DogWalkPreview currentWalkPreview = ((DogOwnerProfileData) profileData).getCurrentWalkPreview();
            if (currentWalkPreview == null) {
                return "";
            }
            return currentWalkPreview.getWalkId();
        }

        return "";
    }

    @Override
    public Pair<String, WalkRequest> getLoggedUserCurrentWalkRequest() {
        if (profileData != null && profileData instanceof StrollerProfileData) {
            StrollerProfileData profileData = (StrollerProfileData) this.profileData;
            WalkRequest currentRequest = profileData.getCurrentRequest();
            if (profileData.getWalkId() == null || currentRequest == null) {
                return new Pair<>(null, null);
            }
            return new Pair<>(profileData.getWalkId(), currentRequest);
        }

        return new Pair<>(null, null);
    }
}
