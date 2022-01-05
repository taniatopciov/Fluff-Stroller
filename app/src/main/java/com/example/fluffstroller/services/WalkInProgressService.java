package com.example.fluffstroller.services;

import com.example.fluffstroller.models.DogWalk;

public interface WalkInProgressService {
    void startWalk(DogWalk dogWalk, String strollerId);
}
