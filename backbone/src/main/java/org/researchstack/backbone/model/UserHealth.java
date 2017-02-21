package org.researchstack.backbone.model;

import android.content.Context;

import org.researchstack.backbone.R;

import java.io.Serializable;

/**
 * Created by TheMDP on 2/15/17.
 *
 * This should mimic the HealthKit values store on iOS
 */

public class UserHealth implements Serializable {

    public static final float NO_VALUE = -1.0f;

    /**
     * Weight in lbs
     */
    private float weight = NO_VALUE;

    /**
     * Height in inches
     */
    private float height = NO_VALUE;

    /**
     * Gender of the user
     */
    private Gender gender = Gender.NOT_SET;

    /** Default constructor for Serializable */
    protected UserHealth() {}

    /**
     * @return valid weight measurement if it has been entered by the user
     *         it will return -1 if no valid weight is available
     */
    public float getWeight() {
        return weight;
    }

    /**
     * @return true if a valid weight measurement has been entered by the user, false otherwise
     */
    public boolean hasWeight() {
        return !(weight < 0);
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * @return valid height measurement if it has been entered by the user
     *         it will return -1 if no valid height is available
     */
    public float getHeight() {
        return height;
    }

    /**
     * @return true if a valid height measurement has been entered by the user, false otherwise
     */
    public boolean hasHeight() {
        return !(height < 0);
    }

    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * @return gender of the user, defaults to NOT_SET
     */
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public enum Gender {
        MALE,
        FEMALE,
        OTHER,
        NOT_SET;

        public String localizedTitle(Context context) {
            switch (this) {
                case MALE:
                    return context.getString(R.string.rsb_GENDER_MALE);
                case FEMALE:
                    return context.getString(R.string.rsb_GENDER_FEMALE);
                case OTHER:
                    return context.getString(R.string.rsb_GENDER_OTHER);
                default:
                    return NOT_SET.toString();  // no need to localize
            }
        }
    }
}
