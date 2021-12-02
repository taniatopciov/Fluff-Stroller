package com.example.fluffstroller.pages.main.dogowner;

import com.example.fluffstroller.models.WalkRequest;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogOwnerMainPageWaitingForStrollerViewModel extends ViewModel {
    private final MutableLiveData<List<String>> dogNames;
    private final MutableLiveData<Integer> initialWalkTime;
    private final MutableLiveData<Integer> totalPrice;
    private final MutableLiveData<List<WalkRequest>> walkRequests;
    private final MutableLiveData<Long> remainingWaitingForStrollerMills;

    public DogOwnerMainPageWaitingForStrollerViewModel() {
        dogNames = new MutableLiveData<>();
        initialWalkTime = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
        walkRequests = new MutableLiveData<>();
        remainingWaitingForStrollerMills = new MutableLiveData<>();
    }

    public MutableLiveData<List<String>> getDogNames() {
        return dogNames;
    }

    public MutableLiveData<Integer> getInitialWalkTime() {
        return initialWalkTime;
    }

    public MutableLiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    public MutableLiveData<List<WalkRequest>> getWalkRequests() {
        return walkRequests;
    }

    public MutableLiveData<Long> getRemainingWaitingForStrollerMills() {
        return remainingWaitingForStrollerMills;
    }

    public void setDogNames(List<String> names) {
        dogNames.postValue(names);
    }

    public void setInitialWalkTime(Integer initialWalkTime) {
        this.initialWalkTime.postValue(initialWalkTime);
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice.postValue(totalPrice);
    }

    public void setWalkRequests(List<WalkRequest> walkRequests) {
        this.walkRequests.postValue(walkRequests);
    }

    public void setRemainingWaitingForStrollerMills(Long remainingWaitingForStrollerMills) {
        this.remainingWaitingForStrollerMills.postValue(remainingWaitingForStrollerMills);
    }
}