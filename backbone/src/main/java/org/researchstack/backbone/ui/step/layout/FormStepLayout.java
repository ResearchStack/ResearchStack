package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.ui.step.body.TextQuestionBody;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.StepResultHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 1/14/17.
 */

public class FormStepLayout extends FixedSubmitBarLayout implements StepLayout {
    private static final String TAG = FormStepLayout.class.getSimpleName();

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Data used to initializeLayout and return
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    protected FormStep formStep;
    protected List<FormStepData> subQuestionStepData;
    protected StepResult<StepResult> stepResult;

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
        initialize(step, null);
    }

    @Override
    public void initialize(Step step, StepResult result)
    {
        validateStepAndResult(step, result);  // Also sets formStep member variable

        subQuestionStepData = new ArrayList<>();
        formStep = (FormStep) step;

        // Add all relevant questions steps
        List<QuestionStep> questionSteps = new ArrayList<>();
        for (QuestionStep questionStep : formStep.getFormSteps()) {
            questionSteps.add(questionStep);
        }

        // Initialize the UI for title and summary, etc
        initStepLayout(formStep);
        // Fill up the step map
        for (QuestionStep subStep : questionSteps) {
            StepResult subStepResult = StepResultHelper.findStepResult(stepResult, subStep.getIdentifier());
            StepBody stepBody = SurveyStepLayout.createStepBody(subStep, subStepResult);
            View surveyStepView = initStepBodyHolder(layoutInflater, stepBodyContainer, subStep, stepBody);
            subQuestionStepData.add(new FormStepData(subStep, stepBody, surveyStepView));
            stepBodyContainer.addView(surveyStepView);
        }
        // refresh skip/next bar
        refreshSubmitBar();
        setupEditTextImeOptions();
    }

    /**
     * @param step to validate it's state
     * @param stepResult step result to validate
     */
    @SuppressWarnings("unchecked")  // needed for StepResult<StepResult> cast
    protected void validateStepAndResult(Step step, StepResult stepResult) {
        if(step != null && step instanceof FormStep)
        {
            formStep = (FormStep)step;

            if (stepResult == null || stepResult.getResults() == null || stepResult.getResults().isEmpty()) {
                this.stepResult = new StepResult<>(formStep);
            } else {
                for (Object resultObj : stepResult.getResults().values()) {
                    if (!(resultObj instanceof StepResult)) {
                        throw new RuntimeException("StepResult must be StepResult<StepResult>");
                    }
                }
                this.stepResult = stepResult;
            }
        }
        else
        {
            throw new RuntimeException("Step being used in FormStepLayout is not a FormStep or is null");
        }
    }

    /**
     * Assign the correct flow for next button to the next EditText
     */
    protected void setupEditTextImeOptions() {
        EditText nextEditText = null;
        EditText previousEditText;
        for (FormStepData stepData : subQuestionStepData) {
            EditText editText = findEditText(stepData);
            if (editText != null) {
                previousEditText = nextEditText;
                nextEditText = editText;
                if (previousEditText != null) {
                    final EditText nextFocus = nextEditText;
                    previousEditText.setOnEditorActionListener((v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            nextFocus.requestFocus();
                            return true;
                        }
                        return false;
                    });
                }
            }
        }
        // Assign the last EditText a Done button, which should automatically hide keyboard
        if (nextEditText != null) {
            nextEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    /**
     * @param stepData that contains a TextQuestionBody as the stepBody member variable
     * @return EditText for this FormStepData if one exists and we can find it, null otherwise
     */
    protected EditText findEditText(FormStepData stepData) {
        if (stepData.view != null) {
            // R.id.value is the EditText from TextQuestionBody
            View viewObj = stepData.view.get().findViewById(R.id.value);
            if (viewObj instanceof EditText) {
                return (EditText)viewObj;
            }
        }
        return null;
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
        for (FormStepData stepData : subQuestionStepData) {
            if(!stepData.step.isOptional())
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
    @NonNull
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
        for (FormStepData stepData : subQuestionStepData) {
            StepResult result = stepData.stepBody.getStepResult(skipped);
            stepResult.getResults().put(stepData.step.getIdentifier(), result);
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
        return isAnswerValid(subQuestionStepData, showErrorAlertOnInvalid, null);
    }

    /**
     * @param stepDataList the list of FormStepData to analyze if they are valid or not
     * @param showErrorAlertOnInvalid if true, error toast is shown if return false, no toast otherwise
     * @param identifierErrorMap key is the step identifier, value is the error message when it is not valid
     * @return true if ALL question steps are valid, false if one or more answers are invalid
     */
    protected boolean isAnswerValid(List<FormStepData> stepDataList, boolean showErrorAlertOnInvalid, Map<String, String> identifierErrorMap) {
        boolean isAnswerValid = true;
        List<String> invalidReasons = new ArrayList<>();

        for (FormStepData stepData : stepDataList) {
            BodyAnswer bodyAnswer = stepData.stepBody.getBodyAnswerState();
            if (bodyAnswer == null || !bodyAnswer.isValid()) {
                isAnswerValid = false;

                String invalidReason = null;
                // This can override error messages to make it easier for the StepLayout
                // to control the error message for the StepBody
                if (identifierErrorMap != null) {
                    invalidReason = identifierErrorMap.get(stepData.step.getIdentifier());
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
     * @param stepIdToFind finds Question step in subQuestionSteps with this String
     * @return QuestionStep with stepIdToFind, or null if one does not exist
     */
    protected FormStepData getFormStepData(String stepIdToFind) {
        for (FormStepData stepData : subQuestionStepData) {
            if (stepData.step.getIdentifier().equals(stepIdToFind)) {
                return stepData;
            }
        }
        return null;
    }

    /**
     * @param stepIdToFind uses step to match find step body
     * @return null if stepIdToFind does not have a step with a corresponding StepBody in subQuestionsStep,
     *         the StepBody object matching stepIdToFind otherwise
     */
    protected StepBody getStepBody(String stepIdToFind) {
        for (FormStepData stepData : subQuestionStepData) {
            if (stepData.step.getIdentifier().equals(stepIdToFind)) {
                return stepData.stepBody;
            }
        }
        return null;
    }

    /**
     * @param stepIdToFind finds Question step in subQuestionSteps with this String
     * @return QuestionStep with stepIdToFind, or null if one does not exist
     */
    protected QuestionStep getQuestionStep(String stepIdToFind) {
        for (FormStepData stepData : subQuestionStepData) {
            if (stepData.step.getIdentifier().equals(stepIdToFind)) {
                return stepData.step;
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

    protected class FormStepData {
        QuestionStep step;
        StepBody stepBody;
        WeakReference<View> view;

        FormStepData(QuestionStep step, StepBody stepBody, View view) {
            this.step = step;
            this.stepBody = stepBody;
            this.view = new WeakReference<>(view);
        }
    }
}
