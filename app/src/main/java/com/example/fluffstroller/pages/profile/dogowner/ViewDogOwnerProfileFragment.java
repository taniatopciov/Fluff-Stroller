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

import com.example.fluffstroller.databinding.ViewDogOwnerProfileFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogOwnerProfileData;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;

public class ViewDogOwnerProfileFragment extends FragmentWithServices {

    private DogOwnerProfileViewModel viewModel;

    private ViewDogOwnerProfileFragmentBinding binding;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = ViewDogOwnerProfileFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(DogOwnerProfileViewModel.class);

        viewModel.getName().observe(getViewLifecycleOwner(), name -> binding.nameTextViewViewOwnerProfile.setText(name));
        viewModel.getEmail().observe(getViewLifecycleOwner(), email -> binding.emailTextViewViewOwnerProfile.setText(email));
        viewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneNumber -> binding.phoneNumberTextViewViewOwnerProfile.setText(phoneNumber));

        DogsAdapter dogsAdapter = new DogsAdapter(false);
        binding.recyclerViewDogsViewDogOwnerProfile.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewDogsViewDogOwnerProfile.setAdapter(dogsAdapter);
        viewModel.getDogs().observe(getViewLifecycleOwner(), dogsAdapter::setDogs);

        viewModel.clearData();

        String profileId = ViewDogOwnerProfileFragmentArgs.fromBundle(getArguments()).getId();
        if (!profileId.equals(loggedUserDataService.getLoggedUserId())) {
            binding.editProfileButtonViewDogOwnerProfile.setVisibility(View.INVISIBLE);

            profileService.getProfileData(profileId).subscribe(response -> {
                if (response.hasErrors() || response.data == null) {
                    Toast.makeText(this.getContext(), "Error fetching data",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                viewModel.setName(response.data.getName());
                viewModel.setEmail(response.data.getEmail());
                viewModel.setPhoneNumber(response.data.getPhoneNumber());

                if (response.data instanceof DogOwnerProfileData) {
                    viewModel.setDogs(((DogOwnerProfileData) response.data).getDogs());
                }
            });

        } else {
            binding.editProfileButtonViewDogOwnerProfile.setOnClickListener(view -> {
                viewModel.setName(loggedUserDataService.getLoggedUserName());
                viewModel.setEmail(loggedUserDataService.getLoggedUserEmail());
                viewModel.setPhoneNumber(loggedUserDataService.getLoggedUserPhoneNumber());
                viewModel.setDogs(loggedUserDataService.getLoggedUserDogs());

                Navigation.findNavController(view).navigate(ViewDogOwnerProfileFragmentDirections.fromViewOwnerProfileToEdit(null));
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}