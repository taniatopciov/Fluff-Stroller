package com.example.fluffstroller.pages.main.dogowner;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.DogOwnerMainPageWaitingForStrollerFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkRequestStatus;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.ConfirmationPopupDialog;
import com.example.fluffstroller.utils.components.CustomToast;
import com.example.fluffstroller.utils.components.InfoPopupDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DogOwnerMainPageWaitingForStrollerFragment extends FragmentWithServices {

    private static final long DOG_WALK_AVAILABLE_TIME_MILLIS = 600000L;
    private static final String DOG_OWNER_MAIN_PAGE_WAITING_FOR_STROLLER_FRAGMENT = "DOG_OWNER_MAIN_PAGE_WAITING_FOR_STROLLER_FRAGMENT";

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private DogWalksService dogWalksService;

    @Injectable
    private ProfileService profileService;

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

        if (walkPreview.getStatus().equals(WalkStatus.IN_PROGRESS) || walkPreview.getStatus().equals(WalkStatus.WAITING_FOR_START)) {
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

        binding.cancelWalkButton.setOnClickListener(view -> {
            new ConfirmationPopupDialog(R.string.cancel_walk_message, ignored -> {
                removeCurrentWalk();
            }, null).show(getChildFragmentManager(), DOG_OWNER_MAIN_PAGE_WAITING_FOR_STROLLER_FRAGMENT);
        });

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

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> binding.totalPriceTextView.setText(totalPrice + " RON"));

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

        dogWalksService.removeCurrentWalk(preview.getWalkId(), loggedUserDataService.getLoggedUserId()).subscribe(response -> {
            if (response.hasErrors()) {
                CustomToast.show(requireActivity(), "Could not remove Walk",
                        Toast.LENGTH_LONG);
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
        String walkId = requestPair.first.getWalkId();
        String strollerId = requestPair.first.getStrollerId();

        dogWalksService.getDogWalk(walkId).subscribe(res -> {
            if (res.hasErrors() || res.data == null) {
                CustomToast.show(requireActivity(), "Could set walk in progress",
                        Toast.LENGTH_LONG);
                return;
            }

            DogWalk dogWalk = res.data;
            List<WalkRequest> requests = dogWalk.getRequests();

            for (WalkRequest request : requests) {
                if (request.getStrollerId().equals(strollerId)) {
                    request.setStatus(WalkRequestStatus.ACCEPTED);
                } else {
                    request.setStatus(WalkRequestStatus.REJECTED);
                }
            }

            dogWalksService.updateDogWalk(loggedUserDataService.getLoggedUserId(), dogWalk.getId(), WalkStatus.WAITING_FOR_START, requests).subscribe(response -> {
                if (response.hasErrors()) {
                    CustomToast.show(requireActivity(), "Could set walk preview in progress",
                            Toast.LENGTH_LONG);
                    return;
                }

                AtomicInteger updatedRequestCount = new AtomicInteger(0);

                for (WalkRequest request : requests) {
                    profileService.updateCurrentRequest(request.getStrollerId(), request).subscribe(res2 -> {
                        if (updatedRequestCount.incrementAndGet() == requests.size()) {
                            loggedUserDataService.setDogWalkPreview(response.data);
                            NavHostFragment.findNavController(this).navigate(DogOwnerMainPageWaitingForStrollerFragmentDirections.actionNavDogOwnerHomeWaitingForStrollerToNavDogOwnerHomeWalkInProgress());
                        }
                    });
                }
            });
        });
    }

    private void handleRequestRejected(Pair<WalkRequest, Integer> requestPair) {
        requestPair.first.setStatus(WalkRequestStatus.REJECTED);
        List<WalkRequest> walkRequests = viewModel.getWalkRequests().getValue();
        for (WalkRequest walk : walkRequests) {
            if (walk.getStrollerId().equals(requestPair.first.getStrollerId())) {
                walk.setStatus(WalkRequestStatus.REJECTED);
                break;
            }
        }

        dogWalksService.updateDogWalk(loggedUserDataService.getLoggedUserId(), loggedUserDataService.getCurrentWalkId(), viewModel.getStatus().getValue(), walkRequests).subscribe(response -> {
            if (response.hasErrors()) {
                CustomToast.show(requireActivity(), "Could not update dog walk",
                        Toast.LENGTH_LONG);
                return;
            }
        });

        profileService.updateCurrentRequest(requestPair.first.getStrollerId(), requestPair.first).subscribe(response -> {
            if (response.hasErrors()) {
                CustomToast.show(requireActivity(), "Could not cancel request",
                        Toast.LENGTH_LONG);
                return;
            }
        });
    }

    private void handleRequestViewProfile(Pair<WalkRequest, Integer> requestPair) {
        NavHostFragment.findNavController(this).navigate(DogOwnerMainPageWaitingForStrollerFragmentDirections.actionGlobalNavViewStrollerProfile(requestPair.first.getStrollerId()));
    }

    private void handleRequestCall(Pair<WalkRequest, Integer> requestPair) {
        Toast.makeText(getContext(), "Call: " + requestPair.first.getStrollerName() + " " + requestPair.second, Toast.LENGTH_SHORT).show();
    }
}