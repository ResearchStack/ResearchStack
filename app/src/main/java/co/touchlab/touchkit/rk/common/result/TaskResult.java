package co.touchlab.touchkit.rk.common.result;

import android.net.Uri;

import java.util.UUID;

public class TaskResult extends CollectionResult
{

    private UUID uuidTask;
    private Uri  outputDirectory;

    public TaskResult(String identifier, UUID uuidTask, Uri outputDirectory)
    {
        super(identifier);
        this.uuidTask = uuidTask;
        this.outputDirectory = outputDirectory;
    }

    public UUID getUuidTask()
    {
        return uuidTask;
    }

    public Uri getOutputDirectory()
    {
        return outputDirectory;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof TaskResult)
        {
            TaskResult result = (TaskResult) o;
            return getIdentifier().equals(result.getIdentifier()) &&
                    uuidTask.equals(result.getUuidTask()) &&
                    outputDirectory.equals(result.getOutputDirectory());

        }
        return false;
    }
}
