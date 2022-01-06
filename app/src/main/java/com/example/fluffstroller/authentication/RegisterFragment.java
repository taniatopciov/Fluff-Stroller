package com.example.fluffstroller.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.RegisterFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.pages.main.home.HomeNavFragmentDirections;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.HideKeyboard;
import com.example.fluffstroller.utils.components.CustomToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends FragmentWithServices {

    @Injectable
    private AuthenticationService authenticationService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    private RegisterFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = RegisterFragmentBinding.inflate(inflater, container, false);

        Spinner userTypeSpinner = binding.userTypeSpinnerRegisterFragment;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.user_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        binding.registerButtonRegisterFragment.setOnClickListener(view -> {
            String name = binding.nameTextWithLabelRegisterFragment.editText.getText().toString();
            String email = binding.emailTextWithLabelRegisterFragment.editText.getText().toString();
            String password = binding.passwordTextWithLabelRegisterFragment.editText.getText().toString();
            String confirmPassword = binding.confirmPasswordTextWithLabelRegisterFragment.editText.getText().toString();
            UserType userType = UserType.convertString(binding.userTypeSpinnerRegisterFragment.getSelectedItem().toString());

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || userType == null) {
                CustomToast.show(requireActivity(), "All fields must be completed",
                        Toast.LENGTH_LONG);
                return;
            }

            if (!validateEmail(email)) {
                CustomToast.show(requireActivity(), "Invalid e-mail address",
                        Toast.LENGTH_LONG);
                return;
            }

            if (!password.equals(confirmPassword)) {
                CustomToast.show(requireActivity(), "Passwords do not match",
                        Toast.LENGTH_LONG);
                return;
            }

            if (password.length() < 8) {
                CustomToast.show(requireActivity(), "Password must be at least 8 characters",
                        Toast.LENGTH_LONG);
                return;
            }

            authenticationService.register(email, password).subscribe(response -> {
                if (response.hasErrors()) {
                    CustomToast.show(requireActivity(), "Registration failed",
                            Toast.LENGTH_LONG);
                    response.exception.printStackTrace();
                    return;
                }

                profileService.createProfile(response.data.getUid(), name, email, userType).subscribe(response2 -> {
                    if (response2.hasErrors()) {
                        CustomToast.show(requireActivity(), "Profile creation failed",
                                Toast.LENGTH_LONG);
                        response2.exception.printStackTrace();
                        return;
                    }

                    loggedUserDataService.setLoggedUserData(response2.data);

                    HideKeyboard.hide(requireActivity());
                    NavHostFragment.findNavController(this).navigate(HomeNavFragmentDirections.actionGlobalNavHome());
                });
            });
        });

        binding.alreadyRegisteredTextViewRegisterFragment.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(RegisterFragmentDirections.navRegisterToLogin());
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    private boolean validateEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}