package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.HashMap;

import co.touchlab.researchstack.core.ui.scene.Scene;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.glue.model.ConsentQuizModel;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.glue.step.ConsentQuizStep;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.scene.MultiSubSectionScene;
import co.touchlab.researchstack.core.ui.scene.SceneImpl;

/**
 * TODO Save attempt in SaveState
 */
public class ConsentQuizScene extends MultiSubSectionScene<Boolean>
{
    private static final String ID_RESULT = "result";

    private HashMap<String, Boolean> results = new HashMap<>();
    private ConsentQuizModel model;
    private int attempt;

    public ConsentQuizScene(Context context, Step step)
    {
        super(context, step);
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
    public SceneImpl onCreateScene(LayoutInflater inflater, int position)
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
        String title = getString(newScene instanceof ConsentQuizEvaluationScene ?
                                         R.string.quiz_evaluation : R.string.quiz);
        getCallbacks().onStepTitleChanged(title);
    }

    @Override
    public void onStepResultChanged(Step step, StepResult result)
    {
        if (!step.getIdentifier().equals(ID_RESULT))
        {
            results.put(step.getIdentifier(), (boolean) result.getResultForIdentifier(StepResult.DEFAULT_KEY));
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
            boolean result = results.get(question.id);
            boolean correct = question.constraints.validation.answer.equals(Boolean.toString(result));
            count += correct ? 1 : 0 ;
        }

        return count;
    }

    public int getIncorrectAnswerCount()
    {
        int questions = model.getQuestions().size();
        int correct = getCorrectAnswerCount();
        return questions - correct;
    }

    @Override
    public void onNextPressed(Step step)
    {
        LogExt.i(getClass(), "onNextPressed");

        if(getCurrentPosition() < model.getQuestions().size())
        {
            LogExt.i(getClass(), "Show next question");
            loadNextScene();
        }
        else
        {
            StepResult<Boolean> result = getStepResult();

            // TODO Rename maxIncorrect variable.
            // The operator should be inclusive for this situation ..... since maxIncorrect is an
            // inclusive upper limit.

            // If we passed
            if (getIncorrectAnswerCount() < model.evalProperties.maxIncorrect)
            {
                LogExt.i(getClass(), "Quiz Passed");
                result.setResultForIdentifier(StepResult.DEFAULT_KEY, true);

                getCallbacks().onStepResultChanged(getStep(), result);
                getCallbacks().onNextPressed(getStep());
            }

            // If we failed
            else
            {
                LogExt.i(getClass(), "Quiz Failed");

                if (attempt == 0)
                {
                    LogExt.i(getClass(), "First attempt, let them retry");

                    attempt = 1;
                    showScene(0, true);
                }
                else
                {
                    LogExt.i(getClass(), "Last attempt, go back to consent visual flow");
                    result.setResultForIdentifier(StepResult.DEFAULT_KEY, false);

                    getCallbacks().onStepResultChanged(getStep(), result);
                    getCallbacks().onNextPressed(getStep());
                }
            }
        }
    }

    @Override
    public StepResult getResultStep(String stepId)
    {
        return super.getResultStep(stepId);
    }

    @Override
    public StepResult<Boolean> createNewStepResult(String id)
    {
        return new StepResult<>(getStep().getIdentifier());
    }
}
