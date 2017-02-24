package org.researchstack.backbone.result;

import android.graphics.Point;
import android.graphics.Rect;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TheMDP on 2/23/17.
 */

public class TappingIntervalResult extends Result {

    /**
     * An array of collected samples, in which each item is an `ORKTappingSample` object that represents a
     * tapping event.
     */
    private List<Sample> samples;

    /**
     * The size of the bounds of the step view containing the tap targets.
     */
    private Size stepViewSize;

    /**
     * The frame of the left button, in points, relative to the step view bounds.
     */
    private Rect buttonRect1;

    /**
     * sThe frame of the right button, in points, relative to the step view bounds.
     */
    private Rect buttonRect2;

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

    public Size getStepViewSize() {
        return stepViewSize;
    }

    public void setStepViewSize(Size stepViewSize) {
        this.stepViewSize = stepViewSize;
    }

    public Rect getButtonRect1() {
        return buttonRect1;
    }

    public void setButtonRect1(Rect buttonRect1) {
        this.buttonRect1 = buttonRect1;
    }

    public Rect getButtonRect2() {
        return buttonRect2;
    }

    public void setButtonRect2(Rect buttonRect2) {
        this.buttonRect2 = buttonRect2;
    }

    public static class Sample implements Serializable {
        /**
         * A relative timestamp indicating the time of the tap event.
         *
         * The timestamp is relative to the value of `startDate` in the `Result` object that includes this
         * sample.
         */
        private long timestamp;

        /**
         * A duration of the tap event.
         *
         * The duration store time interval between touch down and touch release events.
         */
        private long duration;

        /**
         * An enumerated value that indicates which button was tapped, if any.
         *
         * If the value of this property is `ORKTappingButtonIdentifierNone`, it indicates that the tap
         * was near, but not inside, one of the target buttons.
         */
        private TappingButtonIdentifier buttonIdentifier;

        /**
         * The location of the tap within the step's view.
         *
         * The location coordinates are relative to a rectangle whose size corresponds to
         * the `stepViewSize` in the enclosing `ORKTappingIntervalResult` object.
         */
        private Point location;

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

        public Point getLocation() {
            return location;
        }

        public void setLocation(Point location) {
            this.location = location;
        }
    }

    /**
     Values that identify the button that was tapped in a tapping sample.
     */
    public enum TappingButtonIdentifier {
        // The touch landed outside of the two buttons.
        TappingButtonIdentifierNone,
        // The touch landed in the left button.
        TappingButtonIdentifierLeft,
        // The touch landed in the right button.
        TappingButtonIdentifierRight;
    }

    public static final class Size {

        private int width;
        private int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
