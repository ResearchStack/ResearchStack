package org.researchstack.foundation.components.survey.ui.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.foundation.R;
import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks;
import org.researchstack.foundation.components.common.ui.layout.StepLayout;
import org.researchstack.foundation.components.common.ui.views.SubmitBar;
import org.researchstack.foundation.components.singletons.TextViewLinkManager;
import org.researchstack.foundation.components.survey.ui.body.BodyAnswer;
import org.researchstack.foundation.components.common.ui.layout.FixedSubmitBarLayout;
import org.researchstack.foundation.components.survey.step.QuestionStep;
import org.researchstack.foundation.components.survey.ui.body.StepBody;
import org.researchstack.foundation.components.utils.LogExt;
import org.researchstack.foundation.components.utils.TextUtils;
import org.researchstack.foundation.core.models.result.StepResult;
import org.researchstack.foundation.core.models.step.Step;

import java.lang.reflect.Constructor;

public class SurveyStepLayout extends FixedSubmitBarLayout implements StepLayout {
    public static final String TAG = SurveyStepLayout.class.getSimpleName();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Data used to initializeLayout and return
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private QuestionStep questionStep;
    private StepResult stepResult;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Communicate w/ host
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private StepCallbacks callbacks;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Child Views
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private LinearLayout container;
    private StepBody stepBody;

    public SurveyStepLayout(Context context) {
        super(context);
    }

    public SurveyStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurveyStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(Step step) {
        initialize(step, null);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        if (!(step instanceof QuestionStep)) {
            throw new RuntimeException("Step being used in SurveyStep is not a QuestionStep");
        }

        this.questionStep = (QuestionStep) step;
        this.stepResult = result;

        initializeStep();
    }

    @Override
    public View getLayout() {
        return this;
    }

    /**
     * Method allowing a step to consume a back event.
     *
     * @return
     */
    @Override
    public boolean isBackEventConsumed() {
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, getStep(), stepBody.getStepResult(false));
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public int getContentResourceId() {
        return R.layout.rsf_step_layout;
    }

    public void initializeStep() {
        initStepLayout();
        initStepBody();
    }

    public void initStepLayout() {
        LogExt.i(getClass(), "initStepLayout()");

        container = (LinearLayout) findViewById(R.id.rsf_survey_content_container);
        TextView title = (TextView) findViewById(R.id.rsf_survey_title);
        TextView summary = (TextView) findViewById(R.id.rsf_survey_text);
        SubmitBar submitBar = (SubmitBar) findViewById(R.id.rsf_submit_bar);
        submitBar.setPositiveAction(v -> onNextClicked());

        if (questionStep != null) {
            if (!TextUtils.isEmpty(questionStep.getTitle())) {
                title.setVisibility(View.VISIBLE);
                title.setText(questionStep.getTitle());
            }

            if (!TextUtils.isEmpty(questionStep.getText())) {
                summary.setVisibility(View.VISIBLE);
                summary.setText(Html.fromHtml(questionStep.getText()));
                summary.setMovementMethod(
                        TextViewLinkManager.Companion.getSharedManager().getHandler(getContext(), questionStep.getTitle())
                );
            }

            if (questionStep.isOptional()) {
                submitBar.setNegativeTitle(R.string.rsf_step_skip);
                submitBar.setNegativeAction(v -> onSkipClicked());
            } else {
                submitBar.getNegativeActionView().setVisibility(View.GONE);
            }
        }
    }

    public void initStepBody() {
        LogExt.i(getClass(), "initStepBody()");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        stepBody = createStepBody(questionStep, stepResult);
        View body = stepBody.getBodyView(StepBody.VIEW_TYPE_DEFAULT, inflater, this);

        if (body != null) {
            View oldView = container.findViewById(R.id.rsf_survey_step_body);
            int bodyIndex = container.indexOfChild(oldView);
            container.removeView(oldView);
            container.addView(body, bodyIndex);
            body.setId(R.id.rsf_survey_step_body);
        }
    }

    @NonNull
    private StepBody createStepBody(QuestionStep questionStep, StepResult result) {
        try {
            Class cls = questionStep.getStepBodyClass();
            Constructor constructor = cls.getConstructor(Step.class, StepResult.class);
            return (StepBody) constructor.newInstance(questionStep, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, getStep(), stepBody.getStepResult(false));
        return super.onSaveInstanceState();
    }

    protected void onNextClicked() {
        BodyAnswer bodyAnswer = stepBody.getBodyAnswerState();

        if (bodyAnswer == null || !bodyAnswer.isValid()) {
            Toast.makeText(getContext(),
                    bodyAnswer == null
                            ? BodyAnswer.INVALID.getString(getContext())
                            : bodyAnswer.getString(getContext()),
                    Toast.LENGTH_SHORT).show();
        } else {
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                    getStep(),
                    stepBody.getStepResult(false));
        }
    }

    public void onSkipClicked() {
        if (callbacks != null) {
            // empty step result when skipped
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT,
                    getStep(),
                    stepBody.getStepResult(true));
        }
    }

    public Step getStep() {
        return questionStep;
    }

    public String getString(@StringRes int stringResId) {
        return getResources().getString(stringResId);
    }

}
