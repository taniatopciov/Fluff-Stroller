package com.example.flusffstroller.utils.observer;

public interface Observer<T> {
    void accept(Response<T> response);
}
