package com.example.fluffstroller.pages.main.stroller;

import android.graphics.Color;
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
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LocationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;
import com.example.fluffstroller.utils.components.EnableLocationPopupDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class DogStrollerHomePageFragment extends FragmentWithServices implements OnMapReadyCallback {

    private final static int[] AREA_RADIUS_STEPS = {1, 2, 5, 10, 15};
    private static final String STROLLER_MAIN_PAGE_FRAGMENT = "STROLLER_MAIN_PAGE_FRAGMENT";
    private static final double METER_TO_KM = 1000.0;

    @Injectable
    private DogWalksService dogWalksService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LocationService locationService;

    private DogStrollerHomePageViewModel viewModel;
    private DogStrollerHomePageFragmentBinding binding;
    private GoogleMap googleMap;
    private final List<Marker> markerList = new ArrayList<>();
    private Circle mapsCircle;

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
                getAvailableDogWalks();
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

        viewModel.getAvailableWalks().observe(getViewLifecycleOwner(), availableWalks -> {
            requireActivity().runOnUiThread(() -> availableWalksAdapter.setAvailableWalks(availableWalks));

            if (googleMap == null) {
                return;
            }

            for (Marker marker : markerList) {
                marker.remove();
            }

            markerList.clear();

            for (DogWalk walk : availableWalks) {
                Location location = walk.getLocation();

                String dogNames = "";
                if (walk.getDogNames() != null) {
                    dogNames = walk.getDogNames().stream().reduce("", (s, s2) -> s + s2 + ", ");
                    int lastIndex = dogNames.lastIndexOf(", ");
                    if (lastIndex >= 0) {
                        dogNames = dogNames.substring(0, lastIndex);
                    }
                }

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .title(walk.getOwnerName() + " - Dogs:" + dogNames);

                markerList.add(googleMap.addMarker(markerOptions));
            }
        });

        viewModel.getSelectedRadius().observe(getViewLifecycleOwner(), radius -> {
            binding.areaRadiusValueTextView.setText(radius + " km");

            if (googleMap == null || mapsCircle == null) {
                return;
            }

            locationService.getCurrentLocation(getActivity()).subscribe(locationResponse -> {
                if (locationResponse.hasErrors()) {
                    CustomToast.show(requireActivity(), "Could not get current location",
                            Toast.LENGTH_LONG);
                    return;
                }

                if (locationResponse.data == null) {
                    new EnableLocationPopupDialog().show(getChildFragmentManager(), STROLLER_MAIN_PAGE_FRAGMENT);
                    return;
                }

                mapsCircle.setCenter(new LatLng(locationResponse.data.latitude, locationResponse.data.longitude));
                mapsCircle.setRadius(radius * METER_TO_KM);
            });
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

        WalkRequest walkRequest = loggedUserDataService.getLoggedUserCurrentWalkRequest();
        if (walkRequest != null) {

            switch (walkRequest.getStatus()) {
                case PENDING: {
                    viewModel.setWaitingForDogOwnerApproval(true);
                }
                break;

                case ACCEPTED: {
                    NavHostFragment.findNavController(this).navigate(DogStrollerHomePageFragmentDirections.actionNavStrollerHomeToNavStrollerHomeWalkInProgress());
                    return binding.getRoot();
                }

                case REJECTED:
                case CANCELED: {
                    viewModel.setWaitingForDogOwnerApproval(false);
                    // todo add notification for rejected or canceled walkRequest making

                    profileService.updateCurrentRequest(loggedUserDataService.getLoggedUserId(), null).subscribe(response -> {
                        if (response.hasErrors()) {
                            return;
                        }

                        loggedUserDataService.setCurrentRequest(null);
                        viewModel.setWaitingForDogOwnerApproval(false);
                    });
                }
                break;
            }
        } else {
            viewModel.setWaitingForDogOwnerApproval(false);
        }

        getAvailableDogWalks();

        return binding.getRoot();
    }

    private void getAvailableDogWalks() {
        locationService.getCurrentLocation(getActivity()).subscribe(locationResponse -> {
            if (locationResponse.hasErrors()) {
                CustomToast.show(requireActivity(), "Could not get current location",
                        Toast.LENGTH_LONG);
                return;
            }

            if (locationResponse.data == null) {
                new EnableLocationPopupDialog().show(getChildFragmentManager(), STROLLER_MAIN_PAGE_FRAGMENT);
                return;
            }

            String id = loggedUserDataService.getLoggedUserId();
            Integer value = viewModel.getSelectedRadius().getValue();
            if (value == null) {
                value = AREA_RADIUS_STEPS[0];
            }
            Double radius = Double.valueOf(value);

            dogWalksService.getNearbyAvailableDogWalks(id, locationResponse.data, radius).subscribe(response -> {
                if (response.hasErrors() || response.data == null) {
                    CustomToast.show(requireActivity(), "Could not get nearby walks",
                            Toast.LENGTH_LONG);
                    return;
                }

                viewModel.setAvailableWalks(response.data);
            });
        });
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        FragmentActivity activity = getActivity();

        if (activity == null) {
            return;
        }

        locationService.getCurrentLocation(activity).subscribe(locationResponse -> {
            if (locationResponse.hasErrors()) {
                CustomToast.show(activity, "Could not get current location",
                        Toast.LENGTH_LONG);
                return;
            }

            if (locationResponse.data == null) {
                new EnableLocationPopupDialog().show(getChildFragmentManager(), STROLLER_MAIN_PAGE_FRAGMENT);
                return;
            }

            LatLng latLng = new LatLng(locationResponse.data.latitude, locationResponse.data.longitude);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));


            Integer radius = viewModel.getSelectedRadius().getValue();
            if (radius == null) {
                radius = AREA_RADIUS_STEPS[0];
            }
            CircleOptions circleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(radius * METER_TO_KM)
                    .strokeColor(Color.BLUE);
            mapsCircle = googleMap.addCircle(circleOptions);
        });
    }

    private void handleRequestWalk(Pair<DogWalk, Integer> pair) {
        DogWalk dogWalk = pair.first;
        String strollerId = loggedUserDataService.getLoggedUserId();
        String strollerName = loggedUserDataService.getLoggedUserName();
        String strollerPhoneNumber = loggedUserDataService.getLoggedUserPhoneNumber();
        Double strollerRating = loggedUserDataService.getLoggedUserRating();
        WalkRequest walkRequest = new WalkRequest(dogWalk.getId(), strollerId, strollerName, strollerPhoneNumber, strollerRating);

        dogWalksService.requestWalk(walkRequest).subscribe(response -> {
            if (response.hasErrors()) {
                response.exception.printStackTrace();
                return;
            }
            viewModel.setWaitingForDogOwnerApproval(true);
        });
    }

    private void handleViewProfile(Pair<DogWalk, Integer> pair) {
        Toast.makeText(getContext(), "Profile: " + pair.first.getOwnerName() + " " + pair.second, Toast.LENGTH_SHORT).show();
    }

    private void callButtonListener(Pair<DogWalk, Integer> pair) {
        Toast.makeText(getContext(), "Call: " + pair.first.getOwnerName() + " " + pair.second, Toast.LENGTH_SHORT).show();
    }
}