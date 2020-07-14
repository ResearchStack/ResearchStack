package org.researchstack.backbone.ui.permissions;

import androidx.annotation.NonNull;

public interface PermissionMediator {
    void requestPermissions(PermissionListener permissionListener, String... permissions);

    void requestPermissions(String... permissions);

    boolean checkIfShouldShowRequestPermissionRationale(@NonNull String permission);
}