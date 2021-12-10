package com.example.fluffstroller.pages.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fluffstroller.databinding.SplashScreenBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.utils.FragmentWithServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

public class HomeFragment extends FragmentWithServices {

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    private SplashScreenBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SplashScreenBinding.inflate(inflater, container, false);

        if (loggedUserDataService.isUserLogged()) {
            switch (loggedUserDataService.getLogUserType()) {
                case DOG_OWNER: {
                    NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionHomeFragmentToDogOwnerHomeNavigation());
                }
                break;
                case STROLLER: {
                    NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionHomeFragmentToStrollerHomeNavigation());
                }
                break;

                default:
                    break;
            }
        }

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