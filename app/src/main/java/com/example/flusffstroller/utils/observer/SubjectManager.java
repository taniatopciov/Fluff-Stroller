package com.example.flusffstroller.utils.observer;

import java.util.ArrayList;
import java.util.List;

public class SubjectManager {
    private final List<Subject<?>> subjects = new ArrayList<>();

    public <T> Subject<T> add(Subject<T> subject) {
        subjects.add(subject);
        return subject;
    }

    public void clear() {
        for (Subject<?> subject : subjects) {
            subject.clearAllObservers();
        }
        subjects.clear();
    }
}
