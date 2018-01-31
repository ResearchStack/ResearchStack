package org.researchstack.backbone.step.active.recorder;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.step.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationRecorderTest {
    private static final Step DUMMY_STEP = new Step();
    private static final File MOCK_OUTPUT_DIRECTORY = mock(File.class);
    private static final long START_TIME_MILLIS = DateTime.parse("2018-01-29T23:39:44.334Z").getMillis();
    private static final long START_TIME_NANOS = 1234567890L;

    private Context mockContext;
    private LocationRecorder recorder;
    private List<JsonObject> savedJsonList;

    @Before
    public void setup() {
        // Set up mocks.
        LocationManager mockLocationManager = mock(LocationManager.class);
        when(mockLocationManager.isProviderEnabled(any())).thenReturn(true);

        PackageManager mockPackageManager = mock(PackageManager.class);
        when(mockPackageManager.checkPermission(any(), any())).thenReturn(
                PackageManager.PERMISSION_GRANTED);

        mockContext = mock(Context.class);
        when(mockContext.getSystemService(Context.LOCATION_SERVICE)).thenReturn(
                mockLocationManager);

        // Init savedJsonList.
        savedJsonList = new ArrayList<>();
    }

    @Test
    public void absoluteCoordinates() {
        // Setup
        LocationRecorderConfig config = new LocationRecorderConfig.Builder()
                .withUsesRelativeCoordinates(false).build();
        initLocationRecorderFromConfig(config);

        // Start
        recorder.start(mockContext);

        // First coordinate
        Location firstLocation = mockLocationWithLatLong(47.5, -122.5);
        recorder.onLocationChanged(firstLocation);

        // Second coordinate, head northwest 0.1 degrees.
        Location secondLocation = mockLocationWithLatLong(47.6, -122.6);
        recorder.onLocationChanged(secondLocation);

        // Stop
        recorder.stop();

        // Verify saved locations.
        verify(recorder, times(2)).writeJsonObjectToFile(any());

        assertEquals(savedJsonList.size(), 2);
        assertAbsoluteCoordinates(47.5, -122.5, savedJsonList
                .get(0));
        assertAbsoluteCoordinates(47.6, -122.6, savedJsonList
                .get(1));
    }

    @Test
    public void relativeCoordinates() {
        // Setup
        LocationRecorderConfig config = new LocationRecorderConfig.Builder()
                .withUsesRelativeCoordinates(true).build();
        initLocationRecorderFromConfig(config);

        // Start
        recorder.start(mockContext);

        // First coordinate
        Location firstLocation = mockLocationWithLatLong(47.5, -122.5);
        recorder.onLocationChanged(firstLocation);

        // Second coordinate, head northwest 0.1 degrees.
        Location secondLocation = mockLocationWithLatLong(47.6, -122.6);
        recorder.onLocationChanged(secondLocation);

        // Stop
        recorder.stop();

        // Verify saved locations.
        verify(recorder, times(2)).writeJsonObjectToFile(any());

        assertEquals(savedJsonList.size(), 2);
        assertRelativeCoordinates(0.0, 0.0, savedJsonList
                .get(0));
        assertRelativeCoordinates(0.1, -0.1, savedJsonList
                .get(1));
    }

    private void initLocationRecorderFromConfig(LocationRecorderConfig config) {
        // Spy location recorder, so we can prevent things like start/stopJsonDataLogging() from
        // firing.
        recorder = spy((LocationRecorder) config.recorderForStep(DUMMY_STEP,
                MOCK_OUTPUT_DIRECTORY));
        doReturn(0L).when(recorder).getElapsedNanosSinceBootFromLocation(any());
        doNothing().when(recorder).sendLocationUpdateBroadcast(anyDouble(), anyDouble(),
                anyDouble());
        doNothing().when(recorder).startJsonDataLogging();
        doNothing().when(recorder).stopJsonDataLogging();

        // LocationRecorder uses an optimization to re-use JSON objects so we can avoid creating
        // JSON objects multiple times a second. One side effect is that we need to copy and save
        // JSON in unit tests, or mock gets really confused.
        doAnswer(invocation -> {
            JsonObject savedJson = invocation.getArgument(0);
            savedJsonList.add(savedJson.deepCopy());

            // Required return statement.
            return null;
        }).when(recorder).writeJsonObjectToFile(any());
    }

    private static Location mockLocationWithLatLong(double latitude, double longitude) {
        Location location = mock(Location.class);
        when(location.getLatitude()).thenReturn(latitude);
        when(location.getLongitude()).thenReturn(longitude);
        return location;
    }

    private static void assertAbsoluteCoordinates(
            double expectedLatitude, double expectedLongitude, JsonObject actualJsonObject) {
        JsonObject coordinateJsonObject = actualJsonObject.getAsJsonObject(
                LocationRecorder.COORDINATE_KEY);
        assertEquals(expectedLatitude, coordinateJsonObject.get(LocationRecorder.LATITUDE_KEY)
                .getAsDouble(), 0.001);
        assertEquals(expectedLongitude, coordinateJsonObject.get(LocationRecorder.LONGITUDE_KEY)
                .getAsDouble(), 0.001);
        assertFalse(coordinateJsonObject.has(LocationRecorder.RELATIVE_LATITUDE_KEY));
        assertFalse(coordinateJsonObject.has(LocationRecorder.RELATIVE_LONGITUDE_KEY));
    }

    private static void assertRelativeCoordinates(
            double expectedLatitude, double expectedLongitude, JsonObject actualJsonObject) {
        JsonObject coordinateJsonObject = actualJsonObject.getAsJsonObject(
                LocationRecorder.COORDINATE_KEY);
        assertEquals(expectedLatitude,
                coordinateJsonObject.get(LocationRecorder.RELATIVE_LATITUDE_KEY).getAsDouble(),
                0.001);
        assertEquals(expectedLongitude,
                coordinateJsonObject.get(LocationRecorder.RELATIVE_LONGITUDE_KEY).getAsDouble(),
                0.001);
        assertFalse(coordinateJsonObject.has(LocationRecorder.LATITUDE_KEY));
        assertFalse(coordinateJsonObject.has(LocationRecorder.LONGITUDE_KEY));
    }

    @Test
    public void timestamps() {
        // Setup
        LocationRecorderConfig config = new LocationRecorderConfig.Builder().build();
        initLocationRecorderFromConfig(config);

        // Start
        recorder.start(mockContext);

        // First location
        Location firstLocation = mockLocationWithTime(START_TIME_MILLIS, START_TIME_NANOS);
        recorder.onLocationChanged(firstLocation);

        // Second location - 10ms have elapsed (10 million nanos).
        Location secondLocation = mockLocationWithTime(START_TIME_MILLIS + 10,
                START_TIME_NANOS + 10 * 1000 * 1000);
        recorder.onLocationChanged(secondLocation);

        // Stop
        recorder.stop();

        // Verify saved timestamps.
        verify(recorder, times(2)).writeJsonObjectToFile(any());
        assertEquals(savedJsonList.size(), 2);

        // We use Locale to generate a timestamp with timezone. This is a bit wonky to test. For simplicity, just
        // convert to epoch milliseconds.
        String timestampDateStr = savedJsonList.get(0).get(LocationRecorder.TIMESTAMP_DATE_KEY).getAsString();
        long timestampDateMillis = DateTime.parse(timestampDateStr).getMillis();
        assertEquals(START_TIME_MILLIS, timestampDateMillis);

        assertEquals(0.0, savedJsonList.get(0).get(LocationRecorder.TIMESTAMP_IN_SECONDS_KEY).getAsDouble(),
                1e-12);
        assertEquals(START_TIME_NANOS * 1e-9, savedJsonList.get(0).get(LocationRecorder.UPTIME_IN_SECONDS_KEY)
                .getAsDouble(), 1e-12);

        assertFalse(savedJsonList.get(1).has(LocationRecorder.TIMESTAMP_DATE_KEY));
        assertEquals(0.010, savedJsonList.get(1).get(LocationRecorder.TIMESTAMP_IN_SECONDS_KEY).getAsDouble(),
                1e-12);
        assertEquals(START_TIME_NANOS * 1e-9 + 0.010, savedJsonList.get(1)
                .get(LocationRecorder.UPTIME_IN_SECONDS_KEY).getAsDouble(), 1e-12);
    }

    private Location mockLocationWithTime(long epochTimeMillis, long nanosSinceBoot) {
        Location location = mock(Location.class);
        when(location.getTime()).thenReturn(epochTimeMillis);
        doReturn(nanosSinceBoot).when(recorder).getElapsedNanosSinceBootFromLocation(location);
        return location;
    }
}