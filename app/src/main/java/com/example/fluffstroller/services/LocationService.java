package com.example.fluffstroller.services;

import android.app.Activity;

import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.utils.observer.Subject;

public interface LocationService {
    void startRealTimeLocationTracking(Activity activity, String walkId);

    void stopRealTimeLocationTracking(Activity activity);

    boolean isRealTimeLocationRunning(Activity activity);

    Subject<Location> getCurrentLocation(Activity activity);
}
