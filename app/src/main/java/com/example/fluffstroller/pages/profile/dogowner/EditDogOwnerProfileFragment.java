package com.example.fluffstroller.pages.profile.dogowner;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fluffstroller.R;

public class EditDogOwnerProfileFragment extends Fragment {

    private EditDogOwnerProfileViewModel mViewModel;

    public static EditDogOwnerProfileFragment newInstance() {
        return new EditDogOwnerProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_dog_owner_profile_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditDogOwnerProfileViewModel.class);
        // TODO: Use the ViewModel
    }

}