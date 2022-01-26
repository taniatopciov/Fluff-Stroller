package com.example.fluffstroller.services.impl;

import androidx.annotation.NonNull;

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

import java.util.ArrayList;
import java.util.List;

public class WalkInProgressServiceImpl implements WalkInProgressService {
    private static final String WALKS_PATH = "walks";
    private static final String COORDINATES_CHILD = "coordinates";
    private static final String WALK_STATUS_CHILD = "walkStatus";

    private final DatabaseReference firebaseDatabaseReference = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_REALTIME_DATABASE_URL).getReference();

    @Override
    public void startWalk(DogWalk dogWalk, String strollerId) {
        WalkInProgressModel walkInProgress = new WalkInProgressModel(dogWalk.getId(), strollerId, dogWalk.getOwnerId(), dogWalk.getLocation());

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
    public Subject<WalkInProgressModel> getWalkInProgressModel(String walkId) {
        Subject<WalkInProgressModel> subject = new Subject<>();
        DatabaseReference walkReference = firebaseDatabaseReference.child(WALKS_PATH).child(walkId);

        walkReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                WalkInProgressModel value = snapshot.getValue(WalkInProgressModel.class);

                if (value != null) {
                    List<Location> locations = new ArrayList<>();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        if (COORDINATES_CHILD.equals(data.getKey())) {

                            for (DataSnapshot coordinates : data.getChildren()) {
                                locations.add(coordinates.getValue(Location.class));
                            }
                            break;
                        }
                    }

                    value.setSortedCoordinates(locations);
                }

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
