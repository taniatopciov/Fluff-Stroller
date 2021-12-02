package com.example.fluffstroller;

import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.models.UserType;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.FeesService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.services.StrollerService;
import com.example.fluffstroller.services.impl.FirebaseDogWalksService;
import com.example.fluffstroller.services.impl.MockProfileService;
import com.example.fluffstroller.services.impl.MockStrollerService;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
