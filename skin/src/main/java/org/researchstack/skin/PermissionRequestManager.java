package org.researchstack.skin;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import org.researchstack.skin.ui.layout.PermissionStepLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for checking permissions during runtime. It is flexible enough to
 * handle system and custom permissions (like gainging auth for a 3rd party service).
 * <p>
 * Any PermissionRequest should be added to the PermissionRequestManager during {@link
 * Application#onCreate()}
 * <p>
 * The PermissionRequest objects are then presented in {@link PermissionStepLayout} for denile /
 * approval
 */
public abstract class PermissionRequestManager {
    /**
     * Request code for handling a system permission on 6.0+
     */
    public static final int PERMISSION_REQUEST_CODE = 142;

    private static PermissionRequestManager instance;
    private Map<String, PermissionRequest> permissionRequests = new HashMap<>();

    /**
     * Initializes the UiManager singleton. It is best to call this method inside your {@link
     * Application#onCreate()} method.
     *
     * @param manager an implementation of ResourcePathManager
     */
    public static void init(PermissionRequestManager manager) {
        PermissionRequestManager.instance = manager;
    }

    /**
     * Returns a singleton static instance of the this class
     *
     * @return A singleton static instance of the this class
     */
    public static PermissionRequestManager getInstance() {
        if (instance == null) {
            throw new RuntimeException(
                    "PermissionRequestManager instance is null. Make sure to init a concrete implementation of ResearchStack in Application.onCreate()");
        }

        return instance;
    }

    /**
     * Returns a list of permission requests
     *
     * @return list of permission requests
     */
    public List<PermissionRequest> getPermissionRequests() {
        return new ArrayList<>(permissionRequests.values());
    }

    /**
     * Clears the internal map where PermissionRequests are stored and adds the requests that are
     * passed in
     *
     * @param permissionRequests list of permission requests
     */
    public void setPermissionRequests(List<PermissionRequest> permissionRequests) {
        this.permissionRequests.clear();
        addPermissions(permissionRequests);
    }

    /**
     * Bulk add of PermissionRequests
     *
     * @param permissionRequests collection of PermissionRequests
     */
    public void addPermissions(Collection<PermissionRequest> permissionRequests) {
        for (PermissionRequest permissionRequest : permissionRequests) {
            addPermission(permissionRequest);
        }
    }

    /**
     * Bulk add of PermissionRequests
     *
     * @param permissionRequests array of PermissionRequests
     */
    public void addPermissions(PermissionRequest... permissionRequests) {
        for (PermissionRequest permissionRequest : permissionRequests) {
            addPermission(permissionRequest);
        }
    }

    /**
     * Adds a PermissionRequest to the internal storage map. The id of the PermissionRequest is used
     * as the key and PermissionRequest object is the value
     *
     * @param permissionRequest the permission you seek to be granted
     */
    public void addPermission(PermissionRequest permissionRequest) {
        this.permissionRequests.put(permissionRequest.getId(), permissionRequest);
    }

    /**
     * Checks if the permission exists in the internal map and returns true if the permission is not
     * handled by the system. This property is during the creation of a PermissionRequest object
     *
     * @param permissionId the ID of the PermissionRequest
     * @return true if the permission is a non-system permission. This means that its a permission
     * that is not handled by the system grant flow.
     */
    public boolean isNonSystemPermission(String permissionId) {
        return !permissionRequests.isEmpty() &&
                permissionRequests.get(permissionId) != null &&
                !permissionRequests.get(permissionId).isSystemPermission();
    }

    /**
     * Method is used to check if a permission has been granted for a specific permission. This
     * method needs to be able to handle both system and non-system permissions. This can be done by
     * using {@link ContextCompat#checkSelfPermission} for system permissions.
     *
     * @param context      android context
     * @param permissionId the ID of the PermissionRequest
     * @return true if the permission is granted
     */
    public abstract boolean hasPermission(Context context, String permissionId);

    /**
     * This method is called when a non-system permission needs to go through a grant flow. You
     * should be starting an activity using the Activity param passed through and start it via
     * {@link Activity#startActivityForResult(Intent, int)}
     *
     * @param activity the calling activity for when you request a non-system permission
     * @param id       the id of the permission
     */
    public abstract void onRequestNonSystemPermission(Activity activity, String id);

    /**
     * This method is called for handling the result of a non-system permission.
     *
     * @param activity    the receiving activity of the permission result
     * @param requestCode the request code from {@link Activity#onActivityResult(int, int, Intent)}
     * @param resultCode  the result code from {@link Activity#onActivityResult(int, int, Intent)}
     * @param data        the intent from {@link Activity#onActivityResult(int, int, Intent)}
     * @return true if the result was from a permission permission flow was handled
     */
    public abstract boolean onNonSystemPermissionResult(Activity activity, int requestCode, int resultCode, Intent data);

    /**
     * Class represents a permission that the user needs to grant
     */
    public static class PermissionRequest {
        private String id;

        private int iconRes;

        private int titleRes;

        private int textRes;

        private boolean blocking;

        private boolean system;

        /**
         * Default constructor to initialize a PermissinoRequest
         *
         * @param id       the id of the request
         * @param iconRes  the icon used to display a UI in the {@link PermissionStepLayout}
         * @param titleRes the title used to display a UI in the {@link PermissionStepLayout}
         * @param textRes  the explanation used to display a UI in the {@link PermissionStepLayout}
         */
        public PermissionRequest(String id, @DrawableRes int iconRes, @StringRes int titleRes, @StringRes int textRes) {
            this.id = id;
            this.iconRes = iconRes;
            this.titleRes = titleRes;
            this.textRes = textRes;
        }

        /**
         * Returns the id of the permission. If this is a system permission then it should originate
         * from {@link Manifest.permission}
         *
         * @return id of the permission
         */
        public String getId() {
            return id;
        }

        /**
         * Icon of the permission used when displaying UI in the {@link PermissionStepLayout}
         *
         * @return drawable resource id
         */
        @DrawableRes
        public int getIcon() {
            return iconRes;
        }

        /**
         * Title of the permission used when displaying UI in the {@link PermissionStepLayout}
         *
         * @return string resource id
         */
        @StringRes
        public int getTitle() {
            return titleRes;
        }

        /**
         * The explanation used when displaying UI in the {@link PermissionStepLayout}
         *
         * @return string resource id
         */
        @StringRes
        public int getText() {
            return textRes;
        }

        /**
         * This paramter is used in the {@link PermissionStepLayout} that makes a permission
         * optional or not. The user will not be allowed past the step if there are still some
         * permissions havent been granted which this property set to true
         *
         * @param blocking true if the user needs to take action on this permission
         */
        public void setIsBlockingPermission(boolean blocking) {
            this.blocking = blocking;
        }

        /**
         * Returns true if this permissino is blocking
         *
         * @return true if the permission needs to be granted in {@link PermissionStepLayout}
         */
        public boolean isBlockingPermission() {
            return blocking;
        }

        /**
         * Sets to true if the permission should be handled by the system grant flow
         *
         * @param system true if this is a system permission
         */
        public void setIsSystemPermission(boolean system) {
            this.system = system;
        }

        /**
         * Returns true if PermissionRequest should be handled by the system grant flow
         *
         * @return true if PermissionRequest should be handled by the system grant flow
         */
        public boolean isSystemPermission() {
            return system;
        }
    }

}
