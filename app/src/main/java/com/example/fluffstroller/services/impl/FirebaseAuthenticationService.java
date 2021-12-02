package com.example.fluffstroller.services.impl;

import androidx.annotation.NonNull;

import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthenticationService implements AuthenticationService {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationService() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public Subject<FirebaseUser> loginWithEmailAndPassword(String email, String password) {
        Subject<FirebaseUser> subject = new Subject<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            subject.notifyObservers(user);
                        } else {
                            subject.notifyObservers(new Exception());
                        }
                    }
                });

        return subject;
    }

    @Override
    public void logout() {

    }

    @Override
    public Subject<FirebaseUser> register(String name, String email, String password, String userType) {
        return null;
    }
}
