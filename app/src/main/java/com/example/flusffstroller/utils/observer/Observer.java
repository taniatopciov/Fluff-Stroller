package com.example.flusffstroller.utils.observer;

public interface Observer<T> {
    void accept(T t);

    void error(Exception error);
}
