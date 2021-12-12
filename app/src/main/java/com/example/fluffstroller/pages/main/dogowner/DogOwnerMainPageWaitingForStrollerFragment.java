package com.example.fluffstroller.pages.main.dogowner;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.databinding.DogOwnerMainPageWaitingForStrollerFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.services.RemoveDogWalkService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.InfoPopupDialog;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

public class DogOwnerMainPageWaitingForStrollerFragment extends FragmentWithServices {

    private static final long DOG_WALK_AVAILABLE_TIME_MILLIS = 600000L;
    private static final String DOG_OWNER_MAIN_PAGE_WAITING_FOR_STROLLER_FRAGMENT = "DOG_OWNER_MAIN_PAGE_WAITING_FOR_STROLLER_FRAGMENT";

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private DogWalksService dogWalksService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private RemoveDogWalkService removeDogWalkService;

    private final AtomicLong remainingTimeAtomic = new AtomicLong();

    private DogOwnerMainPageWaitingForStrollerViewModel viewModel;
    private DogOwnerMainPageWaitingForStrollerFragmentBinding binding;
    private Timer timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogOwnerMainPageWaitingForStrollerFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(DogOwnerMainPageWaitingForStrollerViewModel.class);

        DogWalkPreview walkPreview = loggedUserDataService.getLoggedUserWalkPreview();

        if (walkPreview == null) {
            NavHostFragment.findNavController(this).navigate(DogOwnerMainPageWaitingForStrollerFragmentDirections.actionNavDogOwnerHomeWaitingForStrollerToNavDogOwnerHome());
            return binding.getRoot();
        }

        if (walkPreview.getStatus() == WalkStatus.IN_PROGRESS) {
            NavHostFragment.findNavController(this).navigate(DogOwnerMainPageWaitingForStrollerFragmentDirections.actionNavDogOwnerHomeWaitingForStrollerToNavDogOwnerHomeWalkInProgress());
            return binding.getRoot();
        }

        String currentWalkId = walkPreview.getWalkId();

        if (currentWalkId.isEmpty()) {
            return binding.getRoot();
        }


        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        binding.cancelWalkButton.setOnClickListener(view -> removeCurrentWalk());

        WalkRequestAdapter walkRequestAdapter = new WalkRequestAdapter(new ArrayList<>(),
                this::handleRequestAccepted, this::handleRequestRejected,
                this::handleRequestViewProfile, this::handleRequestCall);
        binding.requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.requestsRecyclerView.setAdapter(walkRequestAdapter);

        viewModel.getCurrentTime().observe(getViewLifecycleOwner(), this::updateWaitingTime);

        viewModel.getTimerExpired().observe(getViewLifecycleOwner(), unused -> {
            if (timer != null) {
                timer.cancel();
            }
            new InfoPopupDialog("Searching for Stroller has expired", this::removeCurrentWalk)
                    .show(getChildFragmentManager(), DOG_OWNER_MAIN_PAGE_WAITING_FOR_STROLLER_FRAGMENT);
        });

