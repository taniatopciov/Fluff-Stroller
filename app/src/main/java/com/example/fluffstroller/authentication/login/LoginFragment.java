package com.example.fluffstroller.authentication.login;

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

import com.example.fluffstroller.databinding.LoginFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.services.impl.FirebaseAuthenticationService;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends Fragment {

    @Injectable
    private AuthenticationService authenticationService;

    private LoginViewModel mViewModel;
    private LoginFragmentBinding binding;

    public LoginFragment() {
        ServiceLocator.getInstance().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = LoginFragmentBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        binding.noAccountTextViewLoginFragment.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(LoginFragmentDirections.navLoginToRegister());
        });

        binding.loginButton.setOnClickListener(view -> {
            String email = binding.emailTextWithLabelLoginFragment.toString();
            String password = binding.passwordTextWithLabelLoginFragment.toString();

            authenticationService.loginWithEmailAndPassword(email, password).subscribe(response -> {
                if(response.hasErrors()) {
                    Toast.makeText(this.getContext(), "Log in failed.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                //currentUser = response.data()
                getActivity().finish();
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