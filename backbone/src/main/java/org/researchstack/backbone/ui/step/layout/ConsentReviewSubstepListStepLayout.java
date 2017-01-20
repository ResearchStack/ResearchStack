package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.model.ConsentSignatureBody;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;
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
        ConsentSignatureBody consentSignatureBody = createConsentSignatureBody(stepResult);
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
     * @return a completed ConsentSignatureBody model, if all the data is contained in either the
     *         the StepResult or the TaskResult
     */
    protected ConsentSignatureBody createConsentSignatureBody(StepResult stepResult) {

        ConsentSignatureBody body = DataProvider.getInstance().loadLocalConsent(getContext());
        if (body == null) {
            body = new ConsentSignatureBody();
        }

        // Grab signature from step result
        StepResult signatureResult = getResult(ConsentDocumentFactory.CONSENT_SIGNATURE_IDENTIFIER, stepResult);
        if (signatureResult != null) {
            Map<String, Object> signatureData = signatureResult.getResults();
            for (String stepKey : signatureData.keySet()) {
                switch (stepKey) {
                    case ConsentSignatureStepLayout.KEY_SIGNATURE:
                        body.imageData = (String)signatureData.get(stepKey);
                        body.imageMimeType = "image/png";
                        break;
                }
            }
        }
        if (body.imageData == null) {
            throw new IllegalStateException("Image data needs to be accessable at this point for StepLayout to work");
        }

        String usersName = getStringResult(ProfileInfoOption.NAME.getIdentifier(), stepResult);
        if (usersName == null) {
            throw new IllegalStateException("Names needs to be accessable at this point for StepLayout to work");
        }
        body.name = usersName;

        Date usersBirthday = getDateResult(ProfileInfoOption.BIRTHDATE.getIdentifier(), stepResult);
        if (usersBirthday == null) {
            throw new IllegalStateException("Birthdate needs to be accessable at this point for StepLayout to work");
        }
        body.birthdate = usersBirthday;

        // Save Consent Information
        // User is not signed in yet, so we need to save consent info to disk for later upload
        return body;
    }

    /**
     * @param stepIdentifier for result
     * @return Object result if exists, null otherwise
     */
    protected static StepResult getResult(String stepIdentifier, StepResult stepResult) {
        return StepResultHelper.findStepResult(stepResult, stepIdentifier);
    }

    /**
     * @param stepIdentifier for result
     * @return String object if exists, empty string otherwise
     */
    protected static String getStringResult(String stepIdentifier, StepResult stepResult) {
        StepResult idStepResult = getResult(stepIdentifier, stepResult);
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
    protected static Boolean getBooleanResult(String stepIdentifier, StepResult stepResult, TaskResult taskResult) {
        StepResult idStepResult = getResult(stepIdentifier, stepResult);
        if (idStepResult != null) {
            Object resultValue = idStepResult.getResult();
            if (resultValue instanceof Boolean) {
                return (Boolean) resultValue;
            }
        }
        return null;
    }

    /**
     * @param stepIdentifier for result
     * @return String object if exists, empty string otherwise
     */
    protected static Date getDateResult(String stepIdentifier, StepResult stepResult) {
        StepResult idStepResult = getResult(stepIdentifier, stepResult);
        if (idStepResult != null) {
            Object resultValue = idStepResult.getResult();
            if (resultValue instanceof Long) {
                return new Date((Long)resultValue);
            }
        }
        return null;
    }
}
