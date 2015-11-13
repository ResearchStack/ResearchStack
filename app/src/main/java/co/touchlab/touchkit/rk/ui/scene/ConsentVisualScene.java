package co.touchlab.touchkit.rk.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.ConsentDocument;
import co.touchlab.touchkit.rk.common.model.ConsentSection;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.ConsentVisualStep;
import co.touchlab.touchkit.rk.common.step.Step;

public class ConsentVisualScene extends MultiStateScene
{
    private ConsentDocument document;

    public ConsentVisualScene(Context context, Step step)
    {
        super(context, step);
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
    public Scene onCreateScene(LayoutInflater inflater, int scenePos)
    {
        ConsentSection section = document.getSections().get(scenePos);
        Scene scene = new ConsentVisualSectionScene(getContext(), section);
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
    public StepResult createNewStepResult(String id)
    {
        return new StepResult<QuestionResult<Boolean>>(getStep().getIdentifier());
    }
}
