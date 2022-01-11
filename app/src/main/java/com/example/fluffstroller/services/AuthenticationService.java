package com.example.fluffstroller.services;

import com.example.fluffstroller.utils.observer.Subject;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseUser;

public interface AuthenticationService {

    Subject<FirebaseUser> loginWithEmailAndPassword(String email, String password);

    Subject<FirebaseUser> loginWithGoogle(String tokenId);

    void logout();

    Subject<FirebaseUser> register( String email, String password);

    Subject<FirebaseUser> loginWithFacebook(AccessToken accessToken);
}
