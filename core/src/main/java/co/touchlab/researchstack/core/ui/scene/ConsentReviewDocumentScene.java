package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.ConsentReviewDocumentStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;

/**
 * Implement saved state for the following objects:
 * {@link #step}
 * {@link #stepResult}
 * {@link #confirmationDialogBody}
 */
public class ConsentReviewDocumentScene extends RelativeLayout implements Scene
{
    private SceneCallbacks callbacks;

    private String confirmationDialogBody;
    private String htmlContent;

    private ConsentReviewDocumentStep step;
    private StepResult<Boolean> stepResult;

    public ConsentReviewDocumentScene(Context context)
    {
        super(context);
    }

    public ConsentReviewDocumentScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentReviewDocumentScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = (ConsentReviewDocumentStep) step;
        this.confirmationDialogBody = ((ConsentReviewDocumentStep) step).getConfirmMessage();
        this.htmlContent = ((ConsentReviewDocumentStep) step).getConsentHTML();
        this.stepResult = result;

        if(stepResult == null)
        {
            stepResult = new StepResult<>(step.getIdentifier());
        }

        initializeScene();
    }

    public void initializeScene()
    {
        LayoutInflater.from(getContext()).inflate(
                R.layout.scene_consent_doc, this, true);

        WebView pdfView = (WebView) findViewById(R.id.webview);
        pdfView.loadData(htmlContent, "text/html; charset=UTF-8", null);

        View agree = findViewById(R.id.agree);
        RxView.clicks(agree).subscribe(v -> showDialog());

        View disagree = findViewById(R.id.disagree);
        // TODO make this call onSaveStep with false result
        RxView.clicks(disagree).subscribe(v -> callbacks.onCancelStep());
    }

    private void showDialog()
    {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.consent_review_alert_title)
                .setMessage(confirmationDialogBody)
                .setCancelable(false)
                .setPositiveButton(R.string.agree, (dialog, which) -> {
                    stepResult.setResult(true);
                    callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, stepResult);
                })
                .setNegativeButton(R.string.consent_review_cancel, (dialog, which) -> {
                    // Gives them a chance to read it again
                })
                .show();
    }

    @Override
    public View getView()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        return false;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }
}
