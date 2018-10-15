package org.researchstack.backbone.task;

import android.content.Context;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.DecimalAnswerFormat;
import org.researchstack.backbone.answerformat.DurationAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.answerformat.UnknownAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.model.TaskModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This Task allows creation of a special survey from json that has custom navigation logic.
 * <p>
 * Based on the user's answers to questions, they may be taken to a specific step rather than the
 * next one in the task.
 */
public class SmartSurveyTask extends OrderedTask implements Serializable {

    // StepModel types that determine which type of survey question to show
    protected static final String TYPE_BOOLEAN      = "BooleanConstraints";
    protected static final String TYPE_CHOICE       = "MultiValueConstraints";
    protected static final String TYPE_INTEGER      = "IntegerConstraints";
    protected static final String TYPE_DECIMAL      = "DecimalConstraints";
    protected static final String TYPE_TEXT         = "TextConstraints";
    protected static final String TYPE_STRING       = "StringConstraints";
    protected static final String TYPE_DATE         = "DateConstraints";
    protected static final String TYPE_DATE_TIME    = "DateTimeConstraints";
    protected static final String TYPE_DURATION     = "DurationConstraints";

    // StepModel types
    protected static final String SURVEY_TYPE_QUESTION  = "SurveyQuestion";
    protected static final String SURVEY_TYPE_TEXT      = "SurveyTextOnly";
    protected static final String SURVEY_TYPE_INFO      = "SurveyInfoScreen";


    // StepModel uiHints suggest which type of UI element to show for the survey question
    protected static final String UI_HINT_CHECKBOX  = "checkbox";
    protected static final String UI_HINT_RADIO     = "radiobutton";
    protected static final String UI_HINT_LIST      = "list";
    protected static final String UI_HINT_NUMBER    = "numberfield";
    protected static final String UI_HINT_TEXT      = "textfield";

    // Types of Enumeration Label/Values
    protected static final String ENUMERATION_TYPE_OPTION = "SurveyQuestionOption";

    // use this as the 'skipTo' identifier to end the survey instead of going to a question
    public static final String END_OF_SURVEY_MARKER = "END_OF_SURVEY";

    // Skip rules for answers to survey questions
    protected static final String OPERATOR_SKIP = "de";
    protected static final String OPERATOR_EQUAL = "eq";
    protected static final String OPERATOR_NOT_EQUAL = "ne";
    protected static final String OPERATOR_LESS_THAN = "lt";
    protected static final String OPERATOR_GREATER_THAN = "gt";
    protected static final String OPERATOR_LESS_THAN_EQUAL = "le";
    protected static final String OPERATOR_GREATER_THAN_EQUAL = "ge";
    protected static final String OPERATOR_OTHER_THAN = "ot";

    protected HashMap<String, List<TaskModel.RuleModel>> rules;

    protected List<String> staticStepIdentifiers;
    protected List<String> dynamicStepIdentifiers;

    /* Default constructor needed for serilization/deserialization of object */
    public SmartSurveyTask() {
        super();
    }

    /**
     * Class constructor specifying a unique identifier.
     *
     * @param identifier the task identifier, see {@link #getIdentifier()}
     */
    public SmartSurveyTask(String identifier) {
        super(identifier);
    }

