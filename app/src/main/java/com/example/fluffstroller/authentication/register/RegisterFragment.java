package com.example.fluffstroller.authentication.register;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.RegisterFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;

public class RegisterFragment extends FragmentWithServices {

    @Injectable
    private AuthenticationService authenticationService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    private RegisterViewModel mViewModel;
    private RegisterFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = RegisterFragmentBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);

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
                Toast.makeText(this.getContext(), "Empty fields!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this.getContext(), "Passwords do not match!",
                        Toast.LENGTH_LONG).show();
                return;
            }

            authenticationService.register(email, password).subscribe(response -> {
                if (response.hasErrors()) {
                    Toast.makeText(this.getContext(), "Register failed.",
                            Toast.LENGTH_LONG).show();
                    response.exception.printStackTrace();
                    return;
                }

                profileService.createProfile(response.data.getUid(), name, email, userType).subscribe(response2 -> {
                    if (response.hasErrors()) {
                        Toast.makeText(this.getContext(), "Profile creation failed",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    loggedUserDataService.setLoggedUserData(response2.data);
                    requireActivity().setResult(Activity.RESULT_OK);
                    requireActivity().finish();
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
}