package com.example.fluffstroller.pages.profile.dogstroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fluffstroller.databinding.ViewDogOwnerProfileFragmentBinding;
import com.example.fluffstroller.databinding.ViewStrollerProfileFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.DogOwnerProfileData;
import com.example.fluffstroller.models.StrollerProfileData;
import com.example.fluffstroller.pages.profile.dogowner.DogOwnerProfileViewModel;
import com.example.fluffstroller.pages.profile.dogowner.DogsAdapter;
import com.example.fluffstroller.pages.profile.dogowner.ViewDogOwnerProfileFragmentArgs;
import com.example.fluffstroller.pages.profile.dogowner.ViewDogOwnerProfileFragmentDirections;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;

public class ViewStrollerProfileFragment extends FragmentWithServices {

    private StrollerProfileViewModel viewModel;

    private ViewStrollerProfileFragmentBinding binding;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = ViewStrollerProfileFragmentBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(StrollerProfileViewModel.class);

        viewModel.getName().observe(getViewLifecycleOwner(), name -> binding.nameTextViewViewStrollerProfile.setText(name));
        viewModel.getEmail().observe(getViewLifecycleOwner(), email -> binding.emailTextViewViewStrollerProfile.setText(email));
        viewModel.getPhoneNumber().observe(getViewLifecycleOwner(), phoneNumber -> binding.phoneNumberTextViewViewStrollerProfile.setText(phoneNumber));

        ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
        binding.reviewsRecyclerViewStrollerViewProfile.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.reviewsRecyclerViewStrollerViewProfile.setAdapter(reviewsAdapter);
        viewModel.getReviews().observe(getViewLifecycleOwner(), reviewsAdapter::setReviews);

        String profileId = "";
        if (!profileId.equals(loggedUserDataService.getLoggedUserId())) {
            binding.editProfileButtonViewStrollerProfile.setVisibility(View.INVISIBLE);

            profileService.getProfileData(profileId).subscribe(response -> {
                if (response.hasErrors() || response.data == null) {
                    Toast.makeText(this.getContext(), "Error fetching data",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                viewModel.setName(response.data.getName());
                viewModel.setEmail(response.data.getEmail());
                viewModel.setPhoneNumber(response.data.getPhoneNumber());

                if (response.data instanceof StrollerProfileData) {
                    viewModel.setReviews(((StrollerProfileData) response.data).getReviews());
                }
            });

        } else {
            binding.editProfileButtonViewStrollerProfile.setOnClickListener(view -> {
                viewModel.setName(loggedUserDataService.getLoggedUserName());
                viewModel.setEmail(loggedUserDataService.getLoggedUserEmail());
                viewModel.setPhoneNumber(loggedUserDataService.getLoggedUserPhoneNumber());
                viewModel.setDescription(loggedUserDataService.getLoggedUserDescription());
                viewModel.setReviews(loggedUserDataService.getLoggedUserReviews());

//                Navigation.findNavController(view).navigate(ViewS.fromViewToEditDogOwner(null));
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