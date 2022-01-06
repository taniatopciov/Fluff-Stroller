package com.example.fluffstroller.pages.profile.dogstroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fluffstroller.databinding.EditStrollerProfileFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;

public class EditStrollerProfileFragment extends FragmentWithServices {

    private StrollerProfileViewModel viewModel;

    private EditStrollerProfileFragmentBinding binding;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = EditStrollerProfileFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(StrollerProfileViewModel.class);

        viewModel.getName().observe(getViewLifecycleOwner(), name -> binding.nameTextViewWithLabelEditStrollerProfile.setText(name));
        viewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneNumber -> binding.phoneNumberTextViewWithLabelEditStrollerProfile.setText(phoneNumber));
        viewModel.getDescription().observe(getViewLifecycleOwner(), phoneNumber -> binding.descriptionTextViewWithLabelEditStrollerProfile.setText(phoneNumber));

        binding.saveButtonEditStrollerProfile.setOnClickListener(view -> {
            String name = binding.nameTextViewWithLabelEditStrollerProfile.getText();
            String phoneNumber = binding.phoneNumberTextViewWithLabelEditStrollerProfile.getText();
            String description = binding.descriptionTextViewWithLabelEditStrollerProfile.getText();

            profileService.updateDogStrollerProfile(loggedUserDataService.getLoggedUserId(), name, phoneNumber, description).subscribe(response -> {
                if (response.hasErrors()) {
                    CustomToast.show(requireActivity(), "Error updating data",
                            Toast.LENGTH_LONG);
                    return;
                }

                loggedUserDataService.updateStrollerData(name, phoneNumber, description);
                Navigation.findNavController(view).navigate(EditStrollerProfileFragmentDirections.actionEditStrollerProfileFragmentToNavViewStrollerProfile(loggedUserDataService.getLoggedUserId()));
            });
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}