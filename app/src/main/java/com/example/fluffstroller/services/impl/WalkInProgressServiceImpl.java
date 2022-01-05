package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.BuildConfig;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.models.WalkInProgressModel;
import com.example.fluffstroller.services.WalkInProgressService;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

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

    @Override
    public void addLocation(String walkId, double latitude, double longitude) {
        if (walkId == null || walkId.isEmpty()) {
            return;
        }

        DatabaseReference walkReference = firebaseDatabaseReference.child(WALKS_PATH).child(walkId);
        walkReference.child(LATITUDE_CHILD).push().setValue(latitude);
        walkReference.child(LONGITUDE_CHILD).push().setValue(longitude);
    }

    @Override
    public Subject<WalkInProgressModel> getWalkInProgressModel(String walkId) {
        Subject<WalkInProgressModel> subject = new Subject<>();
        DatabaseReference walkReference = firebaseDatabaseReference.child(WALKS_PATH).child(walkId);

        walkReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WalkInProgressModel value = snapshot.getValue(WalkInProgressModel.class);
                subject.notifyObservers(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                subject.notifyObservers(error.toException());
            }
        });

        return subject;
    }

    @Override
    public Subject<Location> getLocations(String walkId) {
        return null;
    }
}
