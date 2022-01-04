package com.example.fluffstroller.pages.main.stroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.DogStrollerHomePageWalkInProgressFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

public class DogStrollerHomePageWalkInProgressFragment extends FragmentWithServices {

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private DogWalksService dogWalksService;

    @Injectable
    private ProfileService profileService;

    private DogStrollerHomePageWalkInProgressViewModel viewModel;
    private DogStrollerHomePageWalkInProgressFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogStrollerHomePageWalkInProgressFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(DogStrollerHomePageWalkInProgressViewModel.class);

        if (loggedUserDataService.getLoggedUserCurrentWalkRequest() == null) {
            NavHostFragment.findNavController(this).navigate(DogStrollerHomePageWalkInProgressFragmentDirections.actionNavStrollerHomeWalkInProgressToNavStrollerHome());
            return binding.getRoot();
        }

        binding.includeAvailableWalkDetails.dogNamesTextView.setVisibility(View.INVISIBLE);
        binding.includeAvailableWalkDetails.requestButton.setVisibility(View.INVISIBLE);

        binding.startWalkButton.setOnClickListener(view -> {
            DogWalk dogWalk = viewModel.getDogWalk().getValue();
            if (dogWalk == null) {
                return;
            }
            dogWalk.setStatus(WalkStatus.IN_PROGRESS);
            dogWalksService.updateDogWalk(dogWalk).subscribe(response -> {
                if (response.hasErrors()) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Could set walk in progress", Toast.LENGTH_SHORT).show());
                    return;
                }

                DogWalkPreview walkPreview = new DogWalkPreview(dogWalk.getId(), WalkStatus.IN_PROGRESS);
                profileService.updateDogWalkPreview(dogWalk.getOwnerId(), walkPreview).subscribe(response1 -> {
                    if (response1.hasErrors()) {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Could set walk preview in progress", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    setControlsForWalkInProgress();
                });
            });
        });

        binding.goToMapPageButton.setOnClickListener(view -> {
            // todo go to map page
//            NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionGlobalNavWalkInProgress());
        });

        binding.includeAvailableWalkDetails.visitProfileButton.setOnClickListener(view -> {
            // todo implement visit profile
            Snackbar.make(view, "Visit Profile", Snackbar.LENGTH_SHORT).show();
        });

        binding.includeAvailableWalkDetails.callImageButton.setOnClickListener(view -> {
            // todo implement call
            Snackbar.make(view, "Call", Snackbar.LENGTH_SHORT).show();
        });

        viewModel.getDogWalk().observe(getViewLifecycleOwner(), dogWalk -> {
            if (dogWalk.getStatus().equals(WalkStatus.IN_PROGRESS)) {
                setControlsForWalkInProgress();
            }

            String concatenatedDogNames = "";
            if (dogWalk.getDogNames() != null) {
                concatenatedDogNames = dogWalk.getDogNames().stream().reduce("", (s, s2) -> s + s2 + ", ");
                int lastIndex = concatenatedDogNames.lastIndexOf(", ");
                if (lastIndex >= 0) {
                    concatenatedDogNames = concatenatedDogNames.substring(0, lastIndex);
                }
            }
            binding.dogsValueTextView.setText(concatenatedDogNames);

            if (dogWalk.getOwnerPhoneNumber() == null || dogWalk.getOwnerPhoneNumber().isEmpty()) {
                binding.includeAvailableWalkDetails.callImageButton.setVisibility(View.INVISIBLE);
            } else {
                binding.includeAvailableWalkDetails.phoneNumberTextView.setText(dogWalk.getOwnerPhoneNumber());
            }

            binding.includeAvailableWalkDetails.dogOwnerNameTextView.setText(dogWalk.getOwnerName());
            binding.includeAvailableWalkDetails.walkingTimeValueTextView.setText(dogWalk.getWalkTime() + " minutes");
            binding.includeAvailableWalkDetails.priceValueTextView.setText(dogWalk.getTotalPrice() + " $");
        });

        WalkRequest currentWalkRequest = loggedUserDataService.getLoggedUserCurrentWalkRequest();

        if (currentWalkRequest == null) {
            return binding.getRoot();
        }

        dogWalksService.getDogWalk(currentWalkRequest.getWalkId()).subscribe(response -> {
            if (response.hasErrors() || response.data == null) {
                return;
            }

            viewModel.setDogWalk(response.data);
        });

        return binding.getRoot();
    }

    private void setControlsForWalkInProgress() {
        binding.startWalkButton.setVisibility(View.GONE);
        binding.goToMapPageButton.setVisibility(View.VISIBLE);
        binding.titleTextView.setText(R.string.walk_in_progress);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}