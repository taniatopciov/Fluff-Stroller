package com.example.fluffstroller.services.impl;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import com.example.fluffstroller.MainActivity;
import com.example.fluffstroller.R;
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.services.LocationService;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.fluffstroller.App.LOCATION_SERVICE_CHANNEL_ID;


public class LocationServiceImpl extends Service implements LocationService {
    private static final int RT_LOCATION_REQUEST_INTERVAL = 4000; // real-time location interval
    private static final int RT_FASTEST_LOCATION_REQUEST_INTERVAL = 2000; // real-time location interval if available sooner
    private static final int LOCATION_SERVICE_ID = 175;
    private final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    private final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";


    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();

                System.out.println(latitude + " " + longitude);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(ACTION_START_LOCATION_SERVICE)) {
                    startRealTimeLocationService();
                } else if (action.equals(ACTION_STOP_LOCATION_SERVICE)) {
                    stopRealTimeLocationService();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void startRealTimeLocationTracking(Activity activity) {
        if (!isRealTimeLocationRunning(activity)) {
            Intent intent = new Intent(activity, getClass());
            intent.setAction(ACTION_START_LOCATION_SERVICE);
            activity.startService(intent);
        }
    }

    @Override
    public void stopRealTimeLocationTracking(Activity activity) {
        if (isRealTimeLocationRunning(activity)) {
            Intent intent = new Intent(activity, getClass());
            intent.setAction(ACTION_STOP_LOCATION_SERVICE);
            activity.startService(intent);
        }
    }

    @Override
    public boolean isRealTimeLocationRunning(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (getClass().getName().equals(serviceInfo.service.getClassName())) {
                    if (serviceInfo.foreground) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public Subject<Location> getCurrentLocation(Activity activity) {
        Subject<Location> subject = new Subject<>();

        LocationServices.getFusedLocationProviderClient(activity)
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        // if is null, the Location is turned off
                        subject.notifyObservers((Location) null);
                        return;
                    }
                    subject.notifyObservers(new Location(location.getLatitude(), location.getLongitude()));
                })
                .addOnFailureListener(subject::notifyObservers);

        return subject;
    }


    private void startRealTimeLocationService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, LOCATION_SERVICE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_map_24)
                .setContentTitle("Walk in Progress")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentText("Running")
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        startForeground(LOCATION_SERVICE_ID, notification);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(RT_LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(RT_FASTEST_LOCATION_REQUEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopRealTimeLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }
}
