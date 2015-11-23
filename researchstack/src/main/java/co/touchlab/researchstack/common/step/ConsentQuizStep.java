package co.touchlab.researchstack.common.step;
import co.touchlab.researchstack.common.model.ConsentQuizModel;
import co.touchlab.researchstack.ui.scene.ConsentQuizScene;

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
}
