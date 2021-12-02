package com.example.fluffstroller.pages.main.dogowner;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogOwnerMainPageViewModel extends ViewModel {
    private final MutableLiveData<List<String>> dogNames;
    private final MutableLiveData<Integer> walkPrice;
    private final MutableLiveData<Integer> walkTime;
    private final MutableLiveData<Integer> fees;
    private final MutableLiveData<Integer> totalPrice;

    public DogOwnerMainPageViewModel() {
        dogNames = new MutableLiveData<>();
        walkPrice = new MutableLiveData<>();
        walkTime = new MutableLiveData<>();
        fees = new MutableLiveData<>();
        totalPrice = new MutableLiveData<>();
    }

    public LiveData<List<String>> getDogNames() {
        return dogNames;
    }

    public MutableLiveData<Integer> getWalkPrice() {
        return walkPrice;
    }

    public MutableLiveData<Integer> getFees() {
        return fees;
    }

    public MutableLiveData<Integer> getWalkTime() {
        return walkTime;
    }

    public MutableLiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    public void setDogNames(List<String> names) {
        dogNames.postValue(names);
    }

    public void setWalkPrice(Integer walkPrice) {
        this.walkPrice.postValue(walkPrice);
    }

    public void setWalkTime(Integer walkTime) {
        this.walkTime.postValue(walkTime);
    }

    public void setFees(Integer fees) {
        this.fees.postValue(fees);
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice.postValue(totalPrice);
    }
}