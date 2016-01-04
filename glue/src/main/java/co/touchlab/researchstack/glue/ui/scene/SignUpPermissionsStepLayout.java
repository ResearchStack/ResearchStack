package co.touchlab.researchstack.glue.ui.scene;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.ui.callbacks.ActivityCallback;
import co.touchlab.researchstack.core.ui.step.layout.StepLayoutImpl;
import co.touchlab.researchstack.glue.R;

public class SignUpPermissionsStepLayout extends StepLayoutImpl
{

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 142;

    private AppCompatButton  permissionButton;
    private ActivityCallback permissionCallback;

    public SignUpPermissionsStepLayout(Context context)
    {
        super(context);
    }

    public SignUpPermissionsStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpPermissionsStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initializeScene()
    {
        //TODO Fix this, very disgusting
        if(getContext() instanceof ActivityCallback)
        {
            permissionCallback = (ActivityCallback) getContext();
        }

        super.initializeScene();
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_permissions, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        super.onBodyCreated(body);

        //TODO Handle permissions UI/Flow on NON-M devices
        permissionButton = (AppCompatButton) body.findViewById(R.id.permission_button);
        RxView.clicks(permissionButton).subscribe(view -> permissionCallback.requestPermissions());

        updatePermissions();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        LogExt.d(getClass(), "Got permission result back in fragment");
        updatePermissions();
    }

    private void updatePermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            LogExt.d(getClass(), "Permission Granted");
            permissionButton.setText(R.string.granted);
            permissionButton.setEnabled(false);
        }
        else
        {
            LogExt.d(getClass(), "Permission Denied");
            permissionButton.setText(R.string.allow);
            permissionButton.setEnabled(true);
        }
    }

}
