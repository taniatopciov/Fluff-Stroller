package com.example.fluffstroller.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import com.example.fluffstroller.R;
import com.example.fluffstroller.databinding.LoginFragmentBinding;
import com.example.fluffstroller.di.Injectable;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.FragmentWithServices;
import com.example.fluffstroller.utils.HideKeyboard;
import com.example.fluffstroller.utils.components.CustomToast;
import com.example.fluffstroller.utils.observer.Subject;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends FragmentWithServices {

    @Injectable
    private AuthenticationService authenticationService;

    @Injectable
    private ProfileService profileService;

    @Injectable
    private LoggedUserDataService loggedUserDataService;

    private LoginFragmentBinding binding;

    private Subject<FirebaseUser> googleSignInSubject;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        authenticationService.getLoginWithGoogleIntent(account.getIdToken()).subscribe(response -> {
                            if (response.hasErrors()) {
                                CustomToast.show(getActivity(), "Google sign in failed",
                                        Toast.LENGTH_LONG);
                                return;
                            }
                            if (googleSignInSubject != null) {
                                googleSignInSubject.notifyObservers(response.data);
                            }
                        });
                    } catch (ApiException e) {
                        CustomToast.show(getActivity(), "Google sign in failed",
                                Toast.LENGTH_LONG);
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
                CustomToast.show(getActivity(), "All fields must be completed",
                        Toast.LENGTH_LONG);
                return;
            }

            authenticationService.loginWithEmailAndPassword(email, password).subscribe(response -> {
                if (response.hasErrors()) {
                    CustomToast.show(getActivity(), "Invalid credentials",
                            Toast.LENGTH_LONG);
                    return;
                }

                profileService.getProfileData(response.data.getUid()).subscribe(response2 -> {
                    if (response2.hasErrors()) {
                        CustomToast.show(getActivity(), "Fetching data from Firebase failed",
                                Toast.LENGTH_LONG);
                        return;
                    }
                    loggedUserDataService.setLoggedUserData(response2.data);

                    HideKeyboard.hide(getActivity());
                    NavHostFragment.findNavController(this).navigate(LoginFragmentDirections.actionLoginFragmentToNavHome());
                });
            });
        });


        NavController navController = NavHostFragment.findNavController(this);

        binding.googleLoginButton.setOnClickListener(view -> {
            googleSignInSubject = new Subject<>();
            googleSignInSubject.subscribe(response -> {
                if (response.data == null) {
                    CustomToast.show(getActivity(), "Fetching data from Firebase failed", Toast.LENGTH_LONG);
                    return;
                }

                profileService.getProfileData(response.data.getUid()).subscribe(response2 -> {
                    if (response2.hasErrors()) {
                        CustomToast.show(getActivity(), "Fetching data from Firebase failed", Toast.LENGTH_LONG);
                        return;
                    }

                    if (response2.data == null) {
                        navController.navigate(LoginFragmentDirections.actionLoginFragmentToSetupProfileTypeFragment(response.data.getUid(), response.data.getDisplayName(), response.data.getEmail()));
                        return;
                    }

                    loggedUserDataService.setLoggedUserData(response2.data);

                    navController.navigate(LoginFragmentDirections.actionLoginFragmentToNavHome());
                });
            });

            googleSignIn();
        });

        LoginManager.getInstance().logOut();
        CallbackManager callbackManager = CallbackManager.Factory.create();
        binding.facebookLoginButton.setPermissions("email", "public_profile");
        binding.facebookLoginButton.setFragment(this);
        binding.facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                authenticationService.loginWithFacebook(loginResult.getAccessToken()).subscribe(response -> {
                    if (response.hasErrors() || response.data == null) {
                        CustomToast.show(getActivity(), "Facebook Login error", Toast.LENGTH_LONG);
                        return;
                    }

                    profileService.getProfileData(response.data.getUid()).subscribe(response2 -> {
                        if (response2.hasErrors()) {
                            CustomToast.show(getActivity(), "Fetching data from Firebase failed",
                                    Toast.LENGTH_LONG);
                            return;
                        }

                        if (response2.data == null) {
                            navController.navigate(LoginFragmentDirections.actionLoginFragmentToSetupProfileTypeFragment(response.data.getUid(), response.data.getDisplayName(), response.data.getEmail()));
                            return;
                        }

                        loggedUserDataService.setLoggedUserData(response2.data);

                        navController.navigate(LoginFragmentDirections.actionLoginFragmentToNavHome());
                    });

                });
            }

            @Override
            public void onCancel() {
                CustomToast.show(getActivity(), "Facebook Login canceled", Toast.LENGTH_LONG);
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                CustomToast.show(getActivity(), "Facebook Login error", Toast.LENGTH_LONG);
            }
        });

        binding.forgotPasswordTextView.setOnClickListener(view -> {
            String email = binding.emailTextWithLabelLoginFragment.editText.getText().toString();

            if (email.isEmpty()) {
                CustomToast.show(getActivity(), R.string.email_field_must_be_set, Toast.LENGTH_LONG);
                return;
            }

            authenticationService.sendResetPasswordRequest(email).subscribe(response -> {
                if (response.hasErrors()) {
                    CustomToast.show(getActivity(), R.string.email_send_failed, Toast.LENGTH_LONG);
                } else {
                    CustomToast.show(getActivity(), R.string.reset_email_sent, Toast.LENGTH_LONG);
                }
            });
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (loggedUserDataService.isUserLogged()) {
            Navigation.findNavController(binding.getRoot()).navigate(LoginFragmentDirections.actionLoginFragmentToNavHome());
        }

    }

    private void googleSignIn() {
        activityResultLauncher.launch(authenticationService.getLoginWithGoogleIntent());
    }
}