package co.touchlab.researchstack.coreapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.model.ConsentDocument;
import co.touchlab.researchstack.core.model.ConsentSection;
import co.touchlab.researchstack.core.model.ConsentSignature;
import co.touchlab.researchstack.core.result.ConsentSignatureResult;
import co.touchlab.researchstack.core.result.TaskResult;
import co.touchlab.researchstack.core.step.ConsentReviewStep;
import co.touchlab.researchstack.core.step.ConsentVisualStep;
import co.touchlab.researchstack.core.storage.file.FileAccess;
import co.touchlab.researchstack.core.task.OrderedTask;
import co.touchlab.researchstack.core.task.Task;
import co.touchlab.researchstack.core.ui.PassCodeActivity;
import co.touchlab.researchstack.core.ui.ViewTaskActivity;

public class MainActivity extends PassCodeActivity
{

    private static final int REQUEST_CONSENT = 0;
    private static final int REQUEST_SURVEY = 1;
    private AppCompatButton consentButton;
    private AppCompatButton surveyButton;

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
    }

    @Override
    protected void onDataReady()
    {
        super.onDataReady();

        AppPrefs prefs = AppPrefs.getInstance(this);
        if (prefs.hasConsented())
        {
            consentButton.setEnabled(false);
            consentButton.setText(R.string.consent_button_done);
            surveyButton.setEnabled(true);
            FileAccess fileAccess = StorageManager
                    .getFileAccess();
            printConsentInfo(fileAccess.readString(this,
                            "/consented_name"),
                    fileAccess.readData(this,
                            "/consented_signature"));
        }
        else
        {
            consentButton.setEnabled(true);
            consentButton.setText(R.string.consent_button);
            surveyButton.setEnabled(false);
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

        }
    }

    private void launchConsent()
    {
        ConsentDocument document = new ConsentDocument();
        document.setTitle("Demo Consent");
        document.setSignaturePageTitle(R.string.consent);
        ArrayList<ConsentSection> sections = new ArrayList<>();

        // create consent visual sections
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

        // create consent review step with signature and document
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
        ConsentSignatureResult signatureResult = ((ConsentSignatureResult) result.getStepResultForStepIdentifier("consent_review"));
        ConsentSignature signature = signatureResult.getSignature();
        boolean consented = signatureResult.isConsented();

        if (consented)
        {
            String fullName = signature.getFullName();
//            byte[] signatureBytes = signature.getSignatureImage();

            AppPrefs prefs = AppPrefs.getInstance(this);
            prefs.setHasConsented(true);
            printConsentInfo(fullName,
                    null);

            StorageManager
                    .getFileAccess()
                    .writeString(this,
                            "/consented_name",
                            fullName);
//            CoreApplication.getInstance().getFileAccess().writeData(this,
//                    "/consented_signature",
//                    signatureBytes);
        }
    }

    private void printConsentInfo(String fullName, byte[] signatureBytes)
    {
        ((TextView) findViewById(R.id.consented_name)).setText(fullName);
//        ((ImageView) findViewById(R.id.consented_signature)).setImageBitmap(BitmapFactory.decodeByteArray(signatureBytes,
//                0,
//                signatureBytes.length));
    }

    private void launchSurvey()
    {

    }
}
