package org.researchstack.backbone.step.active.recorder;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.LocalBroadcastManager;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;

import org.researchstack.backbone.step.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by TheMDP on 2/5/17.
 *
 * The DeviceMotionRecorder incorporates a bunch of sensor fusion sensor readings
 * together to paint a broad picture of the device's orientation and movement over time.
 *
 * This class is an attempt at recording data in a similar way to iOS's device motion recorder.
 *
 * @see <a href="https://developer.android.com/reference/android/hardware/SensorEvent.html#values">
 *      Sensor values</a>
 * @see <a href="https://source.android.com/devices/sensors/sensor-type">Sensor Types</a>
 * @see <a href="https://developer.android.com/guide/topics/sensors/sensors_position.html">
 *     Position Sensors</a>
 * @see <a href="https://developer.android.com/guide/topics/sensors/sensors_motion.html">
 *     Motion Sensors</a>
 */
public class DeviceMotionRecorder extends SensorRecorder {
    private static final Logger logger = LoggerFactory.getLogger(DeviceMotionRecorder.class);

    public static final float GRAVITY_SI_CONVERSION = SensorManager.GRAVITY_EARTH;

    public static final String SENSOR_DATA_TYPE_KEY = "sensorType";
    public static final String SENSOR_DATA_SUBTYPE_KEY = "sensorAndroidType";
    public static final String SENSOR_EVENT_ACCURACY_KEY = "eventAccuracy";

    public static final Map<Integer, String> SENSOR_TYPE_TO_DATA_TYPE;
    public static final Set<Integer> ROTATION_VECTOR_TYPES;

    public static final String ROTATION_REFERENCE_COORDINATE_KEY = "referenceCoordinate";

    public static final String BROADCAST_ROTATION_VECTOR_UPDATE_ACTION = "BroadcastRotationVectorUpdate";
    public static final String BROADCAST_ROTATION_VECTOR_UPDATE_KEY = "RotationVectorUpdate";

    static {
        // build mapping for sensor type and its data type value
        ImmutableMap.Builder<Integer, String>  sensorTypeMapBuilder = ImmutableMap.builder();
        // rotation/gyroscope
        sensorTypeMapBuilder.put(Sensor.TYPE_GYROSCOPE, "rotationRate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorTypeMapBuilder.put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "rotationRateUncalibrated");
        }

        // accelerometer
        sensorTypeMapBuilder.put(Sensor.TYPE_ACCELEROMETER, "acceleration");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sensorTypeMapBuilder.put(
                    Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, "accelerationUncalibrated");
        }

        // gravity
        sensorTypeMapBuilder.put(Sensor.TYPE_GRAVITY, "gravity");

        // acceleration without gravity
        sensorTypeMapBuilder.put(Sensor.TYPE_LINEAR_ACCELERATION, "userAcceleration");

