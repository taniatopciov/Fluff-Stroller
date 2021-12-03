package com.example.fluffstroller.services.impl;

import androidx.annotation.NonNull;

import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthenticationService implements AuthenticationService {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationService() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public Subject<FirebaseUser> loginWithEmailAndPassword(String email, String password) {
        Subject<FirebaseUser> subject = new Subject<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getAuthResultOnCompleteListener(subject));
        return subject;
    }

    @Override
    public Subject<FirebaseUser> loginWithGoogle(String tokenId) {
        Subject<FirebaseUser> subject = new Subject<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(tokenId, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getAuthResultOnCompleteListener(subject));
        return subject;
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
    }

    @Override
    public Subject<FirebaseUser> register(String email, String password) {
        Subject<FirebaseUser> subject = new Subject<>();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getAuthResultOnCompleteListener(subject));
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
