package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.StepResultHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by TheMDP on 1/14/17.
 */

public class FormStepLayout extends FixedSubmitBarLayout implements StepLayout {
    private static final String TAG = FormStepLayout.class.getSimpleName();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Data used to initializeLayout and return
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    protected FormStep formStep;
    // subQuestionSteps will be a map with keys of all List<QuestionStep> formSteps,
    // and the values will be the StepBody's
    protected LinkedHashMap<QuestionStep, StepBody> subQuestionSteps;
    protected StepResult<StepResult> stepResult;
    protected TaskResult taskResult;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Communicate w/ host
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private StepCallbacks callbacks;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Child Views
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    protected LinearLayout container;
    protected LinearLayout stepBodyContainer;

    public FormStepLayout(Context context)
    {
        super(context);
    }

    public FormStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FormStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public FormStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void initialize(Step step)
    {
        initialize(step, null, null);
    }

    @Override
    public void initialize(Step step, StepResult result, TaskResult taskResult)
    {
        validateStep(step);  // Also sets formStep member variable

        stepResult = result;
        if (result == null) {
            stepResult = new StepResult<>(step);
        }
        this.taskResult = taskResult;
        subQuestionSteps = new LinkedHashMap<>();
        formStep = (FormStep) step;

        // Add all relevant questions steps
        List<QuestionStep> questionSteps = new ArrayList<>();
        if (step instanceof FormStep) {
            FormStep formStep = (FormStep)step;
            for (QuestionStep questionStep : formStep.getFormSteps()) {
                questionSteps.add(questionStep);
            }
        } else {  // Normal QuestionStep
            questionSteps.add((QuestionStep) step);
        }

        // Initialize the UI for title and summary, etc
        initStepLayout(formStep);
        // Fill up the step map
        for (QuestionStep subStep : questionSteps) {
            StepResult subStepResult = subQuestionResult(subStep.getIdentifier(), stepResult, taskResult);
            StepBody stepBody = SurveyStepLayout.createStepBody(subStep, subStepResult);
            subQuestionSteps.put(subStep, stepBody);
            View surveyStepView = initStepBodyHolder(layoutInflater, stepBodyContainer, subStep, stepBody);
            stepBodyContainer.addView(surveyStepView);
        }
        // refresh skip/next bar
        refreshSubmitBar();
    }

    /**
     * @param step to validate it's state
     */
    protected void validateStep(Step step) {
        if(step != null && step instanceof FormStep)
        {
            formStep = (FormStep)step;
        }
        else
        {
            throw new RuntimeException("Step being used in FormStepLayout is not a FormStep or is null");
        }
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
    public boolean isBackEventConsumed() {
        updateAllQuestionSteps(false);
        callbacks.onSaveStep(StepCallbacks.ACTION_PREV, formStep, stepResult);
        return false;
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public int getContentResourceId() {
        return R.layout.rsb_form_step_layout;
    }

    public void refreshSubmitBar() {
        submitBar.setPositiveAction(v -> onNextClicked());
        submitBar.setNegativeTitle(R.string.rsb_step_skip);
        submitBar.setNegativeAction(v -> onSkipClicked());
        for (QuestionStep step : subQuestionSteps.keySet()) {
            if(!step.isOptional())
            {
                submitBar.getNegativeActionView().setVisibility(View.GONE);
            }
        }
    }

    /**
     * @param step creates the root container for this form step layout
     */
    protected void initStepLayout(FormStep step)
    {
        LogExt.i(getClass(), "initStepLayout()");

        container = (LinearLayout) findViewById(R.id.rsb_form_step_content_container);
        stepBodyContainer = (LinearLayout) findViewById(R.id.rsb_form_step_body_layout);
        TextView title = (TextView) findViewById(R.id.rsb_form_step_title);
        TextView summary = (TextView) findViewById(R.id.rsb_form_step_summary);

        SurveyStepLayout.setupTitleLayout(getContext(), step, title, summary);
    }

    /**
     * @param layoutInflater used to create StepLayout UI for QuestionStep
     * @param stepBodyContainer container that will hold the returned View
     * @param step the question step to use for the title and summary
     * @param stepBody the step body to use for creating the step body view
     * @return StepLayout View object container StepBody View and title, and text
     */
    @MainThread
    protected static View initStepBodyHolder(LayoutInflater layoutInflater, ViewGroup stepBodyContainer, QuestionStep step, StepBody stepBody)
    {
        LogExt.i(TAG, "initStepLayout()");

        View surveyStepView = layoutInflater.inflate(R.layout.rsb_step_layout, stepBodyContainer, false);

        // Setup title and summary
        TextView title = (TextView) surveyStepView.findViewById(R.id.rsb_survey_title);
        TextView summary = (TextView) surveyStepView.findViewById(R.id.rsb_survey_text);
        SurveyStepLayout.setupTitleLayout(layoutInflater.getContext(), step, title, summary);

        LinearLayout surveyStepContainer = (LinearLayout)surveyStepView.findViewById(R.id.rsb_survey_content_container);
        View bodyView = stepBody.getBodyView(StepBody.VIEW_TYPE_DEFAULT, layoutInflater, surveyStepContainer);
        SurveyStepLayout.replaceStepBodyView(surveyStepContainer, bodyView);

        return surveyStepView;
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        updateAllQuestionSteps(false);
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, formStep, stepResult);
        return super.onSaveInstanceState();
    }

    protected void updateAllQuestionSteps(boolean skipped) {
        for (QuestionStep step : subQuestionSteps.keySet()) {
            StepBody stepBody = subQuestionSteps.get(step);
            StepResult result = stepBody.getStepResult(skipped);
            stepResult.getResults().put(step.getIdentifier(), result);
        }
    }

    protected void onNextClicked()
    {
        boolean isAnswerValid = isAnswerValid(true);
        if (isAnswerValid)
        {
            updateAllQuestionSteps(false);
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, formStep, stepResult);
        }
    }

