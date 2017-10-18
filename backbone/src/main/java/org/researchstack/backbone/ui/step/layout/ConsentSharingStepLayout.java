package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.util.AttributeSet;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.model.ConsentSignature;
import org.researchstack.backbone.model.ConsentSignatureBody;
import org.researchstack.backbone.model.User;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.utils.StepResultHelper;

/**
 * Created by TheMDP on 1/19/17.
 */

public class ConsentSharingStepLayout extends SurveyStepLayout {
    public ConsentSharingStepLayout(Context context) {
        super(context);
    }

    public ConsentSharingStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConsentSharingStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onComplete() {

        if (stepResult != null) {
            Object resultObj = stepResult.getResult();

            // Support true/false for study ID, or a String with sharing scope
            String stringSharingScope = User.DataSharingScope.NONE.getIdentifier();
            if (resultObj instanceof Boolean) {
                if (((Boolean) resultObj)) {
                    stringSharingScope = User.DataSharingScope.ALL.getIdentifier();
                } else {
                    stringSharingScope = User.DataSharingScope.STUDY.getIdentifier();
                }
            } else if (resultObj instanceof String) {
                stringSharingScope = (String)resultObj;
            }

            // Save partial Consent Signature
            ConsentSignatureBody body = DataProvider.getInstance().loadLocalConsent(getContext());
            if (body == null) {
                body = new ConsentSignatureBody();
            }
            body.study = DataProvider.getInstance().getStudyId();
            body.scope = stringSharingScope;
            DataProvider.getInstance().saveLocalConsent(getContext(), body);
        }
        super.onComplete();
    }
}
