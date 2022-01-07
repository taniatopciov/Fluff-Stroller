package com.example.fluffstroller.services;

import java.util.List;
import java.util.function.Consumer;

public interface PermissionsService {
    void checkPermission(String permission, Consumer<Boolean> onPermissionGranted);

    void checkPermissions(List<String> permissions, Consumer<Boolean> onPermissionsGranted);
}
