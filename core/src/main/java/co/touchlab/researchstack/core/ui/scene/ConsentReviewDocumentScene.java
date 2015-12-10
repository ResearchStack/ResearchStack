package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
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

public class ConsentReviewDocumentScene extends RelativeLayout implements Scene<Boolean>
{
    public static final String STEP_ID = "consent_review_doc";

    private WebView pdfView;
    private SceneCallbacks callbacks;
    private Step step = new Step(STEP_ID);
    private StepResult<Boolean> stepResult = new StepResult<>(step.getIdentifier());

    public ConsentReviewDocumentScene(Context context)
    {
        super(context);
        init(context);
    }

    public ConsentReviewDocumentScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public ConsentReviewDocumentScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater.from(context).inflate(
                R.layout.layout_section_consent_review_document, this, true);

        pdfView = (WebView) findViewById(R.id.webview);

        View agree = findViewById(R.id.agree);
        RxView.clicks(agree).subscribe(v -> getCallbacks().onNextStep(step));

        View disagree = findViewById(R.id.disagree);
        RxView.clicks(disagree).subscribe(v -> getCallbacks().onCancelStep());
    }

    public void setHTML(String html)
    {
        pdfView.loadData(html, "text/html; charset=UTF-8", null);
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