    /**
     * Creates a SmartSurveyTask from a {@link TaskModel} object
     *
     * @param context   context for fetching any resources needed
     * @param taskModel Java representation of the task json
     */
    public SmartSurveyTask(Context context, TaskModel taskModel) {
        super(taskModel.identifier);
        steps = new ArrayList<>();
        rules = new HashMap<>();
        staticStepIdentifiers = new ArrayList<>(taskModel.elements.size());
        for (TaskModel.StepModel stepModel : taskModel.elements) {
            if (stepModel.type.equals(SURVEY_TYPE_QUESTION)) {
                AnswerFormat answerFormat = from(context, stepModel.constraints);

                QuestionStep questionStep = new QuestionStep(stepModel.identifier,
                        stepModel.prompt,
                        answerFormat);
                questionStep.setText(stepModel.promptDetail);
                questionStep.setOptional(stepModel.optional);
                steps.add(questionStep);
                staticStepIdentifiers.add(stepModel.identifier);
                rules.put(stepModel.identifier, stepModel.constraints.rules);
            }
            /*
            In a survey JSON file, if you want to define a step that has text but no question,
            set the type to "SurveyTextOnly" instead of "SurveyQuestion"
             */
            else if (stepModel.type.equals(SURVEY_TYPE_TEXT) || stepModel.type.equals(SURVEY_TYPE_INFO)) {
                InstructionStep instructionStep = new InstructionStep(stepModel.identifier, stepModel.prompt, stepModel.promptDetail);
                steps.add(instructionStep);
                staticStepIdentifiers.add(stepModel.identifier);
            } else {
                throw new UnsupportedOperationException("Wasn't a survey question");
            }
        }

        dynamicStepIdentifiers = new ArrayList<>(staticStepIdentifiers);
    }

