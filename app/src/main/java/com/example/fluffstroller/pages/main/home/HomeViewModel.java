package com.example.fluffstroller.pages.main.home;

import com.example.fluffstroller.models.UserType;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<UserState> currentState;

    public HomeViewModel() {
        currentState = new MutableLiveData<>();
    }

    public MutableLiveData<UserState> getUserType() {
        return currentState;
    }

    public void changeState(UserType userType, Boolean isWaiting) {
        this.currentState.postValue(new UserState(userType, isWaiting));
    }

    public static class UserState {
        public final UserType userType;
        public final Boolean hasWalkRequest;

        public UserState(UserType userType, Boolean hasWalkRequest) {
            this.userType = userType;
            this.hasWalkRequest = hasWalkRequest;
        }
    }
}