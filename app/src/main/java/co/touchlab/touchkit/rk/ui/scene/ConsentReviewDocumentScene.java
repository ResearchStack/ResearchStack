package co.touchlab.touchkit.rk.ui.scene;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.joanzapata.pdfview.PDFView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.ui.callbacks.ConsentReviewCallback;

public class ConsentReviewDocumentScene extends Scene
{

    private ConsentReviewCallback callback;

    public ConsentReviewDocumentScene(Context context)
    {
        super(context, null);
    }
    
    @Override
    public View onCreateScene(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.layout_section_consent_review_document, parent, true);
    }

    @Override
    public void onSceneCreated(View scene)
    {
        PDFView pdfView = (PDFView) findViewById(R.id.pdfview);
        //TODO Point pdf to App-delegate
        pdfView.fromAsset("study_overview_consent_form.pdf").load();

        View agree = findViewById(R.id.agree);
        RxView.clicks(agree).subscribe(v -> callback.showConfirmationDialog());

        View disagree = findViewById(R.id.disagree);
        RxView.clicks(disagree).subscribe(v -> callback.closeToWelcomeFlow());
    }

    @Override
    public StepResult createNewStepResult(String id)
    {
        return null;
    }

    public void setCallback(ConsentReviewCallback callback)
    {
        this.callback = callback;
    }

}
