package com.example.fluffstroller.pages.main.stroller;

import com.example.fluffstroller.models.AvailableWalk;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogStrollerHomePageViewModel extends ViewModel {
    private final MutableLiveData<List<AvailableWalk>> availableWalks;
    private final MutableLiveData<Integer> selectedRadius;
    private final MutableLiveData<Boolean> waitingForDogOwnerApproval;

    public DogStrollerHomePageViewModel() {
        availableWalks = new MutableLiveData<>();
        selectedRadius = new MutableLiveData<>();
        waitingForDogOwnerApproval = new MutableLiveData<>();
    }

    public MutableLiveData<List<AvailableWalk>> getAvailableWalks() {
        return availableWalks;
    }

    public MutableLiveData<Integer> getSelectedRadius() {
        return selectedRadius;
    }

    public MutableLiveData<Boolean> getWaitingForDogOwnerApproval() {
        return waitingForDogOwnerApproval;
    }

    public void setAvailableWalks(List<AvailableWalk> availableWalks) {
        this.availableWalks.postValue(availableWalks);
    }

    public void setSelectedRadius(Integer selectedRadius) {
        this.selectedRadius.postValue(selectedRadius);
    }

    public void setWaitingForDogOwnerApproval(Boolean waitingForDogOwnerApproval) {
        this.waitingForDogOwnerApproval.postValue(waitingForDogOwnerApproval);
    }
}