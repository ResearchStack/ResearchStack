package org.researchstack.backbone.model.survey.factory;

import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.text.InputType;

import com.google.gson.JsonElement;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.DecimalAnswerFormat;
import org.researchstack.backbone.answerformat.EmailAnswerFormat;
import org.researchstack.backbone.answerformat.GenderAnswerFormat;
import org.researchstack.backbone.answerformat.IntegerAnswerFormat;
import org.researchstack.backbone.answerformat.PasswordAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.ActiveStepSurveyItem;
import org.researchstack.backbone.model.survey.BooleanQuestionSurveyItem;
import org.researchstack.backbone.model.survey.ChoiceQuestionSurveyItem;
import org.researchstack.backbone.model.survey.CompoundQuestionSurveyItem;
import org.researchstack.backbone.model.survey.DateRangeSurveyItem;
import org.researchstack.backbone.model.survey.FloatRangeSurveyItem;
import org.researchstack.backbone.model.survey.IntegerRangeSurveyItem;
import org.researchstack.backbone.model.survey.ProfileSurveyItem;
import org.researchstack.backbone.model.survey.ScaleQuestionSurveyItem;
import org.researchstack.backbone.model.survey.InstructionSurveyItem;
import org.researchstack.backbone.model.survey.QuestionSurveyItem;
import org.researchstack.backbone.model.survey.SubtaskQuestionSurveyItem;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemType;
import org.researchstack.backbone.model.survey.TimingRangeQuestionSurveyItem;
import org.researchstack.backbone.model.survey.ToggleQuestionSurveyItem;
import org.researchstack.backbone.onboarding.OnboardingSection;
import org.researchstack.backbone.step.CompletionStep;
import org.researchstack.backbone.step.EmailVerificationStep;
import org.researchstack.backbone.step.EmailVerificationSubStep;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.LoginStep;
import org.researchstack.backbone.step.NavigationFormStep;
import org.researchstack.backbone.step.PasscodeStep;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.ProfileStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.RegistrationStep;
import org.researchstack.backbone.step.ShareTheAppStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;
import org.researchstack.backbone.step.ToggleFormStep;
import org.researchstack.backbone.step.NavigationExpectedAnswerQuestionStep;
import org.researchstack.backbone.step.NavigationSubtaskStep;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.ui.step.layout.PasscodeCreationStepLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by TheMDP on 12/29/16.
 *
 * The SurveyFactory controls converting SurveyItem object to Step objects
 * It accounts for all the variations specified in SurveyItemType when looping through each
 * SurveyItem and storing the result in a field you can access using the getSteps method
 *
 * Note that SurveyItem objects should be create from JSON using the GSON library,
 * and a SurveyItemAdapter class to do special de-serialization for the SurveyItems
 */

public class SurveyFactory {

    // The rest of them use the toString of ProfileInfoOption
    public static final String EMAIL_VERIFICATION_SUBSTEP_IDENTIFIER = "emailVerificationSubstep";
    public static final String PASSWORD_CONFIRMATION_IDENTIFIER = "confirmation";
    public static final String CONSENT_QUIZ_IDENTIFIER = "consentQuiz";

    // When set, this will be used
    private CustomStepCreator customStepCreator;

