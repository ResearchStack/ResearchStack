package co.touchlab.researchstack.ui;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.storage.FileAccess;
import co.touchlab.researchstack.common.storage.FileAccessListener;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        FileAccess fileAccess = ResearchStackApplication.getInstance().getFileAccess();
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
        FileAccess fileAccess = ResearchStackApplication.getInstance().getFileAccess();
        fileAccess.unregister(fileAccessListener);
    }

    protected void initFileAccess()
    {
        FileAccess fileAccess = ResearchStackApplication.getInstance().getFileAccess();
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
