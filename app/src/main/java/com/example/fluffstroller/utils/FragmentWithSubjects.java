package com.example.fluffstroller.utils;

import com.example.fluffstroller.utils.observer.Subject;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class FragmentWithSubjects extends Fragment {
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
}
