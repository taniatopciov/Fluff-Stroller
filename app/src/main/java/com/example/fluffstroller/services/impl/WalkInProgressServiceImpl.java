package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.BuildConfig;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.models.WalkInProgressModel;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.services.WalkInProgressService;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class WalkInProgressServiceImpl implements WalkInProgressService {
    private static final String WALKS_PATH = "walks";
    private static final String COORDINATES_CHILD = "coordinates";
    private static final String WALK_STATUS_CHILD = "walkStatus";

    private final DatabaseReference firebaseDatabaseReference = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_REALTIME_DATABASE_URL).getReference();

    @Override
    public void startWalk(DogWalk dogWalk, String strollerId) {
        WalkInProgressModel walkInProgress = new WalkInProgressModel(dogWalk.getId(), dogWalk.getLocation());

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
        walkReference.child(COORDINATES_CHILD).push().setValue(new Location(latitude, longitude));
    }

    @Override
    public void updateWalkStatus(String walkId, WalkStatus status) {
        firebaseDatabaseReference.child(WALKS_PATH)
                .child(walkId)
                .child(WALK_STATUS_CHILD)
                .setValue(status);
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
    public Subject<List<Location>> getLocationsInRealTime(String walkId) {
        Subject<List<Location>> subject = new Subject<>();

        DatabaseReference walkReference = firebaseDatabaseReference.child(WALKS_PATH).child(walkId);
        DatabaseReference coordinatesReference = walkReference.child(COORDINATES_CHILD);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    List<Location> locations = new ArrayList<>();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        locations.add(childSnapshot.getValue(Location.class));
                    }

                    subject.notifyObservers(locations);
                } catch (Exception e) {
                    subject.notifyObservers(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                subject.notifyObservers(error.toException());
            }
        };

        coordinatesReference.addValueEventListener(listener);

        subject.setOnObserversCleared(() -> coordinatesReference.removeEventListener(listener));

        return subject;
    }
}
