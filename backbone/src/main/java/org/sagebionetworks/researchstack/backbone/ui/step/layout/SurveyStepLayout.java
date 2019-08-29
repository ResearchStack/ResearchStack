package org.sagebionetworks.researchstack.backbone.ui.step.layout;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.sagebionetworks.researchstack.backbone.R;
import org.sagebionetworks.researchstack.backbone.ResourcePathManager;
import org.sagebionetworks.researchstack.backbone.result.StepResult;
import org.sagebionetworks.researchstack.backbone.step.QuestionStep;
import org.sagebionetworks.researchstack.backbone.step.Step;
import org.sagebionetworks.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.sagebionetworks.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.sagebionetworks.researchstack.backbone.ui.step.body.BodyAnswer;
import org.sagebionetworks.researchstack.backbone.ui.step.body.StepBody;
import org.sagebionetworks.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.sagebionetworks.researchstack.backbone.ui.views.SubmitBar;
import org.sagebionetworks.researchstack.backbone.utils.LogExt;
import org.sagebionetworks.researchstack.backbone.utils.TextUtils;

import java.lang.reflect.Constructor;

public class SurveyStepLayout extends FixedSubmitBarLayout implements StepLayout
{
    public static final String TAG = SurveyStepLayout.class.getSimpleName();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Data used to initializeLayout and return
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private QuestionStep questionStep;
    protected StepResult stepResult;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Communicate w/ host
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    protected StepCallbacks callbacks;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Child Views
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private LinearLayout container;
    protected StepBody     stepBody;

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
     * @return a boolean indication whether the back event is consumed
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

    @Override
    public int getContentResourceId()
    {
        return R.layout.rsb_step_layout;
    }

    public void initializeStep()
    {
        initStepLayout();
        initStepBody();
    }

    public void initStepLayout()
    {
        LogExt.i(getClass(), "initStepLayout()");

        container = (LinearLayout) findViewById(R.id.rsb_survey_content_container);
        TextView title = (TextView) findViewById(R.id.rsb_survey_title);
        TextView summary = (TextView) findViewById(R.id.rsb_survey_text);
        SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
        submitBar.setPositiveAction(v -> onNextClicked());

        if(questionStep != null)
        {
            setupTitleLayout(getContext(), questionStep, title, summary);

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

    @NonNull
    @MainThread
    // Protected and static so that FormStepLayout can access this method
    protected static void setupTitleLayout(Context context, QuestionStep questionStep, TextView title, TextView summary) {
        if(! TextUtils.isEmpty(questionStep.getTitle()))
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
                    String path = ResourcePathManager.getInstance().
                            generateAbsolutePath(ResourcePathManager.Resource.TYPE_HTML, url);
                    Intent intent = ViewWebDocumentActivity.newIntentForPath(context,
                                                                             questionStep.getTitle(),
                                                                             path);
                    context.startActivity(intent);
                }
            });
        }
    }

    public void initStepBody()
    {
        LogExt.i(getClass(), "initStepBody()");

        stepBody = createStepBody(questionStep, stepResult);
        View body = stepBody.getBodyView(StepBody.VIEW_TYPE_DEFAULT, layoutInflater, this);
        replaceStepBodyView(container, body);
    }

    @NonNull
    @MainThread
    // Protected and static so that FormStepLayout can access this method
    protected static void replaceStepBodyView(LinearLayout container, View body) {
        if(body != null)
        {
            View oldView = container.findViewById(R.id.rsb_survey_step_body);
            int bodyIndex = container.indexOfChild(oldView);
            container.removeView(oldView);
            container.addView(body, bodyIndex);
            body.setId(R.id.rsb_survey_step_body);
        }
    }

    @NonNull
    @MainThread
    // Protected and static so that FormStepLayout can access this method
    protected static StepBody createStepBody(QuestionStep questionStep, StepResult result)
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

    @Override
    public Parcelable onSaveInstanceState()
    {
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, getStep(), stepBody.getStepResult(false));
        return super.onSaveInstanceState();
    }

    protected void onNextClicked()
    {
        BodyAnswer bodyAnswer = stepBody.getBodyAnswerState();

        if(bodyAnswer == null || ! bodyAnswer.isValid())
        {
            Toast.makeText(getContext(),
                           bodyAnswer == null
                                   ? BodyAnswer.INVALID.getString(getContext())
                                   : bodyAnswer.getString(getContext()),
                           Toast.LENGTH_SHORT).show();
        }
        else
        {
            onComplete();
        }
    }

    protected void onComplete() {
        stepResult = stepBody.getStepResult(false);
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, getStep(), stepResult);
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