package org.researchstack.backbone.ui.step.body;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ViewUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class FormBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private FormStep step;
    private StepResult<StepResult> result;
    private ViewGroup parent;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private List<StepBody> formStepChildren;
    private List<QuestionStep> questionSteps;

    public FormBody(Step step, StepResult result) {
        this.step = (FormStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        this.parent = parent;
        // Inflate our container for each compact child StepBody
        LinearLayout body = (LinearLayout) inflater.inflate(R.layout.rsb_step_layout_form_body,
                parent,
                false);

        questionSteps = step.getFormSteps();
        formStepChildren = new ArrayList<>(questionSteps.size());

        // Iterate through all steps and generate each compact view. Store each StepBody child in a
        // list to iterate over (e.g. within getStepResult())
        for (int i = 0; i < questionSteps.size(); i++) {
            StepBody stepBody = createStepBody(questionSteps.get(i));
            if (!questionSteps.get(i).isHidden()) {
                View bodyView = stepBody.getBodyView(VIEW_TYPE_COMPACT, inflater, body);
                body.addView(bodyView);

                if (i < questionSteps.size() - 1) {
                    body.addView(getDividerView(inflater, body));
                }
            }
            formStepChildren.add(stepBody);
        }

        return body;
    }

    private View getDividerView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.rsb_form_step_divider, parent, false);
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        for (StepBody child : formStepChildren) {
            StepResult childResult = child.getStepResult(skipped);
            if (childResult != null) {
                result.setResultForIdentifier(childResult.getIdentifier(), childResult);
            }
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        for (StepBody formStepBody : formStepChildren) {
            BodyAnswer bodyAnswer = formStepBody.getBodyAnswerState();
            if (!bodyAnswer.isValid()) {
                return bodyAnswer;
            }
        }

        ViewUtils.hideSoftInputMethod(parent);

        return BodyAnswer.VALID;
    }

    @NonNull
    private StepBody createStepBody(QuestionStep questionStep) {
        StepResult childResult = result.getResultForIdentifier(questionStep.getIdentifier());

        Class cls = questionStep.getStepBodyClass();
        try {
            Constructor constructor = cls.getConstructor(Step.class, StepResult.class);
            return (StepBody) constructor.newInstance(questionStep, childResult);
        } catch (Exception e) {
            LogExt.e(this.getClass(), "Cannot instantiate step body for step " + questionStep.getStepTitle() + ", class name: " + cls.getCanonicalName());
            throw new RuntimeException(e);
        }
    }

    public List<QuestionStep> getQuestionSteps() {
        return questionSteps;
    }
}
