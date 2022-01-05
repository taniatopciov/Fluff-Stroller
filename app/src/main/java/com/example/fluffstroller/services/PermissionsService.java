package com.example.fluffstroller.services;

import java.util.function.Consumer;

public interface PermissionsService {
    void checkPermission(String permission, Consumer<Boolean> onPermissionGranted);
}
