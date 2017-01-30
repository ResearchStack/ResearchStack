package org.researchstack.skin.task;

import android.content.Context;

import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.PermissionRequestManager;
import org.researchstack.skin.R;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.skin.TaskProvider;
import org.researchstack.skin.UiManager;
import org.researchstack.skin.model.InclusionCriteriaModel;
import org.researchstack.skin.ui.layout.SignUpEligibleStepLayout;
import org.researchstack.skin.ui.layout.SignUpIneligibleStepLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated // use OnboardingManager.getInstance().launchOnboarding(context, TaskType.REGISTRATION);
public class SignUpTask extends OnboardingTask {
    public static final int MINIMUM_STEPS = 2;
    public static final String ID_EMAIL = "ID_EMAIL";
    public static final String ID_PASSWORD = "ID_PASSWORD";
    private boolean hasPasscode;
    private Step inclusionCriteriaStep;

    private Map<String,Step> stepMap = new HashMap<>();
    private Map<String,Boolean> answerMap = new HashMap<>();

    public SignUpTask(Context context)
    {
        super(TaskProvider.TASK_ID_SIGN_UP);

        initSteps(context);

        inclusionCriteriaStep = stepMap.get(SignUpInclusionCriteriaStepIdentifier);

    }

  /**
   * Create steps as defined in the JSON file.
   *
   * @param context
   */
  private void initSteps(Context context) {
        InclusionCriteriaModel model = ResourceManager.getInstance()
                .getInclusionCriteria()
                .create(context);

        for(InclusionCriteriaModel.Step s: model.steps) {
            // step can be null with addition of new OnboardingManager steps, so just ignore them
            if (s != null && s.type != null) {
                switch (s.type) {
                    case INSTRUCTION:
                        Step instruction = null;
                        switch (s.identifier) {
                            case InclusionCriteriaModel.INELIGIBLE_INSTRUCTION_IDENTIFIER:
                                instruction = new Step(SignUpIneligibleStepIdentifier, s.text);
                                instruction.setText(s.detailText);
                                instruction.setStepTitle(R.string.rss_eligibility);
                                instruction.setStepLayoutClass(SignUpIneligibleStepLayout.class);
                                break;
                            case InclusionCriteriaModel.ELIGIBLE_INSTRUCTION_IDENTIFIER:
                                instruction = new Step(SignUpEligibleStepIdentifier, s.text);
                                instruction.setText(s.detailText);
                                instruction.setStepTitle(R.string.rss_eligibility);
                                instruction.setStepLayoutClass(SignUpEligibleStepLayout.class);
                                break;
                            default:
                                instruction.setStepTitle(R.string.rss_eligibility);
                                instruction = new Step(s.identifier, s.text);
                                instruction.setText(s.detailText);
                        }

                        stepMap.put(instruction.getIdentifier(), instruction);
                        break;
                    // TODO: not sure what the differences are between compound/toggle or is compound obsolete?
                    case COMPOUND:
                    case TOGGLE:
                        FormStep form = new FormStep(SignUpInclusionCriteriaStepIdentifier, s.text, s.detailText);
                        List<QuestionStep> questions = new ArrayList<>();

                        if (s.items != null) {
                            // TODO: extend the json to include (yes/no)?
                            BooleanAnswerFormat booleanAnswerFormat =
                                    new BooleanAnswerFormat(context.getString(R.string.rsb_yes), context.getString(R.string.rsb_no));
                            for (InclusionCriteriaModel.Item item : s.items) {
                                QuestionStep question = new QuestionStep(item.identifier, item.text, booleanAnswerFormat);
                                answerMap.put(item.identifier, item.expectedAnswer);
                                questions.add(question);
                            }
                            form.setFormSteps(questions);
                        }
                        form.setStepTitle(R.string.rss_eligibility);
                        form.setOptional(false);
                        stepMap.put(form.getIdentifier(), form);
                        break;
                    case SHARE:
                        Step step = new Step(s.identifier);
                        stepMap.put(step.getIdentifier(), step);
                        break;
                    default:
                        LogExt.i(getClass(), "Unrecognized InclusionCriteriaModel.Step: " + s.type);
                }
            }
        }
    }

