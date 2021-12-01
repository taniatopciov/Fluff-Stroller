package com.example.flusffstroller.pages.main;

import com.example.flusffstroller.models.WalkRequest;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogOwnerMainPageWalkInProgressViewModel extends ViewModel {
    private final MutableLiveData<List<String>> dogNames;
    private final MutableLiveData<Integer> totalPrice;
    private final MutableLiveData<WalkRequest> walkRequest;

    public DogOwnerMainPageWalkInProgressViewModel() {
        dogNames = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
        walkRequest = new MutableLiveData<>();
    }

    public MutableLiveData<List<String>> getDogNames() {
        return dogNames;
    }

    public MutableLiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    public MutableLiveData<WalkRequest> getWalkRequest() {
        return walkRequest;
    }

    public void setDogNames(List<String> dogNames) {
        this.dogNames.setValue(dogNames);
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice.setValue(totalPrice);
    }

    public void setWalkRequest(WalkRequest walkRequest) {
        this.walkRequest.setValue(walkRequest);
    }
}