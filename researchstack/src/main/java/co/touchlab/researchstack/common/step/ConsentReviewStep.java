package co.touchlab.researchstack.common.step;
import co.touchlab.researchstack.common.model.ConsentDocument;
import co.touchlab.researchstack.common.model.ConsentSignature;
import co.touchlab.researchstack.ui.scene.ConsentReviewScene;

public class ConsentReviewStep extends Step
{
    private ConsentSignature signature;
    private ConsentDocument document;
    private final String reasonForConsent;

    public ConsentReviewStep(String identifier, ConsentSignature signature, ConsentDocument document, String reasonForConsent)
    {
        super(identifier);
        this.signature = signature;
        this.document = document;
        this.reasonForConsent = reasonForConsent;
    }

    @Override
    public boolean isShowsProgress()
    {
        return false;
    }

    public ConsentDocument getDocument()
    {
        return document;
    }

    public ConsentSignature getSignature()
    {
        return signature;
    }

    public String getReasonForConsent()
    {
        return reasonForConsent;
    }

    @Override
    public Class getSceneClass()
    {
        return ConsentReviewScene.class;
    }

}
