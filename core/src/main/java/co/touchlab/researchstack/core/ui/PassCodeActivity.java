package co.touchlab.researchstack.core.ui;
import android.support.v7.app.AppCompatActivity;

import co.touchlab.researchstack.core.ResearchStackCoreApplication;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.storage.file.FileAccessListener;

public class PassCodeActivity extends AppCompatActivity
{

    FileAccessListener fileAccessListener = new FileAccessListener()
    {
        @Override
        public void dataReady()
        {
            onDataReady();
        }

        @Override
        public void dataAccessError()
        {
            onDataFailed();
        }
    };

    private void fileAccessRegister()
    {
        FileAccess fileAccess = ResearchStackCoreApplication.getInstance().getFileAccess();
        fileAccess.register(fileAccessListener);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        fileAccessUnregister();
    }

    private void fileAccessUnregister()
    {
        FileAccess fileAccess = ResearchStackCoreApplication.getInstance().getFileAccess();
        fileAccess.unregister(fileAccessListener);
    }

    protected void initFileAccess()
    {
        FileAccess fileAccess = ResearchStackCoreApplication.getInstance().getFileAccess();
        fileAccessRegister();
        fileAccess.initFileAccess(this);
    }

    protected void onDataReady()
    {
        fileAccessUnregister();
    }

    protected void onDataFailed()
    {
        fileAccessUnregister();
    }
}
