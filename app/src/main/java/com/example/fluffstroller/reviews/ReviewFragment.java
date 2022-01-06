package com.example.fluffstroller.reviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fluffstroller.databinding.ReviewFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.Review;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;

public class ReviewFragment extends FragmentWithServices {

    private ReviewFragmentBinding binding;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private DogWalksService dogWalksService;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReviewFragmentBinding.inflate(inflater, container, false);

        String strollerName = ReviewFragmentArgs.fromBundle(getArguments()).getStrollerName();
        binding.strollerNameReviewFragment.setText(strollerName);

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

            String strollerId = ReviewFragmentArgs.fromBundle(getArguments()).getStrollerId();
            Review review = new Review(loggedUserDataService.getLoggedUserName(), description, (double) rating);

            profileService.updateStrollerProfile(strollerId, review).subscribe(response -> {
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
                    //todo navigate to payment
                });
    }
}