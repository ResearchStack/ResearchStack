package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
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

    public ConsentVisualScene(Context context)
    {
        super(context);
    }

    public ConsentVisualScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentVisualScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initializeScene()
    {
        document = ((ConsentVisualStep) getStep()).getDocument();

        super.initializeScene();
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
        ConsentVisualSectionScene scene = new ConsentVisualSectionScene(getContext());
        scene.initialize(new Step("consent_visual_" + scenePos), section);

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
