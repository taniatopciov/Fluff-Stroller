package com.example.fluffstroller.reviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.fluffstroller.databinding.ReviewFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.Review;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;

public class ReviewFragment extends FragmentWithServices {

    private ReviewViewModel viewModel;
    private ReviewFragmentBinding binding;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private ProfileService profileService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ReviewFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ReviewViewModel.class);

        viewModel.getRating().observe(getViewLifecycleOwner(), rating -> binding.ratingBarReviewFragment.setRating(rating));
        viewModel.getDescription().observe(getViewLifecycleOwner(), description -> binding.descriptionTextViewWithLabelReviewFragment.setText(description));

        binding.skipReviewButton.setOnClickListener(view -> {
            //todo change walk status and navigate to payment
        });

        binding.submitReviewButton.setOnClickListener(view -> {
            int rating = binding.ratingBarReviewFragment.getNumStars();
            String description = binding.descriptionTextViewWithLabelReviewFragment.getText();

            if (rating < 1 || description.isEmpty()) {
                CustomToast.show(requireActivity(), "You must use valid data when submitting the review!",
                        Toast.LENGTH_LONG);
                return;
            }

            String strollerId = ReviewFragmentArgs.fromBundle(getArguments()).getStrollerId();
            Review review = new Review(loggedUserDataService.getLoggedUserName(), description, rating);

            profileService.updateStrollerProfile(strollerId, review).subscribe(response -> {
                if (response.hasErrors()) {
                    CustomToast.show(requireActivity(), "Error updating data",
                            Toast.LENGTH_LONG);
                    return;
                }

                //todo change walk status and navigate to payment
            });
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}