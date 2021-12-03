package com.example.fluffstroller.authentication.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fluffstroller.BuildConfig;
import com.example.fluffstroller.R;
import com.example.fluffstroller.authentication.AuthenticationActivity;
import com.example.fluffstroller.databinding.LoginFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

public class LoginFragment extends FragmentWithServices {

    @Injectable
    private AuthenticationService authenticationService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    private LoginViewModel mViewModel;
    private LoginFragmentBinding binding;

    private GoogleSignInClient googleSignInClient;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        authenticationService.loginWithGoogle(account.getIdToken()).subscribe(response -> {
                            if (response.hasErrors()) {
                                Toast toast = Toast.makeText(requireActivity(), "Google sign in failed",
                                        Toast.LENGTH_LONG);
                                changeToastColors(toast);
                                toast.show();
                                return;
                            }

                            //TODO finish google login
                        });
                    } catch (ApiException e) {

                    }
                }
            });

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
                Toast toast = Toast.makeText(this.getContext(), "All fields must be completed",
                        Toast.LENGTH_LONG);
                changeToastColors(toast);
                toast.show();
                return;
            }

            authenticationService.loginWithEmailAndPassword(email, password).subscribe(response -> {
                if (response.hasErrors()) {
                    Toast toast = Toast.makeText(this.getContext(), "Invalid credentials",
                            Toast.LENGTH_LONG);
                    changeToastColors(toast);
                    toast.show();
                    return;
                }

                profileService.getProfileData(response.data.getUid()).subscribe(response2 -> {
                    if (response2.hasErrors()) {
                        Toast toast = Toast.makeText(this.getContext(), "Fetching data from Firebase failed",
                                Toast.LENGTH_LONG);
                        changeToastColors(toast);
                        toast.show();
                        return;
                    }
                    loggedUserDataService.setLoggedUserData(response2.data);
                    requireActivity().setResult(Activity.RESULT_OK);
                    requireActivity().finish();
                });
            });
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEB_CLIENT_ID)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        binding.googleLoginButton.setOnClickListener(view -> {
            googleSignIn();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void changeToastColors(Toast toast) {
        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
        text.setTextColor(ContextCompat.getColor(getContext(), R.color.accent));
        text.setTextSize(16);
    }

    private void googleSignIn() {
        Intent googleSignInIntent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(googleSignInIntent);
    }
}