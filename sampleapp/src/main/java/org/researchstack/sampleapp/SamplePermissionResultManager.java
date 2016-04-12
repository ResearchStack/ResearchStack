package org.researchstack.sampleapp;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import org.researchstack.skin.AppPrefs;
import org.researchstack.skin.PermissionRequestManager;

public class SamplePermissionResultManager extends PermissionRequestManager
{
    private static final int RESULT_REQUEST_CODE_NOTIFICATION = 143;

    @Override
    public boolean hasPermission(Context context, String permissionId)
    {
        if (permissionId.equals(SampleApplication.PERMISSION_NOTIFICATIONS))
        {
            return AppPrefs.getInstance(context).isTaskReminderEnabled();
        }
        else
        {
            return ContextCompat.checkSelfPermission(context, permissionId) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Used to tell if the permission-id should be handled by the system (using
     * {@link Activity#requestPermissions(String[], int)}) or through our own custom implementation
     * in {@link #onRequestNonSystemPermission}
     * @param permissionId
     * @return
     */
    @Override
    public boolean isNonSystemPermission(String permissionId)
    {
        // SampleApplication.PERMISSION_NOTIFICATIONS is our non-system permission so we return true
        // if permissionId's are the same
        return permissionId.equals(SampleApplication.PERMISSION_NOTIFICATIONS);
    }

    /**
     * This method is called when {@link #isNonSystemPermission} returns true. For example, if using
     * Google+ Sign In, you would create your signIn-Intent and start that activity. Any result
     * will then be passed through to {#link onNonSystemPermissionResult}
     * @param permissionId
     */
    @Override
    public void onRequestNonSystemPermission(Activity activity, String permissionId)
    {
        Intent intent = new Intent(activity, NotificationPermissionActivity.class);
        activity.startActivityForResult(intent, RESULT_REQUEST_CODE_NOTIFICATION);
    }

    /**
     * Method is called when your Activity called in {@link #onRequestNonSystemPermission} has
     * returned with a result
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    @Override
    public boolean onNonSystemPermissionResult(Activity activity, int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RESULT_REQUEST_CODE_NOTIFICATION)
        {
            AppPrefs.getInstance(activity).setTaskReminderComplete(resultCode == Activity.RESULT_OK);
            return true;
        }

        return false;
    }
}
