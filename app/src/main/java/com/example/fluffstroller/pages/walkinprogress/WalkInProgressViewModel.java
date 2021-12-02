package com.example.fluffstroller.pages.walkinprogress;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WalkInProgressViewModel extends ViewModel {
    private final MutableLiveData<Integer> elapsedSeconds;
    private final MutableLiveData<Integer> distanceInMeters;

    public WalkInProgressViewModel() {
        elapsedSeconds = new MutableLiveData<>();
        distanceInMeters = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getElapsedSeconds() {
        return elapsedSeconds;
    }

    public MutableLiveData<Integer> getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setElapsedSeconds(Integer elapsedSeconds) {
        this.elapsedSeconds.postValue(elapsedSeconds);
    }

    public void setDistanceInMeters(Integer distanceInMeters) {
        this.distanceInMeters.postValue(distanceInMeters);
    }
}