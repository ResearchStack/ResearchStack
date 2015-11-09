package co.touchlab.touchkit.rk.ui.fragment;

import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.ui.scene.Scene;

public abstract class MultiSceneStepFragment extends StepFragment
{

    /**
     * TODO Consume "onBackPressed" in activity. -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Implement method on {@link MultiSceneStepFragment}. If current {@link Scene} is 0, and back is pressed,
     * allow activity to swap to previous step. Else, let {@link MultiSceneStepFragment} go back a {@link Scene}.
     */

    private boolean isAnimating;

    private TimeInterpolator interpolator = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };

    public MultiSceneStepFragment()
    {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_step_multi_scene, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        int current = getCurrentScene();
        showScene(current, false);
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        throw new RuntimeException(
                "onCreateView must be overriden and should not call through to super impl");
    }

    public int getCurrentScene()
    {
        return getView().getTag() == null ? 0 : (int) getView().getTag();
    }

    public abstract Scene onCreateScene(LayoutInflater inflater, int scenePos);

    public abstract int getSceneCount();

    /**
     * TODO rename method name
     * This method is responsible for telling any subclasses that this scene was just popped off
     * the view stack and a new one is being added shorty. If you need to get the result and save
     * it, do tha tnow.
     */
    public abstract void scenePoppedOffViewStack(Scene scene);

    public void showScene(int position, boolean withAnimation)
    {
        // If the count is 0, crash!
        if(getSceneCount() < 1)
        {
            throw new IllegalStateException("Scene count cannot be 0");
        }

        // If the view is animating, ignore that the request was made
        if (getView() == null || isAnimating)
        {
            return;
        }

        ViewGroup root = (ViewGroup) getView();
        if (root.getChildCount() > 0)
        {
            View oldScene = root.getChildAt(0);
            scenePoppedOffViewStack((Scene) oldScene);
        }

        // Create the next scene
        Scene newScene = onCreateScene(getLayoutInflater(null), position);
        newScene.setCallbacks(getStep(), callbacks);

        root.post(() -> {
            if(withAnimation)
            {
                isAnimating = true;
            }

            boolean hasChild = root.getChildCount() > 0;
            boolean isNextStep = getCurrentScene() < position;

            root.addView(newScene);
            root.setTag(position);

            if(withAnimation)
            {
                int newTranslationX = (isNextStep ? 1 : - 1) * root.getWidth();

                //TODO Should not having a root child but still wanting to animate exist?
                if(hasChild)
                {
                    View oldScene = root.getChildAt(0);
                    oldScene.animate().setInterpolator(interpolator)
                            .translationX(- 1 * newTranslationX).withEndAction(() -> {
                        root.removeView(oldScene);
                        isAnimating = false;
                    });
                }

                newScene.setTranslationX(newTranslationX);
                newScene.animate().setInterpolator(interpolator).translationX(0);
            }
            else
            {
                if (hasChild)
                {
                    root.removeViewAt(0);
                }
            }
        });
    }

    public void goForward()
    {
        int currentScene = getCurrentScene();
        showScene(currentScene + 1 , true);
    }

    public void goBack()
    {
        int currentScene = getCurrentScene();
        showScene(currentScene - 1 , true);
    }

}
