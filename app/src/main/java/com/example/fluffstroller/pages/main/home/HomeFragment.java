package com.example.fluffstroller.pages.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.FragmentHomeBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithSubjects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class HomeFragment extends FragmentWithSubjects {

    @Injectable
    private ProfileService profileService;

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    public HomeFragment() {
        ServiceLocator.getInstance().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        registerSubject(profileService.getLoggedUser()).subscribe(response -> {
            if (response.hasErrors()) {
                response.exception.printStackTrace();
                return;
            }

            homeViewModel.changeState(response.data.getUserType(), response.data.getWalkRequest() != null);
        });


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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(binding.getRoot());

        homeViewModel.getUserType().observe(getViewLifecycleOwner(), state -> {

            if (state.userType.equals(UserType.DOG_OWNER)) {

            } else {
                if (state.hasWalkRequest) {
                    navController.navigate(HomeFragmentDirections.actionNavHomeToNavStrollerHomeWalkInProgress());
                } else {
                    navController.navigate(HomeFragmentDirections.actionNavHomeToNavStrollerHome());
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}