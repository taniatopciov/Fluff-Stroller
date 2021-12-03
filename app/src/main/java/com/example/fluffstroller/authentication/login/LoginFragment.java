package com.example.fluffstroller.authentication.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.databinding.LoginFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class LoginFragment extends FragmentWithServices {

    @Injectable
    private AuthenticationService authenticationService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    private LoginViewModel mViewModel;
    private LoginFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = LoginFragmentBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        binding.noAccountTextViewLoginFragment.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(LoginFragmentDirections.navLoginToRegister());
        });

        binding.loginButton.setOnClickListener(view -> {
            String email = binding.emailTextWithLabelLoginFragment.editText.getText().toString();
            String password = binding.passwordTextWithLabelLoginFragment.editText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this.getContext(), "Empty fields!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            authenticationService.loginWithEmailAndPassword(email, password).subscribe(response -> {
                if (response.hasErrors()) {
                    Toast.makeText(this.getContext(), "Log in failed.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                profileService.getProfileData(response.data.getUid()).subscribe(response2 -> {
                    if (response2.hasErrors()) {
                        Toast.makeText(this.getContext(), "Fetching data from Firebase failed",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    loggedUserDataService.setLoggedUserData(response2.data);
                    requireActivity().setResult(Activity.RESULT_OK);
                    requireActivity().finish();
                });
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