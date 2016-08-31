/*
 * The following code is based off android.widget.ViewAnimator with
 * improvements like
 *
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.researchstack.backbone.ui.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.layout.StepLayout;

import java.security.InvalidParameterException;

/**
 * Base class for a {@link FrameLayout} container that will perform animations
 * when switching between two steps. There will, at most, be two steps when animating. The step
 * going off screen will eventually be removed.
 */
public class StepSwitcher extends FrameLayout
{
    public static final DecelerateInterpolator interpolator = new DecelerateInterpolator(2);

    public static final int SHIFT_LEFT  = 1;
    public static final int SHIFT_RIGHT = - 1;

    private int animationTime;

    /**
     * Creates a new empty StepSwitcher.
     *
     * @param context the application's environment
     */
    public StepSwitcher(Context context)
    {
        super(context);
        init();
    }

    /**
     * Creates a new empty StepSwitcher for the given context and with the
     * specified set attributes.
     *
     * @param context the application environment
     * @param attrs   a collection of attributes
     */
    public StepSwitcher(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    /**
     * Creates a new empty StepSwitcher for the given context and with the
     * specified set attributes.
     *
     * @param context      the application environment
     * @param attrs        a collection of attributes
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     *                     resource that supplies defaults values for the TypedArray.  Can be 0 to
     *                     not look for defaults.
     */
    public StepSwitcher(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        animationTime = getResources().getInteger(R.integer.rsb_config_mediumAnimTime);
    }

    /**
     * Adds a new step to the view hierarchy. If a step is currently showing, the direction
     * parameter is used to indicate which direction(x-axis) that the views should animate to.
     *
     * @param stepLayout the step you want to switch to
     * @param direction  the direction of the animation in the x direction. This values can either be
     *                   {@link StepSwitcher#SHIFT_LEFT} or {@link StepSwitcher#SHIFT_RIGHT}
     */
    public void show(StepLayout stepLayout, int direction)
    {
        // if layouts originate from the same step, ignore show
        View currentStep = findViewById(R.id.rsb_current_step);
        if(currentStep != null)
        {
            String currentStepId = (String) currentStep.getTag(R.id.rsb_step_layout_id);
            String stepLayoutId = (String) stepLayout.getLayout().getTag(R.id.rsb_step_layout_id);
            if(currentStepId.equals(stepLayoutId))
            {
                return;
            }
        }

        // Force crash when invalid direction is passed in. The values of the constants are used
        // when calculating the x-traversal distance
        if(direction != StepSwitcher.SHIFT_LEFT && direction != StepSwitcher.SHIFT_RIGHT)
        {
            throw new InvalidParameterException(
                    "Direction with value: " + direction + " is not supported.");
        }

        post(() -> {
            // Set the id of current as something other than R.id.current_step
            int currentIndex = 0;
            if(currentStep != null)
            {
                currentStep.setId(0);
                currentIndex = indexOfChild(currentStep);
            }

            // Add the new step to the view stack & set the id as the current step. Set the index
            // in the view hierarchy as the same as the current step on-screen
            LayoutParams lp = getLayoutParams(stepLayout);
            addView(stepLayout.getLayout(), currentIndex, lp);
            stepLayout.getLayout().setId(R.id.rsb_current_step);

            // If the old step is gone, we can go ahead and ignore the following animation code.
            // This will usually happen on start-up of the host (e.g. activity)
            if(currentStep != null)
            {
                int newTranslationX = direction * getWidth();

                stepLayout.getLayout().setTranslationX(newTranslationX);
                stepLayout.getLayout()
                        .animate()
                        .setDuration(animationTime)
                        .setInterpolator(interpolator)
                        .translationX(0);

                currentStep.animate()
                        .setInterpolator(interpolator)
                        .setDuration(animationTime)
                        .translationX(- 1 * newTranslationX)
                        .withEndAction(() ->{
                            InputMethodManager imm = (InputMethodManager) getContext()
                                    .getSystemService(Activity.INPUT_METHOD_SERVICE);

                            if(imm.isActive() && imm.isAcceptingText())
                            {
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                            }

                            removeView(currentStep);

                        });
            }
        });
    }

    private LayoutParams getLayoutParams(StepLayout stepLayout)
    {
        LayoutParams lp = (LayoutParams) stepLayout.getLayout().getLayoutParams();
        if(lp == null)
        {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        return lp;
    }

    @Override
    public CharSequence getAccessibilityClassName()
    {
        return StepSwitcher.class.getName();
    }

}
