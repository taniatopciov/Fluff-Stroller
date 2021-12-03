package com.example.fluffstroller.pages.main.dogowner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.DogOwnerMainPageWalkInProgressFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class DogOwnerMainPageWalkInProgressFragment extends FragmentWithServices {

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private DogWalksService dogWalksService;

    private DogOwnerMainPageWalkInProgressViewModel viewModel;
    private DogOwnerMainPageWalkInProgressFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogOwnerMainPageWalkInProgressFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(DogOwnerMainPageWalkInProgressViewModel.class);

        binding.includeWalkRequestDetails.acceptButton.setVisibility(View.INVISIBLE);
        binding.includeWalkRequestDetails.rejectButton.setVisibility(View.INVISIBLE);


        binding.includeWalkRequestDetails.callImageButton.setOnClickListener(view -> {
            // todo implement call
            Snackbar.make(view, "Call", Snackbar.LENGTH_SHORT).show();
        });

        binding.includeWalkRequestDetails.visitProfileButton.setOnClickListener(view -> {
            // todo implement visit profile
            Snackbar.make(view, "Visit Profile", Snackbar.LENGTH_SHORT).show();
        });

        binding.goToMapPageButton.setOnClickListener(view -> {
            // todo implement go to map
            Snackbar.make(view, "Go To Map", Snackbar.LENGTH_SHORT).show();
        });

        viewModel.getDogNames().observe(getViewLifecycleOwner(), dogNames -> {
            String concatenatedDogNames = "";

            if (dogNames != null) {
                concatenatedDogNames = dogNames.stream().reduce("", (s, s2) -> s + s2 + ", ");
                int lastIndex = concatenatedDogNames.lastIndexOf(", ");
                if (lastIndex >= 0) {
                    concatenatedDogNames = concatenatedDogNames.substring(0, lastIndex);
                }
            }

            binding.dogsValueTextView.setText(concatenatedDogNames);
        });

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> binding.totalPriceValueTextView.setText(totalPrice + " $"));

        viewModel.getWalkRequest().observe(getViewLifecycleOwner(), walkRequest -> {
            binding.includeWalkRequestDetails.strollerNameTextView.setText(walkRequest.getStrollerName());
            binding.includeWalkRequestDetails.strollerRatingTextView.setText(formatRating(walkRequest.getStrollerRating()));

            if (walkRequest.getStrollerPhoneNumber() == null || walkRequest.getStrollerPhoneNumber().isEmpty()) {
                binding.includeWalkRequestDetails.callImageButton.setVisibility(View.INVISIBLE);
            } else {
                binding.includeWalkRequestDetails.strollerPhoneNumberTextView.setText(walkRequest.getStrollerPhoneNumber());
            }
        });


        String currentWalkId = loggedUserDataService.getLoggedUserCurrentWalkId();

        if (currentWalkId.isEmpty()) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_dog_owner_home);
            return binding.getRoot();
        }

        dogWalksService.getDogWalk(currentWalkId).subscribe(response -> {
            if (response.hasErrors() || response.data == null) {
                return;
            }

            viewModel.setTotalPrice(response.data.getTotalPrice());
            viewModel.setDogNames(response.data.getDogNames());

            if (response.data.getRequests() != null && response.data.getRequests().size() == 1) {
                viewModel.setWalkRequest(response.data.getRequests().get(0));
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String formatRating(Double rating) {
        return rating + "/5";
    }

}