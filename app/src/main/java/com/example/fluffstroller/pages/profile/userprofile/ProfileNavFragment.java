package com.example.fluffstroller.pages.profile.userprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.fluffstroller.databinding.UserProfileBinding;
import com.example.fluffstroller.utils.FragmentWithServices;

public class ProfileNavFragment extends FragmentWithServices {

    private UserProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = UserProfileBinding.inflate(inflater, container, false);

        ProfileFragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(ProfileFragmentViewModel.class);

        String id = ProfileNavFragmentArgs.fromBundle(getArguments()).getId();
        String userType = ProfileNavFragmentArgs.fromBundle(getArguments()).getUserType();

        viewModel.setUserType(id, userType);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
