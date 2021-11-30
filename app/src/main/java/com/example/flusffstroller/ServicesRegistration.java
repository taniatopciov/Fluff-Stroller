package com.example.flusffstroller;

import com.example.flusffstroller.di.ServiceLocator;
import com.example.flusffstroller.services.FeesService;

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
