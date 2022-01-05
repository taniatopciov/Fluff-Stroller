package com.example.fluffstroller.pages.walkinprogress;

import com.example.fluffstroller.models.Location;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WalkInProgressViewModel extends ViewModel {
    private final MutableLiveData<Long> elapsedSeconds;
    private final MutableLiveData<Float> distanceInMeters;
    private final MutableLiveData<List<Location>> locations;

    public WalkInProgressViewModel() {
        elapsedSeconds = new MutableLiveData<>();
        distanceInMeters = new MutableLiveData<>();
        locations = new MutableLiveData<>();
    }

    public MutableLiveData<Long> getElapsedSeconds() {
        return elapsedSeconds;
    }

    public MutableLiveData<Float> getDistanceInMeters() {
        return distanceInMeters;
    }

    public MutableLiveData<List<Location>> getLocations() {
        return locations;
    }

    public void setElapsedSeconds(Long elapsedSeconds) {
        this.elapsedSeconds.postValue(elapsedSeconds);
    }

    public void setDistanceInMeters(Float distanceInMeters) {
        this.distanceInMeters.postValue(distanceInMeters);
    }

    public void setLocations(List<Location> locations) {
        this.locations.postValue(locations);
    }

    public void increaseElapsedSeconds(long amount) {
        Long value = this.elapsedSeconds.getValue();
        if (value != null) {
            this.elapsedSeconds.postValue(value + amount);
        }
    }
}