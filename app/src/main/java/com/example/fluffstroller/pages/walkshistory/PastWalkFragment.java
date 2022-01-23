package com.example.fluffstroller.pages.walkshistory;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.PastWalkFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.WalkInProgressModel;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LocationService;
import com.example.fluffstroller.services.WalkInProgressService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.stream.Collectors;

public class PastWalkFragment extends FragmentWithServices implements OnMapReadyCallback {

    private PastWalkViewModel viewModel;
    private PastWalkFragmentBinding binding;

    @Injectable
    private WalkInProgressService walkInProgressService;

    @Injectable
    private DogWalksService dogWalksService;

    private GoogleMap googleMap;
    private Polyline polyline;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = PastWalkFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(PastWalkViewModel.class);

        viewModel.getLocations().observe(getViewLifecycleOwner(), locations -> {
            List<LatLng> path = drawCurrentPath(googleMap, locations);
            viewModel.setDistanceInMeters(calculateTotalDistanceInMeters(path));
        });

        viewModel.getDistanceInMeters().observe(getViewLifecycleOwner(), distance -> {
            binding.distanceValueTextView.setText(String.format("%.2f", distance));
        });

        viewModel.getElapsedSeconds().observe(getViewLifecycleOwner(), elapsedSeconds -> {
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds % 60;

            binding.durationValueTextView.setText(minutes + ":" + seconds);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.map.onCreate(savedInstanceState);
        binding.map.onResume();
        binding.map.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.clear();

        String walkId = PastWalkFragmentArgs.fromBundle(getArguments()).getWalkId();
        if (!walkId.isEmpty()) {

            dogWalksService.getDogWalk(walkId).subscribe(res -> {
                if (res.hasErrors() || res.data == null) {
                    return;
                }
                DogWalk dogWalk = res.data;

                walkInProgressService.getWalkInProgressModel(walkId).subscribe(response -> {
                    if (response.hasErrors() || response.data == null) {
                        return;
                    }
                    WalkInProgressModel walkInProgressModel = response.data;

                    viewModel.setWalkId(walkInProgressModel.getWalkId());

                    List<com.example.fluffstroller.models.Location> coordinates = walkInProgressModel.getCoordinates();
                    viewModel.setLocations(coordinates);

                    LatLng latLng = new LatLng(coordinates.get(0).latitude, coordinates.get(0).longitude);
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    if (dogWalk.getWalkStartedMillis() != null) {
                        if (dogWalk.getWalkFinishedMillis() == null) {
                            viewModel.setElapsedSeconds(dogWalk.getWalkTime() * 60L);
                        } else {
                            long elapsedMillis = dogWalk.getWalkFinishedMillis() - dogWalk.getWalkStartedMillis();
                            viewModel.setElapsedSeconds(elapsedMillis / 1000L);
                        }
                    }
                });
            });
        }
    }

    @NonNull
    private List<LatLng> drawCurrentPath(@NonNull GoogleMap googleMap, List<com.example.fluffstroller.models.Location> coordinates) {
        List<LatLng> points = coordinates.stream()
                .map(location -> new LatLng(location.latitude, location.longitude))
                .collect(Collectors.toList());

        if (points.size() > 0) {
            if (polyline != null) {
                polyline.remove();
            }

            PolylineOptions opts = new PolylineOptions()
                    .addAll(points)
                    .color(ContextCompat.getColor(requireContext(), R.color.accent))
                    .width(10);
            polyline = googleMap.addPolyline(opts);
        }

        return points;
    }

    private float calculateTotalDistanceInMeters(List<LatLng> points) {
        float totalDistance = 0;

        for (int i = 1; i < points.size(); i++) {
            Location currLocation = new Location("this");
            currLocation.setLatitude(points.get(i).latitude);
            currLocation.setLongitude(points.get(i).longitude);

            Location lastLocation = new Location("this");
            lastLocation.setLatitude(points.get(i - 1).latitude);
            lastLocation.setLongitude(points.get(i - 1).longitude);

            totalDistance += lastLocation.distanceTo(currLocation);
        }

        return totalDistance / 1000.0f;
    }
}