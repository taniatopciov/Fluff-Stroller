package com.example.fluffstroller.pages.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.HomePageViewModel;
import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.FragmentHomeBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.utils.FragmentWithServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.fragment.app.Fragment;

public class HomeFragment extends FragmentWithServices {

    private FragmentHomeBinding binding;
    private HomePageViewModel homeViewModel;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomePageViewModel.class);

        binding.ownerHomeButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home));
        binding.ownerHomeNoDogsButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home_no_dogs));
        binding.ownerHomeWaitingButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home_waiting_for_stroller));
        binding.ownerHomWalkInProgressButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_dog_owner_home_walk_in_progress));

        binding.strollerHomeButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_stroller_home));
        binding.strollerHomeWalkInProgressButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_stroller_home_walk_in_progress));

        binding.walkInProgressButton.setOnClickListener(view -> Navigation.findNavController(view).navigate(R.id.nav_walk_in_progress));

        binding.addDogButton.setOnClickListener(view -> {
            String loggedUserId = loggedUserDataService.getLoggedUserId();
            if(loggedUserId != null && !loggedUserId.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("id", loggedUserId);
                Navigation.findNavController(view).navigate(R.id.nav_view_dog_owner_profile, bundle);
            } else {
                Toast.makeText(this.getContext(), "Empty or null id",
                        Toast.LENGTH_LONG).show();
            }
        });

//        binding.showOrganizerDetailsButtonEventDetailsPage.setOnClickListener(view -> {
//            String organizerId = mViewModel.getEventOrganizerId().getValue();
//            if (organizerId != null && !organizerId.isEmpty()) {
//                Bundle bundle = new Bundle();
//                bundle.putString("organizerId", organizerId);
//                Navigation.findNavController(view).navigate(R.id.nav_organizerProfile, bundle);
//            }
//        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}