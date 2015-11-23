package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.HashMap;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.helpers.LogExt;
import co.touchlab.researchstack.common.model.ConsentQuizModel;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.step.ConsentQuizStep;
import co.touchlab.researchstack.common.step.QuestionStep;
import co.touchlab.researchstack.common.step.Step;

/**
 * TODO Save attempt in SaveState
 */
public class ConsentQuizScene extends MultiSubSectionScene
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
    public Scene onCreateScene(LayoutInflater inflater, int position)
    {
        Scene scene;

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
    public void onStepResultChanged(Step step, StepResult result)
    {
        if (!step.getIdentifier().equals(ID_RESULT))
        {
            for(ConsentQuizModel.QuizQuestion question : model.getQuestions())
            {
                if(result.getIdentifier().equals(question.id))
                {
                    boolean answer = question.constraints.validation.answer.equals("true");
                    QuestionResult<Boolean> questionResult =  (QuestionResult<Boolean>) result
                            .getResults().get(step.getIdentifier());
                    results.put(step.getIdentifier(), questionResult.getAnswer().booleanValue() == answer);
                    break;
                }
            }
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
        for(boolean result : results.values())
        {
            count += result ? 1 : 0 ;
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
            String id = getStep().getIdentifier();

            QuestionResult<Boolean> questionResult = new QuestionResult<>(id);
            StepResult<QuestionResult<Boolean>> result = createNewStepResult(id);
            result.setResultForIdentifier(id, questionResult);


            //TODO Rename maxIncorrect variable.
            // A comparison should in inclusive for this situation ..... since maxIncorrect is an
            // upper limit.

            // If we passed
            if (getIncorrectAnswerCount() < model.evalProperties.maxIncorrect)
            {
                LogExt.i(getClass(), "Quiz Passed");
                questionResult.setAnswer(true);

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
                    questionResult.setAnswer(false);

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
    public StepResult createNewStepResult(String id)
    {
        return new StepResult<QuestionResult<Boolean>>(getStep().getIdentifier());
    }
}
