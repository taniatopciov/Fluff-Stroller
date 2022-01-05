package com.example.fluffstroller;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String LOCATION_SERVICE_CHANNEL_ID = "location_notification_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(LOCATION_SERVICE_CHANNEL_ID, "Location Service", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("This channel is used by location service");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
