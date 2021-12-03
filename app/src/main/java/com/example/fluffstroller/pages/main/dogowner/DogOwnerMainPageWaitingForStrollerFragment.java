package com.example.fluffstroller.pages.main.dogowner;

import android.os.Bundle;
import android.text.Spanned;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.DogOwnerMainPageWaitingForStrollerFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;


public class DogOwnerMainPageWaitingForStrollerFragment extends FragmentWithServices {

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private DogWalksService dogWalksService;

    private DogOwnerMainPageWaitingForStrollerViewModel viewModel;
    private DogOwnerMainPageWaitingForStrollerFragmentBinding binding;

    // todo implement the timer

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogOwnerMainPageWaitingForStrollerFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(DogOwnerMainPageWaitingForStrollerViewModel.class);

        binding.cancelWalkButton.setOnClickListener(view -> {
            // todo handle walk canceled
            Snackbar.make(view, "Walk canceled", Snackbar.LENGTH_SHORT).show();
        });

        WalkRequestAdapter walkRequestAdapter = new WalkRequestAdapter(new ArrayList<>(),
                this::handleRequestAccepted, this::handleRequestRejected,
                this::handleRequestViewProfile, this::handleRequestCall);
        binding.requestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.requestsRecyclerView.setAdapter(walkRequestAdapter);

        viewModel.getRemainingWaitingForStrollerMills().observe(getViewLifecycleOwner(), millisUntilFinished -> {
            updateWaitingTime(millisUntilFinished);
            if (millisUntilFinished == 0) {
                // todo handle timer finished
                Toast.makeText(getContext(), "Cancel walk", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getWalkCreationTimeMillis().observe(getViewLifecycleOwner(), walkCreationMillis -> {
            // todo start the timer if necessary
            // todo handle the case in which the 10 minutes timer has expired
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

            binding.dogsTextView.setText(formatCurrentDetail(R.string.dogs, concatenatedDogNames));
        });

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> binding.totalPriceTextView.setText(formatCurrentDetail(R.string.total_price_semicolon, totalPrice + " $")));

        viewModel.getWalkTime().observe(getViewLifecycleOwner(), walkTime -> binding.initialWalkTimeTextView.setText(formatCurrentDetail(R.string.initial_walk_time, walkTime + " minutes")));

        String currentWalkId = loggedUserDataService.getLoggedUserCurrentWalkId();

        if (currentWalkId.isEmpty()) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.nav_dog_owner_home);
            return binding.getRoot();
        }

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
    }

    private void updateWaitingTime(Long millisUntilFinished) {
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;

        String text = String.format(Locale.getDefault(), "(%02d:%02d minutes remaining)", minutes, seconds);
        binding.waitingForStrollerTimeTextView.setText(text);
    }


    private Spanned formatCurrentDetail(int resourceId, String text) {
        String result = "<b>" + getResources().getString(resourceId) + "</b> " + text;
        return HtmlCompat.fromHtml(result, HtmlCompat.FROM_HTML_MODE_COMPACT);
    }

    private void handleRequestAccepted(Pair<WalkRequest, Integer> requestPair) {
        Toast.makeText(getContext(), "Accepted: " + requestPair.first.getStrollerName() + " " + requestPair.second, Toast.LENGTH_SHORT).show();
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