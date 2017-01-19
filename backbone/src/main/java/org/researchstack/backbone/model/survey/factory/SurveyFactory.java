package org.researchstack.backbone.model.survey.factory;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.InputType;

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
import org.researchstack.backbone.model.survey.BooleanQuestionSurveyItem;
import org.researchstack.backbone.model.survey.ChoiceQuestionSurveyItem;
import org.researchstack.backbone.model.survey.CompoundQuestionSurveyItem;
import org.researchstack.backbone.model.survey.CustomSurveyItem;
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
import org.researchstack.backbone.model.survey.ToggleQuestionSurveyItem;
import org.researchstack.backbone.step.CustomStep;
import org.researchstack.backbone.step.EmailVerificationStep;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.LoginStep;
import org.researchstack.backbone.step.NavigationFormStep;
import org.researchstack.backbone.step.PasscodeStep;
import org.researchstack.backbone.step.PermissionsStep;
import org.researchstack.backbone.step.ProfileStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.RegistrationStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubtaskStep;
import org.researchstack.backbone.step.ToggleFormStep;
import org.researchstack.backbone.step.NavigationQuestionStep;
import org.researchstack.backbone.step.NavigationSubtaskStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 12/29/16.
 */

public class SurveyFactory {

    // The rest of them use the toString of ProfileInfoOption
    public static final String PASSWORD_CONFIRMATION_IDENTIFIER = "confirmation";
    public static final String CONSENT_SHARING_IDENTIFIER = "consentSharingOptions";

    // When set, this will be used
    CustomStepCreator customStepCreator;

    List<Step> steps;
    /*
     * @param Context is used to localize default true and false string values
     * @param List<SurveyItem>
     */
    public SurveyFactory(Context context, List<SurveyItem> surveyItems) {
        this(context, surveyItems, null);
    }

    /*
     * @param Context is used to localize default true and false string values
     * @param List<SurveyItem>
     */
    public SurveyFactory(Context context, List<SurveyItem> surveyItems, CustomStepCreator customStepCreator) {
        this.customStepCreator = customStepCreator;
        steps = createSteps(context, surveyItems, false);
    }

    SurveyFactory() {
        // Default constructor, mainly used for subclasses
    }

    public List<Step> getSteps() {
        return steps;
    }

    public List<Step> createSteps(Context context, List<SurveyItem> surveyItems, boolean isSubtaskStep) {
        List<Step> steps = new ArrayList<>();
        for (SurveyItem item : surveyItems) {
            Step step = createSurveyStep(context, item, isSubtaskStep);
            if (step != null) {
                steps.add(step);
            }
        }
        return steps;
    }

