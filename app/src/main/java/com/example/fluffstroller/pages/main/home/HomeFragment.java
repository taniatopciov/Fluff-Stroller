package com.example.fluffstroller.pages.main.home;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.databinding.SplashScreenBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.PermissionsService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

public class HomeFragment extends FragmentWithServices {

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private PermissionsService permissionsService;

    private SplashScreenBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SplashScreenBinding.inflate(inflater, container, false);

        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE);
        }
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsService.checkPermissions(permissions, permissionGranted -> {
            if (!permissionGranted) {
                CustomToast.show(requireActivity(), "Permission denied!", Toast.LENGTH_SHORT);
            }
        });

        if (loggedUserDataService.isUserLogged()) {
            switch (loggedUserDataService.getLogUserType()) {
                case DOG_OWNER: {
                    NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionNavHomeToNavDogOwnerHome());
                }
                break;
                case STROLLER: {
                    NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionNavHomeToNavStrollerHome());
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