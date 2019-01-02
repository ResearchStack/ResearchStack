package org.researchstack.backbone.step;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.step.layout.ConsentDocumentStepLayout;

/**
 * This step shows an HTML version of your consent document to the user and allows them to indicate
 * whether they agree or disagree.
 */
public class ConsentDocumentStep extends Step {
    private String html;

    private String confirmMessage;

    public ConsentDocumentStep(String identifier) {
        super(identifier);
    }

    @Override
    public int getStepTitle() {
        return R.string.rsb_consent;
    }

    @Override
    public Class getStepLayoutClass() {
        return ConsentDocumentStepLayout.class;
    }

    /**
     * Returns the HTML string of the consent document.
     *
     * @return the string representation of the entire consent HTML document
     */
    public String getConsentHTML() {
        return html;
    }

    /**
     * Sets the HTML string that is used to show the user your consent document.
     *
     * @param html a string representation of the entire consent HTML document
     */
    public void setConsentHTML(String html) {
        this.html = html;
    }

    /**
     * Gets the message to show the user when they are asked to confirm their agreement.
     *
     * @return the string to show the user during confirmation
     */
    public String getConfirmMessage() {
        return confirmMessage;
    }

    /**
     * Sets the message to show the user when they are asked to confirm their agreement.
     *
     * @param confirmMessage the string to show the user during confirmation
     */
    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }
}
