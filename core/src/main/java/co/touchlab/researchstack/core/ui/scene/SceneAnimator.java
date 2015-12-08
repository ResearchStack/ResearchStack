package co.touchlab.researchstack.core.ui.scene;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.security.InvalidParameterException;

import co.touchlab.researchstack.core.R;


/**
 * TODO This should be apart of a SceneSwitcher class.
 * This class would be fimilar in funciton to how {@link android.widget.ViewSwitcher} works,
 * but using ValuePropertyAnimator to move around and animate the view (i.e. animations are hardcoded)
 */
public class SceneAnimator
{
    public static final int SHIFT_LEFT = 1;
    public static final int SHIFT_RIGHT = -1;

    private DecelerateInterpolator interpolator = new DecelerateInterpolator(2);

    private int animationTime;

    private boolean isAnimating;

    private ViewGroup root;

    public SceneAnimator(ViewGroup root)
    {
        if (root == null)
        {
            throw new InvalidParameterException("Root ViewGroup must not be null");
        }


        this.root = root;
        this.animationTime = root.getResources()
                .getInteger(R.integer.config_mediumAnimTime);
    }

    @Deprecated
    public void show(Scene oldScene, Scene newScene)
    {
        setIsAnimating(false);

        if (oldScene != null)
        {
            root.removeView(oldScene.getView());
        }

        root.addView(newScene.getView());
    }

    public void animate(Scene oldScene, Scene newScene, int direction)
    {
        if (direction != SHIFT_LEFT && direction != SHIFT_RIGHT)
        {
            throw new InvalidParameterException("Direction with value: " + direction + " is not supported.");
        }

        root.post(() -> {
            root.addView(newScene.getView());

            int newTranslationX = direction * root.getWidth();

            newScene.getView().setTranslationX(newTranslationX);
            newScene.getView().animate().setDuration(animationTime).setInterpolator(interpolator)
                    .translationX(0);

            oldScene.getView().animate().withStartAction(() -> setIsAnimating(true))
                    .setInterpolator(interpolator).setDuration(animationTime).translationX(- 1 * newTranslationX).withEndAction(() -> {
                setIsAnimating(false);
                root.removeView(oldScene.getView());
            });
        });
    }

    public boolean isAnimating()
    {
        return isAnimating;
    }

    protected void setIsAnimating(boolean isAnimating)
    {
        this.isAnimating = isAnimating;
    }

}
