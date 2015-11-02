package co.touchlab.touchkit.rk.ui.views;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;

public class ConsentReviewSignatureLayout extends StepLayout
{

    public ConsentReviewSignatureLayout(Context context)
    {
        super(context);
    }

    public ConsentReviewSignatureLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentReviewSignatureLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutResourceIdToAttachToRoot()
    {
        return R.layout.layout_section_consent_review_signature;
    }

    @Override
    protected void onLayoutAttachedToRoot()
    {
        View clear = findViewById(R.id.layout_consent_review_signature_clear);
        clear.setAlpha(0);
        clear.setClickable(false);

        ConsentReviewSignatureView pdfView = (ConsentReviewSignatureView) findViewById(R.id.layout_consent_review_signature);
        pdfView.setCallbacks(new SignatureCallbacks()
        {
            @Override
            public void onSignatureDrawn()
            {
                clear.setClickable(true);
                clear.animate().alpha(1);
            }

            @Override
            public void onSignatureCleared()
            {
                clear.setClickable(false);
                clear.animate().alpha(0);
            }
        });

        RxView.clicks(clear).subscribe(v -> {
            pdfView.clearSignature();
        });
    }

    @Override
    protected View getBodyView(LayoutInflater inflater, LinearLayout parent)
    {
        return null;
    }


    @Override
    protected StepResult getResult()
    {
        return null;
    }
}
