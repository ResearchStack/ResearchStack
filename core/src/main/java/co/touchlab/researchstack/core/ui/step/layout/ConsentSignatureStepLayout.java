package co.touchlab.researchstack.core.ui.step.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.ConsentSignatureStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;
import co.touchlab.researchstack.core.ui.callbacks.SignatureCallbacks;
import co.touchlab.researchstack.core.ui.views.SignatureView;
import co.touchlab.researchstack.core.utils.FormatHelper;

public class ConsentSignatureStepLayout extends RelativeLayout implements StepLayout
{
    public static final String KEY_SIGNATURE      = "ConsentSignatureScene.Signature";
    public static final String KEY_SIGNATURE_DATE = "ConsentSignatureScene.Signature.Date";

    private SignatureView      signatureView;
    private SceneCallbacks     callbacks;
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
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;

        initializeScene();
    }

    private void initializeScene()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.scene_consent_signature, this, true);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(step.getTitle());

        TextView text = (TextView) findViewById(R.id.text);
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

        clear.setClickable(signatureView.isSignatureDrawn());
        clear.setAlpha(signatureView.isSignatureDrawn() ? 1 : 0);

        RxView.clicks(clear).subscribe(v -> {
            signatureView.clearSignature();
        });

        View next = findViewById(R.id.next);
        RxView.clicks(next).subscribe(v -> {
            if(signatureView.isSignatureDrawn())
            {
                setDataToResult();
                callbacks.onSaveStep(SceneCallbacks.ACTION_NEXT, step, result);
            }
            else
            {
                Toast.makeText(getContext(), "Signature Invalid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataToResult()
    {
        String format =  ((ConsentSignatureStep) step).getSignatureDateFormat();
        DateFormat signatureDateFormat = ! TextUtils.isEmpty(format) ?
                new SimpleDateFormat(format) : FormatHelper.getSignatureFormat();
        String formattedSignDate = signatureDateFormat.format(new Date());

        result.setResultForIdentifier(KEY_SIGNATURE, getBase64EncodedImage());
        result.setResultForIdentifier(KEY_SIGNATURE_DATE, formattedSignDate);
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
        callbacks.onSaveStep(SceneCallbacks.ACTION_PREV, step, result);
        return false;
    }

    @Override
    public void setCallbacks(SceneCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    private String getBase64EncodedImage()
    {
        Bitmap bitmap = signatureView.createSignatureBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}