package com.example.flusffstroller.utils.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Subject<T> {
    private final List<Observer<T>> observers = new ArrayList<>();
    private final List<Subject<?>> createdSubjects = new ArrayList<>();

    public void subscribe(Observer<T> observer) {
        observers.add(observer);
    }

    public void unsubscribe(Observer<T> observer) {
        observers.remove(observer);
    }

    public <R> Subject<R> mapAndSubscribe(Function<T, R> function, Observer<R> observer) {
        Subject<R> map = map(function);
        map.subscribe(observer);

        return map;
    }

    public <R> Subject<R> map(Function<T, R> function) {
        Subject<R> result = new Subject<>();

        subscribe(response -> {
            if (response.hasErrors()) {
                result.notifyObservers(response.exception);
            } else {
                result.notifyObservers(function.apply(response.data));
            }
        });

        createdSubjects.add(result);

        return result;
    }

    public void clearAllObservers() {
        for (Subject<?> subject : createdSubjects) {
            subject.clearAllObservers();
        }
        createdSubjects.clear();
        observers.clear();
    }

    public void notifyObservers(T state) {
        for (Observer<T> observer : observers) {
            observer.accept(new Response<>(state));
        }
    }

    public void notifyObservers(Exception error) {
        for (Observer<T> observer : observers) {
            observer.accept(new Response<>(error));
        }
    }
}
