package org.researchstack.backbone.result;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Created by TheMDP on 2/23/17.
 */

public class TappingIntervalResult extends Result {

    /**
     * An array of collected samples, in which each item is an `ORKTappingSample` object that represents a
     * tapping event.
     */
    @SerializedName("TappingSamples")
    private List<Sample> samples;

    /**
     * The size of the bounds of the step view containing the tap targets.
     */
    @SerializedName("TappingViewSize")
    private String stepViewSize;

    /**
     * The frame of the left button, in points, relative to the step view bounds.
     */
    @SerializedName("ButtonRectLeft")
    private String buttonRectLeft;

    /**
     * sThe frame of the right button, in points, relative to the step view bounds.
     */
    @SerializedName("ButtonRectRight")
    private String buttonRectRight;

    /* Default identifier for serialization/deserialization */
    TappingIntervalResult() {
        super();
    }

    public TappingIntervalResult(String identifier) {
        super(identifier);
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public void setStepViewSize(int width, int height) {
        this.stepViewSize = String.format(Locale.getDefault(), "{%d, %d}", width, height);
    }

    public void setButtonRect1(int x, int y, int width, int height) {
        // This is the output format of iOS' NSStringFromCGRect, which is the expected format
        this.buttonRectLeft = String.format(Locale.getDefault(), "{{%d, %d} {%d, %d}}", x, y, width, height);
    }

    public void setButtonRect2(int x, int y, int width, int height) {
        // This is the output format of iOS' NSStringFromCGRect, which is the expected format
        this.buttonRectRight = String.format(Locale.getDefault(), "{{%d, %d} {%d, %d}}", x, y, width, height);
    }

    public static class Sample implements Serializable {
        /**
         * A relative timestamp indicating the time of the tap event.
         *
         * The timestamp is relative to the value of `startDate` in the `Result` object that includes this
         * sample.
         */
        @SerializedName("TapTimeStamp")
        private long timestamp;

        /**
         * A duration of the tap event.
         *
         * The duration store time interval between touch down and touch release events.
         */
        @SerializedName("duration")
        private long duration;

        /**
         * An enumerated value that indicates which button was tapped, if any.
         *
         * If the value of this property is `ORKTappingButtonIdentifierNone`, it indicates that the tap
         * was near, but not inside, one of the target buttons.
         */
        @SerializedName("TappedButtonId")
        private TappingButtonIdentifier buttonIdentifier;

        /**
         * The location of the tap within the step's view.
         *
         * The location coordinates are relative to a rectangle whose size corresponds to
         * the `stepViewSize` in the enclosing `ORKTappingIntervalResult` object.
         */
        @SerializedName("TapCoordinate")
        private String location;

        /* Default identifier for serialization/deserialization */
        public Sample() {
            super();
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public TappingButtonIdentifier getButtonIdentifier() {
            return buttonIdentifier;
        }

        public void setButtonIdentifier(TappingButtonIdentifier buttonIdentifier) {
            this.buttonIdentifier = buttonIdentifier;
        }
        public void setLocation(int x, int y) {
            this.location = String.format(Locale.getDefault(), "{%d, %d}", x, y);
        }
    }

    /**
     Values that identify the button that was tapped in a tapping sample.
     */
    public enum TappingButtonIdentifier {
        // The touch landed outside of the two buttons.
        TappedButtonNone,
        // The touch landed in the left button.
        TappedButtonLeft,
        // The touch landed in the right button.
        TappedButtonRight;
    }
}
