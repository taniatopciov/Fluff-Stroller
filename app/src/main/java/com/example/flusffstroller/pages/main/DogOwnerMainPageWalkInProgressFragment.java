package com.example.flusffstroller.pages.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.flusffstroller.databinding.DogOwnerMainPageWalkInProgressFragmentBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class DogOwnerMainPageWalkInProgressFragment extends Fragment {

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
            binding.includeWalkRequestDetails.strollerPhoneNumberTextView.setText(walkRequest.getStrollerPhoneNumber());
            binding.includeWalkRequestDetails.strollerRatingTextView.setText(formatRating(walkRequest.getStrollerRating()));
        });


        // todo get data from database
        viewModel.setTotalPrice(56);
        List<String> dogNames = new ArrayList<>();
        dogNames.add("John Dog");
        dogNames.add("Jane Dog");
        dogNames.add("Jane Dog");
        dogNames.add("Jane Dog");
        dogNames.add("Jane Dog");
        dogNames.add("Jane Dog");
        dogNames.add("Jane Dog");
        dogNames.add("Jane Dog");
        dogNames.add("Jane Dog");
        viewModel.setDogNames(dogNames);

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