package org.researchstack.backbone.step.active.recorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonObject;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.Step;

import java.io.File;

/**
 * Created by TheMDP on 2/17/17.
 */

/**
 * Since not all apps using this SDK require this LocationRecorder functionality,
 * we don't include it in the manifest and hence also suppress missing permissions warnings in the compiler
 */
@SuppressWarnings({"MissingPermission"})
public class LocationRecorder extends JsonArrayDataRecorder implements LocationListener {

    private static final String TAG = LocationRecorder.class.getSimpleName();

    public static final String TIMESTAMP_KEY   = "timestamp";
    public static final String COORDINATE_KEY  = "coordinate";
    public static final String LONGITUDE_KEY   = "longitude";
    public static final String LATITUDE_KEY    = "latitude";
    public static final String ALTITUDE_KEY    = "altitude";
    public static final String ACCURACY_KEY    = "accuracy";
    public static final String COURSE_KEY      = "course";
    public static final String SPEED_KEY       = "speed";

    private JsonObject jsonObject;
    private JsonObject coordinateJsonObject;

    private LocationManager locationManager = null;
    private long minTime;
    private float minDistance;

    /**
     * @param minTime per Android doc, minimum time interval between location updates, in milliseconds
     * @param minDistance per Android doc, minimum distance between location updates, in meters, no minimum if zero
     * @param identifier the recorder's identifier
     * @param step the step that contains this recorder
     * @param outputDirectory the output directory of the file that will be written with location data
     */
    LocationRecorder(long minTime, float minDistance, String identifier, Step step, File outputDirectory) {
        super(identifier, step, outputDirectory);
        this.minTime = minTime;
        this.minDistance = minDistance;
    }

    @Override
    public void start(Context context) {

        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (getRecorderListener() != null) {
                String errorMsg = context.getString(R.string.rsb_system_feature_gps_text);
                getRecorderListener().onFail(this, new IllegalStateException(errorMsg));
            }
            return;
        }

        // In-app permissions were added in Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager pm = context.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName());
            if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                if (getRecorderListener() != null) {
                    String errorMsg = context.getString(R.string.rsb_permission_location_desc);
                    getRecorderListener().onFail(this, new IllegalStateException(errorMsg));
                }
                return;
            }
        }

        // In Android, you can register for both network and gps location updates
        // Let's just register for both and log all locations to the data file
        // with their corresponding accuracy and other data associated

        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        coordinateJsonObject = new JsonObject();
        jsonObject = new JsonObject();
        startJsonDataLogging();
    }

    /**
     * Ignore the missing permission, since user's of this SDK don't always need it,
     * unless they are specifically doing a walking task test
     */
    @Override
    public void stop() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(this);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
        stopJsonDataLogging();
    }

    // LocationListener methods

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            jsonObject.addProperty(TIMESTAMP_KEY, location.getTime());

            coordinateJsonObject.addProperty(LONGITUDE_KEY, location.getLongitude());
            coordinateJsonObject.addProperty(LATITUDE_KEY, location.getLatitude());
            jsonObject.add(COORDINATE_KEY, coordinateJsonObject);

            if (location.hasAccuracy()) {
                jsonObject.addProperty(ACCURACY_KEY, location.getAccuracy());
            }

            if (location.hasSpeed()) {
                jsonObject.addProperty(SPEED_KEY, location.getSpeed());
            }

            if (location.hasAltitude()) {
                jsonObject.addProperty(ALTITUDE_KEY, location.getAltitude());
            }

            if (location.hasBearing()) {
                jsonObject.addProperty(COURSE_KEY, location.getBearing());
            }

            writeJsonObjectToFile(jsonObject);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i(TAG, s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i(TAG, "onProviderEnabled " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i(TAG, "onProviderDisabled " + s);
    }
}
