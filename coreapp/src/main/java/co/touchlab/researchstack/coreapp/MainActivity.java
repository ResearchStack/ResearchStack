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
import java.util.Arrays;
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
    public static final  String DECIMAL                   = "decimal";
    private static final int    REQUEST_CONSENT           = 0;
    private static final int    REQUEST_SURVEY            = 1;
    private static final String FORM_NAME                 = "form_name";
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
        StorageAccess storageAccess = StorageAccess.getInstance();
        storageAccess.clearData(this, CONSENT_PATH + NAME);
        storageAccess.clearData(this, CONSENT_PATH + SIGNATURE);
        storageAccess.clearData(this, CONSENT_PATH + SIGNATURE_DATE);

        AppPrefs appPrefs = AppPrefs.getInstance(this);
        appPrefs.setHasSurveyed(false);
        appPrefs.setHasConsented(false);

        finish();
    }

    @Override
    protected void onDataReady()
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
            printConsentInfo(loadString(CONSENT_PATH + NAME),
                    loadString(CONSENT_PATH + SIGNATURE),
                    loadString(CONSENT_PATH + SIGNATURE_DATE));
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
                CONSENT,
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
            String fullName = ((StepResult<String>) result.getStepResult(SIGNATURE_FORM_STEP)
                    .getResultForIdentifier(NAME)).getResult();

            String signatureBase64 = (String) result.getStepResult(SIGNATURE)
                    .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);

            String signatureDate = (String) result.getStepResult(SIGNATURE)
                    .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);

            AppPrefs prefs = AppPrefs.getInstance(this);
            prefs.setHasConsented(true);

            saveString(CONSENT_PATH + NAME, fullName);

            saveString(CONSENT_PATH + SIGNATURE, signatureBase64);

            saveString(CONSENT_PATH + SIGNATURE_DATE, signatureDate);

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
        String[] resultKeys = new String[] {
                NAME,
                DATE,
                FORM_NAME,
                FORM_AGE,
                FORM_GENDER,
                FORM_MULTI_CHOICE,
                FORM_DATE_OF_BIRTH,
                NUTRITION,
                MULTI_STEP
        };

        String results = "";
        for(String resultKey : resultKeys)
        {
            results += resultKey + ": " + loadString(SURVEY_PATH + resultKey) + "\n";
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

        FormStep formStep = createFormStep();

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
        OrderedTask task = new OrderedTask("ordered_task",
                "schedule_id",
                instructionStep,
                ageStep,
                dateStep,
                decimalStep,
                formStep,
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
        StepResult<StepResult> formStep = result.getStepResult(FORM_STEP);

        String formName = (String) formStep.getResultForIdentifier(FORM_NAME).getResult();
        saveString(SURVEY_PATH + FORM_NAME, formName);

        Integer formAge = (Integer) formStep.getResultForIdentifier(FORM_AGE).getResult();
        saveString(SURVEY_PATH + FORM_AGE, String.valueOf(formAge));

        String date = (String) result.getStepResult(DATE).getResult();
        saveString(SURVEY_PATH + DATE, date);

        Integer gender = (Integer) formStep.getResultForIdentifier(FORM_GENDER).getResult();
        saveString(SURVEY_PATH + FORM_GENDER, gender == 0 ? "Male" : "Female");

        Object[] multiChoice = (Object[]) formStep.getResultForIdentifier(FORM_MULTI_CHOICE)
                .getResult();
        saveString(SURVEY_PATH + FORM_MULTI_CHOICE, Arrays.toString(multiChoice));

        String dateofBirth = (String) formStep.getResultForIdentifier(FORM_DATE_OF_BIRTH)
                .getResult();
        saveString(SURVEY_PATH + FORM_DATE_OF_BIRTH, dateofBirth.toString());

        Integer nutrition = (Integer) result.getStepResult(NUTRITION).getResult();
        String nutritionString = nutrition == 0 ? "No" : "Yes";
        saveString(SURVEY_PATH + NUTRITION, nutritionString);

        Object[] multiStep = (Object[]) result.getStepResult(MULTI_STEP).getResult();
        saveString(SURVEY_PATH + MULTI_STEP, Arrays.toString(multiStep));

        AppPrefs prefs = AppPrefs.getInstance(this);
        prefs.setHasSurveyed(true);
        initViews();
    }

    private String loadString(String path)
    {
        try
        {
            return new String(StorageAccess.getInstance().loadFile(this, path));
        }
        catch(Exception e)
        {
            return "";
        }
    }

    private void saveString(String path, String string)
    {
        string = string == null ? "" : string;
        StorageAccess.getInstance().saveFile(this, path, string.getBytes());
    }
}
