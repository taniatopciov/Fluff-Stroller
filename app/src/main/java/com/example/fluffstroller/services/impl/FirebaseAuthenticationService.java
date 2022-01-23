package com.example.fluffstroller.services.impl;

import android.app.Activity;
import android.content.Intent;

import com.example.fluffstroller.BuildConfig;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.utils.observer.Subject;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;

public class FirebaseAuthenticationService implements AuthenticationService {

    private final FirebaseAuth firebaseAuth;
    private final GoogleSignInClient googleSignInClient;

    public FirebaseAuthenticationService(Activity activity) {
        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEB_CLIENT_ID)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    @Override
    public Subject<FirebaseUser> loginWithEmailAndPassword(String email, String password) {
        Subject<FirebaseUser> subject = new Subject<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getAuthResultOnCompleteListener(subject));
        return subject;
    }

    @Override
    public Subject<FirebaseUser> getLoginWithGoogleIntent(String tokenId) {
        Subject<FirebaseUser> subject = new Subject<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(tokenId, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getAuthResultOnCompleteListener(subject));
        return subject;
    }

    @Override
    public Intent getLoginWithGoogleIntent() {
        return googleSignInClient.getSignInIntent();
    }

    @Override
    public Subject<FirebaseUser> loginWithFacebook(AccessToken accessToken) {
        Subject<FirebaseUser> subject = new Subject<>();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getAuthResultOnCompleteListener(subject));

        return subject;
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();

        googleSignInClient.signOut().addOnCompleteListener(runnable -> {
        });
    }

    @Override
    public Subject<FirebaseUser> register(String email, String password) {
        Subject<FirebaseUser> subject = new Subject<>();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getAuthResultOnCompleteListener(subject));
        return subject;
    }

    @Override
    public Subject<Boolean> sendResetPasswordRequest(String email) {
        Subject<Boolean> subject = new Subject<>();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                subject.notifyObservers(true);
            } else {
                subject.notifyObservers(task.getException());
            }
        });
        return subject;
    }

    @NonNull
    private OnCompleteListener<AuthResult> getAuthResultOnCompleteListener(Subject<FirebaseUser> subject) {
        return task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                subject.notifyObservers(user);
            } else {
                subject.notifyObservers(task.getException());
            }
        };
    }
}
