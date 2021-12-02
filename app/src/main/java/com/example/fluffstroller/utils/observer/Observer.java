package com.example.fluffstroller.utils.observer;

public interface Observer<T> {
    void accept(Response<T> response);
}