    protected AnswerFormat from(Context context, TaskModel.ConstraintsModel constraints) {
        AnswerFormat answerFormat;
        String type = constraints.type;
        if (type.equals(TYPE_BOOLEAN)) {
            answerFormat = new BooleanAnswerFormat(context.getString(R.string.rsb_yes),
                    context.getString(R.string.rsb_no));
        } else if (type.equals(TYPE_CHOICE)) {
            AnswerFormat.ChoiceAnswerStyle answerStyle = constraints.allowMultiple
                    ? AnswerFormat.ChoiceAnswerStyle.MultipleChoice
                    : AnswerFormat.ChoiceAnswerStyle.SingleChoice;
            answerFormat = new ChoiceAnswerFormat(answerStyle, from(constraints.enumeration));
        } else if (type.equals(TYPE_INTEGER)) {
            answerFormat = new IntegerAnswerFormat(constraints.minValue, constraints.maxValue);
        } else if (type.equals(TYPE_DECIMAL)) {
            answerFormat = new DecimalAnswerFormat(constraints.minValue, constraints.maxValue);
        } else if (type.equals(TYPE_TEXT) || type.equals(TYPE_STRING)) {
            answerFormat = new TextAnswerFormat();
            boolean multipleLines = constraints.multipleLines;
            ((TextAnswerFormat) answerFormat).setIsMultipleLines(multipleLines);
        } else if (type.equals(TYPE_DATE)) {
            answerFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
        } else if (type.equals(TYPE_DATE_TIME)) {
            answerFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.DateAndTime);
        } else if (type.equals(TYPE_DURATION)) {
            answerFormat = new DurationAnswerFormat(constraints.step, constraints.durationUnit);
        } else {
            LogExt.e(SmartSurveyTask.class, "Survey question has answer type not supported:" + type);
            //we can launch an exception here, but I don't thing it would follow the original design
            //throw new RuntimeException("Survey question has answer type not supported:" + type);
            answerFormat = new UnknownAnswerFormat();
        }
        return answerFormat;
    }

    protected Choice[] from(List<TaskModel.EnumerationModel> enumeration) {
        Choice[] choices = new Choice[enumeration.size()];

        for (int i = 0; i < enumeration.size(); i++) {
            TaskModel.EnumerationModel choice = enumeration.get(i);
            if (choice.value instanceof String) {
                choices[i] = new Choice<>(choice.label, (String) choice.value);
            } else if (choice.value instanceof Number) {
                // if the field type is Object, gson turns all numbers into doubles. Assuming Integer
                choices[i] = new Choice<>(choice.label, ((Number) choice.value).intValue());
            } else {
                throw new RuntimeException(
                        "String and Integer are the only supported values for generating Choices from json");
            }
        }
        return choices;
    }

    /**
     * Returns the next step in the task based on current answers, or null if at the end.
     * <p>
     * This method rebuilds the order of the steps based on the current results and returns the next
     * one.
     *
     * @param step   The reference step. Pass null to specify the first step.
     * @param result A snapshot of the current set of results.
     * @return the Step to navigate to
     */
    @Override
    public Step getStepAfterStep(Step step, TaskResult result) {
        String currentIdentifier = step == null ? null : step.getIdentifier();
        refillDynamicStepIdentifiers(currentIdentifier);

        String skipToStep = null;

        List<TaskModel.RuleModel> stepRules = rules.get(currentIdentifier);
        if (stepRules != null && !stepRules.isEmpty()) {
            LogExt.d(getClass(), "Rules exist for this step");
            Object answer = result.getStepResult(currentIdentifier).getResult();
            skipToStep = processRules(stepRules, answer);

            if (skipToStep != null && skipToStep.equals(END_OF_SURVEY_MARKER)) {
                return null;
            }

            if (skipToStep != null) {
                adjustDynamicStepIdentifiers(skipToStep, currentIdentifier);
            }
        } else {
            LogExt.d(getClass(), "No rules for this step");
        }

        String nextStepIdentifier = nextStepIdentifier(true, currentIdentifier);

        return nextStepIdentifier == null ? null : getStep(nextStepIdentifier);
    }

    /**
     * Returns the step that should be before the current step based on current results.
     * <p>
     * This method rebuilds the order of the remaining steps based on the current results and
     * returns the previous one to the current step.
     *
     * @param step   The reference step. Pass null to specify the last step.
     * @param result A snapshot of the current set of results.
     * @return the Step to navigate to
     */
    @Override
    public Step getStepBeforeStep(Step step, TaskResult result) {
        String currentIdentifier = step == null ? null : step.getIdentifier();
        refillDynamicStepIdentifiers(currentIdentifier);
        String previousStepIdentifier = nextStepIdentifier(false, currentIdentifier);
        return previousStepIdentifier == null ? null : getStep(previousStepIdentifier);
    }

    @Override
    public Step getStepWithIdentifier(String identifier) {
        return getStep(identifier);
    }

    private Step getStep(String identifier) {
        if (identifier == null || steps == null) {
            return null;
        }
        for (Step step : steps) {
            if (identifier.equals(step.getIdentifier())) {
                return step;
            }
        }
        return null;
    }

    /**
     * Returns the current progress String for use in the action bar
     * <p>
     * This is updated based on the current and total in the dynamic list of steps.
     *
     * @param context for fetching resources
     * @param step    the current step
     * @return the title that should be displayed for this step
     */
    @Override
    public String getTitleForStep(Context context, Step step) {
        int currentIndex = staticStepIdentifiers.indexOf(step.getIdentifier()) + 1;
        return context.getString(R.string.rsb_format_step_title,
                currentIndex,
                staticStepIdentifiers.size());
    }

    @Override
    public TaskProgress getProgressOfCurrentStep(Step step, TaskResult result) {
        int current = staticStepIdentifiers.indexOf(step == null ? -1 : step.getIdentifier());
        return new TaskProgress(current, staticStepIdentifiers.size());
    }

    @Override
    public void validateParameters() {
        // Construction validates most issues, add some validation here if needed
    }

    protected String nextStepIdentifier(boolean after, String currentIdentifier) {
        if (currentIdentifier == null && after) {
            return !dynamicStepIdentifiers.isEmpty() ? dynamicStepIdentifiers.get(0) : null;
        }

        int currentIndex = dynamicStepIdentifiers.indexOf(currentIdentifier);
        int newIndex = -1;

        if (after) {
            if (currentIndex + 1 < dynamicStepIdentifiers.size()) {
                newIndex = currentIndex + 1;
            }
        } else {
            if (currentIndex >= 1) {
                newIndex = currentIndex - 1;
            }
        }

        return newIndex != -1 ? dynamicStepIdentifiers.get(newIndex) : null;
    }

    protected void refillDynamicStepIdentifiers(String currentIdentifier) {
        //Remove till end in dynamic
        int currentIndexInDynamic = dynamicStepIdentifiers.indexOf(currentIdentifier);
        currentIndexInDynamic = currentIndexInDynamic == -1 ? 0 : currentIndexInDynamic;
        dynamicStepIdentifiers = new ArrayList<>(dynamicStepIdentifiers.subList(0, currentIndexInDynamic));

        //Add array from static
        int currentIndexInStatic = staticStepIdentifiers.indexOf(currentIdentifier);
        currentIndexInStatic = currentIndexInStatic == -1 ? 0 : currentIndexInStatic;

        dynamicStepIdentifiers.addAll(staticStepIdentifiers.subList(currentIndexInStatic,
                staticStepIdentifiers.size()));
    }

    protected void adjustDynamicStepIdentifiers(String skipToIdentifier, String currentIdentifier) {
        int currentIndex = dynamicStepIdentifiers.indexOf(currentIdentifier);
        int skipToIndex = dynamicStepIdentifiers.indexOf(skipToIdentifier);

        if (currentIndex == -1 || skipToIndex == -1) {
            return;
        }

        if (skipToIndex > currentIndex) {
            while (!dynamicStepIdentifiers.get(currentIndex + 1).equals(skipToIdentifier)) {
                dynamicStepIdentifiers.remove(currentIndex + 1);
            }
        }
    }

    protected String processRules(List<TaskModel.RuleModel> stepRules, Object answer) {
        String skipToIdentifier = null;

        for (TaskModel.RuleModel stepRule : stepRules) {
            skipToIdentifier = checkRule(stepRule, answer);
            if (skipToIdentifier != null) {
                break;
            }
        }

        return skipToIdentifier;
    }

    protected String checkRule(TaskModel.RuleModel stepRule, Object answer) {
        String operator = stepRule.operator;
        String skipTo = stepRule.skipTo;
        Object value = stepRule.value;

        if (operator.equals(OPERATOR_SKIP)) {
            return answer == null ? skipTo : null;
        } else if (answer instanceof Integer) {
            return checkNumberRule(operator, skipTo, ((Number) value).intValue(), (Integer) answer);
        } else if (answer instanceof Double) {
            return checkNumberRule(operator, skipTo, ((Number) value).doubleValue(), (Double) answer);
        } else if (answer instanceof Boolean) {
            Boolean booleanValue;

            if (value instanceof Boolean) {
                booleanValue = (Boolean) value;
            } else if (value instanceof Number) {
                booleanValue = ((Number) value).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
            } else if (value instanceof String) {
                booleanValue = Boolean.valueOf((String) value);
            } else {
                throw new RuntimeException("Invalid value for Boolean skip rule");
            }

            return checkEqualsRule(operator, skipTo, booleanValue, answer);
        } else if (answer instanceof String) {
            return checkEqualsRule(operator, skipTo, value, answer);
        } else {
            LogExt.e(getClass(), "Unsupported answer type for smart survey rules");
        }

        return null;
    }

    protected  <T> String checkEqualsRule(String operator, String skipTo, T value, T answer) {
        switch (operator) {
            case OPERATOR_EQUAL:
                return value.equals(answer) ? skipTo : null;
            case OPERATOR_NOT_EQUAL:
                return !value.equals(answer) ? skipTo : null;
        }
        return null;
    }

    protected  <T extends Comparable<T>> String checkNumberRule(String operator, String skipTo, T value, T answer) {
        int compare = answer.compareTo(value);

        switch (operator) {
            case OPERATOR_EQUAL:
                return compare == 0 ? skipTo : null;
            case OPERATOR_NOT_EQUAL:
                return compare != 0 ? skipTo : null;
            case OPERATOR_GREATER_THAN:
                return compare > 0 ? skipTo : null;
            case OPERATOR_GREATER_THAN_EQUAL:
                return compare >= 0 ? skipTo : null;
            case OPERATOR_LESS_THAN:
                return compare < 0 ? skipTo : null;
            case OPERATOR_LESS_THAN_EQUAL:
                return compare <= 0 ? skipTo : null;
        }

        return null;

    }
}
