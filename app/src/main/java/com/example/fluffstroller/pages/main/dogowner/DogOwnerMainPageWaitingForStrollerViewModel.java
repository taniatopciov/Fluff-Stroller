package com.example.fluffstroller.pages.main.dogowner;

import com.example.fluffstroller.models.WalkRequest;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogOwnerMainPageWaitingForStrollerViewModel extends ViewModel {
    private final MutableLiveData<DogWalk> currentDogWalk;
    private final MutableLiveData<List<WalkRequest>> walkRequests;
    private final MutableLiveData<Long> remainingWaitingForStrollerMills;

    public DogOwnerMainPageWaitingForStrollerViewModel() {
        currentDogWalk = new MutableLiveData<>();
        walkRequests = new MutableLiveData<>();
        remainingWaitingForStrollerMills = new MutableLiveData<>();
    }

    public void removeValues() {
        currentDogWalk.setValue(null);
        walkRequests.setValue(null);
        remainingWaitingForStrollerMills.setValue(null);
    }

    public MutableLiveData<DogWalk> getCurrentDogWalk() {
        return currentDogWalk;
    }

    public MutableLiveData<List<WalkRequest>> getWalkRequests() {
        return walkRequests;
    }

    public MutableLiveData<Long> getRemainingWaitingForStrollerMills() {
        return remainingWaitingForStrollerMills;
    }

    public void setCurrentDogWalk(DogWalk dogWalk) {
        this.currentDogWalk.postValue(dogWalk);
    }

    public void setWalkRequests(List<WalkRequest> walkRequests) {
        this.walkRequests.postValue(walkRequests);
    }

    public void setRemainingWaitingForStrollerMills(Long remainingWaitingForStrollerMills) {
        this.remainingWaitingForStrollerMills.postValue(remainingWaitingForStrollerMills);
    }
}