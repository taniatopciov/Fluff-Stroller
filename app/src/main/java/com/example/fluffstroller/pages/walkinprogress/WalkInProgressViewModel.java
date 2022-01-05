package com.example.fluffstroller.pages.walkinprogress;

import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.models.WalkInProgressModel;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WalkInProgressViewModel extends ViewModel {
    private final MutableLiveData<Integer> elapsedSeconds;
    private final MutableLiveData<Integer> distanceInMeters;
    private final MutableLiveData<List<Location>> locations;
    private final MutableLiveData<WalkInProgressModel> walkInProgressModel;

    public WalkInProgressViewModel() {
        elapsedSeconds = new MutableLiveData<>();
        distanceInMeters = new MutableLiveData<>();
        locations = new MutableLiveData<>();
        walkInProgressModel = new MutableLiveData<>();
    }

    public MutableLiveData<Integer> getElapsedSeconds() {
        return elapsedSeconds;
    }

    public MutableLiveData<Integer> getDistanceInMeters() {
        return distanceInMeters;
    }

    public MutableLiveData<List<Location>> getLocations() {
        return locations;
    }

    public MutableLiveData<WalkInProgressModel> getWalkInProgressModel() {
        return walkInProgressModel;
    }

    public void setElapsedSeconds(Integer elapsedSeconds) {
        this.elapsedSeconds.postValue(elapsedSeconds);
    }

    public void setDistanceInMeters(Integer distanceInMeters) {
        this.distanceInMeters.postValue(distanceInMeters);
    }

    public void setLocations(List<Location> locations) {
        this.locations.postValue(locations);
    }

    public void setWalkInProgressModel(WalkInProgressModel walkInProgressModel) {
        this.walkInProgressModel.postValue(walkInProgressModel);
    }
}