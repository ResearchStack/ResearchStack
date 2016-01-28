package co.touchlab.researchstack.coreapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import co.touchlab.researchstack.core.StorageAccess;
import co.touchlab.researchstack.core.answerformat.AnswerFormat;
import co.touchlab.researchstack.core.answerformat.BooleanAnswerFormat;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.answerformat.DecimalAnswerFormat;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.answerformat.UnknownAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.model.ConsentSection;
import co.touchlab.researchstack.core.model.ConsentSignature;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.ConsentDocumentStep;
import co.touchlab.researchstack.core.step.ConsentSignatureStep;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.step.FormStep;
import co.touchlab.researchstack.core.step.InstructionStep;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.task.OrderedTask;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.PinCodeActivity;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.core.ui.step.layout.ConsentSignatureStepLayout;

public class MainActivity extends PinCodeActivity
{

    public static final  String FORM_STEP                 = "form_step";
    public static final  String AGE                       = "age";
    public static final  String INSTRUCTION               = "identifier";
    public static final  String BASIC_INFO_HEADER         = "basic_info_header";
    public static final  String FORM_AGE                  = "form_age";
    public static final  String FORM_GENDER               = "gender";
    public static final  String FORM_MULTI_CHOICE         = "multi_choice";
    public static final  String FORM_DATE_OF_BIRTH        = "date_of_birth";
    public static final  String SURVEY_PATH               = "/survey_";
    public static final  String CONSENT_PATH              = "/consent_";
    public static final  String NUTRITION                 = "nutrition";
    public static final  String SIGNATURE                 = "signature";
    public static final  String SIGNATURE_DATE            = "signature_date";
    public static final  String VISUAL_CONSENT_IDENTIFIER = "visual_consent_identifier";
    public static final  String CONSENT_DOC               = "consent_doc";
    public static final  String SIGNATURE_FORM_STEP       = "form_step";
    public static final  String NAME                      = "name";
    public static final  String CONSENT                   = "consent";
    public static final  String MULTI_STEP                = "multi_step";
    public static final  String DATE                      = "date";
    public static final String DECIMAL       = "decimal";
    private static final int    REQUEST_CONSENT           = 0;
    private static final int    REQUEST_SURVEY            = 1;
    private static final String FORM_NAME                 = "form_name";
    public static final String SAMPLE_SURVEY = "sample_survey";
    private AppCompatButton consentButton;
    private AppCompatButton surveyButton;
    private AppCompatButton clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consentButton = (AppCompatButton) findViewById(R.id.consent_button);
        consentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launchConsent();
            }
        });
        surveyButton = (AppCompatButton) findViewById(R.id.survey_button);
        surveyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launchSurvey();
            }
        });
        clearButton = (AppCompatButton) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clearData();
            }
        });

    }

    private void clearData()
    {
        AppPrefs appPrefs = AppPrefs.getInstance(this);
        appPrefs.setHasSurveyed(false);
        appPrefs.setHasConsented(false);

        finish();
    }

    @Override
    public void onDataReady()
    {
        super.onDataReady();
        initViews();
    }

    private void initViews()
    {
        AppPrefs prefs = AppPrefs.getInstance(this);
        if(prefs.hasConsented())
        {
            consentButton.setEnabled(false);
            consentButton.setText(R.string.consent_button_done);
            surveyButton.setEnabled(true);

            TaskResult result = StorageAccess.getAppDatabase().loadLatestTaskResult(CONSENT);

            // TODO form step result saving is messed up (gson saving inner stepresult as map)
            //            String fullName = ((StepResult<String>) result.getStepResult(SIGNATURE_FORM_STEP)
            //                    .getResultForIdentifier(NAME)).getResult();

            String signatureBase64 = (String) result.getStepResult(SIGNATURE)
                    .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);

            String signatureDate = (String) result.getStepResult(SIGNATURE)
                    .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);

            printConsentInfo("", signatureBase64, signatureDate);
        }
        else
        {
            consentButton.setEnabled(true);
            consentButton.setText(R.string.consent_button);
            surveyButton.setEnabled(false);
        }

        if(prefs.hasSurveyed())
        {
            printSurveyInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CONSENT && resultCode == RESULT_OK)
        {
            processConsentResult((TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT));
        }
        else if(requestCode == REQUEST_SURVEY && resultCode == RESULT_OK)
        {
            processSurveyResult((TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT));
        }
    }

    private void launchConsent()
    {
        ConsentDocument document = new ConsentDocument();
        document.setTitle("Demo Consent");
        document.setSignaturePageTitle(R.string.rsc_consent);

        // Create consent visual sections
        ConsentSection section1 = new ConsentSection(ConsentSection.Type.DataGathering);
        section1.setTitle("The title of the section goes here ...");
        section1.setSummary("The summary about the section goes here ...");
        section1.setContent("The content to show in learn more ...");

        // ...add more sections as needed, then create a visual consent step
        ConsentVisualStep visualStep = new ConsentVisualStep(VISUAL_CONSENT_IDENTIFIER);
        visualStep.setSection(section1);
        visualStep.setNextButtonString(getString(R.string.rsc_next));

        // Create consent signature object and set what info is required
        ConsentSignature signature = new ConsentSignature();
        signature.setRequiresName(true);
        signature.setRequiresSignatureImage(true);

        // Create our HTML to show the user and have them accept or decline.
        StringBuilder docBuilder = new StringBuilder(
                "</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
        String title = getString(R.string.rsc_consent_review_title);
        docBuilder.append(String.format(
                "<h1 style=\"text-align: center; font-family:sans-serif-light;\">%1$s</h1>",
                title));
        String detail = getString(R.string.rsc_consent_review_instruction);
        docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
        docBuilder.append("</div></br>");
        docBuilder.append("<div><h2> HTML Consent Doc goes here </h2></div>");

        // Create the Consent doc step, pass in our HTML doc
        ConsentDocumentStep documentStep = new ConsentDocumentStep(CONSENT_DOC);
        documentStep.setConsentHTML(docBuilder.toString());
        documentStep.setConfirmMessage(getString(R.string.rsc_consent_review_reason));

        // Create Consent form step, to get users first & last name
        FormStep formStep = new FormStep(SIGNATURE_FORM_STEP,
                "Form Title",
                "Form step description");
        formStep.setSceneTitle(R.string.rsc_consent);

        TextAnswerFormat format = new TextAnswerFormat();
        format.setIsMultipleLines(false);

        QuestionStep fullName = new QuestionStep(NAME, "Full name", format);
        formStep.setFormSteps(Collections.singletonList(fullName));

        // Create Consent signature step, user can sign their name
        ConsentSignatureStep signatureStep = new ConsentSignatureStep(SIGNATURE);
        signatureStep.setSceneTitle(R.string.rsc_consent);
        signatureStep.setTitle(getString(R.string.rsc_consent_signature_title));
        signatureStep.setText(getString(R.string.rsc_consent_signature_instruction));
        signatureStep.setSignatureDateFormat(signature.getSignatureDateFormatString());
        signatureStep.setOptional(false);
        signatureStep.setSceneClass(ConsentSignatureStepLayout.class);

        // Finally, create and present a task including these steps.
        Task consentTask = new OrderedTask(CONSENT,
                visualStep,
                documentStep,
                formStep,
                signatureStep);

        // Launch using hte ViewTaskActivity and make sure to listen for the activity result
        Intent intent = ViewTaskActivity.newIntent(this, consentTask);
        startActivityForResult(intent, REQUEST_CONSENT);
    }

    private void processConsentResult(TaskResult result)
    {
        boolean consented = (boolean) result.getStepResult(CONSENT_DOC).getResult();

        if(consented)
        {
            StorageAccess.getAppDatabase().saveTaskResult(result);

            AppPrefs prefs = AppPrefs.getInstance(this);
            prefs.setHasConsented(true);

            initViews();
        }
    }

    private void printConsentInfo(String fullName, String signatureBase64, String consentDate)
    {
        ((TextView) findViewById(R.id.consented_name)).setText(fullName);
        ((TextView) findViewById(R.id.consented_date)).setText(consentDate);
        byte[] signatureBytes = Base64.decode(signatureBase64, Base64.DEFAULT);
        ((ImageView) findViewById(R.id.consented_signature)).setImageBitmap(BitmapFactory.decodeByteArray(
                signatureBytes,
                0,
                signatureBytes.length));
    }

    private void printSurveyInfo()
    {
        TaskResult taskResult = StorageAccess.getAppDatabase().loadLatestTaskResult(SAMPLE_SURVEY);

        String results = "";
        for(String id : taskResult.getResults().keySet())
        {
            StepResult stepResult = taskResult.getStepResult(id);
            results += id + ": " + stepResult.getResult().toString() + "\n";
        }
        ((TextView) findViewById(R.id.survey_results)).setText(results);
    }

    private void launchSurvey()
    {
        InstructionStep instructionStep = new InstructionStep(INSTRUCTION,
                "Selection Survey",
                "This survey can help us understand your eligibility for the fitness study");

        TextAnswerFormat format = new TextAnswerFormat();
        QuestionStep ageStep = new QuestionStep(NAME, "How old are you?", format);

        DateAnswerFormat dateFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
        QuestionStep dateStep = new QuestionStep(DATE, "Enter a date", dateFormat);

        // unimplemented
        DecimalAnswerFormat decimalAnswerFormat = new DecimalAnswerFormat(0f, 1f);
        QuestionStep decimalStep = new QuestionStep(DECIMAL, "Decimal step", decimalAnswerFormat);

        // TODO off until formstep result saving is fixed
        //        FormStep formStep = createFormStep();

        // Create a Boolean step to include in the task.
        QuestionStep booleanStep = new QuestionStep(NUTRITION);
        booleanStep.setTitle("Do you take nutritional supplements?");
        booleanStep.setAnswerFormat(new BooleanAnswerFormat());
        booleanStep.setOptional(false);

        QuestionStep multiStep = new QuestionStep(MULTI_STEP);
        AnswerFormat multiFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.MultipleChoice,
                new Choice<>("Zero", 0),
                new Choice<>("One", 1),
                new Choice<>("Two", 2));
        multiStep.setTitle("Select multiple");
        multiStep.setAnswerFormat(multiFormat);
        multiStep.setOptional(false);

        // Create a task wrapping the steps.
        OrderedTask task = new OrderedTask(SAMPLE_SURVEY,
                instructionStep,
                ageStep,
                dateStep,
                decimalStep,
                //                formStep,
                booleanStep,
                multiStep);

        // Create a task view controller using the task and set a delegate.
        Intent intent = ViewTaskActivity.newIntent(this, task);
        startActivityForResult(intent, REQUEST_SURVEY);
    }

    @NonNull
    private FormStep createFormStep()
    {
        FormStep formStep = new FormStep(FORM_STEP, "Form", "Form groups multi-entry in one page");
        ArrayList<QuestionStep> formItems = new ArrayList<>();

        QuestionStep basicInfoHeader = new QuestionStep(BASIC_INFO_HEADER,
                "Basic Information",
                new UnknownAnswerFormat());
        formItems.add(basicInfoHeader);

        TextAnswerFormat format = new TextAnswerFormat();
        format.setIsMultipleLines(false);
        QuestionStep nameItem = new QuestionStep(FORM_NAME, "Name", format);
        formItems.add(nameItem);

        QuestionStep ageItem = new QuestionStep(FORM_AGE, "Age", new IntegerAnswerFormat(90, 18));
        formItems.add(ageItem);

        AnswerFormat genderFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                new Choice<>("Male", 0),
                new Choice<>("Female", 1));
        QuestionStep genderFormItem = new QuestionStep(FORM_GENDER, "Gender", genderFormat);
        formItems.add(genderFormItem);

        AnswerFormat multiFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.MultipleChoice,
                new Choice<>("Zero", 0),
                new Choice<>("One", 1),
                new Choice<>("Two", 2));
        QuestionStep multiFormItem = new QuestionStep(FORM_MULTI_CHOICE, "Test Multi", multiFormat);
        formItems.add(multiFormItem);

        AnswerFormat dateOfBirthFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
        QuestionStep dateOfBirthFormItem = new QuestionStep(FORM_DATE_OF_BIRTH,
                "Birthdate",
                dateOfBirthFormat);
        formItems.add(dateOfBirthFormItem);

        // ... And so on, adding additional items
        formStep.setFormSteps(formItems);
        return formStep;
    }

    private void processSurveyResult(TaskResult result)
    {
        StorageAccess.getAppDatabase().saveTaskResult(result);

        AppPrefs prefs = AppPrefs.getInstance(this);
        prefs.setHasSurveyed(true);
        initViews();
    }
}
