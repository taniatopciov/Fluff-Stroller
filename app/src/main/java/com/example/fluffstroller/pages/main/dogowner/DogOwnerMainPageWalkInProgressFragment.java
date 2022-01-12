package com.example.fluffstroller.pages.main.dogowner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.DogOwnerMainPageWalkInProgressFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkRequestStatus;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.pages.main.stroller.DogStrollerHomePageWalkInProgressFragmentDirections;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

public class DogOwnerMainPageWalkInProgressFragment extends FragmentWithServices {

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private DogWalksService dogWalksService;

    @Injectable
    private ProfileService profileService;

    private DogOwnerMainPageWalkInProgressViewModel viewModel;
    private DogOwnerMainPageWalkInProgressFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogOwnerMainPageWalkInProgressFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(DogOwnerMainPageWalkInProgressViewModel.class);

        registerSubject(profileService.listenForProfileData(loggedUserDataService.getLoggedUserId())).subscribe(response -> {
            if (response.hasErrors()) {
                CustomToast.show(requireActivity(), "Could not fetch data", Toast.LENGTH_SHORT);
                return;
            }
            loggedUserDataService.setLoggedUserData(response.data);
            DogWalkPreview walkPreview = loggedUserDataService.getLoggedUserWalkPreview();

            if (walkPreview == null || !walkPreview.getStatus().equals(WalkStatus.IN_PROGRESS) && !walkPreview.getStatus().equals(WalkStatus.WAITING_FOR_START)) {
                NavHostFragment.findNavController(this).navigate(DogOwnerMainPageWalkInProgressFragmentDirections.actionNavDogOwnerHomeWalkInProgressToNavDogOwnerHome());
                return;
            }

            String currentWalkId = walkPreview.getWalkId();

            dogWalksService.getDogWalk(currentWalkId).subscribe(response1 -> {
                DogWalk dogWalk = response1.data;
                if (response1.hasErrors() || dogWalk == null) {
                    return;
                }

                viewModel.setTotalPrice(dogWalk.getTotalPrice());
                viewModel.setDogNames(dogWalk.getDogNames());
                viewModel.setWalkStatus(dogWalk.getStatus());

                if (dogWalk.getRequests() != null && dogWalk.getRequests().size() == 1) {
                    for (WalkRequest request : dogWalk.getRequests()) {
                        if (request.getStatus().equals(WalkRequestStatus.ACCEPTED)) {
                            viewModel.setWalkRequest(request);
                            break;
                        }
                    }
                }
            });

        }, false);

        String currentWalkId = loggedUserDataService.getLoggedUserWalkPreview().getWalkId();

        if (currentWalkId.isEmpty()) {
            return binding.getRoot();
        }

        binding.includeWalkRequestDetails.acceptButton.setVisibility(View.INVISIBLE);
        binding.includeWalkRequestDetails.rejectButton.setVisibility(View.INVISIBLE);


        binding.includeWalkRequestDetails.callImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);

            String phoneNumber = viewModel.getWalkRequest().getValue().getStrollerPhoneNumber();
            intent.setData(Uri.parse("tel:" + phoneNumber));

            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Choose Call Application"));
            }
        });

        binding.includeWalkRequestDetails.visitProfileButton.setOnClickListener(view -> {
            WalkRequest walkRequest = viewModel.getWalkRequest().getValue();
            if (walkRequest == null) {
                return;
            }

            NavHostFragment.findNavController(this).navigate(DogStrollerHomePageWalkInProgressFragmentDirections.actionGlobalNavViewStrollerProfile(walkRequest.getStrollerId()));
        });

        binding.goToMapPageButton.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(DogOwnerMainPageWalkInProgressFragmentDirections.actionGlobalNavWalkInProgress());
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

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> binding.totalPriceValueTextView.setText(totalPrice + " RON"));

        viewModel.getWalkRequest().observe(getViewLifecycleOwner(), walkRequest -> {
            binding.includeWalkRequestDetails.strollerNameTextView.setText(walkRequest.getStrollerName());
            binding.includeWalkRequestDetails.strollerRatingTextView.setText(formatRating(walkRequest.getStrollerRating()));

            if (walkRequest.getStrollerPhoneNumber() == null || walkRequest.getStrollerPhoneNumber().isEmpty()) {
                binding.includeWalkRequestDetails.callImageButton.setVisibility(View.INVISIBLE);
            } else {
                binding.includeWalkRequestDetails.strollerPhoneNumberTextView.setText(walkRequest.getStrollerPhoneNumber());
            }
        });

        viewModel.getWalkStatus().observe(getViewLifecycleOwner(), walkStatus -> {
            if (!walkStatus.equals(WalkStatus.IN_PROGRESS)) {
                binding.goToMapPageButton.setEnabled(false);
                binding.walkInProgressTextView.setText(R.string.waiting_for_stroller_to_start);
            } else {
                binding.goToMapPageButton.setEnabled(true);
                binding.walkInProgressTextView.setText(R.string.walk_in_progress);
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