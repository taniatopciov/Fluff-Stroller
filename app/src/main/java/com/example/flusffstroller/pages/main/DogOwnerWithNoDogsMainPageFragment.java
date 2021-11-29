package com.example.flusffstroller.pages.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flusffstroller.databinding.DogOwnerWithNoDogsMainPageFragmentBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DogOwnerWithNoDogsMainPageFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DogOwnerWithNoDogsMainPageFragmentBinding binding = DogOwnerWithNoDogsMainPageFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}