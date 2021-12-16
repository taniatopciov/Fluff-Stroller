package com.example.fluffstroller.services;

import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.utils.observer.Subject;

public interface LocationService {
    Subject<Location> getCurrentLocation();
}
