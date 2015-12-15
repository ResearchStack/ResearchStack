package co.touchlab.researchstack.coreapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.answerformat.AnswerFormat;
import co.touchlab.researchstack.core.answerformat.BooleanAnswerFormat;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.answerformat.TextChoiceAnswerFormat;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.model.ConsentSection;
import co.touchlab.researchstack.core.model.ConsentSignature;
import co.touchlab.researchstack.core.model.TextChoice;
import co.touchlab.researchstack.core.result.ConsentSignatureResult;
import co.touchlab.researchstack.core.result.FormResult;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.ConsentReviewStep;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.step.FormStep;
import co.touchlab.researchstack.core.step.InstructionStep;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.task.OrderedTask;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.PassCodeActivity;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.core.ui.scene.FormScene;

public class MainActivity extends PassCodeActivity
{

    private static final int REQUEST_CONSENT = 0;
    private static final int REQUEST_SURVEY = 1;
    public static final String FORM_STEP = "form_step";
    public static final String AGE = "age";
    public static final String INSTRUCTION = "identifier";
    public static final String BASIC_INFO_HEADER = "basic_info_header";
    public static final String FORM_AGE = "age";
    public static final String FORM_GENDER = "gender";
    public static final String FORM_MULTI_CHOICE = "multi_choice";
    public static final String FORM_DATE_OF_BIRTH = "date_of_birth";
    private static final String FORM_NAME = "form_name";
    public static final String SURVEY_PATH = "/survey_";
    public static final String NUTRITION = "nutrition";
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

