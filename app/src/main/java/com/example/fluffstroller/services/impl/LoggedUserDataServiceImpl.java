package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogOwnerProfileData;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.StrollerProfileData;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.Review;
import com.example.fluffstroller.models.StrollerProfileData;
import com.example.fluffstroller.services.LoggedUserDataService;

import java.util.ArrayList;
import java.util.List;

public class LoggedUserDataServiceImpl implements LoggedUserDataService {
    private ProfileData profileData = null;

    @Override
    public boolean isUserLogged() {
        return profileData != null;
    }

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
    public void setCurrentRequest(WalkRequest request) {
        if (profileData != null && profileData instanceof StrollerProfileData) {
            ((StrollerProfileData) profileData).setCurrentRequest(request);
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
    public WalkRequest getLoggedUserCurrentWalkRequest() {
        if (profileData != null && profileData instanceof StrollerProfileData) {
            return (((StrollerProfileData) profileData).getCurrentRequest());
        }

        return null;
    }

    @Override
    public DogWalkPreview getLoggedUserWalkPreview() {
        if (profileData != null && profileData instanceof DogOwnerProfileData) {
            return ((DogOwnerProfileData) profileData).getCurrentWalkPreview();
        }
        return null;
    }

    @Override
    public UserType getLogUserType() {
        if (profileData != null) {
            return profileData.getUserType();
        }
        return null;
    }

    @Override
    public Double getLoggedUserRating() {
        if (profileData != null && profileData instanceof StrollerProfileData) {
            Double rating = ((StrollerProfileData) profileData).getRating();
            return rating == null ? 0.0 : rating;
        }

        return 0.0;
    }
}
