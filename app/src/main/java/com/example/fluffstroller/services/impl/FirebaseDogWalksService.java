package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.BuildConfig;
import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.Location;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.repository.FirebaseRepository;
import com.example.fluffstroller.services.DogWalksService;
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

public class FirebaseDogWalksService implements DogWalksService {

    public static final String WALKS_PATH = "walks";

    private final FirebaseRepository firebaseRepository;
    private final OkHttpClient client;
    private final Gson gson;

    public FirebaseDogWalksService(FirebaseRepository firebaseRepository) {
        this.firebaseRepository = firebaseRepository;
        client = new OkHttpClient();
        gson = new Gson();
    }

    @Override
    public Subject<DogWalk> createDogWalk(DogWalk dogWalk) {
        return firebaseRepository.addDocument(WALKS_PATH, dogWalk);
    }

    @Override
    public Subject<Boolean> updateDogWalk(DogWalk dogWalk) {
        Map<String, Object> values = new HashMap<>();
        values.put("id", dogWalk.getId());
        values.put("requests", dogWalk.getRequests());
        values.put("status", dogWalk.getStatus());

        return firebaseRepository.updateDocument(WALKS_PATH + "/" + dogWalk.getId(), values);
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
}
