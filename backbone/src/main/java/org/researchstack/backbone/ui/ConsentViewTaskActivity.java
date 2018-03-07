package org.researchstack.backbone.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.print.HtmlToPdfPrinter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.BirthDateAnswerFormat;
import org.researchstack.backbone.answerformat.DateAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.utils.RSHTMLPDFWriter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout.KEY_SIGNATURE;

public class ConsentViewTaskActivity extends ViewTaskActivity implements StepCallbacks
{
    String CONSENT_DOC_LINE_PRINTED_NAME = "'s Name (printed)";
    String CONSENT_DOC_LINE_SIGNATURE = "'s Signature";
    String CONSENT_DOC_LINE_DATE = "Date";

    private static final String ID_FORM_FIRST_NAME = "user_info_form_first_name";
    private static final String ID_FORM_LAST_NAME = "user_info_form_last_name";
    private static final String ID_FORM_DOB = "user_info_form_dob";
    private static final String ID_FORM = "user_info_form";

    String consentHtml;
    String firstName;
    String lastName;
    String signatureBase64;

    public static Intent newIntent(Context context, Task task) {
        Intent intent = new Intent(context, ConsentViewTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @Override
    public void onSaveStep(int action, Step step, StepResult result) {

        if(step instanceof FormStep && step.getIdentifier().equalsIgnoreCase(ID_FORM))
        {
            for (QuestionStep question : ((FormStep) step).getFormSteps())
            {
                if(question.getIdentifier().equalsIgnoreCase(ID_FORM_FIRST_NAME))
                {
                    StepResult nameResult = (StepResult) result.getResultForIdentifier(ID_FORM_FIRST_NAME);
                    if(nameResult != null && nameResult.getResult() != null)
                    {
                        firstName = (String) nameResult.getResult();
                    }
                }
                if(question.getIdentifier().equalsIgnoreCase(ID_FORM_LAST_NAME))
                {
                    StepResult nameResult = (StepResult) result.getResultForIdentifier(ID_FORM_LAST_NAME);
                    if(nameResult != null && nameResult.getResult() != null)
                    {
                        lastName = (String) nameResult.getResult();
                    }
                }
            }
        }
        if(step instanceof ConsentSignatureStep)
        {
            signatureBase64 = (String) result.getResultForIdentifier(KEY_SIGNATURE);
        }
        else if(step instanceof ConsentDocumentStep)
        {
            consentHtml = ((ConsentDocumentStep) step).getConsentHTML();
        }

        super.onSaveStep(action, step, result);
    }

    private String getFormalName(String firstName, String lastName)
    {
        String completeName = null;
        if(lastName != null && firstName != null) completeName = lastName+", "+firstName;
        else if (firstName != null) completeName = firstName;
        else completeName = lastName;
        return completeName;
    }

    @Override
    protected void saveAndFinish() {
        // you can also set title / message
        final AlertDialog dialog = new ProgressDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getString(R.string.rsb_saving_consent))
                .setMessage(getString(R.string.rsb_please_wait))
                .create();

        dialog.show();

        consentHtml += getSignatureHtmlContent(getFormalName(firstName, lastName),
                "Participant",
                signatureBase64,
                new SimpleDateFormat("MM-dd-yyyy").format(new Date())
        );

        new PDFWriteExposer().printPdfFile(this, getCurrentTaskId(), consentHtml, new RSHTMLPDFWriter.PDFFileReadyCallback()
        {
            @Override
            public void onPrintFileReady()
            {
                dialog.dismiss();
                ConsentViewTaskActivity.super.saveAndFinish();
            }
        });
    }


    public static @Nullable FormStep getConsentPersonalInfoFormStep(boolean requiresName, boolean requiresBirthDate)
    {
        if (requiresName || requiresBirthDate)
        {
            List<QuestionStep> formSteps = new ArrayList<>();
            if (requiresName)
            {
                formSteps.add(new QuestionStep(ID_FORM_FIRST_NAME, "First Name", new TextAnswerFormat()));
                formSteps.add(new QuestionStep(ID_FORM_LAST_NAME, "Last Name", new TextAnswerFormat()));
            }

            if (requiresBirthDate)
            {
                Calendar maxDate = Calendar.getInstance();
                maxDate.add(Calendar.YEAR, -18);
                DateAnswerFormat dobFormat = new BirthDateAnswerFormat(null, 18, 0);
                String dobText = "Date of birth";
                formSteps.add(new QuestionStep(ID_FORM_DOB, dobText, dobFormat));
            }

            String formTitle = "Consent";
            FormStep formStep = new FormStep(ID_FORM, formTitle, "");
            formStep.setOptional(false);
            formStep.setFormSteps(formSteps);

            return formStep;
        }

        return null;
    }

    private String getSignatureHtmlContent(@Nullable String completeName, @NonNull String role, @Nullable String signatureB64, @Nullable String signatureDate)
    {
        StringBuffer body = new StringBuffer();

        String hr = "<hr align='left' width='100%' style='height:1px; border:none; color:#000; background-color:#000; margin-top: -10px; margin-bottom: 0px;' />";

        String signatureElementWrapper = "<p><br/><div class='sigbox'><div class='inbox'>%s</div></div>%s%s</p>";
        String imageTag = null;

        List<String> signatureElements = new ArrayList<>();

        if(role == null)
        {
            throw new RuntimeException("Consent role cannot be empty");
        }

        // Signature
        if (completeName != null)
        {
            String base = role+" "+CONSENT_DOC_LINE_PRINTED_NAME;
            String nameElement = String.format(signatureElementWrapper, completeName, hr, base);
            signatureElements.add(nameElement);
        }

        if (signatureB64 != null)
        {
            imageTag = "<img width='100%%' alt='star' src='data:image/png;base64,"+signatureB64+"' />";
            String base = role+" "+CONSENT_DOC_LINE_SIGNATURE;
            String signatureElement = String.format(signatureElementWrapper, imageTag, hr, base);
            signatureElements.add(signatureElement);
        }

        if (signatureElements.size() > 0)
        {
            String base = CONSENT_DOC_LINE_DATE;
            String signatureElement = String.format(signatureElementWrapper, signatureDate, hr, base);
            signatureElements.add(signatureElement);
        }

        int numElements = signatureElements.size();

        if (numElements > 1)
        {
            body.append("<div class='grid border'>");
            for (String element : signatureElements)
            {
                body.append(String.format("<div class='col-1-3 border'>%s</div>", element));
            }
            body.append("</div>");
        }
        else if (numElements == 1)
        {
            body.append(String.format("<div width='200'>%@</div>", signatureElements.get(0)));
        }

        return body.toString();
    }

    class PDFWriteExposer extends RSHTMLPDFWriter
    {
        protected void printPdfFile(Activity context, final String taskId, String htmlConsentDocument, PDFFileReadyCallback callback)
        {
            super.printPdfFile(context, taskId, htmlConsentDocument, callback);
        }
    }
}
