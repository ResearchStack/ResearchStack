package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.SignatureCallbacks;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.SignatureView;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.TextUtils;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsentSignatureStepLayout extends RelativeLayout implements StepLayout
{
    public static final String KEY_SIGNATURE      = "ConsentSignatureStep.Signature";
    public static final String KEY_SIGNATURE_DATE = "ConsentSignatureStep.Signature.Date";

    private SignatureView      signatureView;
    private StepCallbacks      callbacks;
    private Step               step;
    private StepResult<String> result;

    public ConsentSignatureStepLayout(Context context)
    {
        super(context);
    }

    public ConsentSignatureStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ConsentSignatureStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        this.step = step;
        this.result = result == null ? new StepResult<>(step) : result;

        initializeStep();
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    @Override
    public boolean isBackEventConsumed()
    {
        setDataToResult();
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    @Override
    public void receiveIntentExtraOnResult(int requestCode, Intent intent) {

    }

    private void initializeStep()
    {
        LayoutInflater.from(getContext())
                .inflate(R.layout.rsb_step_layout_consent_signature, this, true);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(step.getTitle());

        TextView text = (TextView) findViewById(R.id.summary);
        text.setText(step.getText());

        View clear = findViewById(R.id.layout_consent_review_signature_clear);

        signatureView = (SignatureView) findViewById(R.id.layout_consent_review_signature);
        signatureView.setCallbacks(new SignatureCallbacks()
        {
            @Override
            public void onSignatureStarted()
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
            signatureView.clearSignature();
        });

        clear.setClickable(signatureView.isSignatureDrawn());

        // view.setAlpha() is not working, this is kind of a hack around that
        clear.animate().alpha(signatureView.isSignatureDrawn() ? 1 : 0);

        SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
        submitBar.getNegativeActionView().setVisibility(View.GONE);
        submitBar.setPositiveAction(v -> {
            if(signatureView.isSignatureDrawn())
            {
                setDataToResult();
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, result);
            }
            else
            {
                Toast.makeText(getContext(), R.string.rsb_error_invalid_signature, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToResult()
    {
        String format = ((ConsentSignatureStep) step).getSignatureDateFormat();
        DateFormat signatureDateFormat = ! TextUtils.isEmpty(format)
                ? new SimpleDateFormat(format)
                : FormatHelper.getSignatureFormat();
        String formattedSignDate = signatureDateFormat.format(new Date());

        result.setResultForIdentifier(KEY_SIGNATURE, getBase64EncodedImage());
        result.setResultForIdentifier(KEY_SIGNATURE_DATE, formattedSignDate);
    }

    private String getBase64EncodedImage()
    {
        Bitmap bitmap = signatureView.createSignatureBitmap();

        if(bitmap != null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        else
        {
            return null;
        }
    }

}