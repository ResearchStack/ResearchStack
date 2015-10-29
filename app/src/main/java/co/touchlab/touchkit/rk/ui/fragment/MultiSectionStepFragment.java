package co.touchlab.touchkit.rk.ui.fragment;

import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.touchkit.rk.R;

public abstract class MultiSectionStepFragment extends StepFragment
{

    //TODO Consume "onBackPressed" in activity. -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Implement method on MultiSectionStepFragment. If current section is 0, and back is pressed,
    // allow activity to swap to previous step. Else, let MultiSectionStepFragment go back a section.

    private boolean isAnimating;
    private TimeInterpolator interpolator = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };

    public MultiSectionStepFragment()
    {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_step_multi_section, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        int currentSection = getCurrentSection();
        showConsentSection(currentSection, false);
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        throw new RuntimeException("onCreateView must be overriden and should not call through to super impl");
    }

    public int getCurrentSection()
    {
        return getView().getTag() == null ? 0 : (int) getView().getTag();
    }

    public abstract View createSectionLayout(LayoutInflater inflater, int section);

    public abstract int getSectionCount();

    //TODO this sucks
    public abstract int getNextViewId();

    public void showConsentSection(int section, boolean withAnimation)
    {
        if (isAnimating)
        {
            return;
        }

        ViewGroup root = (ViewGroup) getView();

        // Create the next session, set interpolater for animation
        View newSection = createSectionLayout(getLayoutInflater(null), section);

        View next = newSection.findViewById(getNextViewId());
        if (next != null)
        {
            next.setOnClickListener(v -> {
                if(section == getSectionCount() - 1)
                {
                    callbacks.onNextPressed(step);
                }
                else
                {
                    showConsentSection(section + 1, true);
                }
            });
        }

        if (withAnimation && root.getChildCount() > 0)
        {
            isAnimating = true;

            root.post(() -> {
                boolean isNextStep = getCurrentSection() < section;

                int newTranslationX = (isNextStep ? 1 : - 1) * root.getWidth();
                newSection.setTranslationX(newTranslationX);
                root.addView(newSection);
                root.setTag(section);

                newSection.animate().setInterpolator(interpolator).translationX(0);

                View oldSection = root.getChildAt(0);
                oldSection.animate().setInterpolator(interpolator)
                        .translationX(- 1 * newTranslationX).withEndAction(() -> {
                    root.removeView(oldSection);
                    isAnimating = false;
                });
            });
        }
        else
        {
            root.addView(newSection);
            root.setTag(section);
        }
    }

}
