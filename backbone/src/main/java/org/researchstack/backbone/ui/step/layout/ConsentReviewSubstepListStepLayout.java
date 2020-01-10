package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.DataResponse;
import org.researchstack.backbone.model.ConsentSignatureBody;
import org.researchstack.backbone.model.ProfileInfoOption;
import org.researchstack.backbone.model.User;
import org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.utils.StepHelper;
import org.researchstack.backbone.utils.StepLayoutHelper;
import org.researchstack.backbone.utils.StepResultHelper;

import java.util.Date;
import java.util.Map;

import rx.Observable;

import static org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory.CONSENT_SHARING_IDENTIFIER;
import static org.researchstack.backbone.model.survey.factory.ConsentDocumentFactory.CONSENT_SUBTASK_ID;

/**
 * Created by TheMDP on 1/16/17.
 *
 * Consent ReviewStep contains a number of steps
 */

public class ConsentReviewSubstepListStepLayout extends ViewPagerSubstepListStepLayout implements ViewTaskActivity.ResultListener {

    public static final String LOG_TAG = ConsentReviewSubstepListStepLayout.class.getCanonicalName();

    /**
     * This is passed in by the ResultListener
     */
    private String sharingScope;

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
        Observable<DataResponse> uploadConsent = DataProvider.getInstance()
                .uploadConsent(getContext(), consentSignatureBody)
                .compose(ObservableUtils.applyDefault());

        // Only gives a callback to response on success, the rest is handled by StepLayoutHelper
        StepLayoutHelper.safePerformWithAlerts(uploadConsent, this, response ->
                super.onComplete()
        );
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
        StepResult signatureResult = StepResultHelper.findStepResult(
                stepResult, ConsentDocumentFactory.CONSENT_SIGNATURE_IDENTIFIER);

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

        String usersName = StepResultHelper.findStringResult(ProfileInfoOption.NAME.getIdentifier(), stepResult);
        if (usersName == null) {
            throw new IllegalStateException("Names needs to be accessable at this point for StepLayout to work");
        }
        body.name = usersName;

        Date usersBirthday = StepResultHelper.findDateResult(ProfileInfoOption.BIRTHDATE.getIdentifier(), stepResult);
        if (usersBirthday == null) {
            throw new IllegalStateException("Birthdate needs to be accessable at this point for StepLayout to work");
        }
        body.birthdate = usersBirthday;

        // If one doesn't exist yet then set it to a blank string, otherwise the consent sig will have null value issues
        if (sharingScope == null) {
            if (body.scope == null) {
                sharingScope = "";
            }
        } else {
            body.scope = sharingScope;
        }

        // Save Consent Information
        // User is not signed in yet, so we need to save consent info to disk for later upload
        return body;
    }

    @Override
    public void taskResult(ViewTaskActivity activity, TaskResult taskResult) {
        String identifier = StepResultHelper.subtaskIdentifier(CONSENT_SUBTASK_ID, CONSENT_SHARING_IDENTIFIER);
        Boolean sharingScopeResult =  StepResultHelper.findBooleanResult(identifier, taskResult);
        if (sharingScopeResult != null) {
            if (sharingScopeResult) {
                sharingScope = User.DataSharingScope.ALL.getIdentifier();
            } else {
                sharingScope = User.DataSharingScope.STUDY.getIdentifier();
            }
        }
    }
}
