package org.researchstack.backbone.step;

/**
 * This class represents the final step in the consent process, collecting the signature from the
 * study participant.
 */
public class ConsentSignatureStep extends Step {
    private String signatureDateFormat;

    public ConsentSignatureStep(String identifier) {
        super(identifier);
        setOptional(false);
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
}
