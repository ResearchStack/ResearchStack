package co.touchlab.touchkit.rk.ui.scene;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.helpers.LogExt;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.ui.MainActivity;
import co.touchlab.touchkit.rk.ui.callbacks.ActivityCallback;

public class SignUpPermissionsScene extends Scene
{

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 142;

    private AppCompatButton permissionButton;
    private ActivityCallback permissionCallback;

    public SignUpPermissionsScene(Context context, Step step)
    {
        super(context, step);

        //TODO Fix this, very disgusting
        if (context instanceof ActivityCallback)
        {
            permissionCallback = (ActivityCallback) context;
        }
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
        RxView.clicks(permissionButton)
                .subscribe(view -> permissionCallback.requestPermissions());

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

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }

    @Override
    public void onNextClicked()
    {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getContext().startActivity(intent);
    }
}