        viewModel.getWalkCreationTimeMillis().observe(getViewLifecycleOwner(), walkCreationMillis -> {

            long currentTimeMillis = System.currentTimeMillis();
            long remainingTime = walkCreationMillis + DOG_WALK_AVAILABLE_TIME_MILLIS - currentTimeMillis;
            if (remainingTime <= 0) {
                new InfoPopupDialog("Searching for Stroller has expired", this::removeCurrentWalk)
                        .show(getChildFragmentManager(), DOG_OWNER_MAIN_PAGE_WAITING_FOR_STROLLER_FRAGMENT);
                return;
            }

            remainingTimeAtomic.set(remainingTime);
            viewModel.setCurrentTime(remainingTime);

            timer.cancel();
            timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    remainingTimeAtomic.set(remainingTimeAtomic.get() - 1000);
                    viewModel.setCurrentTime(remainingTimeAtomic.get());
                    if (remainingTimeAtomic.get() < 0) {
                        cancel();
                        timer.cancel();
                        viewModel.setTimerExpired();
                    }
                }
            }, 0, 1000);
        });

        viewModel.getWalkRequests().observe(getViewLifecycleOwner(), walkRequests -> {

            if (walkRequests == null) {
                walkRequests = new ArrayList<>();
            }

            if (walkRequests.isEmpty()) {
                binding.noRequestsTextView.setVisibility(View.VISIBLE);
            } else {
                binding.noRequestsTextView.setVisibility(View.INVISIBLE);
            }

            walkRequestAdapter.setWalkRequests(walkRequests);
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

            binding.dogsTextView.setText(concatenatedDogNames);
        });

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> binding.totalPriceTextView.setText(totalPrice + " $"));

        viewModel.getWalkTime().observe(getViewLifecycleOwner(), walkTime -> binding.initialWalkTimeTextView.setText(walkTime + " minutes"));

        registerSubject(dogWalksService.listenForDogWalkChanges(currentWalkId)).subscribe(response -> {
            if (response.hasErrors() || response.data == null) {
                return;
            }

            viewModel.setCurrentDogWalkDetails(response.data);
        }, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (timer != null) {
            timer.cancel();
        }
    }

    private void removeCurrentWalk() {
        DogWalkPreview preview = loggedUserDataService.getLoggedUserWalkPreview();
        if (preview == null) {
            return;
        }

        removeDogWalkService.removeCurrentWalk(preview.getWalkId(), loggedUserDataService.getLoggedUserId()).subscribe(response -> {
            if (response.hasErrors()) {
                Toast.makeText(getContext(), "Could not remove Walk", Toast.LENGTH_SHORT).show();
                return;
            }
            if (timer != null) {
                timer.cancel();
            }

            loggedUserDataService.setDogWalkPreview(null);
            NavHostFragment.findNavController(this).navigate(DogOwnerMainPageWaitingForStrollerFragmentDirections.actionNavDogOwnerHomeWaitingForStrollerToNavDogOwnerHome());
        });
    }

    private void updateWaitingTime(Long millisUntilFinished) {
        if (millisUntilFinished < 0) {
            return;
        }
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;

        String text = String.format(Locale.getDefault(), "(%02d:%02d minutes remaining)", minutes, seconds);
        binding.waitingForStrollerTimeTextView.setText(text);
    }

    private void handleRequestAccepted(Pair<WalkRequest, Integer> requestPair) {
        // todo clear all other stroller requests
        dogWalksService.setWalkInProgress(requestPair.first.getWalkId()).subscribe(response -> {
            if (response.hasErrors()) {
                Toast.makeText(getContext(), "Could set walk in progress", Toast.LENGTH_SHORT).show();
                return;
            }

            DogWalkPreview walkPreview = loggedUserDataService.getLoggedUserWalkPreview();
            walkPreview.setStatus(WalkStatus.IN_PROGRESS);
            profileService.updateDogWalkPreview(loggedUserDataService.getLoggedUserId(), walkPreview).subscribe(response1 -> {
                if (response1.hasErrors()) {
                    Toast.makeText(getContext(), "Could set walk preview in progress", Toast.LENGTH_SHORT).show();
                    return;
                }

                loggedUserDataService.setDogWalkPreview(walkPreview);
                NavHostFragment.findNavController(this).navigate(DogOwnerMainPageWaitingForStrollerFragmentDirections.actionNavDogOwnerHomeWaitingForStrollerToNavDogOwnerHomeWalkInProgress());
            });
        });
    }

    private void handleRequestRejected(Pair<WalkRequest, Integer> requestPair) {
        Toast.makeText(getContext(), "Rejected: " + requestPair.first.getStrollerName() + " " + requestPair.second, Toast.LENGTH_SHORT).show();
    }

    private void handleRequestViewProfile(Pair<WalkRequest, Integer> requestPair) {
        Toast.makeText(getContext(), "Profile: " + requestPair.first.getStrollerName() + " " + requestPair.second, Toast.LENGTH_SHORT).show();
    }

    private void handleRequestCall(Pair<WalkRequest, Integer> requestPair) {
        Toast.makeText(getContext(), "Call: " + requestPair.first.getStrollerName() + " " + requestPair.second, Toast.LENGTH_SHORT).show();
    }
}