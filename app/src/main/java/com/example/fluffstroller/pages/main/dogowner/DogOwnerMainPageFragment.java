package com.example.fluffstroller.pages.main.dogowner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.DogOwnerMainPageFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.FeesService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.TextWithLabel;
import com.example.fluffstroller.utils.formatting.CurrencyIntegerTextWatcher;
import com.example.fluffstroller.utils.formatting.TimeIntegerTextWatcher;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DogOwnerMainPageFragment extends FragmentWithServices {

    @Injectable
    private FeesService feesService;

    @Injectable
    private DogWalksService dogWalksService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    private DogOwnerMainPageViewModel viewModel;
    private DogOwnerMainPageFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DogOwnerMainPageViewModel.class);

        binding = DogOwnerMainPageFragmentBinding.inflate(inflater, container, false);

        final RecyclerView selectedDogsRecyclerView = binding.selectedDogsRecyclerView;

        binding.walkPriceTextWithLabel.addTextChangedListener(new CurrencyIntegerTextWatcher(binding.walkPriceTextWithLabel.editText, "$", text -> {
            try {
                viewModel.setWalkPrice(Integer.parseInt(text));
            } catch (Exception e) {
                viewModel.setWalkPrice(0);
            }
        }));
        binding.walkTimeTextWithLabel.addTextChangedListener(new TimeIntegerTextWatcher(binding.walkTimeTextWithLabel.editText, "minutes", text -> {
            try {
                viewModel.setWalkTime(Integer.parseInt(text));
            } catch (Exception e) {
                viewModel.setWalkPrice(0);
            }
        }));
        binding.feesTextWithLabel.addTextChangedListener(new CurrencyIntegerTextWatcher(binding.feesTextWithLabel.editText, "$"));
        binding.totalPriceTextWithLabel.addTextChangedListener(new CurrencyIntegerTextWatcher(binding.totalPriceTextWithLabel.editText, "$"));

        DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(new ArrayList<>());
        selectedDogsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        selectedDogsRecyclerView.setAdapter(dogNamesAdapter);

        binding.findStrollerButton.setOnClickListener(view -> {
            List<String> checkedDogs = dogNamesAdapter.getCheckedDogs();

            if (!validateInputs() || checkedDogs.isEmpty()) {
                Snackbar.make(view, R.string.empty_required_fields, Snackbar.LENGTH_SHORT).show();
                return;
            }

            Integer walkTime = viewModel.getWalkTime().getValue();
            Integer totalPrice = viewModel.getTotalPrice().getValue();

            String userId = loggedUserDataService.getLoggedUserId();
            String userName = loggedUserDataService.getLoggedUserName();
            String userPhoneNumber = loggedUserDataService.getLoggedUserPhoneNumber();

            dogWalksService.createDogWalk(new DogWalk(checkedDogs, userId, userName, userPhoneNumber, totalPrice, walkTime)).subscribe(response -> {
                if (response.hasErrors()) {
                    response.exception.printStackTrace();
                    Toast.makeText(getContext(), "Couldn't create walk", Toast.LENGTH_SHORT).show();
                    return;
                }

                DogWalk dogWalk = response.data;
                if (dogWalk == null) {
                    Toast.makeText(getContext(), "Create empty walk", Toast.LENGTH_SHORT).show();
                    return;
                }

                DogWalkPreview walkPreview = new DogWalkPreview(dogWalk.getId(), dogWalk.getStatus());
                profileService.updateDogWalkPreview(userId, walkPreview).subscribe(res1 -> {
                    if (res1.hasErrors()) {
                        res1.exception.printStackTrace();
                        Toast.makeText(getContext(), "Couldn't create walk", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        viewModel.getDogNames().observe(getViewLifecycleOwner(), dogNamesAdapter::setDogNames);

        viewModel.getFees().observe(getViewLifecycleOwner(), fees -> {
            binding.feesTextWithLabel.setText(fees.toString());

            MutableLiveData<Integer> walkPrice = viewModel.getWalkPrice();
            int totalPrice = fees;

            if (walkPrice != null && walkPrice.getValue() != null) {
                totalPrice += walkPrice.getValue();
            }

            viewModel.setTotalPrice(totalPrice);
        });

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> binding.totalPriceTextWithLabel.setText(totalPrice + ""));

        viewModel.getWalkPrice().observe(getViewLifecycleOwner(), walkPrice -> {
            MutableLiveData<Integer> fees = viewModel.getFees();
            int totalPrice = walkPrice;

            if (fees != null && fees.getValue() != null) {
                totalPrice += fees.getValue();
            }

            viewModel.setTotalPrice(totalPrice);
        });

        viewModel.setDogNames(loggedUserDataService.getLoggedUserDogs().stream().map(dog -> dog.getName()).collect(Collectors.toList()));

        viewModel.setFees(feesService.getDogWalkFees());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean validateInputs() {
        return validateEditText(binding.walkPriceTextWithLabel) && validateEditText(binding.walkTimeTextWithLabel)
                && validateEditText(binding.feesTextWithLabel) && validateEditText(binding.totalPriceTextWithLabel);
    }

    private boolean validateEditText(TextWithLabel textWithLabel) {
        return !textWithLabel.editText.getText().toString().isEmpty();
    }
}