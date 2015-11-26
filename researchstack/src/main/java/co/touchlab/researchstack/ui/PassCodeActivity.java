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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FileAccess fileAccess = ResearchStackApplication.getInstance().getFileAccess();
        fileAccess.register(fileAccessListener);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        FileAccess fileAccess = ResearchStackApplication.getInstance().getFileAccess();
        fileAccess.unregister(fileAccessListener);
    }

    protected void initFileAccess()
    {
        FileAccess fileAccess = ResearchStackApplication.getInstance().getFileAccess();
        fileAccess.initFileAccess(this);
    }

    protected void onDataReady()
    {

    }

    protected void onDataFailed()
    {

    }
}
