package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

/**
 * This class represents the final step in the consent process, collecting the signature from the
 * study participant.
 */
public class ConsentSignatureStep extends Step {
    private String signatureDateFormat;

    /* Default constructor needed for serilization/deserialization of object */
    ConsentSignatureStep() {
        super();
        setOptional(false);
    }

    public ConsentSignatureStep(String identifier) {
        super(identifier);
        setOptional(false);
    }

    public ConsentSignatureStep(String identifier, String title, String text) {
        this(identifier);
        setTitle(title);
        setText(text);
    }

    /**
     * Returns the date format string to be used when producing a date string for the PDF or consent
     * review.
     * <p>
     * For example, "yyyy-MM-dd 'at' HH:mm". When the value of this property is <code>null</code>, the current
     * date and time for the current locale is used.
     *
     * @return the date format string
     */
    public String getSignatureDateFormat() {
        return signatureDateFormat;
    }

    /**
     * Sets the date format string to be used when producing a date string for the PDF or consent
     * review.
     * <p>
     * For example, "yyyy-MM-dd 'at' HH:mm". When the value of this property is <code>null</code>, the current
     * date and time for the current locale is used.
     *
     * @param signatureDateFormat a string representing the date format
     */
    public void setSignatureDateFormat(String signatureDateFormat) {
        this.signatureDateFormat = signatureDateFormat;
    }

    @Override
    public Class getStepLayoutClass() {
        return ConsentSignatureStepLayout.class;
    }
}
