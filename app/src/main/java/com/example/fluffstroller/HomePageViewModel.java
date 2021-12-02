package com.example.fluffstroller;

import com.example.fluffstroller.models.DogOwnerProfileData;
import com.example.fluffstroller.models.ProfileData;
import com.example.fluffstroller.models.StrollerProfileData;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomePageViewModel extends ViewModel {

    private final MutableLiveData<StrollerProfileData> strollerProfileData;
    private final MutableLiveData<DogOwnerProfileData> dogOwnerProfileData;

    public HomePageViewModel() {
        strollerProfileData = new MutableLiveData<>();
        dogOwnerProfileData = new MutableLiveData<>();
    }

    public MutableLiveData<StrollerProfileData> getStrollerProfileData() {
        return strollerProfileData;
    }

    public MutableLiveData<DogOwnerProfileData> getDogOwnerProfileData() {
        return dogOwnerProfileData;
    }

    public void setProfileData(ProfileData profileData) {
        try {
            switch (profileData.getUserType()) {
                case DOG_OWNER:
                    this.dogOwnerProfileData.postValue((DogOwnerProfileData) profileData);

                    break;
                case STROLLER:
                    this.strollerProfileData.postValue((StrollerProfileData) profileData);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
