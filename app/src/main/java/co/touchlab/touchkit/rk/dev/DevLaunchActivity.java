package co.touchlab.touchkit.rk.dev;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.common.answerformat.BooleanAnswerFormat;
import co.touchlab.touchkit.rk.common.helpers.LogExt;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.result.TaskResult;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.task.OrderedTask;
import co.touchlab.touchkit.rk.ui.ViewTaskActivity;

public class DevLaunchActivity extends Activity
{

    private QuestionStep questionStepOne;
    private QuestionStep questionStepTwo;

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
            LogExt.d(getClass(),
                    "Got a result back from the ViewTaskActivity");
            TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
            StepResult<QuestionResult<Boolean>> stepResult1 = taskResult.getStepResultForStepIdentifier(questionStepOne.getIdentifier());
            StepResult<QuestionResult<Boolean>> stepResult2 = taskResult.getStepResultForStepIdentifier(questionStepTwo.getIdentifier());
            QuestionResult questionResult1 = stepResult1.getResultForIdentifier(questionStepOne.getIdentifier());
            QuestionResult questionResult2 = stepResult2.getResultForIdentifier(questionStepTwo.getIdentifier());
            LogExt.d(getClass(), questionResult1.getIdentifier() + " result: " + questionResult1.getAnswer());
            LogExt.d(getClass(), questionResult2.getIdentifier() + " result: " + questionResult2.getAnswer());
        }
    }

    public void buttonClick(View view)
    {
        AnswerFormat answerFormat = new BooleanAnswerFormat();
        questionStepOne = new QuestionStep("intro", "What is the color blue?", answerFormat);
        questionStepTwo = new QuestionStep("notIntro", "Why is the color blue?", answerFormat);

        OrderedTask task = new OrderedTask("task",
                questionStepOne,
                questionStepTwo);

        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, ViewTaskActivity.REQUEST_CODE);
    }
}
