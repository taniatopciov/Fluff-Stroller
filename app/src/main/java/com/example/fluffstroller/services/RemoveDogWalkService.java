package com.example.fluffstroller.services;

import com.example.fluffstroller.utils.observer.Subject;

public interface RemoveDogWalkService {
    Subject<Boolean> removeCurrentWalk(String walkId, String dogOwnerId);
}
