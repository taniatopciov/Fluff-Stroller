package com.example.flusffstroller.pages.main.stroller;

import com.example.flusffstroller.models.AvailableWalk;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogStrollerHomePageViewModel extends ViewModel {
    private final MutableLiveData<List<AvailableWalk>> availableWalks;

    public DogStrollerHomePageViewModel() {
        this.availableWalks = new MutableLiveData<>();
    }

    public MutableLiveData<List<AvailableWalk>> getAvailableWalks() {
        return availableWalks;
    }

    public void setAvailableWalks(List<AvailableWalk> availableWalks) {
        this.availableWalks.postValue(availableWalks);
    }
}