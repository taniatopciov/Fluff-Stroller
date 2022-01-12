package com.example.fluffstroller.pages.main.dogowner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.DogOwnerMainPageFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.FeesService;
import com.example.fluffstroller.services.LocationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;
import com.example.fluffstroller.utils.components.EnableLocationPopupDialog;
import com.example.fluffstroller.utils.components.TextWithLabel;
import com.example.fluffstroller.utils.formatting.TimeIntegerTextWatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DogOwnerMainPageFragment extends FragmentWithServices {

    private static final String DOG_OWNER_MAIN_PAGE_FRAGMENT = "DOG_OWNER_MAIN_PAGE_FRAGMENT";

    @Injectable
    private FeesService feesService;

    @Injectable
    private DogWalksService dogWalksService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private LocationService locationService;

    private DogOwnerMainPageViewModel viewModel;
    private DogOwnerMainPageFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DogOwnerMainPageViewModel.class);

        binding = DogOwnerMainPageFragmentBinding.inflate(inflater, container, false);

        List<Dog> loggedUserDogs = loggedUserDataService.getLoggedUserDogs();
        if (loggedUserDogs == null || loggedUserDogs.isEmpty()) {
            NavHostFragment.findNavController(this).navigate(DogOwnerMainPageFragmentDirections.actionNavDogOwnerHomeToNavDogOwnerHomeNoDogs());

            return binding.getRoot();
        }

        registerSubject(profileService.listenForProfileData(loggedUserDataService.getLoggedUserId())).subscribe(res -> {
            if (res.hasErrors()) {
                CustomToast.show(requireActivity(), "Could not fetch data", Toast.LENGTH_SHORT);
                return;
            }

            loggedUserDataService.setLoggedUserData(res.data);
            DogWalkPreview currentWalkPreview = loggedUserDataService.getLoggedUserWalkPreview();

            if (currentWalkPreview != null) {
                switch (currentWalkPreview.getStatus()) {
                    case PENDING: {
                        NavHostFragment.findNavController(this).navigate(DogOwnerMainPageFragmentDirections.actionNavDogOwnerHomeToNavDogOwnerHomeWaitingForStroller());
                    }
                    break;

                    case WAITING_FOR_START:
                    case IN_PROGRESS: {
                        NavHostFragment.findNavController(this).navigate(DogOwnerMainPageFragmentDirections.actionNavDogOwnerHomeToNavDogOwnerHomeWalkInProgress());
                    }
                    break;

                    case ADD_REVIEW: {
                        String currentWalkId = currentWalkPreview.getWalkId();
                        NavHostFragment.findNavController(this).navigate(DogOwnerMainPageFragmentDirections.actionNavDogOwnerHomeToNavReview(currentWalkId));
                    }
                    break;
                    case WAITING_PAYMENT: {
                        String currentWalkId = currentWalkPreview.getWalkId();
                        NavHostFragment.findNavController(this).navigate(DogOwnerMainPageFragmentDirections.actionNavDogOwnerHomeToPaymentFragment(currentWalkId));
                    }
                    break;
                    case PAID: {
                        String currentWalkId = currentWalkPreview.getWalkId();

                        dogWalksService.getDogWalk(currentWalkId).subscribe(response -> {
                            if (response.hasErrors() || response.data == null) {
                                return;
                            }

                            dogWalksService.updateWalkAfterPayment(response.data.getOwnerId(), response.data.getAcceptedRequest().getStrollerId()).subscribe(response2 -> {
                            });
                        });
                    }
                    break;
                }
            }
        }, false);

        final RecyclerView selectedDogsRecyclerView = binding.selectedDogsRecyclerView;

        binding.walkPriceTextWithLabel.addTextChangedListener(new TimeIntegerTextWatcher(binding.walkPriceTextWithLabel.editText, "RON", text -> {
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
        binding.feesTextWithLabel.addTextChangedListener(new TimeIntegerTextWatcher(binding.feesTextWithLabel.editText, "RON"));
        binding.totalPriceTextWithLabel.addTextChangedListener(new TimeIntegerTextWatcher(binding.totalPriceTextWithLabel.editText, "RON"));

        DogNamesAdapter dogNamesAdapter = new DogNamesAdapter(new ArrayList<>());
        selectedDogsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        selectedDogsRecyclerView.setAdapter(dogNamesAdapter);

        binding.findStrollerButton.setOnClickListener(view -> {
            List<String> checkedDogs = dogNamesAdapter.getCheckedDogs();

            if (!validateInputs() || checkedDogs.isEmpty()) {
                CustomToast.show(requireActivity(), getResources().getString(R.string.empty_required_fields),
                        Toast.LENGTH_LONG);
                return;
            }

            locationService.getCurrentLocation(getActivity()).subscribe(response -> {
                if (response.hasErrors()) {
                    CustomToast.show(requireActivity(), "Could not get current location",
                            Toast.LENGTH_LONG);
                    return;
                }

                if (response.data == null) {
                    new EnableLocationPopupDialog().show(getChildFragmentManager(), DOG_OWNER_MAIN_PAGE_FRAGMENT);
                    return;
                }

                createDogWalkForDogs(checkedDogs, response.data);
            });
        });

        viewModel.getDogNames().observe(getViewLifecycleOwner(), dogNamesAdapter::setDogNames);

        viewModel.getFees().observe(getViewLifecycleOwner(), fees -> {
            binding.feesTextWithLabel.setText(fees.toString());

            MutableLiveData<Integer> walkPrice = viewModel.getWalkPrice();
            Double totalPrice = fees;

            if (walkPrice != null && walkPrice.getValue() != null) {
                totalPrice += walkPrice.getValue();
            }

            viewModel.setTotalPrice(totalPrice);
        });

        viewModel.getTotalPrice().observe(getViewLifecycleOwner(), totalPrice -> binding.totalPriceTextWithLabel.setText(totalPrice + ""));

        viewModel.getWalkPrice().observe(getViewLifecycleOwner(), walkPrice -> {
            Double fees = feesService.getDogWalkFees(walkPrice);
            Double totalPrice = Double.valueOf(walkPrice);

            if (fees != null) {
                totalPrice += fees;
            }

            viewModel.setTotalPrice(totalPrice);

            viewModel.setFees(fees);
        });

        viewModel.setDogNames(loggedUserDogs.stream().map(Dog::getName).collect(Collectors.toList()));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createDogWalkForDogs(List<String> checkedDogs, Location location) {
        Integer walkTime = viewModel.getWalkTime().getValue();
        Double totalPrice = viewModel.getTotalPrice().getValue();

        String userId = loggedUserDataService.getLoggedUserId();
        String userName = loggedUserDataService.getLoggedUserName();
        String userPhoneNumber = loggedUserDataService.getLoggedUserPhoneNumber();

        dogWalksService.createDogWalk(new DogWalk(checkedDogs, userId, userName, userPhoneNumber, totalPrice, walkTime, location)).subscribe(response -> {
            if (response.hasErrors() || response.data == null) {
                response.exception.printStackTrace();
                CustomToast.show(requireActivity(), "Couldn't create walk",
                        Toast.LENGTH_LONG);
                return;
            }

            DogWalk dogWalk = response.data;

            DogWalkPreview walkPreview = new DogWalkPreview(dogWalk.getId(), dogWalk.getStatus());
            profileService.updateDogWalkPreview(userId, walkPreview).subscribe(res1 -> {
                if (res1.hasErrors()) {
                    res1.exception.printStackTrace();
                    CustomToast.show(requireActivity(), "Couldn't create walk",
                            Toast.LENGTH_LONG);
                }

                loggedUserDataService.setDogWalkPreview(walkPreview);
                NavHostFragment.findNavController(this).navigate(DogOwnerMainPageFragmentDirections.actionNavDogOwnerHomeToNavDogOwnerHomeWaitingForStroller());
            });
        });
    }


    private boolean validateInputs() {
        return validateEditText(binding.walkPriceTextWithLabel) && validateEditText(binding.walkTimeTextWithLabel)
                && validateEditText(binding.feesTextWithLabel) && validateEditText(binding.totalPriceTextWithLabel);
    }

    private boolean validateEditText(TextWithLabel textWithLabel) {
        return !textWithLabel.editText.getText().toString().isEmpty();
    }
}