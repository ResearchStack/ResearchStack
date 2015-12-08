package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.model.ConsentSection;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.step.Step;

public class ConsentVisualScene extends MultiSubSectionScene
{
    private ConsentDocument document;

    public ConsentVisualScene(Context context, Step step, StepResult result)
    {
        super(context, step, result);
    }

    @Override
    public void onPreInitialized()
    {
        super.onPreInitialized();
        document = ((ConsentVisualStep) getStep()).getDocument();
    }

    @Override
    public int getSceneCount()
    {
        return document.getSections().size();
    }

    @Override
    public SceneImpl onCreateScene(LayoutInflater inflater, int scenePos)
    {
        ConsentSection section = document.getSections().get(scenePos);
        SceneImpl scene = new ConsentVisualSectionScene(getContext(), new Step("consent_visual" + scenePos), section);
        String nextTitle = getString(R.string.next);
        if (section.getType() == ConsentSection.Type.Overview)
        {
            nextTitle = getString(R.string.button_get_started);
        }
        else if (scenePos == getSceneCount() - 1)
        {
            nextTitle = getString(R.string.button_done);
        }

        scene.setNextButtonText(nextTitle);
        return scene;
    }

    @Override
    public void notifyStepResultChanged(Step step, StepResult result)
    {
        // Ignore results generated from the sub-sections
    }

}
