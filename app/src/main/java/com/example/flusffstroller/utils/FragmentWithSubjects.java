package com.example.flusffstroller.utils;

import com.example.flusffstroller.utils.observer.Subject;
import com.example.flusffstroller.utils.observer.SubjectManager;

import androidx.fragment.app.Fragment;

public class FragmentWithSubjects extends Fragment {
    private final SubjectManager subjectManager = new SubjectManager();

    protected <T> Subject<T> registerSubject(Subject<T> subject) {
        return subjectManager.add(subject);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subjectManager.clear();
    }
}
