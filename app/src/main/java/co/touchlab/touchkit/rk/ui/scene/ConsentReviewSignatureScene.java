package co.touchlab.touchkit.rk.ui.scene;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.ui.views.ConsentReviewSignatureView;
import co.touchlab.touchkit.rk.ui.views.SignatureCallbacks;

public class ConsentReviewSignatureScene extends Scene
{

    public ConsentReviewSignatureScene(Context context)
    {
        super(context);
    }

    public ConsentReviewSignatureScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentReviewSignatureScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getBodyLayoutResourceId()
    {
        return R.layout.scene_consent_review_signature;
    }

    @Override
    public void onBodyCreated(View body)
    {
        View clear = body.findViewById(R.id.layout_consent_review_signature_clear);

        ConsentReviewSignatureView signatureView = (ConsentReviewSignatureView) body.findViewById(R.id.layout_consent_review_signature);
        signatureView.setCallbacks(new SignatureCallbacks()
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

        clear.setAlpha(signatureView.isSignatureDrawn() ? 1 : 0);
        clear.setClickable(signatureView.isSignatureDrawn());

        RxView.clicks(clear).subscribe(v -> {
            signatureView.clearSignature();
        });
    }

    @Override
    protected StepResult getResult()
    {
        return null;
    }
}
