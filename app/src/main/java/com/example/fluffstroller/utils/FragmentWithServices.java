package com.example.fluffstroller.utils;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class FragmentWithServices extends Fragment {
    private final List<Subject<?>> subjects = new ArrayList<>();

    protected <T> Subject<T> registerSubject(Subject<T> subject) {
        subjects.add(subject);
        return subject;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        for (Subject<?> subject : subjects) {
            subject.clearAllObservers();
        }
        subjects.clear();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServiceLocator.getInstance().inject(this);
    }
}
