package com.example.flusffstroller.pages.main;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DogOwnerMainPageViewModel extends ViewModel {
    private final MutableLiveData<List<String>> dogNames;

    public DogOwnerMainPageViewModel() {
        dogNames = new MutableLiveData<>();
    }

    public LiveData<List<String>> getDogNames() {
        return dogNames;
    }

    public void setDogNames(List<String> names) {
        dogNames.setValue(names);
    }
}