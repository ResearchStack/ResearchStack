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
import co.touchlab.touchkit.rk.common.step.ConsentVisualStep;
import co.touchlab.touchkit.rk.ui.views.ConsentSectionLayout;

public class ConsentVisualStepFragment extends MultiSectionStepFragment
{

    private ConsentVisualStep step;
    private ConsentDocument document;

    public ConsentVisualStepFragment()
    {
        super();
    }

    public static Fragment newInstance(ConsentVisualStep step)
    {
        ConsentVisualStepFragment fragment = new ConsentVisualStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        step = (ConsentVisualStep) getArguments().getSerializable(KEY_QUESTION_STEP);
        document = step.getDocument();
    }

    @Override
    public int getSectionCount()
    {
        return document.getSections().size();
    }

    @Override
    public int getNextViewId()
    {
        return R.id.layout_consent_next;
    }

    @Override
    public View createSectionLayout(LayoutInflater inflater, int section)
    {
        ConsentSectionLayout layout = (ConsentSectionLayout) inflater
                .inflate(R.layout.item_consent_section, (ViewGroup) getView(), false);
        layout.setData(document.getSections().get(section));
        return layout;
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