    /**
     * Can be used to make a SurveyFactor and take advantage of its SurveyItem to Step methods
     */
    public SurveyFactory() {
        super();
        // Default constructor, mainly used for subclasses
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param surveyItems a list of survey items that will be transformed into Steps
     * @return a list of steps
     */
    public List<Step> createSurveySteps(Context context, List<SurveyItem> surveyItems) {
        List<Step> steps = new ArrayList<>();
        if (surveyItems != null) {
            for (SurveyItem item : surveyItems) {
                Step step = createSurveyStep(context, item, false);
                if (step != null) {
                    steps.add(step);
                }
            }
        }
        return steps;
    }
    
    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item the survey item to act upon
     * @param isSubtaskStep true if this is within a subtask step already, false otherwise
     * @return a step created from the item
     */
    public Step createSurveyStep(Context context, SurveyItem item, boolean isSubtaskStep) {

        switch (item.type) {
            case INSTRUCTION:
            case INSTRUCTION_COMPLETION:
                if (!(item instanceof InstructionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, INSTRUCTION types must be InstructionSurveyItems");
                }
                if (item.type == SurveyItemType.INSTRUCTION_COMPLETION) {
                    return createInstructionCompletionStep((InstructionSurveyItem)item);
                }
                return createInstructionStep((InstructionSurveyItem)item);
            case SUBTASK:
                if (!(item instanceof SubtaskQuestionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, SUBTASK types must be SubtaskQuestionSurveyItem");
                }
                return createSubtaskStep(context, ((SubtaskQuestionSurveyItem)item));
            case QUESTION_BOOLEAN:
            case QUESTION_DATE:
            case QUESTION_DATE_TIME:
            case QUESTION_DECIMAL:
            case QUESTION_DURATION:
            case QUESTION_INTEGER:
            case QUESTION_MULTIPLE_CHOICE:
            case QUESTION_SCALE:
            case QUESTION_SINGLE_CHOICE:
            case QUESTION_TEXT:
            case QUESTION_TIME:
            case QUESTION_TIMING_RANGE:
                if (!(item instanceof QuestionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, QUESTION_* types must be QuestionSurveyItem");
                }
                return createQuestionStep(context, (QuestionSurveyItem)item);
            case QUESTION_TOGGLE:
                if (!(item instanceof ToggleQuestionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, QUESTION_TOGGLE types must be ToggleQuestionSurveyItem");
                }
                return createToggleFormStep(context, (ToggleQuestionSurveyItem)item);
            case QUESTION_COMPOUND:
                if (!(item instanceof CompoundQuestionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, QUESTION_COMPOUND types must be CompoundQuestionSurveyItem");
                }
                return createCompoundStep(context, (CompoundQuestionSurveyItem)item);
            case ACCOUNT_REGISTRATION:
                if (!(item instanceof ProfileSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, ACCOUNT_REGISTRATION types must be ProfileSurveyItem");
                }
                return createRegistrationStep(context, (ProfileSurveyItem)item);
            case ACCOUNT_PROFILE:
                if (!(item instanceof ProfileSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, ACCOUNT_PROFILE types must be ProfileSurveyItem");
                }
                return createProfileStep(context, (ProfileSurveyItem)item);
            case ACCOUNT_LOGIN:
                if (!(item instanceof ProfileSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, ACCOUNT_LOGIN types must be ProfileSurveyItem");
                }
                return createLoginStep(context, (ProfileSurveyItem)item);
            case ACCOUNT_COMPLETION:
                // TODO: finish the completion step layout, for now just use a simple instruction
                // TODO: should show the cool check mark animation, see iOS
                if (!(item instanceof InstructionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, ACCOUNT_COMPLETION types must be InstructionSurveyItem");
                }
                return createInstructionStep((InstructionSurveyItem)item);
            case ACCOUNT_EMAIL_VERIFICATION:
                if (!(item instanceof InstructionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, ACCOUNT_EMAIL_VERIFICATION types must be InstructionSurveyItem");
                }
                return createEmailVerificationStep(context, (InstructionSurveyItem)item);
            case ACCOUNT_PERMISSIONS:
                return createPermissionsStep(item);
            case ACCOUNT_DATA_GROUPS:
                return createNotImplementedStep(item);
            case ACCOUNT_EXTERNAL_ID:
                return createNotImplementedStep(item);
            case PASSCODE:
                return createPasscodeStep(context, item);
            case SHARE_THE_APP:
                if (!(item instanceof InstructionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, SHARE_THE_APP types must be InstructionSurveyItem");
                }
                return createShareTheAppStep(context, (InstructionSurveyItem)item);
            case ACTIVE_STEP:
                if (!(item instanceof ActiveStepSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, ACTIVE_STEP types must be ActiveStepSurveyItem");
                }
                return createShareTheAppStep(context, (InstructionSurveyItem)item);
            case CUSTOM:
                // To override a custom step from survey item mapping,
                // You need to override the CustomStepCreator
                if (customStepCreator != null) {
                    Step step = customStepCreator.createCustomStep(context, item, isSubtaskStep, this);
                    if (step != null) {
                        return step;
                    }
                }
                return createCustomStep(context, item, isSubtaskStep);
        }

        return null;
    }

    /**
     * @param item InstructionSurveyItem from JSON
     * @return valid InstructionStep matching the InstructionSurveyItem
     */
    public InstructionStep createInstructionStep(InstructionSurveyItem item) {
        InstructionStep step = new InstructionStep(item.identifier, item.title, item.text);
        fillInstructionStep(step, item);
        return step;
    }

    /**
     * @param item InstructionSurveyItem from JSON
     * @return valid CompletionStep matching the InstructionSurveyItem
     */
    public CompletionStep createInstructionCompletionStep(InstructionSurveyItem item) {
        CompletionStep step = new CompletionStep(item.identifier, item.title, item.text);
        fillInstructionStep(step, item);
        return step;
    }

    /** helper to display to dev that this step is not implemented yet */
    InstructionStep createNotImplementedStep(SurveyItem item) {
        return new InstructionStep(
                item.identifier,
                "Not implemented",
                "Type of step not implemented yet.");
    }

    /**
     * Helper method for instruction steps
     *
     * @param step the instruction step to fill with the item's info
     * @param item the item to fil up the step with its fields
     */
    public void fillInstructionStep(InstructionStep step, InstructionSurveyItem item) {
        if (item.footnote != null) {
            step.setFootnote(item.footnote);
        }
        if (item.nextIdentifier != null) {
            step.setNextStepIdentifier(item.nextIdentifier);
        }
        if (item.detailText != null) {
            step.setMoreDetailText(item.detailText);
        }
        if (item.image != null) {
            step.setImage(item.image);
        }
        if (item.iconImage != null) {
            step.setIconImage(item.iconImage);
        }
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item SubtaskQuestionSurveyItem item from JSON that contains nested SurveyItems
     * @return a subtask step by recursively calling createSurveyStep for inner subtask steps
     */
    public SubtaskStep createSubtaskStep(Context context, SubtaskQuestionSurveyItem item) {
        if (item.items == null || item.items.isEmpty()) {
            throw new IllegalStateException("subtasks must have step items to proceed");
        }

        List<Step> substeps = new ArrayList<>();
        for (SurveyItem subItem : item.items) {
            Step step = createSurveyStep(context, subItem, true);
            substeps.add(step);
        }

        SubtaskStep step;
        if (item.usesNavigation()) {
            NavigationSubtaskStep navStep = new NavigationSubtaskStep(item.identifier, substeps);
            transferNavigationRules(item, navStep);
            step = navStep;
        } else {
            step = new SubtaskStep(item.identifier, substeps);
        }

        return step;
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item SubtaskQuestionSurveyItem item from JSON that contains nested SurveyItems
     * @return a subtask step by recursively calling createSurveyStep for inner subtask steps
     */
    public FormStep createCompoundStep(Context context, CompoundQuestionSurveyItem item) {
        if (item.items == null || item.items.isEmpty()) {
            throw new IllegalStateException("compound surveys must have step items to proceed");
        }

        List<QuestionStep> questionSteps = new ArrayList<>();
        for (SurveyItem subItem : item.items) {
            if (subItem instanceof QuestionSurveyItem) {
                QuestionStep step = createQuestionStep(context, (QuestionSurveyItem)subItem);
                questionSteps.add(step);
            }
        }

        FormStep step = new FormStep(item.identifier, item.title, item.text, questionSteps);
        return step;
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item QuestionSurveyItem from JSON
     * @return QuestionStep converted from the item
     */
    public QuestionStep createQuestionStep(Context context, QuestionSurveyItem item) {
        AnswerFormat format = null;
        switch (item.type) {
            case QUESTION_BOOLEAN:
                if (!(item instanceof BooleanQuestionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, QUESTION_BOOLEAN types must be BooleanQuestionSurveyItem");
                }
                BooleanQuestionSurveyItem boolItem = (BooleanQuestionSurveyItem)item;
                String yes = null, no = null;
                // First try and get the true / value choices from the BooleanQuestionSurveyItem
                // Since it may sometimes, but not always provided
                if (boolItem.items != null && boolItem.items.size() == 2) {
                    for (Choice<Boolean> choice : boolItem.items) {
                        if (choice.getValue() == true) {
                            yes = choice.getText();
                        } else {
                            no = choice.getText();
                        }
                    }
                }
                // If they are not provided, use the default yes / no strings for true / false
                if (yes == null) {
                    yes = context.getString(R.string.rsb_yes);
                }
                if (no == null) {
                    no = context.getString(R.string.rsb_no);
                }
                format = new BooleanAnswerFormat(yes, no);
                break;
            case QUESTION_DATE:
            case QUESTION_DATE_TIME:
                if (!(item instanceof DateRangeSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, QUESTION_DATE types must be DateRangeSurveyItem");
                }
                DateRangeSurveyItem dateSurveyItem = (DateRangeSurveyItem)item;
                DateRangeSurveyItem dateItem = (DateRangeSurveyItem)item;
                AnswerFormat.DateAnswerStyle style = AnswerFormat.DateAnswerStyle.Date;
                if (dateSurveyItem.type == SurveyItemType.QUESTION_DATE_TIME) {
                    style = AnswerFormat.DateAnswerStyle.DateAndTime;
                }
                format = new DateAnswerFormat(style, dateItem.min, dateItem.max, dateItem.defaultValue);
                break;
            case QUESTION_TIME:
                format = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.TimeOfDay);
                break;
            case QUESTION_DECIMAL:
                if (!(item instanceof FloatRangeSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, QUESTION_DECIMAL types must be FloatRangeSurveyItem");
                }
                FloatRangeSurveyItem floatItem = (FloatRangeSurveyItem)item;
                format = new DecimalAnswerFormat(floatItem.min, floatItem.max);
                break;
            case QUESTION_INTEGER:
                if (!(item instanceof IntegerRangeSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, QUESTION_INTEGER types must be IntegerRangeSurveyItem");
                }
                IntegerRangeSurveyItem intItem = (IntegerRangeSurveyItem)item;
                format = new IntegerAnswerFormat(intItem.min, intItem.max);
                break;
            case QUESTION_DURATION:
                // TODO: create DurationQuestionSurveyItem and also TimeIntervalAnswerFormat
                format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                        new Choice<>("TODO: Duration type not implemented", true));
                break;
            case QUESTION_SCALE:
                if (!(item instanceof ScaleQuestionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, QUESTION_SCALE types must be ScaleQuestionSurveyItem");
                }
                ScaleQuestionSurveyItem scaleItem = (ScaleQuestionSurveyItem)item;
                // TODO: create scale answer formats, see iOS versions
                format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                        new Choice<>("TODO: Scale (integer, continuous, text) survey not implemented", true));
                break;
            case QUESTION_MULTIPLE_CHOICE:
            case QUESTION_SINGLE_CHOICE:
            {
                if (!(item instanceof ChoiceQuestionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, this type must be ChoiceQuestionSurveyItem");
                }
                ChoiceQuestionSurveyItem singleItem = (ChoiceQuestionSurveyItem)item;
                if (singleItem.items == null || singleItem.items.isEmpty()) {
                    throw new IllegalStateException("ChoiceQuestionSurveyItem must have choices");
                }
                AnswerFormat.ChoiceAnswerStyle answerStyle = AnswerFormat.ChoiceAnswerStyle.SingleChoice;
                if (item.type == SurveyItemType.QUESTION_MULTIPLE_CHOICE) {
                    answerStyle = AnswerFormat.ChoiceAnswerStyle.MultipleChoice;
                }
                Choice[] choices = singleItem.items.toArray(new Choice[singleItem.items.size()]);
                format = new ChoiceAnswerFormat(answerStyle, choices);
                break;
            }
            case QUESTION_TIMING_RANGE:     // Single choice question, but with "Not Sure" as an added option
            {
                if (!(item instanceof TimingRangeQuestionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, QUESTION_TIMING_RANGE type must be TimingRangeQuestionSurveyItem");
                }
                if (item.items == null || item.items.isEmpty()) {
                    throw new IllegalStateException("TimingRangeQuestionSurveyItem must have Range items");
                }
                List<Choice<String>> choiceList = new ArrayList<>();
                TimingRangeQuestionSurveyItem subItem = (TimingRangeQuestionSurveyItem)item;
                for (IntegerRangeSurveyItem integerRange : subItem.items) {
                    choiceList.add(convertToTextChoice(context, integerRange));
                }
                String notSure = context.getString(R.string.rsb_not_sure);
                choiceList.add(new Choice<>(notSure, notSure));
                Choice[] choices = choiceList.toArray(new Choice[choiceList.size()]);
                format = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice, choices);
                break;
            }
            case QUESTION_TEXT:
                format = new TextAnswerFormat();
                break;
        }

        QuestionStep step = null;
        // Attach the navigation components to the step if there are any
        if (item.usesNavigation()) {
            NavigationExpectedAnswerQuestionStep navStep = new NavigationExpectedAnswerQuestionStep(item.identifier, item.title, format);
            transferNavigationRules(item, navStep);
            step = navStep;
        } else {
            step = new QuestionStep(item.identifier, item.title, format);
        }
        step.setText(item.text);
        step.setOptional(item.optional);
        step.setPlaceholder(item.placeholderText);
        // TODO: iOS has footnote, do we need that as well?

        return step;
    }

    /**
     * Toggles are actually a FormStep, since they are a list of other QuestionSteps
     * Similar to a subtask step, but only as it relates to QuestionSurveyItems
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item ToggleQuestionSurveyItem from JSON, that has nested boolean QuestionSurveyItems
     * @return a ToggleFormStep which is a form step that is also a NavigationStep
     */
    public ToggleFormStep createToggleFormStep(Context context, ToggleQuestionSurveyItem item) {
        if (item.items == null || item.items.isEmpty()) {
            throw new IllegalStateException("toggle questions must have questions in the json");
        }
        List<QuestionStep> questionSteps = new ArrayList<>();
        for (BooleanQuestionSurveyItem questionItem : item.items) {
            QuestionStep questionStep = createQuestionStep(context, questionItem);
            questionSteps.add(questionStep);
        }

        ToggleFormStep step = new ToggleFormStep(item.identifier, item.title, item.text, questionSteps);
        transferNavigationRules(item, step);

        return step;
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param profileInfoOptions type of profile item that should be included in profile form step
     * @param addConfirmPasswordOption true if confirm password should be added with password, false otherwise
     * @return a QuestionStep that can be used to get the correct data type for ProfileInfoOption
     */
    public List<QuestionStep> createQuestionSteps(
            Context context,
            List<ProfileInfoOption> profileInfoOptions,
            boolean addConfirmPasswordOption)
    {
        List<QuestionStep> questionSteps = new ArrayList<>();
        for (ProfileInfoOption profileInfo : profileInfoOptions) {
            switch (profileInfo) {
                case EMAIL:
                    questionSteps.add(createEmailQuestionStep(context, profileInfo));
                    break;
                case PASSWORD:
                    questionSteps.add(createPasswordQuestionStep(context, profileInfo));
                    // For password fields, we also need a "Confirm Password" field, if applicable
                    if (addConfirmPasswordOption) {
                        questionSteps.add(createConfirmPasswordQuestionStep(context));
                    }
                    break;
                case NAME:
                    questionSteps.add(createNameQuestionStep(context, profileInfo));
                    break;
                case BIRTHDATE:
                    questionSteps.add(createBirthdateQuestionStep(context, profileInfo));
                    break;
                case GENDER:
                    createGenderQuestionStep(context, profileInfo);
                    break;
                case EXTERNAL_ID:
                    // TODO: implement external ID step, which is used for internal app usage
                    break;
                case BLOOD_TYPE:            // ChoiceTextAnswerFormat, see HealthKit blood types
                case FITZPATRICK_SKIN_TYPE: // ChoiceTextAnswerFormat
                case WHEEL_CHAIR_USE:       // boolean?
                case HEIGHT:                // ScaleAnswerFormat  with units
                case WEIGHT:                // ScaleAnswerFormat  with units
                case WAKE_TIME:             // DateAnswerFormat, but with Time value
                case SLEEP_TIME:            // DateAnswerFormat, but with Time value
                    // TODO: implement these when we need to
                    break;
            }
        }
        return questionSteps;
    }

    /**
     * @param context used to generate title and placeholder title for step
     * @param profileOption used to set step identifier
     * @return QuestionStep used for gathering user's email
     */
    public QuestionStep createEmailQuestionStep(Context context, ProfileInfoOption profileOption) {
        return createGenericQuestionStep(context,
                profileOption.getIdentifier(),
                R.string.rsb_email,
                R.string.rsb_email_placeholder,
                new EmailAnswerFormat());
    }

    /**
     * @param context used to generate title and placeholder title for step
     * @param profileOption used to set step identifier
     * @return QuestionStep used for gathering user's password
     */
    public QuestionStep createPasswordQuestionStep(Context context, ProfileInfoOption profileOption) {
        return createGenericQuestionStep(context,
                profileOption.getIdentifier(),
                R.string.rsb_password,
                R.string.rsb_password_placeholder,
                new PasswordAnswerFormat());
    }

    /**
     * @param context used to generate title and placeholder title for step
     * @param profileOption used to set step identifier
     * @return QuestionStep used for gathering user's password
     */
    public QuestionStep createNameQuestionStep(Context context, ProfileInfoOption profileOption) {
        TextAnswerFormat format = new TextAnswerFormat();
        format.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        format.setIsMultipleLines(false);

        return createGenericQuestionStep(context,
                profileOption.getIdentifier(),
                R.string.rsb_name,
                R.string.rsb_name_placeholder,
                format);
    }

    /**
     * @param context used to generate title and placeholder title for step
     * @return QuestionStep used for gathering user's password
     */
    public QuestionStep createConfirmPasswordQuestionStep(Context context) {
        return createGenericQuestionStep(context,
                PASSWORD_CONFIRMATION_IDENTIFIER,
                R.string.rsb_confirm_password,
                R.string.rsb_confirm_password_placeholder,
                new PasswordAnswerFormat());
    }

    /**
     * @param context used to generate title and placeholder title for step
     * @param profileOption that will be associated with this QuestionStep
     * @return QuestionStep used for gathering user's password
     */
    public QuestionStep createBirthdateQuestionStep(Context context, ProfileInfoOption profileOption) {
        return createGenericQuestionStep(context,
                profileOption.getIdentifier(),
                R.string.rsb_birthdate,
                R.string.rsb_birthdate_placeholder,
                new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date));
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param profileOption that will be associated with this QuestionStep
     * @return QuestionStep used for gathering user's password
     */
    public QuestionStep createGenderQuestionStep(Context context, ProfileInfoOption profileOption) {
        return createGenericQuestionStep(context,
                profileOption.getIdentifier(),
                R.string.rsb_gender,
                R.string.rsb_gender_placeholder,
                new GenderAnswerFormat(context));
    }

    /**
     * a helper method to make a re-usable generic method for creating question steps
     * @param context can be any context, activity or application, used to access "R" resources
     * @param identifier the identifier for the question step
     * @param titleRes the string resource for the title of the question step
     * @param placeholderRes the string resource for the placeholder title for the question step
     * @param format the answer format for the question step
     * @return QuestionStep with title, placeholder, and format all filled in
     */
    public QuestionStep createGenericQuestionStep(
            Context context,
            String identifier,
            @StringRes int titleRes,
            @StringRes int placeholderRes,
            AnswerFormat format)
    {
        String title = context.getString(titleRes);
        QuestionStep step = new QuestionStep(identifier, title, format);
        String placeholder = context.getString(placeholderRes);
        step.setPlaceholder(placeholder);
        step.setOptional(false);
        return step;
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item ProfileSurveyItem from JSON
     * @return valid RegistrationStep matching the ProfileSurveyItem
     */
    public RegistrationStep createRegistrationStep(Context context, ProfileSurveyItem item) {
        List<ProfileInfoOption> options = createProfileInfoOptions(context, item, defaultRegistrationOptions());
        return new RegistrationStep(
                item.identifier, item.title, item.text,
                options, createQuestionSteps(context, options, true)); // true = create ConfirmPassword step
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item ProfileSurveyItem from JSON
     * @return valid ProfileStep matching the ProfileSurveyItem
     */
    public ProfileStep createProfileStep(Context context, ProfileSurveyItem item) {
        List<ProfileInfoOption> options = createProfileInfoOptions(context, item, defaultProfileOptions());
        return new ProfileStep(
                item.identifier, item.title, item.text,
                options, createQuestionSteps(context, options, false)); // false = dont create ConfirmPassword step
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item InstructionSurveyItem from JSON
     * @return valid EmailVerificationSubStep matching the InstructionSurveyItem
     */
    public LoginStep createLoginStep(Context context, ProfileSurveyItem item) {
        List<ProfileInfoOption> options = createProfileInfoOptions(context, item, defaultLoginOptions());
        return new LoginStep(
                item.identifier, item.title, item.text,
                options, createQuestionSteps(context, options, false)); // false = dont create ConfirmPassword step
    }

    /** Helper for determining which profile info options to use */
    List<ProfileInfoOption> createProfileInfoOptions(
            Context context,
            ProfileSurveyItem item,
            List<ProfileInfoOption> defaultOptions)
    {
        List<ProfileInfoOption> options = defaultOptions;
        // Use profile info options that were provided in the JSON if available
        if (item.items != null && !item.items.isEmpty()) {
            options = ProfileInfoOption.toProfileInfoOptions(item.items);
        }
        return options;
    }

    List<ProfileInfoOption> defaultLoginOptions() {
        List<ProfileInfoOption> profileInfo = new ArrayList<>();
        profileInfo.add(ProfileInfoOption.EMAIL);
        profileInfo.add(ProfileInfoOption.PASSWORD);
        return profileInfo;
    }

    List<ProfileInfoOption> defaultRegistrationOptions() {
        List<ProfileInfoOption> profileInfo = new ArrayList<>();
        profileInfo.add(ProfileInfoOption.NAME);
        profileInfo.add(ProfileInfoOption.EMAIL);
        profileInfo.add(ProfileInfoOption.PASSWORD);
        return profileInfo;
    }

    List<ProfileInfoOption> defaultProfileOptions() {
        List<ProfileInfoOption> profileInfo = new ArrayList<>();  // blank for default profile
        return profileInfo;
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item InstructionSurveyItem from JSON
     * @return valid EmailVerificationStep matching the InstructionSurveyItem
     */
    public EmailVerificationStep createEmailVerificationStep(Context context, InstructionSurveyItem item) {
        EmailVerificationSubStep emailSubstep = new EmailVerificationSubStep(
                EMAIL_VERIFICATION_SUBSTEP_IDENTIFIER, item.title, item.text);
        fillInstructionStep(emailSubstep, item);

        String changeEmailTitle = context.getString(R.string.rsb_change_email_title);
        RegistrationStep registrationStep = new RegistrationStep(
                context, this, OnboardingSection.REGISTRATION_IDENTIFIER, changeEmailTitle, null);

        EmailVerificationStep emailVerificationStep = new EmailVerificationStep(
                item.identifier, emailSubstep, registrationStep);

        return emailVerificationStep;
    }

    /**
     * @param item SurveyItem from JSON
     * @return valid PermissionsStep matching the SurveyItem
     */
    public PermissionsStep createPermissionsStep(SurveyItem item) {
        return new PermissionsStep(item.identifier, item.title, item.text);
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item SurveyItem from JSON
     * @return valid PasscodeStep matching the SurveyItem
     */
    public Step createPasscodeStep(Context context, SurveyItem item) {

        PasscodeStep step = new PasscodeStep(item.identifier, item.title, item.text);
        step.setStateOrdinal(PasscodeCreationStepLayout.State.CREATE.ordinal());

        // Fingerprint API was added with api 23
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);

            // This is by far the most secure way to store data, so if the user has it,
            // we will make them take advantage of it; however, they can switch to passcode from the step layout
            if (fingerprintManager.isHardwareDetected() &&
                fingerprintManager.hasEnrolledFingerprints())
            {
                step.setUseFingerprint(true);
            }
        }

        return step;
    }

    public ShareTheAppStep createShareTheAppStep(Context context, InstructionSurveyItem item) {
        ShareTheAppStep step = new ShareTheAppStep(item.identifier, item.title, item.text);
        fillInstructionStep(step, item);

        if (item.items != null && !item.items.isEmpty()) {
            step.setShareTypeList(ShareTheAppStep.ShareType.toShareTypeList(item.items));
        }

        if (step.getTitle() == null) {
            step.setTitle(context.getString(R.string.rsb_share_the_app_title));
        }

        if (step.getText() == null) {
            step.setText(context.getString(R.string.rsb_share_the_app_text));
        }

        return step;
    }

    public ActiveStep createActiveStep(Context context, ActiveStepSurveyItem item) {
        ActiveStep step = new ActiveStep(item.identifier, item.title, item.text);
        return step;
    }

    /**
     * @param context can be any context, activity or application, used to access "R" resources
     * @param item InstructionSurveyItem from JSON
     * @param isSubtaskStep true if this is within a subtask step already, false otherwise
     * @return valid CustomStep matching the InstructionSurveyItem
     */
    public Step createCustomStep(Context context, SurveyItem item, boolean isSubtaskStep) {
        return new InstructionStep(item.identifier, item.title, item.text);
    }

    /*
     * Transfers the QuestionSurveyItem nav properties over to NavigationStep
     */
    private void transferNavigationRules(QuestionSurveyItem item, NavigationExpectedAnswerQuestionStep toStep) {
        toStep.setSkipIfPassed(item.skipIfPassed);
        toStep.setSkipToStepIdentifier(item.skipIdentifier);
        toStep.setExpectedAnswer(item.expectedAnswer);
    }

    /*
     * Transfers the QuestionSurveyItem nav properties over to NavigationStep
     */
    private void transferNavigationRules(QuestionSurveyItem item, NavigationFormStep toStep) {
        toStep.setSkipIfPassed(item.skipIfPassed);
        toStep.setSkipToStepIdentifier(item.skipIdentifier);
    }

    /*
     * Transfers the QuestionSurveyItem nav properties over to NavigationStep
     */
    private void transferNavigationRules(QuestionSurveyItem item, NavigationSubtaskStep toStep) {
        toStep.setSkipIfPassed(item.skipIfPassed);
        toStep.setSkipToStepIdentifier(item.skipIdentifier);
    }

    public Choice<String> convertToTextChoice(Context context, IntegerRangeSurveyItem item) {
        String timeUnitStr;
        switch (item.unit) {
            case "minutes":
                timeUnitStr = context.getString(R.string.rsb_time_minutes);
                break;
            case "hours":
                timeUnitStr = context.getString(R.string.rsb_time_hours);
                break;
            case "days":
                timeUnitStr = context.getString(R.string.rsb_time_days);
                break;
            case "weeks":
                timeUnitStr = context.getString(R.string.rsb_time_weeks);
                break;
            case "months":
                timeUnitStr = context.getString(R.string.rsb_time_months);
                break;
            case "years":
                timeUnitStr = context.getString(R.string.rsb_time_years);
                break;
            default:
                timeUnitStr = context.getString(R.string.rsb_time_seconds);
                break;
        }

        // Note: in all cases, the value is returned in English so that the localized
        // values will result in the same answer in any table. It is up to the researcher to translate.
        if (item.max != null) {
            // We also have a min
            if (item.min != null) {
                String maxStr = String.format(Locale.getDefault(), "%d %s", item.max, timeUnitStr);
                String maxText = String.format(context.getString(R.string.rsb_range_ago), maxStr);

                return new Choice<>(
                        String.format(Locale.getDefault(), "%d-%s", item.min, maxText),
                        String.format(Locale.US, "%d-%d %s ago", item.min, item.max, timeUnitStr));
            } else { // we just have a max
                String maSxtr = String.format(Locale.getDefault(), "%d %s", item.max, timeUnitStr);
                String text = String.format(context.getString(R.string.rsb_less_than_ago), maSxtr);
                return new Choice<>(text, String.format(Locale.US, "Less than %d %s ago", item.max, timeUnitStr));
            }
        } else { // does not have a max
            String minStr = String.format(Locale.getDefault(), "%d %s", item.min, timeUnitStr);
            String text = String.format(context.getString(R.string.rsb_more_than_ago), minStr);
            return new Choice<>(text, String.format(Locale.US, "More than %d %s ago", item.min, timeUnitStr));
        }
    }

    public void setCustomStepCreator(CustomStepCreator customStepCreator) {
        this.customStepCreator = customStepCreator;
    }

    public CustomStepCreator getCustomStepCreator() {
        return customStepCreator;
    }

    /**
     * This can be used by another class to implement custom conversion from a CustomSurveyItem to a CustomStep
     */
    public interface CustomStepCreator {
        Step createCustomStep(Context context, SurveyItem item, boolean isSubtaskStep, SurveyFactory factory);
    }
}