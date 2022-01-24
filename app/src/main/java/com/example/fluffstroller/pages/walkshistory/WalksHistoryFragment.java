package com.example.fluffstroller.pages.walkshistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.WalksHistoryFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;

public class WalksHistoryFragment extends FragmentWithServices {

    private WalksHistoryViewModel viewModel;
    private WalksHistoryFragmentBinding binding;
    private NavController navController;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private DogWalksService dogWalksService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = WalksHistoryFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(WalksHistoryViewModel.class);

        PastWalksAdapter pastWalksAdapter = new PastWalksAdapter(loggedUserDataService.getLogUserType(), this::onPastWalkCardClick);
        binding.pastWalksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.pastWalksRecyclerView.setAdapter(pastWalksAdapter);
        viewModel.getPastWalks().observe(getViewLifecycleOwner(), pastWalksAdapter::setPastWalks);

        dogWalksService.getPastDogWalks(loggedUserDataService.getLoggedUserId()).subscribe(response -> {
            if (response.hasErrors()) {
                CustomToast.show(requireActivity(), R.string.couldnt_get_past_walks,
                        Toast.LENGTH_LONG);
            }

            viewModel.setPastWalks(response.data);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(binding.getRoot());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onPastWalkCardClick(String walkId) {
        navController.navigate(WalksHistoryFragmentDirections.actionNavWalksHistoryToPastWalkFragment(walkId));
    }
}