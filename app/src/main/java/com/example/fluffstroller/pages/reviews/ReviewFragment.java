package com.example.fluffstroller.pages.reviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.fluffstroller.databinding.ReviewFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.Review;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;

public class ReviewFragment extends FragmentWithServices {

    private ReviewFragmentBinding binding;
    private ReviewViewModel viewModel;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private DogWalksService dogWalksService;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReviewFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        String dogWalkId = ReviewFragmentArgs.fromBundle(getArguments()).getDogWalkId();

        dogWalksService.getDogWalk(dogWalkId).subscribe(response -> {
            if (response.hasErrors() || response.data == null) {
                CustomToast.show(requireActivity(), "Could not get current dog walk",
                        Toast.LENGTH_LONG);
                return;
            }

            DogWalk dogWalk = response.data;
            WalkRequest walkRequest = dogWalk.getAcceptedRequest();
            if (walkRequest != null) {
                viewModel.setStrollerName(walkRequest.getStrollerName());
                viewModel.setStrollerId(walkRequest.getStrollerId());
            }
        });

        viewModel.getStrollerName().observe(getViewLifecycleOwner(), name -> binding.strollerNameReviewFragment.setText(name));

        binding.skipReviewButton.setOnClickListener(view -> {
            changeWalkStatusAndNavigateToPayment();
        });

        binding.submitReviewButton.setOnClickListener(view -> {
            float rating = binding.ratingBarReviewFragment.getRating();
            String description = binding.descriptionTextViewWithLabelReviewFragment.getText();

            if (rating < 1 || description.isEmpty()) {
                CustomToast.show(requireActivity(), "You must use valid data when submitting the review!",
                        Toast.LENGTH_LONG);
                return;
            }

            Review review = new Review(loggedUserDataService.getLoggedUserName(), description, (double) rating);

            profileService.updateStrollerProfile(viewModel.getStrollerId().getValue(), review).subscribe(response -> {
                if (response.hasErrors()) {
                    CustomToast.show(requireActivity(), "Error updating data",
                            Toast.LENGTH_LONG);
                    return;
                }

                changeWalkStatusAndNavigateToPayment();
            });
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void changeWalkStatusAndNavigateToPayment() {
        dogWalksService.updateDogWalk(loggedUserDataService.getLoggedUserId(), loggedUserDataService.getCurrentWalkId(), WalkStatus.WAITING_PAYMENT, null)
                .subscribe(response -> {
                    if (response.hasErrors()) {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Could not update current walk status", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    loggedUserDataService.setDogWalkPreview(response.data);
                    NavHostFragment.findNavController(this).navigate(ReviewFragmentDirections.actionNavReviewToPaymentFragment(response.data.getWalkId()));
                });
    }
}