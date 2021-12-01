package com.example.flusffstroller.pages.main.dogowner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flusffstroller.R;
import com.example.flusffstroller.databinding.DogOwnerMainPageFragmentBinding;
import com.example.flusffstroller.di.Injectable;
import com.example.flusffstroller.di.ServiceLocator;
import com.example.flusffstroller.models.DogWalk;
import com.example.flusffstroller.services.DogWalksService;
import com.example.flusffstroller.services.FeesService;
import com.example.flusffstroller.services.ProfileService;
import com.example.flusffstroller.utils.FragmentWithSubjects;
import com.example.flusffstroller.utils.components.TextWithLabel;
import com.example.flusffstroller.utils.formatting.CurrencyIntegerTextWatcher;
import com.example.flusffstroller.utils.formatting.TimeIntegerTextWatcher;
import com.example.flusffstroller.utils.observer.Observer;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DogOwnerMainPageFragment extends FragmentWithSubjects {

    @Injectable
    private FeesService feesService;

    @Injectable
    private DogWalksService dogWalksService;

    @Injectable
    private ProfileService profileService;

    private DogOwnerMainPageViewModel viewModel;
    private DogOwnerMainPageFragmentBinding binding;

    public DogOwnerMainPageFragment() {
        ServiceLocator.getInstance().inject(this);
    }

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

            String userId = profileService.getLoggedUserId();

            final DogWalk dogWalk = new DogWalk(userId, checkedDogs, walkTime, totalPrice);
            registerSubject(dogWalksService.createDogWalk(dogWalk))
                    .subscribe(new Observer<String>() {
                        @Override
                        public void accept(String id) {
                            dogWalk.setId(id);
                            registerSubject(profileService.setCurrentDogWalk(dogWalk))
                                    .subscribe(new Observer<Boolean>() {
                                        @Override
                                        public void accept(Boolean success) {
                                            DogOwnerMainPageWaitingForStrollerViewModel waitingForStrollerViewModel = new ViewModelProvider(requireActivity()).get(DogOwnerMainPageWaitingForStrollerViewModel.class);
                                            waitingForStrollerViewModel.setCurrentDogWalk(dogWalk);

                                            Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home_waiting_for_stroller);
                                        }

                                        @Override
                                        public void error(Exception error) {
                                            error.printStackTrace();
                                            Toast.makeText(getContext(), "Couldn't create walk", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void error(Exception error) {
                            error.printStackTrace();
                            Toast.makeText(getContext(), "Couldn't create walk", Toast.LENGTH_SHORT).show();
                        }
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

        registerSubject(profileService.getLoggedUserDogs()).subscribe(new Observer<List<String>>() {
            @Override
            public void accept(List<String> dogNames) {
                viewModel.setDogNames(dogNames);
            }

            @Override
            public void error(Exception error) {
                error.printStackTrace();
            }
        });

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