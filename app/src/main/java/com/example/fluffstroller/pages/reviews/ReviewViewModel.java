package com.example.fluffstroller.pages.reviews;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReviewViewModel extends ViewModel {
    private final MutableLiveData<String> strollerName;
    private final MutableLiveData<String> strollerId;

    public ReviewViewModel() {
        strollerName = new MutableLiveData<>();
        strollerId = new MutableLiveData<>();
    }

    public MutableLiveData<String> getStrollerName() {
        return strollerName;
    }

    public MutableLiveData<String> getStrollerId() {
        return strollerId;
    }

    public void setStrollerName(String strollerName) {
        this.strollerName.postValue(strollerName);
    }

    public void setStrollerId(String strollerId) {
        this.strollerId.postValue(strollerId);
    }
}