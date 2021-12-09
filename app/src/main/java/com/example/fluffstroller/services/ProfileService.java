package com.example.fluffstroller.services;

import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.Review;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public interface ProfileService {
    Subject<Boolean> updateDogWalkPreview(String userId, DogWalkPreview walkPreview);

    Subject<ProfileData> getProfileData(String userId);

    Subject<ProfileData> listenForProfileData(String userId);

    Subject<ProfileData> createProfile(String uid, String name, String email, UserType userType);

    Subject<Boolean> updateDogsArray(String id, ArrayList<Dog> dogs);

    Subject<Boolean> updateReviewsArray(String id, ArrayList<Review> reviews);

    public Subject<Boolean> updateDogOwnerProfile(String id, String name, String phoneNumber, List<Dog> dogs);

    public Subject<Boolean> updateDogStrollerProfile(String id, String name, String phoneNumber, String description, List<Review> reviews);
}
