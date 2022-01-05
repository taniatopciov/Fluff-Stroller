package com.example.fluffstroller;

import android.app.Activity;

import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.FeesService;
import com.example.fluffstroller.services.LocationService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.PhotoService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.services.RemoveDogWalkService;
import com.example.fluffstroller.services.impl.FirebaseAuthenticationService;
import com.example.fluffstroller.services.impl.FirebaseDogWalksService;
import com.example.fluffstroller.services.impl.FirebaseProfileService;
import com.example.fluffstroller.services.impl.FirebaseRemoveDogWalkService;
import com.example.fluffstroller.services.impl.LocationServiceImpl;
import com.example.fluffstroller.services.impl.LoggedUserDataServiceImpl;
import com.example.fluffstroller.services.impl.PhotoServiceFirestorage;

public class ServicesRegistration {
    private final static ServicesRegistration instance = new ServicesRegistration();
    private static boolean servicesRegistered = false;

    private ServicesRegistration() {
    }

    public static ServicesRegistration getInstance() {
        return instance;
    }

    public void registerServices(Activity activity) {
        if (servicesRegistered) {
            return;
        }
        servicesRegistered = true;

        FirebaseRepository firebaseRepository = new FirebaseRepository();
        PhotoService photoService = new PhotoServiceFirestorage();

        FirebaseProfileService firebaseProfileService = new FirebaseProfileService(firebaseRepository, photoService);
        FirebaseDogWalksService firebaseDogWalksService = new FirebaseDogWalksService(firebaseRepository);

        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        serviceLocator.register(FeesService.class, new FeesService());
        serviceLocator.register(RemoveDogWalkService.class, new FirebaseRemoveDogWalkService(firebaseDogWalksService, firebaseProfileService));
        serviceLocator.register(ProfileService.class, firebaseProfileService);
        serviceLocator.register(DogWalksService.class, firebaseDogWalksService);
        serviceLocator.register(AuthenticationService.class, new FirebaseAuthenticationService());
        serviceLocator.register(LoggedUserDataService.class, new LoggedUserDataServiceImpl());
        serviceLocator.register(LocationService.class, new LocationServiceImpl(activity));
        serviceLocator.register(PhotoService.class, photoService);
    }
}
