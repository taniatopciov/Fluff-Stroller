package com.example.fluffstroller.pages.payment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BalanceViewModel extends ViewModel {
    private final MutableLiveData<Double> balance;
    private final MutableLiveData<Double> transferableMoney;
    private final MutableLiveData<Boolean> loadingCircle;
    private final MutableLiveData<Boolean> disablePayButton;

    public BalanceViewModel() {
        balance = new MutableLiveData<>();
        transferableMoney = new MutableLiveData<>();
        loadingCircle = new MutableLiveData<>();
        disablePayButton = new MutableLiveData<>();
    }

    public MutableLiveData<Double> getBalance() {
        return balance;
    }

    public MutableLiveData<Double> getTransferableMoney() {
        return transferableMoney;
    }

    public MutableLiveData<Boolean> getLoadingCircle() {
        return loadingCircle;
    }

    public MutableLiveData<Boolean> getDisablePayButton() {
        return disablePayButton;
    }

    public void setBalance(Double balance) {
        this.balance.postValue(balance);
    }

    public void setTransferableMoney(Double transferableMoney) {
        this.transferableMoney.postValue(transferableMoney);
    }

    public void setLoadingCircle(Boolean value) {
        this.loadingCircle.postValue(value);
    }

    public void setDisablePayButton(Boolean value) {
        this.disablePayButton.postValue(value);
    }
}