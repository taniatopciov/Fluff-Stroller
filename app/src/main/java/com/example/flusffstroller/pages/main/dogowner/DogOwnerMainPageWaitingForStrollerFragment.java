package com.example.flusffstroller.pages.main.dogowner;

import android.os.Bundle;
import android.text.Spanned;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flusffstroller.R;
import com.example.flusffstroller.databinding.DogOwnerMainPageWaitingForStrollerFragmentBinding;
import com.example.flusffstroller.models.WalkRequest;
import com.example.flusffstroller.models.WalkStatus;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

public class DogOwnerMainPageWaitingForStrollerFragment extends Fragment {

    private DogOwnerMainPageWaitingForStrollerViewModel viewModel;
    private DogOwnerMainPageWaitingForStrollerFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DogOwnerMainPageWaitingForStrollerFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(DogOwnerMainPageWaitingForStrollerViewModel.class);

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

        viewModel.getWalkRequests().observe(getViewLifecycleOwner(), walkRequestAdapter::setWalkRequests);

        viewModel.getCurrentDogWalk().observe(getViewLifecycleOwner(), dogWalk -> {
            String concatenatedDogNames = "";

            if (dogWalk.getDogNames() != null) {
                concatenatedDogNames = dogWalk.getDogNames().stream().reduce("", (s, s2) -> s + s2 + ", ");
                int lastIndex = concatenatedDogNames.lastIndexOf(", ");
                if (lastIndex >= 0) {
                    concatenatedDogNames = concatenatedDogNames.substring(0, lastIndex);
                }
            }

            binding.dogsTextView.setText(formatCurrentDetail(R.string.dogs, concatenatedDogNames));

            binding.totalPriceTextView.setText(formatCurrentDetail(R.string.total_price_semicolon, dogWalk.getTotalPrice() + " $"));

            binding.initialWalkTimeTextView.setText(formatCurrentDetail(R.string.initial_walk_time, dogWalk.getWalkTime() + " minutes"));
        });

        // todo get requests from database
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        List<WalkRequest> walkRequests = new ArrayList<>();
                        walkRequests.add(new WalkRequest("1", "1234", "Stroller1", "0745", 2.4, WalkStatus.PENDING));
                        walkRequests.add(new WalkRequest("2", "abc", "Stroller2", "123", 4.0, WalkStatus.PENDING));
                        walkRequests.add(new WalkRequest("3", "5661", "Stroller3", "", 5.0, WalkStatus.PENDING));

                        if (walkRequests.size() > 0) {
                            binding.noRequestsTextView.setVisibility(View.INVISIBLE);
                        } else {
                            binding.noRequestsTextView.setVisibility(View.VISIBLE);
                        }

                        viewModel.setWalkRequests(walkRequests);

                        cancel();
                    }
                },
                1000
        );

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