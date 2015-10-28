package co.touchlab.touchkit.rk.ui;
import android.support.v4.app.Fragment;

import co.touchlab.touchkit.rk.common.step.ConsentReviewStep;
import co.touchlab.touchkit.rk.common.step.ConsentSharingStep;
import co.touchlab.touchkit.rk.common.step.ConsentVisualStep;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.ui.fragment.ConsentReviewStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.ConsentSharingStepFragment;
import co.touchlab.touchkit.rk.ui.fragment.ConsentVisualStepFragment;

public class ConsentActivity extends ViewTaskActivity
{
    @Override
    protected Fragment getFragmentForStep(Step step)
    {
        if(step instanceof ConsentVisualStep)
        {
            return ConsentVisualStepFragment.newInstance((ConsentVisualStep) step);
        }
        else if(step instanceof ConsentSharingStep)
        {
            return ConsentSharingStepFragment.newInstance((ConsentSharingStep) step);
        }
        else if(step instanceof ConsentReviewStep)
        {
            return ConsentReviewStepFragment.newInstance((ConsentReviewStep) step);
        }
        else
        {
            return super.getFragmentForStep(step);
        }
    }
}
