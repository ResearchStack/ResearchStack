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

package co.touchlab.researchstack.core.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.security.InvalidParameterException;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.ui.step.layout.StepLayout;

/**
 * Base class for a {@link FrameLayout} container that will perform animations
 * when switching between two scenes. There will, at most, be two scenes when animating. The scene
 * going off screen will eventually be removed.
 */
public class SceneSwitcher extends FrameLayout
{
    public static final DecelerateInterpolator interpolator = new DecelerateInterpolator(2);

    public static final int SHIFT_LEFT  = 1;
    public static final int SHIFT_RIGHT = - 1;

    private int animationTime;

    /**
     * Creates a new empty SceneSwitcher.
     *
     * @param context the application's environment
     */
    public SceneSwitcher(Context context)
    {
        super(context);
        init();
    }

    /**
     * Creates a new empty SceneSwitcher for the given context and with the
     * specified set attributes.
     *
     * @param context the application environment
     * @param attrs   a collection of attributes
     */
    public SceneSwitcher(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    /**
     * Creates a new empty SceneSwitcher for the given context and with the
     * specified set attributes.
     *
     * @param context      the application environment
     * @param attrs        a collection of attributes
     * @param defStyleAttr An attribute in the current theme that contains a reference to a style
     *                     resource that supplies defaults values for the TypedArray.  Can be 0 to
     *                     not look for defaults.
     */
    public SceneSwitcher(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        animationTime = getResources().getInteger(R.integer.rsc_config_mediumAnimTime);
    }

    /**
     * Adds a new scene to the view hierarchy. If a scene is currently showing, the direction
     * parameter is used to indicate which direction(x-axis) that the views should animate to.
     *
     * @param stepLayout the scene you want to switch to
     * @param direction  the direction of the animation in the x direction. This values can either be
     *                   {@link SceneSwitcher#SHIFT_LEFT} or {@link SceneSwitcher#SHIFT_RIGHT}
     */
    public void show(StepLayout stepLayout, int direction)
    {
        // Force crash when invalid direction is passed in. The values of the constants are used
        // when calculating the x-traversal distance
        if(direction != SceneSwitcher.SHIFT_LEFT && direction != SceneSwitcher.SHIFT_RIGHT)
        {
            throw new InvalidParameterException(
                    "Direction with value: " + direction + " is not supported.");
        }

        post(() -> {
            // Get the current scene, set the id as something other than R.id.current_scene
            View currentScene = findViewById(R.id.rsc_current_scene);
            int currentIndex = 0;
            if(currentScene != null)
            {
                currentScene.setId(0);
                currentIndex = indexOfChild(currentScene);
            }

            // Add the new scene to the view stack & set the id as the current scene. Set the index
            // in the view hierarchy as the same as the current scene on-screen
            LayoutParams lp = getLayoutParams(stepLayout);
            addView(stepLayout.getLayout(), currentIndex, lp);
            stepLayout.getLayout()
                      .setId(R.id.rsc_current_scene);

            // If the old scene is gone, we can go ahead and ignore the following animation code.
            // This will usually happen on start-up of the host (e.g. activity)
            if(currentScene != null)
            {
                int newTranslationX = direction * getWidth();

                stepLayout.getLayout()
                          .setTranslationX(newTranslationX);
                stepLayout.getLayout()
                          .animate()
                          .setDuration(animationTime)
                          .setInterpolator(interpolator)
                          .translationX(0);

                currentScene.animate()
                            .setInterpolator(interpolator)
                            .setDuration(animationTime)
                            .translationX(- 1 * newTranslationX)
                            .withEndAction(() -> removeView(currentScene));
            }
        });
    }

    private LayoutParams getLayoutParams(StepLayout stepLayout)
    {
        LayoutParams lp = (LayoutParams) stepLayout.getLayout()
                                                   .getLayoutParams();
        if(lp == null)
        {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
        return lp;
    }

    @Override
    public CharSequence getAccessibilityClassName()
    {
        return SceneSwitcher.class.getName();
    }

}
