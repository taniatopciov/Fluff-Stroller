package com.example.fluffstroller.pages.main.stroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.DogStrollerHomePageWalkInProgressFragmentBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class DogStrollerHomePageWalkInProgressFragment extends Fragment {

    private DogStrollerHomePageWalkInProgressViewModel mViewModel;
    private DogStrollerHomePageWalkInProgressFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogStrollerHomePageWalkInProgressFragmentBinding.inflate(inflater, container, false);

        // todo add call button and phone number to includeAvailableWalkDetails
        binding.includeAvailableWalkDetails.dogNamesTextView.setVisibility(View.INVISIBLE);
        binding.includeAvailableWalkDetails.requestButton.setVisibility(View.INVISIBLE);

        binding.startWalkButton.setOnClickListener(view -> {
            setControlsForWalkInProgress();
        });

        binding.goToMapPageButton.setOnClickListener(view -> {
            Snackbar.make(view, "Go to map", Snackbar.LENGTH_SHORT).show();
        });

        binding.includeAvailableWalkDetails.visitProfileButton.setOnClickListener(view -> {
            // todo implement visit profile
            Snackbar.make(view, "Visit Profile", Snackbar.LENGTH_SHORT).show();
        });

        binding.includeAvailableWalkDetails.callImageButton.setOnClickListener(view -> {
            // todo implement call
            Snackbar.make(view, "Call", Snackbar.LENGTH_SHORT).show();
        });

        mViewModel = new ViewModelProvider(this).get(DogStrollerHomePageWalkInProgressViewModel.class);

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