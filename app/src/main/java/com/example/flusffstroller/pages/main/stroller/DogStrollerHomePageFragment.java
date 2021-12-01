package com.example.flusffstroller.pages.main.stroller;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flusffstroller.databinding.DogStrollerHomePageFragmentBinding;
import com.example.flusffstroller.models.AvailableWalk;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class DogStrollerHomePageFragment extends Fragment implements OnMapReadyCallback {

    private DogStrollerHomePageViewModel viewModel;
    private DogStrollerHomePageFragmentBinding binding;
    private GoogleMap mMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogStrollerHomePageFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(DogStrollerHomePageViewModel.class);

        AvailableWalksAdapter availableWalksAdapter = new AvailableWalksAdapter(new ArrayList<>(),
                this::handleRequestWalk,
                this::handleViewProfile);
        binding.availableWalksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.availableWalksRecyclerView.setAdapter(availableWalksAdapter);

        viewModel.getAvailableWalks().observe(getViewLifecycleOwner(), availableWalks -> requireActivity().runOnUiThread(() -> availableWalksAdapter.setAvailableWalks(availableWalks)));

        // todo replace with database call
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        List<AvailableWalk> availableWalks = new ArrayList<>();
                        availableWalks.add(new AvailableWalk("1", "1234", "Owner1", new ArrayList<>(Arrays.asList("John Dog", "Dog1", "Dog2", "Jane Dog", "James Dogg", "Snoop Dogg")), 20, 24));
                        availableWalks.add(new AvailableWalk("2", "abc", "Owner2", new ArrayList<>(Arrays.asList("John Dog", "Jane Dog")), 50, 15));
                        availableWalks.add(new AvailableWalk("3", "0863", "Owner3", new ArrayList<>(Collections.singletonList("Johny Dog")), 6, 14));

                        if (availableWalks.size() > 0) {
//                            binding.noAvailableWalksNearbyTextView.setVisibility(View.INVISIBLE);
                        } else {
//                            binding.noAvailableWalksNearbyTextView.setVisibility(View.VISIBLE);
                        }

                        viewModel.setAvailableWalks(availableWalks);

                        cancel();
                    }
                },
                1000
        );


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

    private void handleRequestWalk(Pair<AvailableWalk, Integer> pair) {
        Toast.makeText(getContext(), "Request: " + pair.first.getDogOwnerName() + " " + pair.second, Toast.LENGTH_SHORT).show();
    }

    private void handleViewProfile(Pair<AvailableWalk, Integer> pair) {
        Toast.makeText(getContext(), "Profile: " + pair.first.getDogOwnerName() + " " + pair.second, Toast.LENGTH_SHORT).show();
    }
}