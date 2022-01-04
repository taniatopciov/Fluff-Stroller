package com.example.fluffstroller.pages.profile.userprofile;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileFragmentViewModel extends ViewModel {
    private final MutableLiveData<Pair<String, String>> idAndUserTypePair;

    public ProfileFragmentViewModel() {
        idAndUserTypePair = new MutableLiveData<>();
    }

    public MutableLiveData<Pair<String, String>> getIdAndUserTypePair() {
        return idAndUserTypePair;
    }

    public void setUserType(String id, String userType) {
        this.idAndUserTypePair.setValue(new Pair<>(id, userType));
    }
}
