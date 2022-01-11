package com.example.fluffstroller.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.SetupProfileTypeFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.components.CustomToast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;


public class SetupProfileTypeFragment extends FragmentWithServices {

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    @Injectable
    private ProfileService profileService;

    private SetupProfileTypeFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SetupProfileTypeFragmentBinding.inflate(inflater, container, false);

        Spinner userTypeSpinner = binding.userTypeSpinnerRegisterFragment;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.user_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        String userId = SetupProfileTypeFragmentArgs.fromBundle(getArguments()).getUserId();
        String name = SetupProfileTypeFragmentArgs.fromBundle(getArguments()).getName();
        String email = SetupProfileTypeFragmentArgs.fromBundle(getArguments()).getEmail();

        binding.confirmButton.setOnClickListener(view -> {
            UserType userType = UserType.convertString(binding.userTypeSpinnerRegisterFragment.getSelectedItem().toString());

            profileService.createProfile(userId, name, email, userType).subscribe(response2 -> {
                if (response2.hasErrors()) {
                    CustomToast.show(requireActivity(), "Profile creation failed", Toast.LENGTH_LONG);
                    response2.exception.printStackTrace();
                    return;
                }


                loggedUserDataService.setLoggedUserData(response2.data);
                NavHostFragment.findNavController(this).navigate(SetupProfileTypeFragmentDirections.actionSetupProfileTypeFragmentToNavHome());
            });
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.hide();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.show();
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}