package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.ui.callbacks.SignatureCallbacks;
import co.touchlab.researchstack.core.ui.views.ConsentReviewSignatureView;

public class ConsentReviewSignatureScene extends SceneImpl
{

    private ConsentReviewSignatureView signatureView;

    public ConsentReviewSignatureScene(Context context)
    {
        super(context, null);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate( R.layout.scene_consent_review_signature, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        View clear = body.findViewById(R.id.layout_consent_review_signature_clear);

        signatureView = (ConsentReviewSignatureView) body.findViewById(
                R.id.layout_consent_review_signature);
        signatureView.setCallbacks(new SignatureCallbacks()
        {
            @Override
            public void onSignatureDrawn()
            {
                clear.setClickable(true);
                clear.animate()
                        .alpha(1);
            }

            @Override
            public void onSignatureCleared()
            {
                clear.setClickable(false);
                clear.animate()
                        .alpha(0);
            }
        });

        clear.setAlpha(signatureView.isSignatureDrawn() ? 1 : 0);
        clear.setClickable(signatureView.isSignatureDrawn());

        RxView.clicks(clear).subscribe(v -> {
            signatureView.clearSignature();
        });
    }

    @Override
    public StepResult createNewStepResult(String id)
    {
        return null;
    }

    public Bitmap getSignatureImage()
    {
        return signatureView.createSignatureBitmap();
    }
}
