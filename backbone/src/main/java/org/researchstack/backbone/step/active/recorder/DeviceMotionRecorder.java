package org.researchstack.backbone.step.active.recorder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.LogExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by TheMDP on 2/5/17.
 *
 * The DeviceMotionRecorder incorporates a bunch of sensor fusion sensor readings
 * together to paint a broad picture of the device's orientation and movement over time
 *
 * This class is an attempt at mimicing iOS' device motion class which has all of these
 * sensor values updated at the same time.  However, on Android, we need to collect
 * them all in parrallel and write the group at a frequency separate of onSensorValueChanged
 *
 * @see <a href="https://developer.android.com/reference/android/hardware/SensorEvent.html#values">
 *      Sensor values</a>
 * @see <a href="https://source.android.com/devices/sensors/sensor-type">Sensor Types</a>
 */

public class DeviceMotionRecorder extends SensorRecorder {
    private static final Logger logger = LoggerFactory.getLogger(DeviceMotionRecorder.class);

    public static final float GRAVITY_SI_CONVERSION = SensorManager.GRAVITY_EARTH;

    public static final String SENSOR_INT_TYPE_KEY = "sensorTypeAndroid";
    public static final String SENSOR_STRING_TYPE_KEY = "sensorTypeNameAndroid";
    public static final String SENSOR_DATA_TYPE_KEY = "sensorType";
    public static final String SENSOR_DATA_SUBTYPE_KEY = "sensorAndroidType";

    public static final String SENSOR_ACCURACY_TYPE_KEY = "sensorAccuracy";
    public static final String SENSOR_NAME_KEY = "sensorName";
    public static final String SENSOR_EVENT_ACCURACY_KEY = "eventAccuracy";

    public static final Map<Integer, String> SENSOR_TYPE_TO_DATA_TYPE;
    public static final Set<Integer> ROTATION_VECTOR_TYPES;

    static {
        ImmutableMap.Builder<Integer, String>  sensorTypeMapBuilder = ImmutableMap.builder();
        sensorTypeMapBuilder.put(Sensor.TYPE_ROTATION_VECTOR, "attitude");
        sensorTypeMapBuilder.put(Sensor.TYPE_GYROSCOPE, "rotationRate");
        sensorTypeMapBuilder.put(Sensor.TYPE_ACCELEROMETER, "acceleration");
        sensorTypeMapBuilder.put(Sensor.TYPE_GRAVITY, "gravity");
        sensorTypeMapBuilder.put(Sensor.TYPE_LINEAR_ACCELERATION, "userAcceleration");
        sensorTypeMapBuilder.put(Sensor.TYPE_MAGNETIC_FIELD, "magneticField");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sensorTypeMapBuilder.put(Sensor.TYPE_GAME_ROTATION_VECTOR, "attitude");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sensorTypeMapBuilder.put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "attitude");
        }
        SENSOR_TYPE_TO_DATA_TYPE = sensorTypeMapBuilder.build();

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


    DeviceMotionRecorder(double frequency, String identifier, Step step, File outputDirectory) {
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
        if (hasAvailableType(availableSensorList, Sensor.TYPE_GRAVITY)) {
            sensorTypeList.add(Sensor.TYPE_GRAVITY);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_LINEAR_ACCELERATION)) {
            sensorTypeList.add(Sensor.TYPE_LINEAR_ACCELERATION);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_GYROSCOPE)) {
            sensorTypeList.add(Sensor.TYPE_GYROSCOPE);
        }
        if (hasAvailableType(availableSensorList, Sensor.TYPE_MAGNETIC_FIELD)) {
            sensorTypeList.add(Sensor.TYPE_MAGNETIC_FIELD);
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
            logger.warn("Unable find type key for sensor type: "
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
            case Sensor.TYPE_ROTATION_VECTOR:
                recordRotationVector(sensorEvent,jsonObject);
                break;
            default:
                logger.warn("Unable to record sensor type: " + sensorType);
        }
    }
    /**
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#accelerometer">
     *     Sensor Types: Accelerometer</a>
     * @param sensorEvent accelerometer event
     * @param jsonObject
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
     * @param sensorEvent
     * @param jsonObject
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
     * @param sensorEvent
     * @param jsonObject
     */
    @VisibleForTesting
    void recordGravityEvent(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1] / GRAVITY_SI_CONVERSION);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2] / GRAVITY_SI_CONVERSION);
    }
    /**
     * Sensor.TYPE_ROTATION_VECTOR relative to East-North-Up coordinate frame.
     * Sensor.TYPE_GAME_ROTATION_VECTOR  no magnetometer
     * Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR similar to a rotation vector sensor but using a
     *  magnetometer and no gyroscope
     *
     * @see <a href="https://source.android.com/devices/sensors/sensor-types#attitude_composite_sensors">
     *     https://source.android.com/devices/sensors/sensor-types#rotation_vector
     *      https://source.android.com/devices/sensors/sensor-types#game_rotation_vector
     *     https://source.android.com/devices/sensors/sensor-types#geomagnetic_rotation_vector
     * @param sensorEvent
     * @param jsonObject
     */
    @VisibleForTesting
    void recordRotationVector(SensorEvent sensorEvent, JsonObject jsonObject) {
        // indicate android sensor subtype
        int sensorType = sensorEvent.sensor.getType();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && Sensor.TYPE_GAME_ROTATION_VECTOR == sensorType) {
            jsonObject.addProperty(SENSOR_DATA_SUBTYPE_KEY, "gameRotationVector");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR == sensorType) {
            jsonObject.addProperty(SENSOR_DATA_SUBTYPE_KEY, "geomagneticRotationVector");
        }

        // x = rot_axis.y * sin(theta/2)
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        // y = rot_axis.y * sin(theta/2)
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        // z = rot_axis.z * sin(theta/2)
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // w = cos(theta/2)
            jsonObject.addProperty(W_KEY, sensorEvent.values[3]);
            // estimated accuracy in radians, or -1 if unavailable
            jsonObject.addProperty(ACCURACY_KEY, sensorEvent.values[4]);
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

    void recordMagneticField(SensorEvent sensorEvent, JsonObject jsonObject) {
        jsonObject.addProperty(X_KEY, sensorEvent.values[0]);
        jsonObject.addProperty(Y_KEY, sensorEvent.values[1]);
        jsonObject.addProperty(Z_KEY, sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        int sensorType = sensor.getType();
        String sensorTypeKey = SENSOR_TYPE_TO_DATA_TYPE.get(sensorType);
        if (Strings.isNullOrEmpty(sensorTypeKey)) {
            logger.warn("Unable find type key for sensor type: "
                    + sensorType);
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SENSOR_DATA_TYPE_KEY, sensorTypeKey);
        jsonObject.addProperty(SENSOR_ACCURACY_TYPE_KEY, i);

        jsonObject.addProperty(SENSOR_INT_TYPE_KEY, sensorType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            jsonObject.addProperty(SENSOR_STRING_TYPE_KEY, sensor.getStringType());
        }
        jsonObject.addProperty(SENSOR_NAME_KEY, sensor.getVendor() + ";" + sensor.getName());

        LogExt.d(SensorRecorder.class, "Recording sensor accuracy change json: " + jsonObject);
        writeJsonObjectToFile(jsonObject);
    }
}