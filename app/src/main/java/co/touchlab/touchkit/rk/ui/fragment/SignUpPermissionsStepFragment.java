package co.touchlab.touchkit.rk.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.helpers.LogExt;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;
import co.touchlab.touchkit.rk.ui.MainActivity;

public class SignUpPermissionsStepFragment extends StepFragment
{

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 142;

    private AppCompatButton permissionButton;

    public SignUpPermissionsStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpPermissionsStepFragment fragment = new SignUpPermissionsStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_permissions,
                null);

        permissionButton = (AppCompatButton) root.findViewById(R.id.permission_button);
        RxView.clicks(permissionButton)
                .subscribe(view -> askForPermission());

        updatePermissions();

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        LogExt.d(getClass(),
                "Got permission result back in fragment");
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        updatePermissions();
    }

    private void askForPermission()
    {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void updatePermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
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
    protected void onNextPressed()
    {
        Intent intent = new Intent(getActivity(),
                MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
