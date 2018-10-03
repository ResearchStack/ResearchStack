package org.researchstack.backbone.ui.step.layout;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.PermissionRequestManager;
import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.ActivityCallback;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.ThemeUtils;

import java.util.List;

public class PermissionStepLayout extends LinearLayout implements StepLayout, StepPermissionRequest
{
    private Step step;
    private StepResult<Boolean>     result;
    private StepCallbacks           callbacks;
    private ActivityCallback        permissionCallback;

    private SubmitBar  submitBar;

    public PermissionStepLayout(Context context)
    {
        this(context, null);
    }

    public PermissionStepLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PermissionStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if(getContext() instanceof ActivityCallback)
        {
            permissionCallback = (ActivityCallback) getContext();
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        permissionCallback = null;
        callbacks = null;
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        this.result = result == null ? new StepResult<>(step) : result;

        initializeStep();
    }

    public void initializeStep()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Inflate step UI
        inflater.inflate(R.layout.rsb_layout_permission, this, true);

        // Add Sub-items to our ScrollView
        LinearLayout permissionContainer = (LinearLayout) findViewById(R.id.rsb_container_permission_items);

        List<PermissionRequestManager.PermissionRequest> items = PermissionRequestManager.getInstance()
                .getPermissionRequests();

        for(PermissionRequestManager.PermissionRequest item : items)
        {
            boolean isGranted = PermissionRequestManager.getInstance().hasPermission(getContext(), item.getId());

            View child = inflater.inflate(R.layout.rsb_item_permission_content,
                    permissionContainer,
                    false);

            // Set tag to update action-state in the future
            child.setTag(item.getId());

            // Set Icon w/ accentTint
            Drawable icon = ContextCompat.getDrawable(getContext(), item.getIcon());
            icon = DrawableCompat.wrap(icon);
            DrawableCompat.setTint(icon, ThemeUtils.getAccentColor(getContext()));
            ((ImageView) child.findViewById(R.id.rsb_permission_icon)).setImageDrawable(icon);

            // Set title
            ((TextView) child.findViewById(R.id.rsb_permission_title)).setText(item.getTitle());

            // Set details
            ((TextView) child.findViewById(R.id.rsb_permission_details)).setText(item.getText());

            // Text action
            TextView action = (TextView) child.findViewById(R.id.rsb_permission_button);
            action.setText(isGranted
                    ? R.string.rsb_granted
                    : item.isBlockingPermission() ? R.string.rsb_allow : R.string.rsb_optional);
            RxView.clicks(action).subscribe(o -> {
                permissionCallback.onRequestPermission(item.getId());
            });
            action.setEnabled(!isGranted);

            permissionContainer.addView(child);
        }

        // Set submit bar behavior
        submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.setPositiveTitle(R.string.rsb_next);
        submitBar.setPositiveAction(v -> {
            if (isAnswerValid())
            {
                onNext(true);
            }
        });
        submitBar.getNegativeActionView().setVisibility(GONE);
    }

    @Override
    public void onUpdateForPermissionResult()
    {
        updatePermissionItems();
    }

    private void updatePermissionItems()
    {
        List<PermissionRequestManager.PermissionRequest> items = PermissionRequestManager.getInstance()
                .getPermissionRequests();

        for(PermissionRequestManager.PermissionRequest item : items)
        {
            boolean isGranted = PermissionRequestManager.getInstance().hasPermission(getContext(), item.getId());

            View parent = findViewWithTag(item.getId());

            TextView action = (TextView) parent.findViewById(R.id.rsb_permission_button);
            action.setText(isGranted
                    ? R.string.rsb_granted
                    : item.isBlockingPermission() ? R.string.rsb_allow : R.string.rsb_optional);
            action.setEnabled(!isGranted);
        }
    }

    private void onNext(boolean answerCorrect)
    {
        // Save the result and go to the next question
        result.setResult(answerCorrect);
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
    }

    public boolean isAnswerValid()
    {
        List<PermissionRequestManager.PermissionRequest> items = PermissionRequestManager.getInstance()
                .getPermissionRequests();

        for(PermissionRequestManager.PermissionRequest item : items)
        {
            boolean isGranted = PermissionRequestManager.getInstance().hasPermission(getContext(), item.getId());

            if (!isGranted && item.isBlockingPermission())
            {
                String permissionName = getResources().getString(item.getTitle());
                String formattedError = getResources().getString(
                        R.string.rsb_permission_continue_invalid, permissionName.toLowerCase());
                Toast.makeText(getContext(), formattedError, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

}
