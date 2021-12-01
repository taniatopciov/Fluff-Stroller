package com.example.flusffstroller.utils.observer;

import java.util.ArrayList;
import java.util.List;

public class Subject<T> {
    private final List<Observer<T>> observers = new ArrayList<>();

    public void subscribe(Observer<T> observer) {
        observers.add(observer);
    }

    public void unsubscribe(Observer<T> observer) {
        observers.remove(observer);
    }

    public void clearAllObservers() {
        observers.clear();
    }

    public void notifyObservers(T state) {
        for (Observer<T> observer : observers) {
            observer.accept(state);
        }
    }

    public void notifyObservers(Exception error) {
        for (Observer<T> observer : observers) {
            observer.error(error);
        }
    }
}
