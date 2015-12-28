package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.ConsentSignatureStep;
import co.touchlab.researchstack.core.ui.callbacks.SignatureCallbacks;
import co.touchlab.researchstack.core.ui.views.ConsentReviewSignatureView;
import co.touchlab.researchstack.core.utils.FormatUtils;

public class ConsentReviewSignatureScene extends SceneImpl<String>
{
    public static final String KEY_SIGNATURE = "ConsentReviewSignatureScene.Signature";
    public static final String KEY_SIGNATURE_DATE = "ConsentReviewSignatureScene.Signature.Date";

    private ConsentReviewSignatureView signatureView;

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
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate( R.layout.scene_consent_signature, parent, false);
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
    public boolean isAnswerValid()
    {
        return signatureView.isSignatureDrawn();
    }

    @Override
    public StepResult<String> getStepResult()
    {
        ConsentSignatureStep step = (ConsentSignatureStep) getStep();
        String format =  step.getSignatureDateFormat();
        String formattedSignDate;

        if (!TextUtils.isEmpty(format)) {
            formattedSignDate = SimpleDateFormat.getDateInstance()
                                                .format(new Date());
        } else {
            formattedSignDate = FormatUtils.formatSignature(new Date());
        }

        StepResult<String> stepResult = super.getStepResult();
        stepResult.setResultForIdentifier(KEY_SIGNATURE, getBase64EncodedImage());
        stepResult.setResultForIdentifier(KEY_SIGNATURE_DATE, formattedSignDate);
        return stepResult;
    }

    public String getBase64EncodedImage()
    {
        Bitmap bitmap = signatureView.createSignatureBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
