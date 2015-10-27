package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.ConsentDocument;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.ConsentStep;
import co.touchlab.touchkit.rk.ui.views.ConsentSectionLayout;

public class ConsentStepFragment extends StepFragment
{

    private ConsentStep step;
    private ConsentDocument document;
    private boolean isAnimating;

    public ConsentStepFragment()
    {
        super();
    }

    public static Fragment newInstance(ConsentStep step)
    {
        ConsentStepFragment fragment = new ConsentStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_step_consent, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        step = (ConsentStep) getArguments().getSerializable(KEY_QUESTION_STEP);
        document = step.getDocument();

        int currentSection = getCurrentSection();
        showConsentSection(currentSection, false);
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        return null;
    }

    public int getCurrentSection()
    {
        return getView().getTag() == null ? 0 : (int) getView().getTag();
    }

    public void showConsentSection(int section, boolean withAnimation)
    {
        if (isAnimating)
        {
            return;
        }

        ViewGroup root = (ViewGroup) getView();

        ConsentSectionLayout newSection = createConsentSectionLayout();
        View.OnClickListener onNextClicked = v ->
        {
            if (section == document.getSections().size() - 1)
            {
                callbacks.onNextPressed(step);
            }
            else
            {
                showConsentSection(section + 1, true);
            }
        };
        newSection.setData(document.getSections().get(section), onNextClicked);

        if (withAnimation && root.getChildCount() > 0)
        {
            isAnimating = true;

            root.post(() -> {
                boolean isNextStep = getCurrentSection() < section;

                int newTranslationX = (isNextStep ? 1 : - 1) * root.getWidth();
                newSection.setTranslationX(newTranslationX);
                root.addView(newSection);
                root.setTag(section);

                newSection.animate().translationX(0);

                View oldSection = root.getChildAt(0);
                oldSection.animate().translationX(- 1 * newTranslationX).withEndAction(() -> {
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

    public ConsentSectionLayout createConsentSectionLayout()
    {
        LayoutInflater inflater = getLayoutInflater(null);
        ConsentSectionLayout layout = (ConsentSectionLayout) inflater.inflate(
                R.layout.item_consent_section, (ViewGroup) getView(), false);
        layout.animate().setInterpolator(t -> {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        });
        return layout;
    }
}
