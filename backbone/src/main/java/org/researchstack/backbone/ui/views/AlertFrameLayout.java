package org.researchstack.backbone.ui.views;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.researchstack.backbone.R;

/**
 * Created by TheMDP on 1/16/17.
 */

public class AlertFrameLayout extends FrameLayout {
    protected AlertDialog alertDialog;
    protected ProgressDialog progressDialog;

    public AlertFrameLayout(Context context) {
        super(context);
    }

    public AlertFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlertFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AlertFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Helper method for ProfileSteps that need to make calls to the web
     * Uses default localization of "Loading..."
     */
    protected void showLoadingDialog() {
        showLoadingDialog(getContext().getString(R.string.rsb_loading_ellipses));
    }

    /**
     * Helper method for ProfileSteps that need to make calls to the web
     */
    protected void showLoadingDialog(String title) {
        hideLoadingDialog();  // just in case these are showing
        hideAlertDialog();
        progressDialog = ProgressDialog.show(getContext(), "", title);
    }

    /**
     * Helper method for ProfileSteps that need to make calls to the web
     */
    protected void hideLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Helper method for showing an error alert
     * @param message message that will be show with alert
     */
    protected void showOkAlertDialog(String message) {
        hideLoadingDialog();  // just in case these are showing
        hideAlertDialog();
        alertDialog = new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(getContext().getString(R.string.rsb_ok), null)
                .create();
        alertDialog.show();
    }

    protected void hideAlertDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
