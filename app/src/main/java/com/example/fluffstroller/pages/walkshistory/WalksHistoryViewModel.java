package com.example.fluffstroller.pages.walkshistory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fluffstroller.models.DogWalk;

import java.util.List;

public class WalksHistoryViewModel extends ViewModel {
    private final MutableLiveData<List<DogWalk>> pastWalks;

    public WalksHistoryViewModel() {
        pastWalks = new MutableLiveData<>();
    }

    public LiveData<List<DogWalk>> getPastWalks() {
        return pastWalks;
    }

    public void setPastWalks(List<DogWalk> pastWalks) {
        this.pastWalks.postValue(pastWalks);
    }
}