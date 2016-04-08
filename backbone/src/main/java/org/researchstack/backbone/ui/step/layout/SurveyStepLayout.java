package org.researchstack.backbone.ui.step.layout;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.R;
import org.researchstack.backbone.helpers.LogExt;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.ui.views.SubmitBar;

import java.lang.reflect.Constructor;

public class SurveyStepLayout extends RelativeLayout implements StepLayout
{
    public static final String TAG = SurveyStepLayout.class.getSimpleName();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Data used to initializeLayout and return
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private QuestionStep questionStep;
    private StepResult   stepResult;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Communicate w/ host
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private StepCallbacks callbacks;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Child Views
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private LinearLayout container;
    private TextView     title;
    private TextView     summary;
    private SubmitBar    submitBar;
    private StepBody     stepBody;

    public SurveyStepLayout(Context context)
    {
        super(context);
    }

    public SurveyStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SurveyStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(Step step)
    {
        initialize(step, null);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        if(! (step instanceof QuestionStep))
        {
            throw new RuntimeException("Step being used in SurveyStep is not a QuestionStep");
        }

        this.questionStep = (QuestionStep) step;
        this.stepResult = result;

        initializeStep();
    }

    @Override
    public View getLayout()
    {
        return this;
    }

    /**
     * Method allowing a step to consume a back event.
     *
     * @return
     */
    @Override
    public boolean isBackEventConsumed()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, getStep(), stepBody.getStepResult(false));
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    public void initializeStep()
    {
        LogExt.i(getClass(), "initializeLayout()");

        if(getContext() instanceof StepCallbacks)
        {
            setCallbacks((StepCallbacks) getContext());
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View layout = onCreateLayout(inflater, this);
        onLayoutCreated(layout);

        View body = onCreateBody(inflater, this);
        onBodyCreated(body);
    }

    public View onCreateLayout(LayoutInflater inflater, ViewGroup parent)
    {
        LogExt.i(getClass(), "onCreateLayout()");
        return inflater.inflate(getRootLayoutResourceId(), parent, true);
    }

    public void onLayoutCreated(View layout)
    {
        LogExt.i(getClass(), "onLayoutCreated()");

        View filler = findViewById(R.id.filler);

        container = (LinearLayout) findViewById(R.id.content_container);
        container.addOnLayoutChangeListener(new OnLayoutChangeListener()
        {
            boolean isChangeFromFiller = false;

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                LogExt.i("SurveyStepLayout", "LAYOUT CHANGE");
                int stepLayoutHeight = SurveyStepLayout.this.getHeight();
                int contentHeightSansFiller = container.getHeight() - filler.getHeight();

                // Make sure we have layout and height to measure
                if(stepLayoutHeight == 0 || contentHeightSansFiller == 0)
                {
                    return;
                }

                // if the last call to this resulted in filler changing size, ignore
                if(isChangeFromFiller)
                {
                    isChangeFromFiller = false;
                    return;
                }

                // If our content does not take up the entire height of the screen, increase height
                // of the filler space
                if(contentHeightSansFiller < stepLayoutHeight)
                {
                    filler.post(() -> {
                        ViewGroup.LayoutParams params = filler.getLayoutParams();
                        params.height = stepLayoutHeight - contentHeightSansFiller;
                        filler.setLayoutParams(params);

                        isChangeFromFiller = true;
                    });
                }
                else if (contentHeightSansFiller > stepLayoutHeight && filler.getHeight() != 0)
                {
                    filler.post(() -> {
                        ViewGroup.LayoutParams params = filler.getLayoutParams();
                        params.height = 0;
                        filler.setLayoutParams(params);

                        isChangeFromFiller = true;
                    });
                }
            }
        });

        title = (TextView) findViewById(R.id.title);
        summary = (TextView) findViewById(R.id.text);
        submitBar = (SubmitBar) findViewById(R.id.submit_bar);

        submitBar.setPositiveAction(v -> onNextClicked());

        if(questionStep != null)
        {
            if (! TextUtils.isEmpty(questionStep.getTitle()))
            {
                title.setVisibility(View.VISIBLE);
                title.setText(questionStep.getTitle());
            }

            if(! TextUtils.isEmpty(questionStep.getText()))
            {
                summary.setVisibility(View.VISIBLE);
                summary.setText(Html.fromHtml(questionStep.getText()));
                summary.setMovementMethod(new TextViewLinkHandler()
                {
                    @Override
                    public void onLinkClick(String url)
                    {
                        Intent intent = ViewWebDocumentActivity.newIntent(getContext(),
                                questionStep.getTitle(),
                                url);
                        getContext().startActivity(intent);
                    }
                });
            }

            if(questionStep.isOptional())
            {
                submitBar.setNegativeTitle(R.string.rsb_step_skip);
                submitBar.setNegativeAction(v -> onSkipClicked());
            }
            else
            {
                submitBar.getNegativeActionView().setVisibility(View.GONE);
            }
        }
    }

    protected int getRootLayoutResourceId()
    {
        return R.layout.step_layout;
    }


    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        LogExt.i(getClass(), "onCreateBody()");

        stepBody = createStepBody(questionStep, stepResult);
        View body = stepBody.getBodyView(StepBody.VIEW_TYPE_DEFAULT, inflater, parent);

        if(body != null)
        {
            View oldView = container.findViewById(R.id.step_body);
            int bodyIndex = container.indexOfChild(oldView);
            container.removeView(oldView);
            container.addView(body, bodyIndex);
            body.setId(R.id.step_body);
        }

        return body;
    }

    @NonNull
    private StepBody createStepBody(QuestionStep questionStep, StepResult result)
    {
        try
        {
            Class cls = questionStep.getStepBodyClass();
            Constructor constructor = cls.getConstructor(Step.class, StepResult.class);
            return (StepBody) constructor.newInstance(questionStep, result);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void onBodyCreated(View body)
    {
        if(body != null)
        {
            LogExt.i(getClass(), "onBodyCreated()");
        }
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, getStep(), stepBody.getStepResult(false));
        return super.onSaveInstanceState();
    }

    protected void onNextClicked()
    {
        BodyAnswer bodyAnswer = stepBody.getBodyAnswerState();

        if (bodyAnswer == null || !bodyAnswer.isValid())
        {
            Toast.makeText(getContext(),
                    bodyAnswer == null ? BodyAnswer.INVALID.getReason() : bodyAnswer.getReason(),
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                    getStep(), stepBody.getStepResult(false));
        }
    }

    public void onSkipClicked()
    {
        if(callbacks != null)
        {
            // empty step result when skipped
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                    getStep(),
                    stepBody.getStepResult(true));
        }
    }

    public Step getStep()
    {
        return questionStep;
    }

    public String getString(@StringRes int stringResId)
    {
        return getResources().getString(stringResId);
    }

}
