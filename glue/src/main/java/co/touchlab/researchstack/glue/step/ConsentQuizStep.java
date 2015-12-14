package co.touchlab.researchstack.glue.step;

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.glue.ui.scene.ConsentQuizScene;

@Deprecated
public class ConsentQuizStep extends QuestionStep
{
    private ConsentQuizModel model;

    public ConsentQuizStep(String identifier, ConsentQuizModel model)
    {
        super(identifier);
        this.model = model;
    }

    public ConsentQuizModel getModel()
    {
        return model;
    }

    @Override
    public Class getSceneClass()
    {
        return ConsentQuizScene.class;
    }

    @Override
    public int getSceneTitle()
    {
        return R.string.consent;
    }
}
