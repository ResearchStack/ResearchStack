package co.touchlab.researchstack.core.ui;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.storage.file.auth.AuthDataAccess;
import co.touchlab.researchstack.core.storage.file.auth.AuthFileAccessListener;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.core.storage.file.auth.PassCodeDialog;

public class PassCodeActivity extends AppCompatActivity
{

    AuthFileAccessListener fileAccessListener = new AuthFileAccessListener<PinCodeConfig>()
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

        @Override
        public void dataAuth(PinCodeConfig config)
        {
            onDataAuth(config);
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

        if(StorageManager.getFileAccess() instanceof AuthDataAccess)
        {
            LogExt.i(getClass(), "logAccessTime()");
            ((AuthDataAccess) StorageManager.getFileAccess()).logAccessTime();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        initFileAccess();

        //        if(StorageManager.getFileAccess() instanceof AuthDataAccess)
        //        {
        //            LogExt.i(getClass(), "checkAutoLock()");
//            ((AuthDataAccess) StorageManager.getFileAccess())
//                    .checkAutoLock(this);
//        }
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

    private void initFileAccess()
    {
        LogExt.i(getClass(), "initFileAccess()");
        FileAccess fileAccess = StorageManager.getFileAccess();
        fileAccessRegister();
        fileAccess.initFileAccess(this);
    }

    protected void onDataReady()
    {
        LogExt.i(getClass(), "onDataReady()");
        fileAccessUnregister();
    }

    protected void onDataFailed()
    {
        LogExt.e(getClass(), "onDataFailed()");
        fileAccessUnregister();
    }

    protected void onDataAuth(PinCodeConfig config)
    {
        LogExt.e(getClass(), "onDataAuth()");

        if(StorageManager.getFileAccess() instanceof AuthDataAccess &&
                !((AuthDataAccess)StorageManager.getFileAccess()).hasPinCode(this))
        {
            PassCodeDialog dialog = new PassCodeDialog(this, config, R.style.Core_Dialog);
            dialog.setTitle("Enter your passphrase");
            dialog.setAuthAction((pin) -> ((AuthDataAccess) StorageManager.getFileAccess()).authenticate(
                    this,
                    pin));
            dialog.setFailAction((e) -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT)
                    .show());
            dialog.show();
        }
        else
        {
            onDataReady();
        }
    }
}