        // magnetic field
        sensorTypeMapBuilder.put(Sensor.TYPE_MAGNETIC_FIELD, "magneticField");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorTypeMapBuilder.put(
                    Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "magneticFieldUncalibrated");
        }

        // attitude
        sensorTypeMapBuilder.put(Sensor.TYPE_ROTATION_VECTOR, "attitude");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorTypeMapBuilder.put(Sensor.TYPE_GAME_ROTATION_VECTOR, "attitude");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sensorTypeMapBuilder.put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "attitude");
        }
        SENSOR_TYPE_TO_DATA_TYPE = sensorTypeMapBuilder.build();

        // build mapping for rotation type
        ImmutableSet.Builder<Integer> rotationTypeBuilder =ImmutableSet.builder();
        rotationTypeBuilder.add(Sensor.TYPE_ROTATION_VECTOR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            rotationTypeBuilder.add(Sensor.TYPE_GAME_ROTATION_VECTOR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            rotationTypeBuilder.add(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        }
        ROTATION_VECTOR_TYPES = rotationTypeBuilder.build();
    }

    public static final String X_KEY    = "x";
    public static final String Y_KEY    = "y";
    public static final String Z_KEY    = "z";
    public static final String W_KEY    = "w";
    public static final String ACCURACY_KEY = "estimatedAccuracy";

    public static final String X_UNCALIBRATED_KEY   = "xUncalibrated";
    public static final String Y_UNCALIBRATED_KEY   = "yUncalibrated";
    public static final String Z_UNCALIBRATED_KEY   = "zUncalibrated";
    public static final String X_BIAS_KEY           = "xBias";
    public static final String Y_BIAS_KEY           = "yBias";
    public static final String Z_BIAS_KEY           = "zBias";

    public DeviceMotionRecorder(double frequency, String identifier, Step step, File outputDirectory) {
        super(frequency, identifier, step, outputDirectory);
    }

    @Override
    public void start(Context context) {
        super.start(context);
    }

    @Override
    protected List<Integer> getSensorTypeList(List<Sensor> availableSensorList) {
        List<Integer> sensorTypeList = new ArrayList<>();

        // Only add these sensors if the device has them
        if (hasAvailableType(availableSensorList, Sensor.TYPE_ACCELEROMETER)) {
            sensorTypeList.add(Sensor.TYPE_ACCELEROMETER);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && hasAvailableType(availableSensorList, Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)) {
            sensorTypeList.add(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_GRAVITY)) {
            sensorTypeList.add(Sensor.TYPE_GRAVITY);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_LINEAR_ACCELERATION)) {
            sensorTypeList.add(Sensor.TYPE_LINEAR_ACCELERATION);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_GYROSCOPE)) {
            sensorTypeList.add(Sensor.TYPE_GYROSCOPE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && hasAvailableType(availableSensorList, Sensor.TYPE_GYROSCOPE_UNCALIBRATED)) {
            sensorTypeList.add(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_MAGNETIC_FIELD)) {
            sensorTypeList.add(Sensor.TYPE_MAGNETIC_FIELD);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && hasAvailableType(availableSensorList, Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED)) {
            sensorTypeList.add(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_ROTATION_VECTOR)) {
            sensorTypeList.add(Sensor.TYPE_ROTATION_VECTOR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (hasAvailableType(availableSensorList, Sensor.TYPE_GAME_ROTATION_VECTOR)) {
                sensorTypeList.add(Sensor.TYPE_GAME_ROTATION_VECTOR);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hasAvailableType(availableSensorList, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)) {
                sensorTypeList.add(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
            }
        }
        return sensorTypeList;
    }

    @Override
    public void recordSensorEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        int sensorType = sensorEvent.sensor.getType();
        String sensorTypeKey = SENSOR_TYPE_TO_DATA_TYPE.get(sensorType);

        if (Strings.isNullOrEmpty(sensorTypeKey)) {
            logger.warn("Unable to find type key for sensor type: "
                    + sensorType);
            return;
        }

        jsonObject.addProperty(SENSOR_DATA_TYPE_KEY, sensorTypeKey);
        jsonObject.addProperty(SENSOR_EVENT_ACCURACY_KEY, sensorEvent.accuracy);

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                recordAccelerometerEvent(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_GRAVITY:
                recordGravityEvent(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                recordLinearAccelerometerEvent(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_GYROSCOPE:
                recordGyroscope(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                recordMagneticField(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                recordUncalibrated(sensorEvent, jsonObject);
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
            case Sensor.TYPE_ROTATION_VECTOR:
                recordRotationVector(sensorEvent, jsonObject);
                sendRotationVectorUpdateBroadcast(
                    jsonObject.get(X_KEY).getAsFloat(),
                    jsonObject.get(Y_KEY).getAsFloat(),
                    jsonObject.get(Z_KEY).getAsFloat(),
                    jsonObject.get(W_KEY).getAsFloat());
                break;
            default:
                logger.warn("Unable to record sensor type: " + sensorType);
        }
    }

    /**
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#accelerometer">
     *     Sensor Types: Accelerometer</a>
     */
    @VisibleForTesting
    void recordAccelerometerEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }

    /**
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#linear_acceleration">
     *     Sensor Types: Accelerometer</a>
     */
    @VisibleForTesting
    void recordLinearAccelerometerEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        // acceleration = gravity + linear-acceleration
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }

    /**
     * Direction and magnitude of gravity.
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#gravity">
     *     Sensor Types: Gravity </a>
     */
    @VisibleForTesting
    void recordGravityEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }

    /**
     * Sensor.TYPE_ROTATION_VECTOR relative to East-North-Up coordinate frame.
     * Sensor.TYPE_GAME_ROTATION_VECTOR no magnetometer
     * Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR similar to a rotation vector sensor but using a
     *  magnetometer and no gyroscope
     *
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#attitude_composite_sensors">
     *     https://source.android.com/devices/sensors/sensor-types#rotation_vector
     *      https://source.android.com/devices/sensors/sensor-types#game_rotation_vector
     *     https://source.android.com/devices/sensors/sensor-types#geomagnetic_rotation_vector
     */
    @VisibleForTesting
    void recordRotationVector(SensorEvent sensorEvent, JsonObject jsonObject) {
        // indicate android sensor subtype
        int sensorType = sensorEvent.sensor.getType();
        if (Sensor.TYPE_ROTATION_VECTOR == sensorType) {
            jsonObject.addProperty(SENSOR_DATA_SUBTYPE_KEY, "rotationVector");
            jsonObject.addProperty(ROTATION_REFERENCE_COORDINATE_KEY, "East-Up-North");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && Sensor.TYPE_GAME_ROTATION_VECTOR == sensorType) {
            jsonObject.addProperty(SENSOR_DATA_SUBTYPE_KEY, "gameRotationVector");
            jsonObject.addProperty(ROTATION_REFERENCE_COORDINATE_KEY, "zUp");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR == sensorType) {
            jsonObject.addProperty(SENSOR_DATA_SUBTYPE_KEY, "geomagneticRotationVector");
            jsonObject.addProperty(ROTATION_REFERENCE_COORDINATE_KEY, "East-Up-North");
        }

        //The first three elements of the rotation vector are equal to the last three components of a unit quaternion:
        // x = rot_axis.y * sin(theta/2)
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        // y = rot_axis.y * sin(theta/2)
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        // z = rot_axis.z * sin(theta/2)
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //The fourth element of the rotation vector is equal to the first component of a unit quaternion:
            // w = cos(theta/2)
            jsonObject.addProperty(W_KEY, sensorEvent.values[3]);

            // game rotation vector never provides accuracy, always returns zero
            if (Sensor.TYPE_GAME_ROTATION_VECTOR != sensorType) {
                // estimated accuracy in radians, or -1 if unavailable
                jsonObject.addProperty(ACCURACY_KEY, sensorEvent.values[4]);
            }
        } else if (sensorEvent.values.length > 3) {
            // this value was optional before SDK Level 18
            // w = cos(theta/2)
            jsonObject.addProperty(W_KEY, sensorEvent.values[3]);
        }
    }

    void recordGyroscope(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
    }

    // used for uncalibrated gyroscope, uncalibrated accelerometer, and uncalibrated magnetic field
    void recordUncalibrated(SensorEvent sensorEvent, JsonObject jsonObject) {
        // conceptually: _uncalibrated = _calibrated + _bias.
        jsonObject.addProperty(X_UNCALIBRATED_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_UNCALIBRATED_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_UNCALIBRATED_KEY, sensorEvent.values[2]);

        jsonObject.addProperty(X_BIAS_KEY, sensorEvent.values[3]);
        jsonObject.addProperty(Y_BIAS_KEY, sensorEvent.values[4]);
        jsonObject.addProperty(Z_BIAS_KEY, sensorEvent.values[5]);
    }

    void recordMagneticField(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // no-op
    }

    protected void sendRotationVectorUpdateBroadcast(float x, float y, float z, float w) {
        RotationVectorUpdateHolder dataHolder = new RotationVectorUpdateHolder();
        dataHolder.setX(x);
        dataHolder.setY(y);
        dataHolder.setZ(z);
        dataHolder.setW(w);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BROADCAST_ROTATION_VECTOR_UPDATE_KEY, dataHolder);
        Intent intent = new Intent(BROADCAST_ROTATION_VECTOR_UPDATE_ACTION);
        intent.putExtras(bundle);
        intent.setAction(org.researchstack.backbone.step.active.recorder.DeviceMotionRecorder.BROADCAST_ROTATION_VECTOR_UPDATE_ACTION);
        LocalBroadcastManager.getInstance(appContext).
            sendBroadcast(intent);
    }

    /**
     * @param intent must have action of BROADCAST_ROTATION_VECTOR_UPDATE_ACTION
     * @return the RotationVectorUpdateHolder contained in the broadcast
     */
    public static RotationVectorUpdateHolder getRotationVectorUpdateHolder(Intent intent) {
        if (intent.getAction() == null ||
                !intent.getAction().equals(BROADCAST_ROTATION_VECTOR_UPDATE_ACTION) ||
                intent.getExtras() == null ||
                !intent.getExtras().containsKey(BROADCAST_ROTATION_VECTOR_UPDATE_KEY)) {
            return null;
        }
        return (RotationVectorUpdateHolder) intent.getExtras()
                .getSerializable(BROADCAST_ROTATION_VECTOR_UPDATE_KEY);
    }

    public static class RotationVectorUpdateHolder implements Serializable {
        private float x;
        private float y;
        private float z;
        private float w;

        public float getX() { return x; }
        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }
        
        public void setY(float y) {
            this.y = y;
        }

        public float getZ() {
            return z;
        }
        
        public void setZ(float z) {
            this.z = z;
        }

        public float getW() {
            return w;
        }
        
        public void setW(float w) {
            this.w = w;
        }
    }
}
