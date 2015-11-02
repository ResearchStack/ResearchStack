package co.touchlab.touchkit.rk.ui.views;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.joanzapata.pdfview.PDFView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.ui.callbacks.ConsentReviewCallback;

public class ConsentReviewDocumentLayout extends FrameLayout
{

    private ConsentReviewCallback callback;

    public ConsentReviewDocumentLayout(Context context)
    {
        super(context);
        init();
    }

    public ConsentReviewDocumentLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ConsentReviewDocumentLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_section_consent_review_document, this, true);

        PDFView pdfView = (PDFView) findViewById(R.id.pdfview);
        //TODO Point pdf to App-delegate
        pdfView.fromAsset("study_overview_consent_form.pdf").load();

        View agree = findViewById(R.id.agree);
        RxView.clicks(agree).subscribe(v -> callback.showConfirmationDialog());

        View disagree = findViewById(R.id.disagree);
        RxView.clicks(disagree).subscribe(v -> callback.closeToWelcomeFlow());
    }

    public void setCallback(ConsentReviewCallback callback)
    {
        this.callback = callback;
    }

}