    @Override
    public Step getStepAfterStep(Step step, TaskResult result) {
        Step nextStep = null;

        if (step == null) {
            nextStep = inclusionCriteriaStep;
        } else if (step.getIdentifier().equals(SignUpInclusionCriteriaStepIdentifier)) {
            if (UiManager.getInstance().isInclusionCriteriaValid(result.getStepResult(step.getIdentifier()))) {
            if(isInclusionCriteriaValid(result.getStepResult(step.getIdentifier())))
                nextStep = stepMap.get(SignUpEligibleStepIdentifier);
            } else {
                nextStep = stepMap.get(SignUpIneligibleStepIdentifier);
            }
        } else if (step.getIdentifier().equals(SignUpEligibleStepIdentifier)) {
            if (!hasPasscode) {
                nextStep = getPassCodeCreationStep();
            } else if (!PermissionRequestManager.getInstance().getPermissionRequests().isEmpty()) {
                nextStep = getPermissionStep();
            } else {
                nextStep = getSignUpStep();
            }
        } else if (step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier)) {
            if (!PermissionRequestManager.getInstance().getPermissionRequests().isEmpty()) {
                nextStep = getPermissionStep();
            } else {
                nextStep = getSignUpStep();
            }
        } else if (step.getIdentifier().equals(SignUpPermissionsStepIdentifier)) {
            nextStep = getSignUpStep();
        }

        return nextStep;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult result) {
        Step prevStep = null;

        if (step.getIdentifier().equals(SignUpInclusionCriteriaStepIdentifier)) {
            prevStep = null;
        } else if (step.getIdentifier().equals(SignUpEligibleStepIdentifier)) {
            prevStep = inclusionCriteriaStep;

        } else if (step.getIdentifier().equals(SignUpIneligibleStepIdentifier)) {
            prevStep = inclusionCriteriaStep;

        } else if (step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier)) {
            prevStep = stepMap.get(SignUpEligibleStepIdentifier);
        } else if (step.getIdentifier().equals(SignUpPermissionsStepIdentifier)) {
            if (hasPasscode) {
                // Force user to create a new pin
                prevStep = getPassCodeCreationStep();
            } else {
                prevStep = stepMap.get(SignUpEligibleStepIdentifier);
            }
        } else if (step.getIdentifier().equals(SignUpStepIdentifier)) {
            if (!PermissionRequestManager.getInstance().getPermissionRequests().isEmpty()) {
                prevStep = getPermissionStep();
            } else if (hasPasscode) {
                // Force user to create a new pin
                prevStep = getPassCodeCreationStep();
            } else {
                prevStep = stepMap.get(SignUpEligibleStepIdentifier);
            }
        }

        return prevStep;
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result) {
        int stepPosition = 0;

        if (step == null || step.getIdentifier().equals(SignUpInclusionCriteriaStepIdentifier)) {
            stepPosition = 0;
        } else if (step.getIdentifier().equals(SignUpEligibleStepIdentifier)) {
            stepPosition = 1;

        } else if (step.getIdentifier().equals(SignUpIneligibleStepIdentifier)) {
            stepPosition = 1;

        } else if (step.getIdentifier().equals(SignUpPassCodeCreationStepIdentifier)) {
            stepPosition = 2;

        } else if (step.getIdentifier().equals(SignUpPermissionsStepIdentifier)) {
            stepPosition = 3;

        } else if (step.getIdentifier().equals(SignUpStepIdentifier)) {
            stepPosition = 4;

        }

        return new TaskProgress(stepPosition, MINIMUM_STEPS);
    }

    public void setHasPasscode(boolean hasPasscode) {
        this.hasPasscode = hasPasscode;
    }

    private Boolean getBooleanAnswer(Map mapStepResult, String id)
    {
        StepResult stepResult = (StepResult)mapStepResult.get(id);
        if (stepResult == null) return false;
        Map mapResult = stepResult.getResults();
        if (mapResult == null) return false;
        Boolean answer = (Boolean)mapResult.get(StepResult.DEFAULT_KEY);
        if (answer == null || answer == false)
        {
            return false;
        }
        else
        {
            return true;
        }

    }

    protected boolean isInclusionCriteriaValid(StepResult stepResult)
    {
        if(stepResult != null)
        {
            Map mapStepResult = stepResult.getResults();
            for(Object obj: mapStepResult.keySet())
            {
                String id = (String)obj;
                Boolean answer = getBooleanAnswer(mapStepResult, id);
                Boolean expectedAnswer = answerMap.get(id);
                if(answer.booleanValue() != expectedAnswer.booleanValue())
                {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

}
