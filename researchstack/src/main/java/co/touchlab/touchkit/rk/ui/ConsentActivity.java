package co.touchlab.touchkit.rk.ui;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.ui.scene.Scene;

@Deprecated
public class ConsentActivity extends ViewTaskActivity
{
    @Override
    protected Scene getSceneForStep(Step step)
    {
//        if(step instanceof ConsentVisualStep)
//        {
//            return ConsentVisualState.newInstance((ConsentVisualStep) step);
//        }
//        else if(step instanceof ConsentSharingStep)
//        {
//            return ConsentSharingStepFragment.newInstance((ConsentSharingStep) step);
//        }
//        else if(step instanceof ConsentReviewStep)
//        {
//            return ConsentReviewScene.newInstance((ConsentReviewStep) step);
//        }
//        else
//        {
//            return super.getSceneForStep(step);
//        }
        return null;
    }
}
