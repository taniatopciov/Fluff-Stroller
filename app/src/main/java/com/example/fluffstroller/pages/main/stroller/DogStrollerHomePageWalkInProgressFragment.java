package com.example.fluffstroller.pages.main.stroller;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.DogStrollerHomePageWalkInProgressFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LocationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.PermissionsService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.services.WalkInProgressService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class DogStrollerHomePageWalkInProgressFragment extends FragmentWithServices {

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private DogWalksService dogWalksService;

    @Injectable
    private WalkInProgressService walkInProgressService;

    @Injectable
    private PermissionsService permissionsService;

    @Injectable
    private LocationService locationService;

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
            permissionsService.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, permissionGranted -> {
                if (!permissionGranted) {
                    CustomToast.show(requireActivity(), "Permission denied!", Toast.LENGTH_SHORT);
                    return;
                }

                DogWalk dogWalk = viewModel.getDogWalk().getValue();
                if (dogWalk == null) {
                    return;
                }

                dogWalksService.updateDogWalk(dogWalk.getOwnerId(), dogWalk.getId(), WalkStatus.IN_PROGRESS, dogWalk.getRequests()).subscribe(response -> {
                    if (response.hasErrors()) {
                        CustomToast.show(requireActivity(), "Could set walk in progress", Toast.LENGTH_SHORT);
                        return;
                    }

                    loggedUserDataService.setDogWalkPreview(response.data);
                    walkInProgressService.startWalk(dogWalk, loggedUserDataService.getLoggedUserId());
                    setControlsForWalkInProgress();
                    locationService.startRealTimeLocationTracking(getActivity(), dogWalk.getId());
                });
            });
        });

        binding.goToMapPageButton.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(DogStrollerHomePageWalkInProgressFragmentDirections.actionGlobalNavWalkInProgress());
        });

        binding.includeAvailableWalkDetails.visitProfileButton.setOnClickListener(view -> {
            DogWalk dogWalk = viewModel.getDogWalk().getValue();
            if (dogWalk == null) {
                return;
            }

            NavHostFragment.findNavController(this).navigate(DogStrollerHomePageWalkInProgressFragmentDirections.actionGlobalNavViewDogOwnerProfile(dogWalk.getOwnerId()));
        });

        binding.includeAvailableWalkDetails.callImageButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);

            DogWalk dogWalk = viewModel.getDogWalk().getValue();
            intent.setData(Uri.parse("tel:" + dogWalk.getOwnerPhoneNumber()));

            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Choose Call Application"));
            }
        });

        viewModel.getDogWalk().observe(getViewLifecycleOwner(), dogWalk -> {
            if (dogWalk.getStatus().equals(WalkStatus.WAITING_PAYMENT) || dogWalk.getStatus().equals(WalkStatus.ADD_REVIEW)) {
                binding.startWalkButton.setVisibility(View.INVISIBLE);
                binding.titleTextView.setText(R.string.waiting_for_payment);
            } else if (dogWalk.getStatus().equals(WalkStatus.IN_PROGRESS)) {
                setControlsForWalkInProgress();
                locationService.startRealTimeLocationTracking(getActivity(), dogWalk.getId());
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
            binding.includeAvailableWalkDetails.priceValueTextView.setText(dogWalk.getTotalPrice() + " RON");
        });
        NavController navController = NavHostFragment.findNavController(this);
        registerSubject(profileService.listenForProfileData(loggedUserDataService.getLoggedUserId())).subscribe(res -> {
            if (res.hasErrors()) {
                CustomToast.show(requireActivity(), "Could not fetch data", Toast.LENGTH_SHORT);
                return;
            }
            loggedUserDataService.setLoggedUserData(res.data);
            WalkRequest currentWalkRequest = loggedUserDataService.getLoggedUserCurrentWalkRequest();

            if (currentWalkRequest == null) {
                navController.navigate(DogStrollerHomePageWalkInProgressFragmentDirections.actionNavStrollerHomeWalkInProgressToNavStrollerHome());
                return;
            }

            dogWalksService.getDogWalk(currentWalkRequest.getWalkId()).subscribe(response -> {
                if (response.hasErrors() || response.data == null) {
                    return;
                }

                viewModel.setDogWalk(response.data);
            });
        }, false);

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