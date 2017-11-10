package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;

/**
 * Created by TheMDP on 12/31/16.
 */

public enum SurveyItemType {

    CUSTOM(null),
    // Subtask subtypes
    @SerializedName("subtask")
    SUBTASK                     ("subtask"),                // SubtaskStep
    // Instruction subtypes
    @SerializedName("instruction")
    INSTRUCTION                 ("instruction"),            // InstructionStep
    @SerializedName("completion")
    INSTRUCTION_COMPLETION      ("completion"),             // CompletionStep
    // Question, aka Form, Subtypes
    @SerializedName("compound")
    QUESTION_COMPOUND           ("compound"),               // QuestionSteps > 1
    @SerializedName("toggle")
    QUESTION_TOGGLE             ("toggle"),                 // SBABooleanToggleFormStep
    @SerializedName("boolean")
    QUESTION_BOOLEAN            ("boolean"),                // ORKBooleanAnswerFormat
    @SerializedName("singleChoiceText")
    QUESTION_SINGLE_CHOICE      ("singleChoiceText"),       // ORKTextChoiceAnswerFormat of style SingleChoiceTextQuestion
    @SerializedName("multipleChoiceText")
    QUESTION_MULTIPLE_CHOICE    ("multipleChoiceText"),     // ORKTextChoiceAnswerFormat of style MultipleChoiceTextQuestion
    @SerializedName("textfield")
    QUESTION_TEXT               ("textfield"),              // ORKTextAnswerFormat
    @SerializedName("datePicker")
    QUESTION_DATE               ("datePicker"),             // ORKDateAnswerFormat of style Date
    @SerializedName("timeAndDatePicker")
    QUESTION_DATE_TIME          ("timeAndDatePicker"),      // ORKDateAnswerFormat of style DateTime
    @SerializedName("timePicker")
    QUESTION_TIME               ("timePicker"),             // ORKTimeOfDayAnswerFormat
    @SerializedName("timeInterval")
    QUESTION_DURATION           ("timeInterval"),           // ORKTimeIntervalAnswerFormat
    @SerializedName("numericInteger")
    QUESTION_INTEGER            ("numericInteger"),         // ORKNumericAnswerFormat of style Integer
    @SerializedName("numericDecimal")
    QUESTION_DECIMAL            ("numericDecimal"),         // ORKNumericAnswerFormat of style Decimal
    @SerializedName("scaleInteger")
    QUESTION_SCALE              ("scaleInteger"),           // ORKScaleAnswerFormat
    @SerializedName("timingRange")
    QUESTION_TIMING_RANGE       ("timingRange"),            // Timing Range: ORKTextChoiceAnswerFormat of style SingleChoiceTextQuestion
    // Account subtypes
    @SerializedName("registration")
    ACCOUNT_REGISTRATION        ("registration"        ),   // ProfileStep
    @SerializedName("login")
    ACCOUNT_LOGIN               ("login"               ),   // LoginStep
    @SerializedName("emailVerification")
    ACCOUNT_EMAIL_VERIFICATION  ("emailVerification"   ),   // EmailVerificationStep
    @SerializedName("externalID")
    ACCOUNT_EXTERNAL_ID         ("externalID"          ),   // ExternalIDStep
    @SerializedName("permissions")
    ACCOUNT_PERMISSIONS         ("permissions"         ),   // PermissionsStep
    @SerializedName("onboardingCompletion")
    ACCOUNT_COMPLETION          ("onboardingCompletion"),   // OnboardingCompletionStep
    @SerializedName("dataGroups")
    ACCOUNT_DATA_GROUPS         ("dataGroups"),             // DataGroupsStep
    @SerializedName("profile")
    ACCOUNT_PROFILE             ("profile"),                // ProfileQuestionStep or ProfileFormStep
    @SerializedName("shareApp")
    SHARE_THE_APP               ("shareApp"),               // ShareTheAppStep
    // Passcode subtypes
    @SerializedName(value="PASSCODE", alternate={"passcodeType4Digit", "passcodeType6Digit"})
    PASSCODE                    ("passcode"),               // iOS has 6 digit too, but for now only support 4 digit
    // Active Step
    @SerializedName("active")
    ACTIVE_STEP    ("active"),                              // ActiveStep


    // Consent subtypes
    @SerializedName(ConsentDocumentFactory.CONSENT_SHARING_IDENTIFIER)
    CONSENT_SHARING_OPTIONS     (ConsentDocumentFactory.CONSENT_SHARING_IDENTIFIER),  // ConsentSharingStep
    @SerializedName(ConsentDocumentFactory.CONSENT_REVIEW_IDENTIFIER)
    CONSENT_REVIEW              (ConsentDocumentFactory.CONSENT_REVIEW_IDENTIFIER),   // ConsentReviewStep
    @SerializedName             ("consentVisual")
    CONSENT_VISUAL              ("consentVisual"),                                    // VisualConsentStep
    @SerializedName             ("reconsent.instruction")
    RE_CONSENT                  ("reconsent.instruction");                            // ReconsentInstructionStep

    SurveyItemType(String rawValue) {
        value = rawValue;
    }

    private String value;
    public String getValue() {
        return value;
    }

    public boolean isQuestionSubtype() {
        switch (this) {
            case QUESTION_COMPOUND:
            case QUESTION_TOGGLE:
            case QUESTION_BOOLEAN:
            case QUESTION_SINGLE_CHOICE:
            case QUESTION_MULTIPLE_CHOICE:
            case QUESTION_TEXT:
            case QUESTION_DATE:
            case QUESTION_DATE_TIME:
            case QUESTION_TIME:
            case QUESTION_DURATION:
            case QUESTION_INTEGER:
            case QUESTION_DECIMAL:
            case QUESTION_SCALE:
            case QUESTION_TIMING_RANGE:
                return true;
            default:
                return false;
        }
    }
}
