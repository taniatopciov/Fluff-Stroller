package com.example.flusffstroller;

import com.example.flusffstroller.di.ServiceLocator;
import com.example.flusffstroller.repository.FirebaseRepository;
import com.example.flusffstroller.services.DogWalksService;
import com.example.flusffstroller.services.FeesService;
import com.example.flusffstroller.services.ProfileService;
import com.example.flusffstroller.services.StrollerService;
import com.example.flusffstroller.services.impl.FirebaseDogWalksService;
import com.example.flusffstroller.services.impl.MockProfileService;
import com.example.flusffstroller.services.impl.MockStrollerService;

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
        serviceLocator.register(ProfileService.class, new MockProfileService(firebaseRepository));
        serviceLocator.register(DogWalksService.class, new FirebaseDogWalksService(firebaseRepository));
    }
}
