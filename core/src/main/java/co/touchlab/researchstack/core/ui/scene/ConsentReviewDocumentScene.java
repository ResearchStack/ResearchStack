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
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;

/**
 * Implement saved state for the following objects:
 * {@link #step}
 * {@link #stepResult}
 * {@link #confirmationDialogBody}
 */
public class ConsentReviewDocumentScene extends RelativeLayout implements Scene<Boolean>
{
    public static final String STEP_ID = "consent_review_doc";

    private WebView pdfView;
    private SceneCallbacks callbacks;
    private Step step;
    private StepResult<Boolean> stepResult;
    private String confirmationDialogBody;

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
    public void initialize(Step step, StepResult<Boolean> result)
    {
        this.step = step;
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
                R.layout.layout_section_consent_review_document, this, true);

        pdfView = (WebView) findViewById(R.id.webview);

        View agree = findViewById(R.id.agree);
        RxView.clicks(agree).subscribe(v -> showDialog());

        View disagree = findViewById(R.id.disagree);
        RxView.clicks(disagree).subscribe(v -> getCallbacks().onCancelStep());
    }

    private void showDialog()
    {
        new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setTitle(R.string.consent_review_alert_title)
                .setMessage(confirmationDialogBody)
                .setCancelable(false)
                .setPositiveButton(R.string.agree, (dialog, which) -> {
                    stepResult.setResultForIdentifier(StepResult.DEFAULT_KEY, true);
                    callbacks.notifyStepResultChanged(step, stepResult);
                    callbacks.onNextStep(step);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    stepResult.setResultForIdentifier(StepResult.DEFAULT_KEY, false);
                    callbacks.notifyStepResultChanged(step, stepResult);
                })
                .show();
    }

    public void displayHTML(String html)
    {
        pdfView.loadData(html, "text/html; charset=UTF-8", null);
    }

    public void setConfirmationDialogBody(String confirmationDialogBody)
    {
        this.confirmationDialogBody = confirmationDialogBody;
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
    public void setStepResult(StepResult<Boolean> result)
    {
        stepResult = result;
    }

    @Override
    public StepResult<Boolean> getStepResult()
    {
        return stepResult;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    @Override
    public SceneCallbacks getCallbacks()
    {
        return callbacks;
    }
}
