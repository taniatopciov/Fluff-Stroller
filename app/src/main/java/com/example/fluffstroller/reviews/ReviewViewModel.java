package com.example.fluffstroller.reviews;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReviewViewModel extends ViewModel {
    private final MutableLiveData<Integer> rating;
    private final MutableLiveData<String> description;

    public ReviewViewModel() {
        rating = new MutableLiveData<>();
        description = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getRating() {
        return rating;
    }

    public MutableLiveData<String> getDescription() {
        return description;
    }

    public void setRating(Integer rating) {
        this.rating.postValue(rating);
    }

    public void setDescription(String description) {
        this.description.postValue(description);
    }
}