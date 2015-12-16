package co.touchlab.researchstack.coreapp;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.answerformat.BooleanAnswerFormat;
import co.touchlab.researchstack.core.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.helpers.LogExt;
import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.model.ConsentSection;
import co.touchlab.researchstack.core.model.ConsentSignature;
import co.touchlab.researchstack.core.result.ConsentSignatureResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.result.TextQuestionResult;
import co.touchlab.researchstack.core.step.ConsentReviewDocumentStep;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.step.InstructionStep;
import co.touchlab.researchstack.core.step.FormStep;
import co.touchlab.researchstack.core.step.InstructionStep;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.task.OrderedTask;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.PassCodeActivity;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;
import co.touchlab.researchstack.core.ui.scene.ConsentReviewSignatureScene;
import co.touchlab.researchstack.core.ui.scene.FormScene;

public class MainActivity extends PassCodeActivity
{

    private static final int REQUEST_CONSENT = 0;
    private static final int REQUEST_SURVEY = 1;
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
        LogExt.d(getClass(), "onDataReady");
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
            surveyButton.setEnabled(false);
            printSurveyInfo(loadString("/survey_age"),
                    loadString("/survey_nutrition"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

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

        // Create consent visual sections
        ConsentSection section1 = new ConsentSection(ConsentSection.Type.DataGathering);
        section1.setTitle("The title of the section goes here ...");
        section1.setSummary("The summary about the section goes here ...");
        section1.setContent("The content to show in learn more ...");

        // ...add more sections as needed, then create a visual consent step
        ConsentVisualStep visualStep = new ConsentVisualStep("visual_consent_identifier", section1);
        visualStep.setNextButtonString(getString(R.string.next));

        // Create consent signature object and set what info is required
        ConsentSignature signature = new ConsentSignature();
        signature.setRequiresName(true);
        signature.setRequiresSignatureImage(true);

        // Create our HTML to show the user and have them accept or decline.
        StringBuilder docBuilder = new StringBuilder("</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
        String title = getString(R.string.consent_review_title);
        docBuilder.append(String.format(
                "<h1 style=\"text-align: center; font-family:sans-serif-light;\">%1$s</h1>", title));
        String detail =  getString(R.string.consent_review_instruction);
        docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
        docBuilder.append("</div></br>");
        docBuilder.append("<div><h2> HTML Consent Doc goes here </h2></div>");

        // Create the Consent doc step, pass in our HTML doc
        ConsentReviewDocumentStep documentStep = new ConsentReviewDocumentStep("consent_doc");
        documentStep.setConsentHTML(docBuilder.toString());
        documentStep.setConfirmMessage(getString(R.string.consent_review_reason));

        // Create Consent form step, to get users first & last name
        FormStep formStep = new FormStep("form_step", "Form Title", "Form step description");
        formStep.setSceneTitle(R.string.consent);

        TextAnswerFormat format = new TextAnswerFormat();
        format.setIsMultipleLines(false);

        FormScene.FormItem fullName = new FormScene.FormItem(
                formStep.getIdentifier(), "Full name", format, "Required");
        formStep.setFormItems(Collections.singletonList(fullName));

        // Create Consent signature step, user can sign their name
        Step signatureStep = new Step("signature");
        signatureStep.setTitle(getString(R.string.consent_signature_title));
        signatureStep.setText(getString(R.string.consent_signature_instruction));
        signatureStep.setOptional(false);
        signatureStep.setSceneClass(ConsentReviewSignatureScene.class);

        // Finally, create and present a task including these steps.
        Task consentTask = new OrderedTask("consent", "consent",
                visualStep,
                documentStep,
                formStep,
                signatureStep);

        // Launch using hte ViewTaskActivity and make sure to listen for the activity result
        Intent intent = ViewTaskActivity.newIntent(this,
                consentTask);
        startActivityForResult(intent,
                REQUEST_CONSENT);
    }

    private void processConsentResult(TaskResult result)
    {
        ConsentSignatureResult signatureResult = (ConsentSignatureResult) result.getStepResult("consent_doc")
                .getResult();
        ConsentSignature signature = signatureResult.getSignature();
        boolean consented = signatureResult.isConsented();

        if (consented)
        {
            TextQuestionResult formResult = (TextQuestionResult) result.getStepResult("form_step")
                    .getResult();
            String fullName = formResult.getTextAnswer();

            String signatureBase64 = (String) result.getStepResult("signature").getResult();

            AppPrefs prefs = AppPrefs.getInstance(this);
            prefs.setHasConsented(true);

            saveString("/consented_name", fullName);

            saveString("/consented_signature", signatureBase64);

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

    private void printSurveyInfo(String age, String nutrition)
    {
        ((TextView) findViewById(R.id.survey_age)).setText("Age: " + age);
        ((TextView) findViewById(R.id.survey_nutrition)).setText("Takes nutrition supplements: " + nutrition);
    }

    private void launchSurvey()
    {
        InstructionStep instructionStep = new InstructionStep("identifier",
                "Selection Survey",
                "This survey can help us understand your eligibility for the fitness study");

        IntegerAnswerFormat format = new IntegerAnswerFormat(90,
                18);
        QuestionStep ageStep = new QuestionStep("age",
                "How old are you?",
                format);

        // TODO fix form steps
//        FormStep formStep = new FormStep("form_step", "Form", "Form groups multi-entry in one page");
//        ArrayList<FormScene.FormItem> formItems = new ArrayList<>();
//
//        TextChoice[] textChoices = new TextChoice[2];
//        textChoices[0] = new TextChoice<>("Male", "male", null);
//        textChoices[0] = new TextChoice<>("Female", "female", null);
//        AnswerFormat genderFormat = new TextChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice, textChoices);
//        FormScene.FormItem genderFormItem = new FormScene.FormItem("Basic Information", "Gender", genderFormat, "Gender");
//        formItems.add(genderFormItem);
//
//        AnswerFormat dateOfBirthFormat = new DateAnswerFormat(AnswerFormat.DateAnswerStyle.Date);
//        FormScene.FormItem dateOfBirthFormItem = new FormScene.FormItem("date_of_birth", "DOB", dateOfBirthFormat, "DOB");
//        formItems.add(dateOfBirthFormItem);

        // ... And so on, adding additional items
//        formStep.setFormItems(formItems);

        // Create a Boolean step to include in the task.
        QuestionStep booleanStep = new QuestionStep("nutrition");
        booleanStep.setTitle("Do you take nutritional supplements?");
        booleanStep.setAnswerFormat(new BooleanAnswerFormat());
        booleanStep.setOptional(false);

        // Create a task wrapping the steps.
        OrderedTask task = new OrderedTask("ordered_task",
                "schedule_id",
                instructionStep,
                ageStep,
//                formStep,
                booleanStep);

        // Create a task view controller using the task and set a delegate.
        Intent intent = ViewTaskActivity.newIntent(this,
                task);
        startActivityForResult(intent,
                REQUEST_SURVEY);
    }

    private void processSurveyResult(TaskResult result)
    {
        AppPrefs prefs = AppPrefs.getInstance(this);
        prefs.setHasSurveyed(true);

        int age = (int) result.getStepResult("age")
                .getResult();
        String ageString = String.valueOf(age);

        int nutrition = (int) result.getStepResult("nutrition")
                .getResult();
        String nutritionString = nutrition == 0 ? "No" : "Yes";

        saveString("/survey_age",
                ageString);
        saveString("/survey_nutrition",
                nutritionString);

        initViews();
    }

    private String loadString(String path)
    {
        return StorageManager
                .getFileAccess()
                .readString(this,
                        path);
    }

    private void saveString(String path, String ageString)
    {
        StorageManager
                .getFileAccess()
                .writeString(this,
                        path,
                        ageString);
    }
}
