package com.example.fluffstroller.pages.walkshistory;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fluffstroller.models.Location;

import java.util.List;

public class PastWalkViewModel extends ViewModel {
    private final MutableLiveData<Long> elapsedSeconds;
    private final MutableLiveData<Float> distanceInMeters;
    private final MutableLiveData<List<Location>> locations;
    private final MutableLiveData<String> walkId;

    public PastWalkViewModel() {
        elapsedSeconds = new MutableLiveData<>();
        distanceInMeters = new MutableLiveData<>();
        locations = new MutableLiveData<>();
        walkId = new MutableLiveData<>();
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

    public MutableLiveData<String> getWalkId() {
        return walkId;
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

    public void setWalkId(String walkId) {
        this.walkId.postValue(walkId);
    }
}

