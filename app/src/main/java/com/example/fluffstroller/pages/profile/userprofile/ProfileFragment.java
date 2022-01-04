package com.example.fluffstroller.pages.profile.userprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.fluffstroller.databinding.SplashScreenBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.utils.FragmentWithServices;

public class ProfileFragment extends FragmentWithServices {

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    private SplashScreenBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SplashScreenBinding.inflate(inflater, container, false);

        ProfileFragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(ProfileFragmentViewModel.class);

        viewModel.getIdAndUserTypePair().observe(getViewLifecycleOwner(), pair -> {
            String id = pair.first;
            String userType = pair.second;

            String profileId;
            UserType profileUserType;

            if (id == null || userType == null) {
                profileId = loggedUserDataService.getLoggedUserId();
                profileUserType = loggedUserDataService.getLogUserType();
            } else {
                profileId = id;
                profileUserType = UserType.convertString(userType);
            }

            switch (profileUserType) {
                case DOG_OWNER: {
                    NavHostFragment.findNavController(this).navigate(ProfileFragmentDirections.actionStartProfileToViewDogOwnerProfile(profileId));
                }
                break;
                case STROLLER: {
                    NavHostFragment.findNavController(this).navigate(ProfileFragmentDirections.actionStartProfileToViewStrollerProfile(profileId));
                }
                break;

                default:
                    break;
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.hide();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
