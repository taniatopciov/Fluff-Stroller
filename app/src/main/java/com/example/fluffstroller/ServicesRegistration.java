package com.example.fluffstroller;

import com.example.fluffstroller.di.ServiceLocator;
import com.example.fluffstroller.services.FeesService;

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

        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        serviceLocator.register(FeesService.class, new FeesService());
    }
}
