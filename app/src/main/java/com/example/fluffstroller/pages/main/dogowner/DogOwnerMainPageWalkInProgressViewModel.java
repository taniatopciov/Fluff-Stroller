package com.example.fluffstroller.pages.main.dogowner;

import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkStatus;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogOwnerMainPageWalkInProgressViewModel extends ViewModel {
    private final MutableLiveData<List<String>> dogNames;
    private final MutableLiveData<Double> totalPrice;
    private final MutableLiveData<WalkRequest> walkRequest;
    private final MutableLiveData<WalkStatus> walkStatus;

    public DogOwnerMainPageWalkInProgressViewModel() {
        dogNames = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
        walkRequest = new MutableLiveData<>();
        walkStatus = new MutableLiveData<>();
    }

    public MutableLiveData<List<String>> getDogNames() {
        return dogNames;
    }

    public MutableLiveData<Double> getTotalPrice() {
        return totalPrice;
    }

    public MutableLiveData<WalkRequest> getWalkRequest() {
        return walkRequest;
    }

    public MutableLiveData<WalkStatus> getWalkStatus() {
        return walkStatus;
    }

    public void setDogNames(List<String> dogNames) {
        this.dogNames.postValue(dogNames);
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice.postValue(totalPrice);
    }

    public void setWalkRequest(WalkRequest walkRequest) {
        this.walkRequest.postValue(walkRequest);
    }

    public void setWalkStatus(WalkStatus status) {
        this.walkStatus.postValue(status);
    }
}