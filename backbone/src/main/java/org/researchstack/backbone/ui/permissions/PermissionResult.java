package org.researchstack.backbone.ui.permissions;

import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class PermissionResult {

    private final Map<String, Integer> permissionResults = new HashMap<>();

    public PermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            permissionResults.put(permissions[i], grantResults[i]);
        }
    }

    public boolean containsResult(String permission) {
        return permissionResults.containsKey(permission);
    }

    public boolean isGranted(String permission) {
        final Integer result = permissionResults.get(permission);
        return result != null && result == PackageManager.PERMISSION_GRANTED;
    }
}
