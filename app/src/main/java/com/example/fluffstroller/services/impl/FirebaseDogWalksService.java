package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.BuildConfig;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.DogWalkPreview;
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkRequestStatus;
import com.example.fluffstroller.models.WalkStatus;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.LoggedUserDataService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.utils.observer.Subject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirebaseDogWalksService implements DogWalksService {

    public static final String WALKS_PATH = "walks";

    private final FirebaseRepository firebaseRepository;
    private final ProfileService profileService;
    private final LoggedUserDataService loggedUserDataService;
    private final OkHttpClient client;
    private final Gson gson;

    public FirebaseDogWalksService(FirebaseRepository firebaseRepository, ProfileService profileService, LoggedUserDataService loggedUserDataService) {
        this.firebaseRepository = firebaseRepository;
        this.profileService = profileService;
        this.loggedUserDataService = loggedUserDataService;
        client = new OkHttpClient();
        gson = new Gson();
    }

    @Override
    public Subject<DogWalk> createDogWalk(DogWalk dogWalk) {
        return firebaseRepository.addDocument(WALKS_PATH, dogWalk);
    }

    @Override
    public Subject<DogWalkPreview> updateDogWalk(String ownerId, String walkId, WalkStatus walkStatus, List<WalkRequest> requests) {
        Subject<DogWalkPreview> subject = new Subject<>();
        Map<String, Object> values = new HashMap<>();
        values.put("id", walkId);
        values.put("status", walkStatus);
        if (requests != null) {
            values.put("requests", requests);
        }

        firebaseRepository.updateDocument(WALKS_PATH + "/" + walkId, values).subscribe(response -> {
            if (response.hasErrors()) {
                subject.notifyObservers(response.exception);
                return;
            }

            DogWalkPreview walkPreview = new DogWalkPreview(walkId, walkStatus);
            profileService.updateDogWalkPreview(ownerId, walkPreview).subscribe(response1 -> {
                if (response1.hasErrors()) {
                    subject.notifyObservers(response1.exception);
                } else {
                    subject.notifyObservers(walkPreview);
                }
            });
        });

        return subject;
    }

    @Override
    public Subject<DogWalk> getDogWalk(String id) {
        return firebaseRepository.getDocument(WALKS_PATH + "/" + id, DogWalk.class);
    }

    @Override
    public Subject<DogWalk> listenForDogWalkChanges(String walkId) {
        return firebaseRepository.listenForDocumentChanges(WALKS_PATH + "/" + walkId, DogWalk.class);
    }

    @Override
    public Subject<Boolean> removeWalk(String walkId) {
        return firebaseRepository.deleteDocument(WALKS_PATH, walkId);
    }

    @Override
    public Subject<List<DogWalk>> getNearbyAvailableDogWalks(String id, Location currentLocation, Double radius) {
        Subject<List<DogWalk>> subject = new Subject<>();

        new Thread(() -> {
            try {
                HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(BuildConfig.NEARBY_WALKS_SERVICE_URL).newBuilder()
                        .addQueryParameter("id", id)
                        .addQueryParameter("latitude", String.valueOf(currentLocation.latitude))
                        .addQueryParameter("longitude", String.valueOf(currentLocation.longitude))
                        .addQueryParameter("radius", String.valueOf(radius));

                Request request = new Request.Builder()
                        .url(httpUrlBuilder.build())
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                String responseString = response.body().string();

                List<DogWalk> data = gson.fromJson(responseString, new TypeToken<List<DogWalk>>() {
                }.getType());

                subject.notifyObservers(data);

            } catch (Exception e) {
                subject.notifyObservers(e);
            }
        }).start();

        return subject;
    }

    @Override
    public Subject<Boolean> requestWalk(WalkRequest walkRequest) {
        return firebaseRepository.addItemToArray(WALKS_PATH + "/" + walkRequest.getWalkId(), "requests", walkRequest);
    }

    @Override
    public Subject<Boolean> updateWalkAfterPayment(String ownerId, String strollerId) {
        Subject<Boolean> subject = new Subject<>();

        AtomicBoolean updatedDogWalkPreview = new AtomicBoolean(false);
        AtomicBoolean updatedCurrentRequest = new AtomicBoolean(false);

        profileService.updateDogWalkPreview(ownerId, null).subscribe(response1 -> {
            if(response1.hasErrors()) {
                subject.notifyObservers(response1.exception);
                return;
            }
            updatedDogWalkPreview.set(true);

            if(updatedDogWalkPreview.get() && updatedCurrentRequest.get()) {
                loggedUserDataService.setDogWalkPreview(null);
                subject.notifyObservers(true);
            }
        });

        profileService.updateCurrentRequest(strollerId,null).subscribe(response2 -> {
            if(response2.hasErrors()) {
                subject.notifyObservers(response2.exception);
                return;
            }
            updatedCurrentRequest.set(true);

            if(updatedDogWalkPreview.get() && updatedCurrentRequest.get()) {
                loggedUserDataService.setCurrentRequest(null);
                subject.notifyObservers(true);
            }
        });

        return subject;
    }

    @Override
    public Subject<Boolean> removeCurrentWalk(String walkId, String dogOwnerId) {
        Subject<Boolean> subject = new Subject<>();

        getDogWalk(walkId).subscribe(response -> {
            if (response.hasErrors() || response.data == null) {
                subject.notifyObservers(response.exception);
                return;
            }
            DogWalk dogWalk = response.data;
            List<WalkRequest> requests = dogWalk.getRequests();
            rejectStrollerWalkRequests(requests);

            removeWalk(walkId).subscribe(response1 -> {
                if (response.hasErrors()) {
                    subject.notifyObservers(response.exception);
                    return;
                }

                profileService.updateDogWalkPreview(dogOwnerId, null).subscribe(response2 -> {
                    if (response.hasErrors()) {
                        subject.notifyObservers(response.exception);
                        return;
                    }

                    subject.notifyObservers(true);
                });
            });
        });

        return subject;
    }

    private void rejectStrollerWalkRequests(List<WalkRequest> requests) {
        if (requests != null) {
            for (WalkRequest request : requests) {
                if (request.getStatus() == WalkRequestStatus.PENDING) {
                    request.setStatus(WalkRequestStatus.CANCELED);
                    profileService.updateCurrentRequest(request.getStrollerId(), request).subscribe(response -> {
                    });
                }
            }
        }
    }
}
