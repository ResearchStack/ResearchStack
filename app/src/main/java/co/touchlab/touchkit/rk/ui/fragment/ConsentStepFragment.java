package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.ConsentDocument;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.ConsentStep;
import co.touchlab.touchkit.rk.ui.adapter.ConsentPagerAdapter;

public class ConsentStepFragment extends StepFragment
{

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
        ConsentStep step = (ConsentStep) getArguments().getSerializable(KEY_QUESTION_STEP);
        ConsentDocument document = step.getDocument();

        ViewPager pager = (ViewPager) inflater.inflate(R.layout.fragment_step_consent, container, false);
        ConsentPagerAdapter adapter = new ConsentPagerAdapter(getContext(), document.getSections());
        pager.setAdapter(adapter);

        return pager;
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
}
