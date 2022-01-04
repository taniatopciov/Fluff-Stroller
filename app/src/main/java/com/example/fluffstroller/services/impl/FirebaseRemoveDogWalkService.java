package com.example.fluffstroller.services.impl;

import com.example.fluffstroller.models.DogWalk;
import com.example.fluffstroller.models.WalkRequest;
import com.example.fluffstroller.models.WalkRequestStatus;
import com.example.fluffstroller.services.DogWalksService;
import com.example.fluffstroller.services.ProfileService;
import com.example.fluffstroller.services.RemoveDogWalkService;
import com.example.fluffstroller.utils.observer.Subject;

import java.util.List;

public class FirebaseRemoveDogWalkService implements RemoveDogWalkService {

    private final DogWalksService dogWalksService;
    private final ProfileService profileService;

    public FirebaseRemoveDogWalkService(DogWalksService dogWalksService, ProfileService profileService) {
        this.dogWalksService = dogWalksService;
        this.profileService = profileService;
    }

    @Override
    public Subject<Boolean> removeCurrentWalk(String walkId, String dogOwnerId) {
        Subject<Boolean> subject = new Subject<>();

        dogWalksService.getDogWalk(walkId).subscribe(response -> {
            if (response.hasErrors() || response.data == null) {
                subject.notifyObservers(response.exception);
                return;
            }
            DogWalk dogWalk = response.data;
            List<WalkRequest> requests = dogWalk.getRequests();
            rejectStrollerWalkRequests(requests);

            dogWalksService.removeWalk(walkId).subscribe(response1 -> {
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
