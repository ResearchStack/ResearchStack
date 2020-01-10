package org.researchstack.backbone.step.active.recorder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.JsonObject;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.FormatHelper;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Since not all apps using this SDK require this LocationRecorder functionality,
 * we don't include it in the manifest and hence also suppress missing permissions warnings in the compiler
 */
@SuppressWarnings({"MissingPermission"})
public class LocationRecorder extends JsonArrayDataRecorder implements LocationListener {

    private static final String TAG = LocationRecorder.class.getSimpleName();

    private static final String SHARED_PREFS_KEY = "LocationRecorder";
    private static final String LAST_RECORDED_DIST_KEY = "LastRecordedTotalDistance";

    public static final String COORDINATE_KEY  = "coordinate";
    public static final String LONGITUDE_KEY   = "longitude";
    public static final String LATITUDE_KEY    = "latitude";
    public static final String ALTITUDE_KEY    = "altitude";
    public static final String ACCURACY_KEY    = "accuracy";
    public static final String COURSE_KEY      = "course";
    public static final String RELATIVE_LATITUDE_KEY = "relativeLatitude";
    public static final String RELATIVE_LONGITUDE_KEY = "relativeLongitude";
    public static final String SPEED_KEY       = "speed";
    public static final String TIMESTAMP_DATE_KEY = "timestampDate";
    public static final String TIMESTAMP_IN_SECONDS_KEY = "timestamp";
    public static final String UPTIME_IN_SECONDS_KEY = "uptime";

    private JsonObject jsonObject;
    private JsonObject coordinateJsonObject;

    private LocationManager locationManager = null;
    private final long minTime;
    private final float minDistance;
    private final boolean usesRelativeCoordinates;

    private double totalDistance;
    private Location firstLocation;
    private Location lastLocation;
    private long startTimeNanosSinceBoot;

    public static final String BROADCAST_LOCATION_UPDATE_ACTION  = "LocationRecorder_BroadcastLocationUpdate";
    private static final String BROADCAST_LOCATION_UPDATE_KEY    = "LocationUpdate";

    private SharedPreferences locationRecorderPrefs;

