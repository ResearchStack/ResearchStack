package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.jetbrains.annotations.NotNull;
import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.LocalizationUtils;

/**
 * Implement saved state for the following objects:
 * {@link #step}
 * {@link #stepResult}
 * {@link #confirmationDialogBody}
 */
public class ConsentDocumentStepLayout extends ConstraintLayout implements StepLayout {
    private StepCallbacks callbacks;

    private String confirmationDialogBody;
    private String htmlContent;

    private ConsentDocumentStep step;
    private StepResult<Boolean> stepResult;

    private SubmitBar submitBar;

    public ConsentDocumentStepLayout(Context context) {
        super(context);
    }

    public ConsentDocumentStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConsentDocumentStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        this.step = (ConsentDocumentStep) step;
        this.confirmationDialogBody = ((ConsentDocumentStep) step).getConfirmMessage();
        this.htmlContent = ((ConsentDocumentStep) step).getConsentHTML();
        this.stepResult = result;

        if (stepResult == null) {
            stepResult = new StepResult<>(step);
        }

        initializeStep();
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        stepResult.setResult(false);
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, stepResult);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void setCancelEditMode(boolean isCancelEdit) {
        // no-op: Only needed when the user is on edit mode inside regular steps
    }

    @Override
    public void setRemoveFromBackStack(boolean removeFromBackStack) {
        // no-op: Only needed when the user is on edit mode inside regular steps
    }

    @Override
    public void isEditView(boolean isEditView) {
        // no-op: Only needed when the user is on edit mode inside regular steps
    }

    @Override
    public void setStepResultTo(@NotNull StepResult originalResult) {
        // no-op: Only needed when the user is on edit mode inside regular steps
    }

    @Override
    public StepResult getStepResult() {
        return stepResult;
    }

    private void initializeStep() {
        LayoutInflater.from(getContext()).inflate(R.layout.rsb_step_layout_consent_doc, this, true);

        WebView pdfView = findViewById(R.id.webview);

        pdfView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                submitBar = findViewById(R.id.submit_bar);
                setSubmitBarButtonsActions();
                submitBar.setPositiveTitleColor(step.getColorSecondary());
                submitBar.setNegativeTitleColor(step.getPrimaryColor());
                submitBar.setNegativeTitle(LocalizationUtils.getLocalizedString(getContext(),R.string.rsb_disagree));
                submitBar.setPositiveTitle(LocalizationUtils.getLocalizedString(getContext(),R.string.rsb_agree));
            }
        });

        pdfView.loadData(htmlContent, "text/html; charset=UTF-8", null);

    }

    private void setSubmitBarButtonsActions() {
        submitBar.setPositiveAction(actionView -> submitBarOnClick());
        submitBar.setNegativeAction(actionView -> disagreeConsent());
    }

    private void submitBarOnClick() {
        submitBar.clearActions();
        showDialog((dialog, which) -> {
            stepResult.setResult(true);
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, stepResult);
            submitBar.clearActions();
        });
    }

    private void disagreeConsent() {
        stepResult.setResult(false);
        callbacks.onSaveStep(StepCallbacks.ACTION_END, step, stepResult);
    }

    private void showDialog(MaterialDialog.SingleButtonCallback positiveAction) {
        new MaterialDialog.Builder(getContext())
                .title(LocalizationUtils.getLocalizedString(getContext(),R.string.rsb_consent_review_alert_title))
                .content(confirmationDialogBody)
                .theme(Theme.LIGHT)
                .cancelable(false)
                .positiveColor(step.getColorSecondary())
                .negativeColor(step.getPrimaryColor())
                .negativeText(LocalizationUtils.getLocalizedString(getContext(),R.string.rsb_consent_review_cancel))
                .positiveText(LocalizationUtils.getLocalizedString(getContext(),R.string.rsb_agree))
                .onPositive(positiveAction)
                .cancelListener(dialog -> setSubmitBarButtonsActions())
                .show();
    }
}