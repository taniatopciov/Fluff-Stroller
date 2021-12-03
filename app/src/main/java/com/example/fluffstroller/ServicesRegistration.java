package com.example.fluffstroller;

import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.AuthenticationService;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.FeesService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.services.StrollerService;
import com.example.fluffstroller.services.impl.FirebaseAuthenticationService;
import com.example.fluffstroller.services.impl.FirebaseDogWalksService;
import com.example.fluffstroller.services.impl.FirebaseProfileService;
import com.example.fluffstroller.services.impl.LoggedUserDataServiceImpl;
import com.example.fluffstroller.services.impl.MockStrollerService;

public class ServicesRegistration {
    private final static ServicesRegistration instance = new ServicesRegistration();
    private static boolean servicesRegistered = false;

    private ServicesRegistration() {
    }

    public static ServicesRegistration getInstance() {
        return instance;
    }

    public void registerServices() {
        if (servicesRegistered) {
            return;
        }
        servicesRegistered = true;

        FirebaseRepository firebaseRepository = new FirebaseRepository();

        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        serviceLocator.register(FeesService.class, new FeesService());
        serviceLocator.register(StrollerService.class, new MockStrollerService());
        serviceLocator.register(ProfileService.class, new FirebaseProfileService(firebaseRepository));
        serviceLocator.register(DogWalksService.class, new FirebaseDogWalksService(firebaseRepository));
        serviceLocator.register(AuthenticationService.class, new FirebaseAuthenticationService());
        serviceLocator.register(LoggedUserDataService.class, new LoggedUserDataServiceImpl());
    }
}