    public static float getLastRecordedTotalDistance(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);
        return prefs.getFloat(LAST_RECORDED_DIST_KEY, 0f);
    }

    protected void setLastRecordedTotalDistance(float totalDistance) {
        if (locationRecorderPrefs == null) {
            return;
        }
        locationRecorderPrefs.edit().putFloat(LAST_RECORDED_DIST_KEY, totalDistance).apply();
    }

    /**
     * @param minTime per Android doc, minimum time interval between location updates, in milliseconds
     * @param minDistance per Android doc, minimum distance between location updates, in meters, no minimum if zero
     * @param identifier the recorder's identifier
     * @param step the step that contains this recorder
     * @param outputDirectory the output directory of the file that will be written with location data
     */
    LocationRecorder(
            long minTime, float minDistance, boolean usesRelativeCoordinates, String identifier,
            Step step, File outputDirectory) {
        super(identifier, step, outputDirectory);
        this.minTime = minTime;
        this.minDistance = minDistance;
        this.usesRelativeCoordinates = usesRelativeCoordinates;
    }

    public long getMinTime() {
        return minTime;
    }

    public float getMinDistance() {
        return minDistance;
    }

    /**
     * If this is set to true, the recorder will produce relative GPS coordinates, using the
     * user's initial position as zero in the relative coordinate system. If this is set to
     * false, the recorder will produce absolute GPS coordinates.
     */
    public boolean getUsesRelativeCoordinates() {
        return usesRelativeCoordinates;
    }

    @Override
    public void start(Context context) {
        firstLocation = null;
        lastLocation = null;
        totalDistance = 0;
        locationRecorderPrefs = context.getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

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
            if (firstLocation == null) {
                // Initialize first location
                firstLocation = location;
            }

            // getElapsedReatimeNanos() is long nanoseconds since system boot time.
            long locationNanos = getElapsedNanosSinceBootFromLocation(location);
            if (startTimeNanosSinceBoot == 0) {
                // Initialize start time.
                startTimeNanosSinceBoot = locationNanos;

                // Add timestamp date, which is the ISO timestamp representing the activity start time.
                // Location.getTime() is always epoch milliseconds, so we can use as is.
                jsonObject.addProperty(TIMESTAMP_DATE_KEY, new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601,
                        Locale.getDefault()).format(location.getTime()));
            } else if (jsonObject.has(TIMESTAMP_DATE_KEY)) {
                // Because we re-use the jsonObject, we need to clear the timestamp date key after the first iteration.
                jsonObject.remove(TIMESTAMP_DATE_KEY);
            }

            // Timestamps
            // timestamp is seconds since start of the activity (locationNanos minus startTimeNanos, divided by a billion).
            // uptime is a monotonically increasing timestamp in seconds, with any arbitrary zero. (We use
            // getElapsedRealtimeNanos(), divided by a billion.)
            jsonObject.addProperty(TIMESTAMP_IN_SECONDS_KEY, (locationNanos - startTimeNanosSinceBoot) * 1e-9);
            jsonObject.addProperty(UPTIME_IN_SECONDS_KEY, locationNanos * 1e-9);

            // GPS coordinates
            if (usesRelativeCoordinates) {
                // Subtract from the firstLocation to get relative coordinates.
                double relativeLatitude = location.getLatitude() - firstLocation.getLatitude();
                double relativeLongitude = location.getLongitude() - firstLocation.getLongitude();
                coordinateJsonObject.addProperty(RELATIVE_LATITUDE_KEY, relativeLatitude);
                coordinateJsonObject.addProperty(RELATIVE_LONGITUDE_KEY, relativeLongitude);
            } else {
                // Use absolute coordinates given by the location.
                coordinateJsonObject.addProperty(LONGITUDE_KEY, location.getLongitude());
                coordinateJsonObject.addProperty(LATITUDE_KEY, location.getLatitude());
            }
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

            if (lastLocation != null) {
                totalDistance += lastLocation.distanceTo(location);
            }
            sendLocationUpdateBroadcast(
                    location.getLongitude(), location.getLatitude(), totalDistance);

            lastLocation = location;
        }
    }

    // Wrapper method which encapsulates getting the elapsed realtime nanos, or falls back to elasped realtime (millis)
    // for older OS versions.
    // Package-scoped so this can be mocked for unit tests.
    long getElapsedNanosSinceBootFromLocation(Location location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return location.getElapsedRealtimeNanos();
        } else {
            return (long) (SystemClock.elapsedRealtime() * 1e6); // millis to nanos
        }
    }

    protected void sendLocationUpdateBroadcast(double longitude, double latitude, double distance) {
        setLastRecordedTotalDistance((float)distance);
        LocationUpdateHolder locationHolder = new LocationUpdateHolder();
        locationHolder.setLongitude(longitude);
        locationHolder.setLatitude(latitude);
        locationHolder.setTotalDistance(distance);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BROADCAST_LOCATION_UPDATE_KEY, locationHolder);
        Intent intent = new Intent(BROADCAST_LOCATION_UPDATE_ACTION);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * @param intent must have action of BROADCAST_LOCATION_UPDATE_ACTION
     * @return the LocationUpdateHolder contained in the broadcast
     */
    public static LocationUpdateHolder getLocationUpdateHolder(Intent intent) {
        if (intent.getAction() == null ||
                !intent.getAction().equals(BROADCAST_LOCATION_UPDATE_ACTION) ||
                intent.getExtras() == null ||
                intent.getExtras().containsKey(BROADCAST_LOCATION_UPDATE_KEY)) {
            return null;
        }
        return (LocationUpdateHolder) intent.getExtras()
                .getSerializable(BROADCAST_LOCATION_UPDATE_KEY);
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

    public static class LocationUpdateHolder implements Serializable {
        private double longitude;
        private double latitude;
        private double totalDistance;

        public LocationUpdateHolder() {
            super();
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getTotalDistance() {
            return totalDistance;
        }

        public void setTotalDistance(double distance) {
            this.totalDistance = distance;
        }
    }
}
