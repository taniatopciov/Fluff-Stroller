package com.example.fluffstroller.pages.profile.dogowner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fluffstroller.models.Dog;

import java.util.ArrayList;
import java.util.List;

public class DogOwnerProfileViewModel extends ViewModel {
    private final MutableLiveData<List<Dog>> dogs;
    private final MutableLiveData<String> name;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> phoneNumber;

    public DogOwnerProfileViewModel() {
        dogs = new MutableLiveData<>();
        name = new MutableLiveData<>();
        email = new MutableLiveData<>();
        phoneNumber = new MutableLiveData<>();
    }

    public LiveData<List<Dog>> getDogs() {
        return dogs;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setDogs(List<Dog> dogsList) {
        dogs.postValue(dogsList);
    }

    public void setName(String name) {
        this.name.postValue(name);
    }

    public void setEmail(String email) {
        this.email.postValue(email);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.postValue(phoneNumber);
    }

    public void clearData() {
        dogs.postValue(new ArrayList<>());
        name.postValue("");
        email.postValue("");
        phoneNumber.postValue("");
    }

    public void addDog(Dog addedDog) {
        List<Dog> dogsList = dogs.getValue();

        if (dogsList == null) {
            dogsList = new ArrayList<>();
        }

        dogsList.add(addedDog);
        dogs.postValue(dogsList);
    }
}