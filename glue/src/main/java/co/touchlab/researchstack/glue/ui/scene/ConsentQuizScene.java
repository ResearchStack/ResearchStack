package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.HashMap;

import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.scene.MultiSubSectionScene;
import co.touchlab.researchstack.core.ui.scene.Scene;
import co.touchlab.researchstack.core.ui.scene.SceneImpl;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;
import co.touchlab.researchstack.glue.step.ConsentQuizStep;

/**
 * TODO Save attempt in SaveState
 */
public class ConsentQuizScene extends MultiSubSectionScene<Boolean>
{
    private static final String ID_RESULT = "result";

    private HashMap<String, Boolean> results = new HashMap<>();
    private ConsentQuizModel model;
    private int attempt;

    public ConsentQuizScene(Context context, Step step, StepResult result)
    {
        super(context, step, result);
    }

    @Override
    public void onPreInitialized()
    {
        super.onPreInitialized();
        model = ((ConsentQuizStep) getStep()).getModel();
    }

    /**
     * @return the amount of questions plus 1 for the quiz evaluation step.
     */
    @Override
    public int getSceneCount()
    {
        return model.getQuestions().size() + 1;
    }

    @Override
    public Scene onCreateScene(LayoutInflater inflater, int position)
    {
        SceneImpl scene;

        if (position < getSceneCount() - 1)
        {
            ConsentQuizModel.QuestionProperties properties = model.getQuestionProperties();
            ConsentQuizModel.QuizQuestion question = model.getQuestions().get(position);

            QuestionStep step = new QuestionStep(question.id, question.question);
            step.setOptional(false);

            scene = new ConsentQuizQuestionScene(getContext(), step, properties, question);
            scene.setNextButtonText(R.string.submit);
        }
        else
        {
            QuestionStep step = new QuestionStep(ID_RESULT);
            step.setOptional(false);

            int incorrect = getIncorrectAnswerCount();
            ConsentQuizModel.EvaluationProperties properties = model.getEvaluationProperties();
            scene = new ConsentQuizEvaluationScene(getContext(), step, properties, attempt, incorrect);
        }

        return scene;
    }

    @Override
    public void onSceneChanged(Scene oldScene, Scene newScene)
    {
        String title = getString(
                newScene instanceof ConsentQuizEvaluationScene ? R.string.quiz_evaluation :
                        R.string.quiz);
        getCallbacks().onStepTitleChanged(title);
    }

    @Override
    protected void onNextClicked()
    {
        StepResult<Boolean> result = getStepResult();

        // TODO Rename maxIncorrect variable.
        // The operator should be inclusive for this situation ..... since maxIncorrect is an
        // inclusive upper limit.

        // If we passed
        if (getIncorrectAnswerCount() < model.evalProperties.maxIncorrect)
        {
            result.setResultForIdentifier(StepResult.DEFAULT_KEY, true);
            super.onNextClicked();
        }

        // If we failed
        else
        {
            // If first attempt at quiz, let user retry
            if (attempt < 1)
            {
                attempt = 1;
                showScene(0, true);
            }

            // Max attempts exhausted, go back to consent visual flow
            else
            {
                result.setResultForIdentifier(StepResult.DEFAULT_KEY, false);
                super.onNextClicked();
            }
        }
    }

    @Override
    public void notifyStepResultChanged(Step step, StepResult result)
    {
        if (!ID_RESULT.equals(result.getIdentifier()))
        {
            boolean answer = (boolean) result.getResultForIdentifier(StepResult.DEFAULT_KEY);
            results.put(result.getIdentifier(), answer);
        }
    }

    @Override
    public boolean isBackEventConsumed()
    {
        int currentScene = getCurrentPosition();
        if (currentScene != 0)
        {
            ConsentQuizModel.QuizQuestion question = model.getQuestions().get(currentScene - 1);
            results.remove(question.id);
        }

        return super.isBackEventConsumed();
    }

    public int getCorrectAnswerCount()
    {
        int count = 0;

        for(ConsentQuizModel.QuizQuestion question : model.getQuestions())
        {
            Boolean boolResult = results.get(question.id);
            if (boolResult != null)
            {
                boolean correct = question.constraints.validation.answer
                        .equals(Boolean.toString(boolResult));
                count += correct ? 1 : 0;
            }
        }

        return count;
    }

    public int getIncorrectAnswerCount()
    {
        int questions = model.getQuestions().size();
        int correct = getCorrectAnswerCount();
        return questions - correct;
    }

}
