package com.example.fluffstroller.services;

import android.content.Intent;

import com.example.fluffstroller.utils.observer.Subject;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseUser;

public interface AuthenticationService {

    Subject<FirebaseUser> loginWithEmailAndPassword(String email, String password);

    Subject<FirebaseUser> getLoginWithGoogleIntent(String tokenId);

    Intent getLoginWithGoogleIntent();

    void logout();

    Subject<FirebaseUser> register( String email, String password);

    Subject<FirebaseUser> loginWithFacebook(AccessToken accessToken);

    Subject<Boolean> sendResetPasswordRequest(String email);
}
