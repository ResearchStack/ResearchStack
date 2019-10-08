package org.researchstack.backbone.ui.permissions;

import androidx.annotation.NonNull;

public interface PermissionListener {
    /**
     * Callback for when a {@link PermissionMediator} issues the response from a permission
     * request.
     * <b>The name is misleading</b>, because the permission result doesn't guarantee in any
     * capacity that the permission is granted; always check again.
     *
     * @param permissionResult the result of the permission request. Cannot be null, but doesn't
     *                         imply the permission was granted.
     */
    void onPermissionGranted(@NonNull PermissionResult permissionResult);
}
