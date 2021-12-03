package com.example.fluffstroller.pages.main.stroller;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.fluffstroller.databinding.DogStrollerHomePageFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class DogStrollerHomePageFragment extends FragmentWithServices implements OnMapReadyCallback {

    private final static int[] AREA_RADIUS_STEPS = {1, 2, 5, 10, 15};

    @Injectable
    private DogWalksService dogWalksService;

    private DogStrollerHomePageViewModel viewModel;
    private DogStrollerHomePageFragmentBinding binding;
    private GoogleMap mMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogStrollerHomePageFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(DogStrollerHomePageViewModel.class);

        AvailableWalksAdapter availableWalksAdapter = new AvailableWalksAdapter(new ArrayList<>(),
                this::handleRequestWalk,
                this::handleViewProfile,
                this::callButtonListener);
        binding.availableWalksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.availableWalksRecyclerView.setAdapter(availableWalksAdapter);

        binding.areaRadiusSeekBar.setMax(AREA_RADIUS_STEPS.length - 1);
        binding.areaRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                viewModel.setSelectedRadius(AREA_RADIUS_STEPS[progress]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        binding.areaRadiusSeekBar.setProgress(0);
        viewModel.setSelectedRadius(AREA_RADIUS_STEPS[0]);

        binding.waitingForApprovalFrameLayout.setOnClickListener(view -> {
            // must be present to prevent click propagation
        });

        viewModel.getAvailableWalks().observe(getViewLifecycleOwner(), availableWalks -> requireActivity().runOnUiThread(() -> availableWalksAdapter.setAvailableWalks(availableWalks)));

        viewModel.getSelectedRadius().observe(getViewLifecycleOwner(), radius -> {
            binding.areaRadiusValueTextView.setText(radius + " km");
        });

        viewModel.getWaitingForDogOwnerApproval().observe(getViewLifecycleOwner(), waitingForApproval -> {
            if (waitingForApproval) {
                binding.waitingForApprovalFrameLayout.setVisibility(View.VISIBLE);
                binding.controlsLayout.setVisibility(View.INVISIBLE);
                binding.availableWalksRecyclerView.setVisibility(View.INVISIBLE);
            } else {
                binding.waitingForApprovalFrameLayout.setVisibility(View.GONE);
                binding.controlsLayout.setVisibility(View.VISIBLE);
                binding.availableWalksRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        // todo replace with database call - dogWalksService listen for nearby Walks

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.map.onCreate(savedInstanceState);
        binding.map.onResume();
        binding.map.getMapAsync(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void handleRequestWalk(Pair<DogWalk, Integer> pair) {
        Toast.makeText(getContext(), "Request: " + pair.first.getOwnerName() + " " + pair.second, Toast.LENGTH_SHORT).show();
    }

    private void handleViewProfile(Pair<DogWalk, Integer> pair) {
        Toast.makeText(getContext(), "Profile: " + pair.first.getOwnerName() + " " + pair.second, Toast.LENGTH_SHORT).show();
    }

    private void callButtonListener(Pair<DogWalk, Integer> pair) {
        Toast.makeText(getContext(), "Call: " + pair.first.getOwnerName() + " " + pair.second, Toast.LENGTH_SHORT).show();
    }
}