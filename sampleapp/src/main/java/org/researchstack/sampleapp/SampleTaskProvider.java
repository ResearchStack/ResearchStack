package org.researchstack.sampleapp;
import android.content.Context;

import org.researchstack.backbone.task.Task;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.task.ConsentTask;
import org.researchstack.skin.task.SignInTask;
import org.researchstack.skin.task.SignUpTask;

import java.util.HashMap;

public class SampleTaskProvider extends TaskProvider
{
    private HashMap<String, Task> map = new HashMap<>();

    public SampleTaskProvider(Context context)
    {
        put(TASK_ID_INITIAL, new SampleInitialTask(TASK_ID_INITIAL));
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
