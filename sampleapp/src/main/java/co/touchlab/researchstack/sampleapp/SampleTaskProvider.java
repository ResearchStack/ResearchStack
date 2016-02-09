package co.touchlab.researchstack.sampleapp;
import android.content.Context;

import java.util.HashMap;

import co.touchlab.researchstack.backbone.task.Task;
import co.touchlab.researchstack.skin.TaskProvider;
import co.touchlab.researchstack.skin.task.ConsentTask;
import co.touchlab.researchstack.skin.task.InitialTask;
import co.touchlab.researchstack.skin.task.SignInTask;
import co.touchlab.researchstack.skin.task.SignUpTask;

public class SampleTaskProvider extends TaskProvider
{
    private HashMap<String, Task> map = new HashMap<>();

    public SampleTaskProvider(Context context)
    {
        put(TASK_ID_INITIAL, new InitialTask(TASK_ID_INITIAL));
        put(TASK_ID_CONSENT, new ConsentTask(context, TASK_ID_CONSENT));
        put(TASK_ID_SIGN_IN, new SignInTask());
        put(TASK_ID_SIGN_UP, new SignUpTask());
    }

    @Override
    public Task get(String taskId)
    {
        return map.get(taskId);
    }

    @Override
    public void put(String id, Task task)
    {
        map.put(id, task);
    }
}
