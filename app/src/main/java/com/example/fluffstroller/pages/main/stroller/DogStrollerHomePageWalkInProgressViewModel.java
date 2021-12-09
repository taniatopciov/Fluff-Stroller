package com.example.fluffstroller.pages.main.stroller;

import com.example.fluffstroller.models.DogWalk;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogStrollerHomePageWalkInProgressViewModel extends ViewModel {
    private final MutableLiveData<DogWalk> dogWalk;

    public DogStrollerHomePageWalkInProgressViewModel() {
        dogWalk = new MutableLiveData<>();
    }

    public MutableLiveData<DogWalk> getDogWalk() {
        return dogWalk;
    }

    public void setDogWalk(DogWalk dogWalk) {
        this.dogWalk.postValue(dogWalk);
    }
}