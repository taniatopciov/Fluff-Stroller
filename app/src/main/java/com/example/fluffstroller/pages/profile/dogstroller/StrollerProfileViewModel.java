package com.example.fluffstroller.pages.profile.dogstroller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fluffstroller.models.Review;

import java.util.List;

public class StrollerProfileViewModel extends ViewModel {
    private final MutableLiveData<List<Review>> reviews;
    private final MutableLiveData<String> name;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> phoneNumber;
    private final MutableLiveData<String> description;
    private final MutableLiveData<Double> rating;
    private final MutableLiveData<Integer> reviewsNumber;

    public StrollerProfileViewModel() {
        reviews = new MutableLiveData<>();
        name = new MutableLiveData<>();
        email = new MutableLiveData<>();
        phoneNumber = new MutableLiveData<>();
        description = new MutableLiveData<>();
        rating = new MutableLiveData<>();
        reviewsNumber = new MutableLiveData<>();
    }

    public LiveData<List<Review>> getReviews() {
        return reviews;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public MutableLiveData<String> getDescription() {
        return description;
    }

    public MutableLiveData<Double> getRating() {
        return rating;
    }

    public MutableLiveData<Integer> getReviewsNumber() {
        return reviewsNumber;
    }

    public void setReviews(List<Review> reviewsList) {
        reviews.postValue(reviewsList);
    }

    public void setName(String name) {
        this.name.postValue(name);
    }

    public void setEmail(String email) {
        this.email.postValue(email);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.postValue(phoneNumber);
    }

    public void setDescription(String description) {
        this.description.postValue(description);
    }

    public void setRating(Double rating) {
        this.rating.postValue(rating);
    }

    public void setReviewsNumber(Integer reviewsNumber) {
        this.reviewsNumber.postValue(reviewsNumber);
    }
}