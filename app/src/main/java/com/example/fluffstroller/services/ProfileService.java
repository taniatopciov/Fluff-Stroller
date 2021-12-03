package com.example.fluffstroller.services;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.Review;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.ArrayList;

public interface ProfileService {
    Subject<Boolean> setCurrentDogWalk(DogWalk dogWalk);

    Subject<ProfileData> getProfileData(String userId);

    Subject<ProfileData> createProfile(String uid, String name, String email, UserType userType);

    Subject<Boolean> updateDogsArray(String id, ArrayList<Dog> dogs);

    Subject<Boolean> updateReviewsArray(String id, ArrayList<Review> reviews);

    Subject<Boolean> updateDogOwnerProfile(String id, ProfileData profileData);

    Subject<Boolean> updateDogStrollerProfile(String id, ProfileData profileData);
}
