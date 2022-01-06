package com.example.fluffstroller.pages.walkinprogress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fluffstroller.databinding.WalkInProgressFragmentBinding;
import com.example.fluffstroller.utils.components.CustomToast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class WalkInProgressPage extends Fragment implements OnMapReadyCallback {
    private WalkInProgressFragmentBinding binding;
    private GoogleMap mMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WalkInProgressFragmentBinding.inflate(inflater, container, false);

        WalkInProgressViewModel viewModel = new ViewModelProvider(this).get(WalkInProgressViewModel.class);

        binding.finishWalkButton.setOnClickListener(view -> {
            CustomToast.show(requireActivity(), "Finish Walk",
                    Toast.LENGTH_LONG);
        });

        viewModel.getDistanceInMeters().observe(getViewLifecycleOwner(), distance -> {
            binding.distanceValueTextView.setText(distance + "");
        });

        viewModel.getElapsedSeconds().observe(getViewLifecycleOwner(), elapsedSeconds -> {
            int minutes = elapsedSeconds / 60;
            int seconds = elapsedSeconds % 60;

            binding.durationValueTextView.setText(minutes + ":" + seconds);
        });

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
}