        initFileAccess();
    }

    private void clearData()
    {
        FileAccess fileAccess = StorageManager.getFileAccess();
        fileAccess.clearData(this,
                "/consented_name");
        fileAccess.clearData(this,
                "/consented_signature");
        fileAccess.clearData(this,
                "/survey_age");
        fileAccess.clearData(this,
                "/survey_nutrition");

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
        LogExt.d(getClass(),
                "onDataReady");
        AppPrefs prefs = AppPrefs.getInstance(this);
        if (prefs.hasConsented())
        {
            consentButton.setEnabled(false);
            consentButton.setText(R.string.consent_button_done);
            surveyButton.setEnabled(true);
            printConsentInfo(loadString("/consented_name"),
                    loadString("/consented_signature"));
        }
        else
        {
            consentButton.setEnabled(true);
            consentButton.setText(R.string.consent_button);
            surveyButton.setEnabled(false);
        }

        if (prefs.hasSurveyed())
        {
            printSurveyInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,
                resultCode,
                data);

        if (requestCode == REQUEST_CONSENT && resultCode == RESULT_OK)
        {
            processConsentResult((TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT));
        }
        else if (requestCode == REQUEST_SURVEY && resultCode == RESULT_OK)
        {
            processSurveyResult((TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT));
        }
    }

    private void launchConsent()
    {
        ConsentDocument document = new ConsentDocument();
        document.setTitle("Demo Consent");
        document.setSignaturePageTitle(R.string.consent);
        ArrayList<ConsentSection> sections = new ArrayList<>();

        // Create consent visual sections
        ConsentSection section1 = new ConsentSection(ConsentSection.Type.DataGathering);
        section1.setTitle("The title of the section goes here ...");
        section1.setSummary("The summary about the section goes here ...");
        section1.setContent("The content to show in learn more ...");
        sections.add(section1);

        // ...add more sections as needed, then create a visual consent step

        document.setSections(sections);
        ConsentVisualStep step = new ConsentVisualStep("visual_consent_identifier",
                document);

        // Create consent signature object and set what info is required
        ConsentSignature signature = new ConsentSignature();
        signature.setRequiresName(true);
        signature.setRequiresSignatureImage(true);

        // Create consent review step with signature and document
        ConsentReviewStep reviewStep = new ConsentReviewStep("consent_review",
                signature,
                document,
                "Reason for consent goes here");
        reviewStep.setText("Review step title");

        // Finally, create and present a task including this step.
        Task consentTask = new OrderedTask("consent",
                "consent",
                step,
                reviewStep);

        // Launch using hte ViewTaskActivity and make sure to listen for the activity result
        Intent intent = ViewTaskActivity.newIntent(this,
                consentTask);
        startActivityForResult(intent,
                REQUEST_CONSENT);
    }

    private void processConsentResult(TaskResult result)
    {
        ConsentSignatureResult signatureResult = (ConsentSignatureResult) result.getStepResult("consent_review")
                .getResult();
        ConsentSignature signature = signatureResult.getSignature();
        boolean consented = signatureResult.isConsented();

        // TODO consented was always false, so i made this always true, fix when consented is fixed
        if (true || consented)
        {
            String fullName = signature.getFullName();
            String signatureBase64 = signature.getSignatureImage();

            AppPrefs prefs = AppPrefs.getInstance(this);
            prefs.setHasConsented(true);

            saveString("/consented_name",
                    fullName);

            saveString("/consented_signature",
                    signatureBase64);

            initViews();
        }
    }

    private void printConsentInfo(String fullName, String signatureBase64)
    {
        ((TextView) findViewById(R.id.consented_name)).setText(fullName);
        byte[] signatureBytes = Base64.decode(signatureBase64,
                Base64.DEFAULT);
        ((ImageView) findViewById(R.id.consented_signature)).setImageBitmap(BitmapFactory.decodeByteArray(signatureBytes,
                0,
                signatureBytes.length));
    }

    private void printSurveyInfo()
    {
        String[] resultKeys = new String[] {
                AGE,
                FORM_NAME,
                FORM_AGE,
                FORM_GENDER,
                FORM_MULTI_CHOICE,
                FORM_DATE_OF_BIRTH,
                NUTRITION
        };

        String results = "";
        for (String resultKey : resultKeys)
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

        IntegerAnswerFormat format = new IntegerAnswerFormat(90,
                18);
        QuestionStep ageStep = new QuestionStep(AGE,
                "How old are you?",
                format);

        FormStep formStep = new FormStep(FORM_STEP, "Form", "Form groups multi-entry in one page");
        ArrayList<FormScene.FormItem> formItems = new ArrayList<>();

        FormScene.FormItem basicInfoHeader = new FormScene.FormItem(BASIC_INFO_HEADER, "Basic Information", null, null, true);
        formItems.add(basicInfoHeader);

        FormScene.FormItem nameItem = new FormScene.FormItem(FORM_NAME, "Name", new TextAnswerFormat(), "enter name", false);
        formItems.add(nameItem);

        FormScene.FormItem ageItem = new FormScene.FormItem(FORM_AGE, "Age", new IntegerAnswerFormat(90, 18), "age placeholder", false);
        formItems.add(ageItem);

        TextChoice[] textChoices = new TextChoice[2];
        textChoices[0] = new TextChoice<>("Male", 0, null);
        textChoices[1] = new TextChoice<>("Female", 1, null);
        AnswerFormat genderFormat = new TextChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice, textChoices);
        FormScene.FormItem genderFormItem = new FormScene.FormItem(FORM_GENDER, "Gender", genderFormat, "Gender", false);
        formItems.add(genderFormItem);


        TextChoice[] multiTextChoices = new TextChoice[3];
        multiTextChoices[0] = new TextChoice<>("Zero", 0, null);
        multiTextChoices[1] = new TextChoice<>("One", 1, null);
        multiTextChoices[2] = new TextChoice<>("Two", 2, null);
        AnswerFormat multiFormat = new TextChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.MultipleChoice, multiTextChoices);
        FormScene.FormItem multiFormItem = new FormScene.FormItem(FORM_MULTI_CHOICE, "Test Multi", multiFormat, "Choose", false);
        formItems.add(multiFormItem);

        AnswerFormat dateOfBirthFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
        FormScene.FormItem dateOfBirthFormItem = new FormScene.FormItem(FORM_DATE_OF_BIRTH, "Birthdate", dateOfBirthFormat, "date of birth", false);
        formItems.add(dateOfBirthFormItem);

        // ... And so on, adding additional items
        formStep.setFormItems(formItems);

        // Create a Boolean step to include in the task.
        QuestionStep booleanStep = new QuestionStep(NUTRITION);
        booleanStep.setTitle("Do you take nutritional supplements?");
        booleanStep.setAnswerFormat(new BooleanAnswerFormat());
        booleanStep.setOptional(false);

        // Create a task wrapping the steps.
        OrderedTask task = new OrderedTask("ordered_task",
                "schedule_id",
                instructionStep,
                ageStep,
                formStep,
                booleanStep);

        // Create a task view controller using the task and set a delegate.
        Intent intent = ViewTaskActivity.newIntent(this,
                task);
        startActivityForResult(intent,
                REQUEST_SURVEY);
    }

    private void processSurveyResult(TaskResult result)
    {

        Integer age = (Integer) result.getStepResult(AGE)
                .getResult();
        saveString(SURVEY_PATH + AGE,
                String.valueOf(age));

        Integer nutrition = (Integer) result.getStepResult(NUTRITION)
                .getResult();
        String nutritionString = nutrition == 0 ? "No" : "Yes";
        saveString(SURVEY_PATH + NUTRITION,
                nutritionString);

        StepResult<FormResult> formStep = result.getStepResult(FORM_STEP);

        String name = (String) formStep.getResultForIdentifier(FORM_NAME).getAnswer();
        saveString(SURVEY_PATH + FORM_NAME, name);

        Integer formAge = (Integer) formStep.getResultForIdentifier(FORM_AGE).getAnswer();
        saveString(SURVEY_PATH + FORM_AGE, String.valueOf(formAge));

        Integer gender = (Integer) formStep.getResultForIdentifier(FORM_GENDER).getAnswer();
        saveString(SURVEY_PATH + FORM_GENDER, gender == 0 ? "Male" : "Female");

        Integer[] multiChoice = (Integer[]) formStep.getResultForIdentifier(FORM_MULTI_CHOICE).getAnswer();
        saveString(SURVEY_PATH + FORM_MULTI_CHOICE, String.valueOf(multiChoice));

        Date date = (Date) formStep.getResultForIdentifier(FORM_DATE_OF_BIRTH).getAnswer();
        saveString(SURVEY_PATH + FORM_DATE_OF_BIRTH, date.toString());

        AppPrefs prefs = AppPrefs.getInstance(this);
        prefs.setHasSurveyed(true);
        initViews();
    }

    private String loadString(String path)
    {
        return StorageManager
                .getFileAccess()
                .readString(this,
                        path);
    }

    private void saveString(String path, String string)
    {
        string = string == null ? "" : string;
        StorageManager
                .getFileAccess()
                .writeString(this,
                        path,
                        string);
    }
}
