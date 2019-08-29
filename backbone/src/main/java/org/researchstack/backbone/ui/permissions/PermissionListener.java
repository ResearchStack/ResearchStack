package org.researchstack.backbone.ui.permissions;

import androidx.annotation.NonNull;

public interface PermissionListener {
    void onPermissionGranted(@NonNull PermissionResult permissionResult);
}
