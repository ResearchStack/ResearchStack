package org.researchstack.skin;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PermissionRequestManager
{
    public static final int PERMISSION_REQUEST_CODE = 142;

    private static PermissionRequestManager instance;

    public static void init(PermissionRequestManager manager)
    {
        PermissionRequestManager.instance = manager;
    }

    public static PermissionRequestManager getInstance()
    {
        if(instance == null)
        {
            throw new RuntimeException(
                    "PermissionRequestManager instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    private Map<String, PermissionRequest> permissionRequests = new HashMap<>();

    public List<PermissionRequest> getPermissionRequests()
    {
        return new ArrayList<>(permissionRequests.values());
    }

    public void setPermissionRequests(List<PermissionRequest> permissionRequests)
    {
        this.permissionRequests.clear();
        addPermission(permissionRequests);
    }

    public void addPermission(Collection<PermissionRequest> permissionRequests)
    {
        for(PermissionRequest permissionRequest : permissionRequests)
        {
            addPermission(permissionRequest);
        }
    }

    public void addPermission(PermissionRequest... permissionRequests)
    {
        for(PermissionRequest permissionRequest : permissionRequests)
        {
            addPermission(permissionRequest);
        }
    }

    public void addPermission(PermissionRequest permissionRequest)
    {
        this.permissionRequests.put(permissionRequest.getId(), permissionRequest);
    }

    public boolean isNonSystemPermission(String id)
    {
        return !permissionRequests.isEmpty() && !permissionRequests.get(id).isSystemPermission();
    }

    public abstract boolean hasPermission(Context context, String permissionId);

    public abstract boolean onNonSystemPermissionResult(Activity activity, int requestCode, int resultCode, Intent data);

    public abstract void onRequestNonSystemPermission(Activity activity, String id);

    public static class PermissionRequest
    {
        private String id;

        private int iconRes;

        private int titleRes;

        private int textRes;

        private boolean blocking;

        private boolean system;

        public PermissionRequest(String id, @DrawableRes int iconRes, @StringRes int titleRes, @StringRes int textRes)
        {
            this.id = id;
            this.iconRes = iconRes;
            this.titleRes = titleRes;
            this.textRes = textRes;
        }

        public String getId()
        {
            return id;
        }

        @DrawableRes
        public int getIcon(){
            return iconRes;
        }

        @StringRes
        public int getTitle(){
            return titleRes;
        }

        @StringRes
        public int getText(){
            return textRes;
        }

        public void setIsBlockingPermission(boolean blocking)
        {
            this.blocking = blocking;
        }

        public boolean isBlockingPermission()
        {
            return blocking;
        }

        public void setIsSystemPermission(boolean system)
        {
            this.system = system;
        }

        public boolean isSystemPermission()
        {
            return system;
        }
    }

}
