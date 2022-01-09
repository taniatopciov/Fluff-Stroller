package com.example.fluffstroller.pages.payment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fluffstroller.models.DogWalk;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loadingCircle;
    private final MutableLiveData<Boolean> disablePayButton;
    private final MutableLiveData<DogWalk> dogWalk;

    public PaymentViewModel() {
        loadingCircle = new MutableLiveData<>();
        disablePayButton = new MutableLiveData<>();
        dogWalk = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getLoadingCircle() {
        return loadingCircle;
    }

    public MutableLiveData<Boolean> getDisablePayButton() {
        return disablePayButton;
    }

    public MutableLiveData<DogWalk> getDogWalk() {
        return dogWalk;
    }

    public void setLoadingCircle(Boolean loadingCircle) {
        this.loadingCircle.postValue(loadingCircle);
    }

    public void setDogWalk(DogWalk dogWalk) {
        this.dogWalk.postValue(dogWalk);
    }

    public void setDisablePayButton(Boolean value) {
        this.disablePayButton.postValue(value);
    }
}