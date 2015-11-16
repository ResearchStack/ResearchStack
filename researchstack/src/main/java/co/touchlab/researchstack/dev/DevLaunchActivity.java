package co.touchlab.researchstack.dev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.answerformat.AnswerFormat;
import co.touchlab.researchstack.common.answerformat.BooleanAnswerFormat;
import co.touchlab.researchstack.common.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.common.helpers.LogExt;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.result.TaskResult;
import co.touchlab.researchstack.common.step.QuestionStep;
import co.touchlab.researchstack.common.task.OrderedTask;
import co.touchlab.researchstack.ui.ViewTaskActivity;

public class DevLaunchActivity extends Activity
{

    private QuestionStep questionStepOne;
    private QuestionStep questionStepTwo;
    private QuestionStep questionStepThree;
    private QuestionStep questionStepFour;
    private QuestionStep questionStepFive;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ViewTaskActivity.REQUEST_CODE && resultCode == RESULT_OK)
        {
            LogExt.d(getClass(), "Got a result back from the ViewTaskActivity");

            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            dev_printResult(taskResult, questionStepOne.getIdentifier());
            dev_printResult(taskResult, questionStepTwo.getIdentifier());
            dev_printResult(taskResult, questionStepThree.getIdentifier());
            dev_printResult(taskResult, questionStepFour.getIdentifier());
            dev_printResult(taskResult, questionStepFive.getIdentifier());
        }
    }

    private void dev_printResult(TaskResult result, String id)
    {
        StepResult<QuestionResult<Boolean>> stepResult = result.getStepResultForStepIdentifier(id);
        QuestionResult questionResult = stepResult.getResultForIdentifier(id);
        LogExt.d(getClass(), questionResult.getIdentifier() + " result: " + questionResult.getAnswer());
    }

    public void buttonClick(View view)
    {
        AnswerFormat booleanFormat = new BooleanAnswerFormat();
        AnswerFormat textFormat = new TextAnswerFormat();
        questionStepOne = new QuestionStep("intro", "What is the color blue?", booleanFormat);
        questionStepTwo = new QuestionStep("text", "Is the color blue?", textFormat);
        questionStepThree = new QuestionStep("red", "Truck Red?", booleanFormat);
        questionStepFour = new QuestionStep("green", "Car Green?", booleanFormat);
        questionStepFive = new QuestionStep("blue", "Plane Blue?", booleanFormat);


        OrderedTask task = new OrderedTask("task",
                questionStepOne, questionStepTwo, questionStepThree, questionStepFour, questionStepFive);

        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, ViewTaskActivity.REQUEST_CODE);
    }
}
