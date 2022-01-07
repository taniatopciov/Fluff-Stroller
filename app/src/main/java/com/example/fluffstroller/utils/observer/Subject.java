package com.example.fluffstroller.utils.observer;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Subject<T> {
    private final List<Pair<Observer<T>, Boolean>> observers = new ArrayList<>();
    private final List<Subject<?>> createdSubjects = new ArrayList<>();
    private Runnable onObserversCleared = null;

    public void setOnObserversCleared(Runnable onObserversCleared) {
        this.onObserversCleared = onObserversCleared;
    }

    public void subscribe(Observer<T> observer, boolean unsubscribeAfterNotify) {
        observers.add(new Pair<>(observer, unsubscribeAfterNotify));
    }

    public void subscribe(Observer<T> observer) {
        subscribe(observer, true);
    }

    public void unsubscribe(Observer<T> observer) {
        List<Pair<Observer<T>, Boolean>> pairsToRemove = observers.stream().filter(pair -> pair.first.equals(observer)).collect(Collectors.toList());
        for (Pair<Observer<T>, Boolean> pair : pairsToRemove) {
            observers.remove(pair);
        }
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

    public Subject<T> peek(Consumer<T> consumer) {
        Subject<T> result = new Subject<>();

        subscribe(response -> {
            if (response.hasErrors()) {
                result.notifyObservers(response.exception);
            } else {
                consumer.accept(response.data);
                result.notifyObservers(response.data);
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
        if (onObserversCleared != null) {
            onObserversCleared.run();
        }
    }

    public void notifyObservers(T state) {
        List<Pair<Observer<T>, Boolean>> pairsToRemove = new ArrayList<>();
        for (Pair<Observer<T>, Boolean> pair : observers) {
            pair.first.accept(new Response<>(state));

            if (pair.second) {
                pairsToRemove.add(pair);
            }
        }

        for (Pair<Observer<T>, Boolean> pair : pairsToRemove) {
            observers.remove(pair);
        }
    }

    public void notifyObservers(Exception error) {
        List<Pair<Observer<T>, Boolean>> pairsToRemove = new ArrayList<>();
        for (Pair<Observer<T>, Boolean> pair : observers) {
            pair.first.accept(new Response<>(error));

            if (pair.second) {
                pairsToRemove.add(pair);
            }
        }

        for (Pair<Observer<T>, Boolean> pair : pairsToRemove) {
            observers.remove(pair);
        }
    }
}
