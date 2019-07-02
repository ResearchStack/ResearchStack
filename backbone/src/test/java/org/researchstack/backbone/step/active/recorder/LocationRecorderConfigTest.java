package org.researchstack.backbone.step.active.recorder;

import org.junit.Test;
import org.researchstack.backbone.step.Step;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LocationRecorderConfigTest {
    private static final Step DUMMY_STEP = new Step();
    private static final File MOCK_OUTPUT_DIRECTORY = mock(File.class);
    private static final String TEST_IDENTIFIER = "test-identifier";
    private static final long TEST_MIN_TIME = 1000L;
    private static final float TEST_MIN_DISTANCE = 10;

    @Test
    public void constructorWithIdentifier() {
        LocationRecorderConfig config = new LocationRecorderConfig(TEST_IDENTIFIER);
        LocationRecorder recorder = (LocationRecorder) config.recorderForStep(DUMMY_STEP,
                MOCK_OUTPUT_DIRECTORY);
        assertCommonProps(recorder);
        assertEquals(TEST_IDENTIFIER, recorder.getIdentifier());
        assertEquals(LocationRecorderConfig.DEFAULT_MIN_TIME, recorder.getMinTime());
        assertEquals((double) LocationRecorderConfig.DEFAULT_LOCATION_DISTANCE,
                recorder.getMinDistance(), 0.001);
        assertFalse(recorder.getUsesRelativeCoordinates());
    }

    @Test
    public void constructorWithIdentifierMinTimeMinDistance() {
        LocationRecorderConfig config = new LocationRecorderConfig(TEST_IDENTIFIER, TEST_MIN_TIME,
                TEST_MIN_DISTANCE);
        LocationRecorder recorder = (LocationRecorder) config.recorderForStep(DUMMY_STEP,
                MOCK_OUTPUT_DIRECTORY);
        assertCommonProps(recorder);
        assertEquals(TEST_IDENTIFIER, recorder.getIdentifier());
        assertEquals(TEST_MIN_TIME, recorder.getMinTime());
        assertEquals(TEST_MIN_DISTANCE, recorder.getMinDistance(), 0.001);
        assertFalse(recorder.getUsesRelativeCoordinates());
    }

    @Test
    public void builderDefaults() {
        LocationRecorderConfig config = new LocationRecorderConfig.Builder().build();
        LocationRecorder recorder = (LocationRecorder) config.recorderForStep(DUMMY_STEP,
                MOCK_OUTPUT_DIRECTORY);
        assertCommonProps(recorder);
        assertNull(recorder.getIdentifier());
        assertEquals(LocationRecorderConfig.DEFAULT_MIN_TIME, recorder.getMinTime());
        assertEquals((double) LocationRecorderConfig.DEFAULT_LOCATION_DISTANCE,
                recorder.getMinDistance(), 0.001);
        assertFalse(recorder.getUsesRelativeCoordinates());
    }

    @Test
    public void builderWithAllParams() {
        LocationRecorderConfig config = new LocationRecorderConfig.Builder()
                .withIdentifier(TEST_IDENTIFIER).withMinTime(TEST_MIN_TIME)
                .withMinDistance(TEST_MIN_DISTANCE).withUsesRelativeCoordinates(true).build();
        LocationRecorder recorder = (LocationRecorder) config.recorderForStep(DUMMY_STEP,
                MOCK_OUTPUT_DIRECTORY);
        assertCommonProps(recorder);
        assertEquals(TEST_IDENTIFIER, recorder.getIdentifier());
        assertEquals(TEST_MIN_TIME, recorder.getMinTime());
        assertEquals(TEST_MIN_DISTANCE, recorder.getMinDistance(), 0.001);
        assertTrue(recorder.getUsesRelativeCoordinates());
    }

    private static void assertCommonProps(LocationRecorder recorder) {
        assertSame(DUMMY_STEP, recorder.getStep());
        assertSame(MOCK_OUTPUT_DIRECTORY, recorder.getOutputDirectory());
    }
}
