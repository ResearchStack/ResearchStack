package co.touchlab.researchstack.core.ui;
import android.support.v7.app.AppCompatActivity;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.storage.file.FileAccessListener;
import co.touchlab.researchstack.core.storage.file.aes.AesFileAccess;

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
        FileAccess fileAccess = StorageManager.getFileAccess();
        fileAccess.register(fileAccessListener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        ((AesFileAccess) StorageManager.getFileAccess())
                .logPauseTime();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        ((AesFileAccess) StorageManager.getFileAccess())
                .checkTimeOut(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        fileAccessUnregister();
    }

    private void fileAccessUnregister()
    {
        FileAccess fileAccess = StorageManager.getFileAccess();
        fileAccess.unregister(fileAccessListener);
    }

    protected void initFileAccess()
    {
        FileAccess fileAccess = StorageManager.getFileAccess();
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
