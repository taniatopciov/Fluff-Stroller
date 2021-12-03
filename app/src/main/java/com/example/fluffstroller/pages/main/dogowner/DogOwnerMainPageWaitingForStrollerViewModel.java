package com.example.fluffstroller.pages.main.dogowner;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.WalkRequest;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogOwnerMainPageWaitingForStrollerViewModel extends ViewModel {
    private final MutableLiveData<List<String>> dogNames;
    private final MutableLiveData<Integer> walkTime;
    private final MutableLiveData<Integer> totalPrice;
    private final MutableLiveData<Long> walkCreationTimeMillis;

    private final MutableLiveData<List<WalkRequest>> walkRequests;
    private final MutableLiveData<Long> remainingWaitingForStrollerMills;

    public DogOwnerMainPageWaitingForStrollerViewModel() {
        dogNames = new MutableLiveData<>();
        walkTime = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
        walkCreationTimeMillis = new MutableLiveData<>();
        walkRequests = new MutableLiveData<>();
        remainingWaitingForStrollerMills = new MutableLiveData<>();
    }

    public MutableLiveData<List<String>> getDogNames() {
        return dogNames;
    }

    public MutableLiveData<Integer> getWalkTime() {
        return walkTime;
    }

    public MutableLiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    public MutableLiveData<Long> getWalkCreationTimeMillis() {
        return walkCreationTimeMillis;
    }

    public MutableLiveData<List<WalkRequest>> getWalkRequests() {
        return walkRequests;
    }

    public MutableLiveData<Long> getRemainingWaitingForStrollerMills() {
        return remainingWaitingForStrollerMills;
    }

    public void setCurrentDogWalkDetails(DogWalk dogWalk) {
        this.dogNames.postValue(dogWalk.getDogNames());
        this.walkTime.postValue(dogWalk.getWalkTime());
        this.totalPrice.postValue(dogWalk.getTotalPrice());
        this.walkCreationTimeMillis.postValue(dogWalk.getCreationTimeMillis());
        this.walkRequests.postValue(dogWalk.getRequests());
    }

    public void setRemainingWaitingForStrollerMills(Long remainingWaitingForStrollerMills) {
        this.remainingWaitingForStrollerMills.postValue(remainingWaitingForStrollerMills);
    }
}