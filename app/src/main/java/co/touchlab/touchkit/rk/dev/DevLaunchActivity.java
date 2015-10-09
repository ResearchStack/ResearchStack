package co.touchlab.touchkit.rk.dev;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.common.answerformat.BooleanAnswerFormat;
import co.touchlab.touchkit.rk.common.step.QuestionStep;
import co.touchlab.touchkit.rk.common.task.OrderedTask;
import co.touchlab.touchkit.rk.ui.ViewTaskActivity;

public class DevLaunchActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AnswerFormat answerFormat = new BooleanAnswerFormat();
        QuestionStep questionStepOne = new QuestionStep("intro", "What is the color blue?", answerFormat);
        QuestionStep questionStepTwo = new QuestionStep("notIntro", "Why is the color blue?", answerFormat);

        OrderedTask task = new OrderedTask("task", questionStepOne, questionStepTwo);

        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, ViewTaskActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