    /**
     * @param showErrorAlertOnInvalid if true, error toast is shown if return false, no toast otherwise
     * @return true if subQuestionSteps steps are valid, false if one or more answers are invalid
     */
    protected boolean isAnswerValid(boolean showErrorAlertOnInvalid) {
        return isAnswerValid(subQuestionSteps.keySet(), showErrorAlertOnInvalid, null);
    }

    /**
     * @param questionSteps the set of question steps to analyze if they are valid or not
     * @param showErrorAlertOnInvalid if true, error toast is shown if return false, no toast otherwise
     * @param identifierErrorMap key is the step identifier, value is the error message when it is not valid
     * @return true if ALL question steps are valid, false if one or more answers are invalid
     */
    protected boolean isAnswerValid(Set<QuestionStep> questionSteps, boolean showErrorAlertOnInvalid, Map<String, String> identifierErrorMap) {
        boolean isAnswerValid = true;
        List<String> invalidReasons = new ArrayList<>();

        for (QuestionStep step : questionSteps) {
            BodyAnswer bodyAnswer = subQuestionSteps.get(step).getBodyAnswerState();
            if (bodyAnswer == null || !bodyAnswer.isValid()) {
                isAnswerValid = false;

                String invalidReason = null;
                // This can override error messages to make it easier for the StepLayout
                // to control the error message for the StepBody
                if (identifierErrorMap != null) {
                    invalidReason = identifierErrorMap.get(step.getIdentifier());
                }

                // This is the main way to get an error message, it is based off of the StepBody
                if (invalidReason == null) {
                    invalidReason = (bodyAnswer == null
                            ? BodyAnswer.INVALID.getString(getContext())
                            : bodyAnswer.getString(getContext()));
                }

                invalidReasons.add(invalidReason);
            }
        }

        if(!isAnswerValid && showErrorAlertOnInvalid)
        {
            String invalidReason = android.text.TextUtils.join(", ", invalidReasons) + ".";
            Toast.makeText(getContext(), invalidReason, Toast.LENGTH_SHORT).show();
        }
        return isAnswerValid;
    }

    /**
     * @param stepToFind uses step to match find step body
     * @return null if stepToFind does not have a corresponding StepBody in subQuestionsStep,
     *         the StepBody object matching stepToFind otherwise
     */
    protected StepBody getStepBody(QuestionStep stepToFind) {
        return getStepBody(stepToFind.getIdentifier());
    }

    /**
     * @param stepIdToFind uses step to match find step body
     * @return null if stepIdToFind does not have a step with a corresponding StepBody in subQuestionsStep,
     *         the StepBody object matching stepIdToFind otherwise
     */
    protected StepBody getStepBody(String stepIdToFind) {
        QuestionStep step = getQuestionStep(stepIdToFind);
        if (step != null) {
            return subQuestionSteps.get(step);
        }
        return null;
    }

    /**
     * @param stepIdToFind finds Question step in subQuestionSteps with this String
     * @return QuestionStep with stepIdToFind, or null if one does not exist
     */
    protected QuestionStep getQuestionStep(String stepIdToFind) {
        for (QuestionStep questionStep : subQuestionSteps.keySet()) {
            if (questionStep.getIdentifier().equals(stepIdToFind)) {
                return questionStep;
            }
        }
        return null;
    }

    public void onSkipClicked()
    {
        if(callbacks != null)
        {
            updateAllQuestionSteps(true);
            // empty step result when skipped
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, formStep, stepResult);
        }
    }

    public FormStep getStep() {
        return formStep;
    }

    public String getString(@StringRes int stringResId)
    {
        return getResources().getString(stringResId);
    }

    protected QuestionStep getFirstQuestionStep() {
        Map.Entry<QuestionStep, StepBody> firstStepEntry = subQuestionSteps.entrySet().iterator().next();
        return firstStepEntry.getKey();
    }

    protected StepBody getFirstStepBody() {
        Map.Entry<QuestionStep, StepBody> firstStepEntry = subQuestionSteps.entrySet().iterator().next();
        return firstStepEntry.getValue();
    }

    protected StepResult subQuestionResult(String stepIdentifier, StepResult stepResult, TaskResult taskResult) {
        StepResult subQuestionResult = StepResultHelper.findStepResult(stepResult, stepIdentifier);
        if (subQuestionResult == null) {
            subQuestionResult = StepResultHelper.findStepResult(taskResult, stepIdentifier);
        }
        return subQuestionResult;
    }
}