    public Step createSurveyStep(Context context, SurveyItem item, boolean isSubtaskStep) {
        switch (item.type) {
            case INSTRUCTION:
            case INSTRUCTION_COMPLETION:
                if (!(item instanceof InstructionSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, INSTRUCTION types must be InstructionSurveyItems");
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
                return createEmailVerificationStep((InstructionSurveyItem)item);
            case ACCOUNT_PERMISSIONS:
                return createPermissionsStep(item);
            case ACCOUNT_DATA_GROUPS:
                return createNotImplementedStep(item);
            case ACCOUNT_EXTERNAL_ID:
                return createNotImplementedStep(item);
            case PASSCODE:
                return createPasscodeStep(item);
            case CUSTOM:
                if (!(item instanceof CustomSurveyItem)) {
                    throw new IllegalStateException("Error in json parsing, CUSTOM types must be CustomSurveyItem");
                }
                CustomSurveyItem customItem = (CustomSurveyItem)item;
                // To override a custom step from survey item mapping,
                // You need to override the CustomStepCreator
                if (customStepCreator != null) {
                    return customStepCreator.createCustomStep(customItem, this);
                }
                return createCustomStep((CustomSurveyItem)item);
        }

        // Handled by ConsentDocumentFactory subclass
//        case CONSENT_REVIEW:
//        case CONSENT_SHARING_OPTIONS:
//        case CONSENT_VISUAL:

        return null;
    }

    /**
     * @param item InstructionSurveyItem from JSON
     * @return valid InstructionStep matching the InstructionSurveyItem
     */
    InstructionStep createInstructionStep(InstructionSurveyItem item) {
        InstructionStep step = new InstructionStep(item.identifier, item.title, item.text);
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

    /** Helper method for instruction steps */
    void fillInstructionStep(InstructionStep step, InstructionSurveyItem item) {
        step.setFootnote(item.footnote);
        step.setNextStepIdentifier(item.nextIdentifier);
        step.setMoreDetailText(item.detailText);
        step.setImage(item.image);
        step.setIconImage(item.iconImage);
    }

    /**
     * @param item SubtaskQuestionSurveyItem item from JSON that contains nested SurveyItems
     * @return a subtask step by recursively calling createSurveyStep for inner subtask steps
     */
    SubtaskStep createSubtaskStep(Context context, SubtaskQuestionSurveyItem item) {
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
     * @param item SubtaskQuestionSurveyItem item from JSON that contains nested SurveyItems
     * @return a subtask step by recursively calling createSurveyStep for inner subtask steps
     */
    FormStep createCompoundStep(Context context, CompoundQuestionSurveyItem item) {
        if (item.items == null || item.items.isEmpty()) {
            throw new IllegalStateException("compound surveys must have step items to proceed");
        }

        List<QuestionStep> questionSteps = new ArrayList<>();
        for (QuestionSurveyItem subItem : item.items) {
            QuestionStep step = createQuestionStep(context, subItem);
            questionSteps.add(step);
        }

        FormStep step = new FormStep(item.identifier, item.title, item.text, questionSteps);
        return step;
    }

    /**
     * @param item QuestionSurveyItem from JSON
     * @return QuestionStep converted from the item
     */
    QuestionStep createQuestionStep(Context context, QuestionSurveyItem item) {
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
            case QUESTION_TIMING_RANGE:     // Single choice question, but with "Not Sure" as an added option
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
                } else if (item.type == SurveyItemType.QUESTION_TIMING_RANGE) {
                    if (!(singleItem.items.get(0).getValue() instanceof String)) {
                        throw new IllegalStateException("Error in json parsing, QUESTION_TIMING_RANGE text choices must be Strings");
                    }
                    String notSure = context.getString(R.string.rsb_not_sure);
                    singleItem.items.add(new Choice(notSure, notSure));
                }
                Choice[] choices = singleItem.items.toArray(new Choice[singleItem.items.size()]);
                format = new ChoiceAnswerFormat(answerStyle, choices);
                break;
            }
            case QUESTION_TEXT:
                format = new TextAnswerFormat();
                break;
        }

        QuestionStep step = null;
        // Attach the navigation components to the step if there are any
        if (item.usesNavigation()) {
            NavigationQuestionStep navStep = new NavigationQuestionStep(item.identifier, item.title, format);
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
     * @param item ToggleQuestionSurveyItem from JSON, that has nested boolean QuestionSurveyItems
     * @return a ToggleFormStep which is a form step that is also a NavigationStep
     */
    ToggleFormStep createToggleFormStep(Context context, ToggleQuestionSurveyItem item) {
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
     * @param profileInfoOptions type of profile item that should be included in profile form step
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
        // TODO: how do we designate the error message for AnswerFormat like in iOS?
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
     * @param context used to generate title and placeholder title for step
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
     * @return QuestionStep with title, placeholder, and format all filled in
     */
    QuestionStep createGenericQuestionStep(
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
     * @param item InstructionSurveyItem from JSON
     * @return valid EmailVerificationStep matching the InstructionSurveyItem
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
     * @param item InstructionSurveyItem from JSON
     * @return valid EmailVerificationStep matching the InstructionSurveyItem
     */
    public EmailVerificationStep createEmailVerificationStep(InstructionSurveyItem item) {
        EmailVerificationStep step = new EmailVerificationStep(item.identifier, item.title, item.text);
        fillInstructionStep(step, item);
        return step;
    }

    /**
     * @param item SurveyItem from JSON
     * @return valid PermissionsStep matching the SurveyItem
     */
    public PermissionsStep createPermissionsStep(SurveyItem item) {
        return new PermissionsStep(item.identifier, item.title, item.text);
    }

    /**
     * @param item SurveyItem from JSON
     * @return valid PasscodeStep matching the SurveyItem
     */
    public PasscodeStep createPasscodeStep(SurveyItem item) {
        return new PasscodeStep(item.identifier, item.title, item.text);
    }

    /**
     * @param item InstructionSurveyItem from JSON
     * @return valid CustomStep matching the InstructionSurveyItem
     */
    public CustomStep createCustomStep(CustomSurveyItem item) {
        CustomStep step = new CustomStep(item.identifier, item.title, item.getTypeIdentifier());
        step.setText(item.text);
        return step;
    }

    /*
     * Transfers the QuestionSurveyItem nav properties over to NavigationStep
     */
    void transferNavigationRules(QuestionSurveyItem item, NavigationQuestionStep toStep) {
        toStep.setSkipIfPassed(item.skipIfPassed);
        toStep.setSkipToStepIdentifier(item.skipIdentifier);
        toStep.setExpectedAnswer(item.expectedAnswer);
    }

    /*
     * Transfers the QuestionSurveyItem nav properties over to NavigationStep
     */
    void transferNavigationRules(QuestionSurveyItem item, NavigationFormStep toStep) {
        toStep.setSkipIfPassed(item.skipIfPassed);
        toStep.setSkipToStepIdentifier(item.skipIdentifier);
    }

    /*
     * Transfers the QuestionSurveyItem nav properties over to NavigationStep
     */
    void transferNavigationRules(QuestionSurveyItem item, NavigationSubtaskStep toStep) {
        toStep.setSkipIfPassed(item.skipIfPassed);
        toStep.setSkipToStepIdentifier(item.skipIdentifier);
    }

    /**
     * This can be used by another class to implement custom conversion from a CustomSurveyItem to a CustomStep
     */
    public interface CustomStepCreator {
        CustomStep createCustomStep(CustomSurveyItem item, SurveyFactory factory);
    }
}