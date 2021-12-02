package com.example.fluffstroller.pages.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fluffstroller.HomePageViewModel;
import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.FragmentHomeBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomePageViewModel homeViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomePageViewModel.class);

        binding.ownerHomeButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home));
        binding.ownerHomeNoDogsButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home_no_dogs));
        binding.ownerHomeWaitingButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home_waiting_for_stroller));
        binding.ownerHomWalkInProgressButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home_walk_in_progress));

        binding.strollerHomeButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_stroller_home));
        binding.strollerHomeWalkInProgressButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_stroller_home_walk_in_progress));

        binding.walkInProgressButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_walk_in_progress));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}