package co.touchlab.touchkit.rk.dev;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import co.touchlab.touchkit.rk.common.step.InstructionStep;
import co.touchlab.touchkit.rk.common.task.OrderedTask;
import co.touchlab.touchkit.rk.ui.ViewTaskActivity;

public class DevLaunchActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        InstructionStep introStep = new InstructionStep("intro");
        introStep.setTitle("Welcome to ResearchKit");

        OrderedTask task = new OrderedTask("task", introStep);

        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, ViewTaskActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
