package com.example.fluffstroller.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fluffstroller.BuildConfig;
import com.example.fluffstroller.databinding.LoginFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.HideKeyboard;
import com.example.fluffstroller.utils.components.CustomToast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

public class LoginFragment extends FragmentWithServices {

    @Injectable
    private AuthenticationService authenticationService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

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
                                CustomToast.show(requireActivity(), "Google sign in failed",
                                        Toast.LENGTH_LONG);
                                return;
                            }

                            //TODO finish google login
                        });
                    } catch (ApiException e) {

                    }
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = LoginFragmentBinding.inflate(inflater, container, false);

        binding.noAccountTextViewLoginFragment.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(LoginFragmentDirections.navLoginToRegister());
        });

        binding.loginButton.setOnClickListener(view -> {
            String email = binding.emailTextWithLabelLoginFragment.editText.getText().toString();
            String password = binding.passwordTextWithLabelLoginFragment.editText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                CustomToast.show(requireActivity(), "All fields must be completed",
                        Toast.LENGTH_LONG);
                return;
            }

            authenticationService.loginWithEmailAndPassword(email, password).subscribe(response -> {
                if (response.hasErrors()) {
                    CustomToast.show(requireActivity(), "Invalid credentials",
                            Toast.LENGTH_LONG);
                    return;
                }

                profileService.getProfileData(response.data.getUid()).subscribe(response2 -> {
                    if (response2.hasErrors()) {
                        CustomToast.show(requireActivity(), "Fetching data from Firebase failed",
                                Toast.LENGTH_LONG);
                        return;
                    }
                    loggedUserDataService.setLoggedUserData(response2.data);

                    HideKeyboard.hide(requireActivity());
                    NavHostFragment.findNavController(this).navigate(LoginFragmentDirections.actionLoginFragmentToNavHome());
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

    private void googleSignIn() {
        Intent googleSignInIntent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(googleSignInIntent);
    }
}