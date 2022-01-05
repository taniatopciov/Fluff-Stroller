package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.BuildConfig;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.models.WalkInProgressModel;
import com.example.fluffstroller.services.WalkInProgressService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WalkInProgressServiceImpl implements WalkInProgressService {
    private static final String WALKS_PATH = "walks";
    private static final String LATITUDE_CHILD = "latitude";
    private static final String LONGITUDE_CHILD = "longitude";
    private static final String CREATION_TIME_MILLIS_CHILD = "creationTimeMillis";

    private final DatabaseReference firebaseDatabaseReference = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_REALTIME_DATABASE_URL).getReference();

    @Override
    public void startWalk(DogWalk dogWalk, String strollerId) {
        WalkInProgressModel walkInProgress = new WalkInProgressModel(dogWalk.getId(), dogWalk.getOwnerId(), strollerId, dogWalk.getCreationTimeMillis());
        Location location = dogWalk.getLocation();
        walkInProgress.getLongitude().add(location.longitude);
        walkInProgress.getLatitude().add(location.latitude);

        firebaseDatabaseReference.child(WALKS_PATH)
                .child(dogWalk.getId())
                .setValue(walkInProgress);
    }
}
