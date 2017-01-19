package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.model.ConsentSignatureBody;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.StepResultHelper;

import java.util.Date;
import java.util.Map;

/**
 * Created by TheMDP on 1/16/17.
 *
 * Consent ReviewStep contains a number of steps
 */

public class ConsentReviewSubstepListStepLayout extends ViewPagerSubstepListStepLayout {

    public ConsentReviewSubstepListStepLayout(Context context) {
        super(context);
    }

    public ConsentReviewSubstepListStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onComplete() {
        ConsentSignatureBody consentSignatureBody = createConsentSignatureBody(stepResult, taskResult);
        if (DataProvider.getInstance().isSignedIn(getContext())) {
            uploadConsent(consentSignatureBody);
        } else {
            DataProvider.getInstance().saveLocalConsent(getContext(), consentSignatureBody);
            super.onComplete();
        }
    }

    protected void uploadConsent(ConsentSignatureBody consentSignatureBody) {
        DataProvider.getInstance()
                .uploadConsent(getContext(), consentSignatureBody)
                .compose(ObservableUtils.applyDefault())
                .subscribe(dataResponse -> {
                    hideLoadingDialog();
                    if(dataResponse.isSuccess()) {
                        super.onComplete();
                    } else {
                        showOkAlertDialog(dataResponse.getMessage());
                    }
                }, throwable -> {
                    hideLoadingDialog();
                    showOkAlertDialog(throwable.getMessage());
                });
    }

    /**
     * @param stepResult The StepResult for the current step
     * @param taskResult The TaskResult from the Task this Step belongs to
     * @return a completed ConsentSignatureBody model, if all the data is contained in either the
     *         the StepResult or the TaskResult
     */
    protected static ConsentSignatureBody createConsentSignatureBody(StepResult stepResult, TaskResult taskResult) {

        String studyId = DataProvider.getInstance().getStudyId();

        // Grab signature from step result
        String base64Image = null;
        StepResult signatureResult = getResult(ConsentDocumentFactory.CONSENT_SIGNATURE_IDENTIFIER, stepResult, taskResult);
        if (signatureResult != null) {
            Map<String, Object> signatureData = signatureResult.getResults();
            for (String stepKey : signatureData.keySet()) {
                switch (stepKey) {
                    case ConsentSignatureStepLayout.KEY_SIGNATURE:
                        base64Image = (String)signatureData.get(stepKey);
                        break;
                }
            }
        }

        // Grab user's name and birthday from step result
        String usersName = getStringResult(ProfileInfoOption.NAME.getIdentifier(), stepResult, taskResult);
        Date usersBirthdate = getDateResult(ProfileInfoOption.BIRTHDATE.getIdentifier(), stepResult, taskResult);

        // Grab sharing scope from TaskREsult
        String sharingScope = getStringResult(ConsentDocumentFactory.CONSENT_SHARING_IDENTIFIER, stepResult, taskResult);

        // Save Consent Information
        // User is not signed in yet, so we need to save consent info to disk for later upload
        return new ConsentSignatureBody(studyId, usersName, usersBirthdate, base64Image, "image/png", sharingScope);
    }

    /**
     * @param stepIdentifier for result
     * @return Object result if exists, null otherwise
     */
    protected static StepResult getResult(String stepIdentifier, StepResult stepResult, TaskResult taskResult) {
        StepResult result = StepResultHelper.findStepResult(stepResult, stepIdentifier);
        if (result == null && taskResult != null && !taskResult.getResults().isEmpty()) {
            for (StepResult taskStepResult : taskResult.getResults().values()) {
                if (result != null) {
                    result = StepResultHelper.findStepResult(taskStepResult, stepIdentifier);
                }
            }
        }
        return result;
    }

    /**
     * @param stepIdentifier for result
     * @return String object if exists, empty string otherwise
     */
    protected static String getStringResult(String stepIdentifier, StepResult stepResult, TaskResult taskResult) {
        StepResult idStepResult = getResult(stepIdentifier, stepResult, taskResult);
        if (idStepResult != null) {
            Object resultValue = idStepResult.getResult();
            if (resultValue instanceof String) {
                return (String) resultValue;
            }
        }
        return null;
    }

    /**
     * @param stepIdentifier for result
     * @return String object if exists, empty string otherwise
     */
    protected static Date getDateResult(String stepIdentifier, StepResult stepResult, TaskResult taskResult) {
        StepResult idStepResult = getResult(stepIdentifier, stepResult, taskResult);
        if (idStepResult != null) {
            Object resultValue = idStepResult.getResult();
            if (resultValue instanceof Long) {
                return new Date((Long)resultValue);
            }
        }
        return null;
    }
}
