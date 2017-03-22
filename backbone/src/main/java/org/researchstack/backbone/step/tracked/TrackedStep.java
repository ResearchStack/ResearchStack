package org.researchstack.backbone.step.tracked;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.step.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by TheMDP on 3/21/17.
 *
 * A TrackedStep is a special Step that is a part of a TrackedDataObjectCollection
 * A tracked step can be any type that is included
 */

public class TrackedStep extends Step {

    private Type trackingType;

    /* Default constructor needed for serialization/deserialization of object */
    TrackedStep() {
        super();
    }

    /**
     * @param identifier for step
     * @param title for step
     * @param detailText for step
     * @param trackingType associated with this step
     */
    public TrackedStep(String identifier, String title, String detailText, Type trackingType) {
        super(identifier, title);
        setText(detailText);
        setOptional(false);
        this.trackingType = trackingType;
    }

    public Type getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(Type trackingType) {
        this.trackingType = trackingType;
    }

    public enum Type {
        @SerializedName("introduction")
        INTRODUCTION,
        @SerializedName("changed")
        CHANGED,
        @SerializedName("completion")
        COMPLETION,
        @SerializedName("activity")
        ACTIVITY,
        @SerializedName("selection")
        SELECTION,
        @SerializedName("frequency")
        FREQUENCY;
    }

    public enum TypeIncludes {

        STAND_ALONE_SURVEY(Arrays.asList(
                Type.INTRODUCTION,
                Type.SELECTION,
                Type.FREQUENCY,
                Type.COMPLETION)),

        ACTIVITY_ONLY(Arrays.asList(
                Type.ACTIVITY)),

        SURVEY_AND_ACTIVITY(Arrays.asList(
                Type.INTRODUCTION,
                Type.SELECTION,
                Type.FREQUENCY,
                Type.ACTIVITY)),

        CHANGED_AND_ACTIVITY(Arrays.asList(
                Type.CHANGED,
                Type.SELECTION,
                Type.FREQUENCY,
                Type.ACTIVITY)),

        CHANGED_ONLY(Arrays.asList(
                Type.CHANGED)),

        NONE(new ArrayList<>());

        private List<Type> typeList;
        private Type nextStepIfNoChange;

        TypeIncludes(List<Type> typeList) {
            if (typeList.contains(Type.CHANGED) && !typeList.contains(Type.ACTIVITY)) {
                typeList = Arrays.asList(
                        Type.CHANGED,
                        Type.SELECTION,
                        Type.FREQUENCY,
                        Type.ACTIVITY);
                nextStepIfNoChange = Type.COMPLETION;
            }
            else {
                this.typeList = typeList;
                nextStepIfNoChange = Type.ACTIVITY;
            }
        }

        public boolean includeSurvey() {
            return typeList.contains(Type.INTRODUCTION) || typeList.contains(Type.CHANGED);
        }

        public boolean shouldInclude(Type type) {
            return typeList.contains(type);
        }

        public List<Type> getTypeList() {
            return typeList;
        }

        public Type getNextStepIfNoChange() {
            return nextStepIfNoChange;
        }
    }
}
