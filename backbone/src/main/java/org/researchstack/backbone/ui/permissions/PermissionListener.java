package org.researchstack.backbone.ui.permissions;

import android.support.annotation.NonNull;

public interface PermissionListener {
    void onPermissionGranted(@NonNull PermissionResult permissionResult);
}
