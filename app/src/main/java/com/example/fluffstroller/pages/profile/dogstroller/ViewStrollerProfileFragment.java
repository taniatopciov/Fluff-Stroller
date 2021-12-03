package com.example.fluffstroller.pages.profile.dogstroller;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fluffstroller.R;

public class ViewStrollerProfileFragment extends Fragment {

    private ViewStrollerProfileViewModel mViewModel;

    public static ViewStrollerProfileFragment newInstance() {
        return new ViewStrollerProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_stroller_profile_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ViewStrollerProfileViewModel.class);
        // TODO: Use the ViewModel
    }

}