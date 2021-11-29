package com.example.flusffstroller.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flusffstroller.R;
import com.example.flusffstroller.databinding.FragmentHomeBinding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.ownerHomeButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home));

        binding.ownerHomeNoDogsButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home_no_dogs));

        binding.ownerHomeWaitingButton.setOnClickListener(view -> {

        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}