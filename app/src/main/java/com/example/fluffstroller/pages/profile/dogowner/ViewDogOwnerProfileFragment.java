package com.example.fluffstroller.pages.profile.dogowner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fluffstroller.databinding.ViewDogOwnerProfileFragmentBinding;

public class ViewDogOwnerProfileFragment extends Fragment {

    private DogOwnerProfileViewModel mViewModel;

    private ViewDogOwnerProfileFragmentBinding binding;

    public static ViewDogOwnerProfileFragment newInstance() {
        return new ViewDogOwnerProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = ViewDogOwnerProfileFragmentBinding.inflate(inflater, container, false);

        binding.editProfileButtonViewDogOwnerProfile.setOnClickListener(view -> {
            Toast.makeText(this.getContext(), "edit button",
                    Toast.LENGTH_LONG).show();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}