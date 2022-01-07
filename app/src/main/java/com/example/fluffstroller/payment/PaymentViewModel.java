package com.example.fluffstroller.payment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PaymentViewModel extends ViewModel {
    private final MutableLiveData<Boolean> loadingCircle;
    private final MutableLiveData<Void> disablePayButton;

    public PaymentViewModel() {
        loadingCircle = new MutableLiveData<>();
        disablePayButton = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getLoadingCircle() {
        return loadingCircle;
    }

    public MutableLiveData<Void> getDisablePayButton() {
        return disablePayButton;
    }

    public void setLoadingCircle(Boolean loadingCircle) {
        this.loadingCircle.postValue(loadingCircle);
    }

    public void disablePayButton() {
        this.disablePayButton.postValue(null);
    }
}