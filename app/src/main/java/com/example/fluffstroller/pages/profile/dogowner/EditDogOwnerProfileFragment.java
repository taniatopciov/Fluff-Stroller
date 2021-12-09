package com.example.fluffstroller.pages.profile.dogowner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fluffstroller.databinding.EditDogOwnerProfileFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.Dog;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;

import java.util.List;

public class EditDogOwnerProfileFragment extends FragmentWithServices {

    private DogOwnerProfileViewModel viewModel;

    private EditDogOwnerProfileFragmentBinding binding;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = EditDogOwnerProfileFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(DogOwnerProfileViewModel.class);

        DogsAdapter dogsAdapter = new DogsAdapter(true);

        //todo add arguments for navigation between fragments

        binding.saveButtonEditDogOwnerProfile.setOnClickListener(view -> {
            String name = binding.nameTextViewWithLabelEditDogOwnerProfile.getText();
            String phoneNumber = binding.phoneNumberTextViewWithLabelEditDogOwnerProfile.getText();
            List<Dog> dogs = dogsAdapter.getDogs();

            profileService.updateDogOwnerProfile(loggedUserDataService.getLoggedUserId(), name, phoneNumber, dogs).subscribe(response -> {
                if (response.hasErrors()) {
                    Toast.makeText(this.getContext(), "Error updating data",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Navigation.findNavController(view).navigate(EditDogOwnerProfileFragmentDirections.fromEditOwnerProfileToViewProfile(loggedUserDataService.getLoggedUserId()));
            });
        });

        binding.addDogButtonEditDogOwnerProfile.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(EditDogOwnerProfileFragmentDirections.fromEditOwnerProfileToAddDog());
        });

        viewModel.getName().observe(getViewLifecycleOwner(), name -> binding.nameTextViewWithLabelEditDogOwnerProfile.setText(name));
        viewModel.getEmail().observe(getViewLifecycleOwner(), email -> binding.emailTextViewWithLabelEditDogOwnerProfile.setText(email));
        viewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneNumber -> binding.phoneNumberTextViewWithLabelEditDogOwnerProfile.setText(phoneNumber));

        binding.recyclerViewDogsEditDogOwnerProfile.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewDogsEditDogOwnerProfile.setAdapter(dogsAdapter);
        viewModel.getDogs().observe(getViewLifecycleOwner(), dogsAdapter::setDogs);

        Dog addedDog = EditDogOwnerProfileFragmentArgs.fromBundle(getArguments()).getDog();
        if(addedDog != null) {
            viewModel.addDog(addedDog);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}