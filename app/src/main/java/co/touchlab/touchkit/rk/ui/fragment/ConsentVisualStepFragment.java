package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.ConsentDocument;
import co.touchlab.touchkit.rk.common.model.ConsentSection;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.ConsentVisualStep;
import co.touchlab.touchkit.rk.ui.scene.ConsentVisualScene;
import co.touchlab.touchkit.rk.ui.scene.Scene;

public class ConsentVisualStepFragment extends MultiSceneStepFragment
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
    public int getSceneCount()
    {
        return document.getSections().size();
    }

    @Override
    public Scene onCreateScene(LayoutInflater inflater, int scenePos)
    {
        ConsentSection section = document.getSections().get(scenePos);
        ConsentVisualScene scene = new ConsentVisualScene(getContext(), section);
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
    public void scenePoppedOffViewStack(Scene scene)
    {
        // Ignore, we don't need to save any data from the scenes
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
