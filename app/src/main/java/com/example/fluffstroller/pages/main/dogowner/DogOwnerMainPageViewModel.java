package com.example.fluffstroller.pages.main.dogowner;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogOwnerMainPageViewModel extends ViewModel {
    private final MutableLiveData<List<String>> dogNames;
    private final MutableLiveData<Integer> walkPrice;
    private final MutableLiveData<Integer> fees;

    public DogOwnerMainPageViewModel() {
        dogNames = new MutableLiveData<>();
        walkPrice = new MutableLiveData<>();
        fees = new MutableLiveData<>();
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

    public void setDogNames(List<String> names) {
        dogNames.postValue(names);
    }

    public void setWalkPrice(Integer walkPrice) {
        this.walkPrice.postValue(walkPrice);
    }

    public void setFees(Integer fees) {
        this.fees.postValue(fees);
    }
}