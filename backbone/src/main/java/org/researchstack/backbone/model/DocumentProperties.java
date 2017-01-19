package org.researchstack.backbone.model;

import com.google.gson.annotations.SerializedName;

public class DocumentProperties {
    @SerializedName("htmlDocument")
    private String htmlDocument;

    @SerializedName("investigatorShortDescription")
    private String investigatorShortDescription;

    @SerializedName("investigatorLongDescription")
    private String investigatorLongDescription;

    @SerializedName("htmlContent")
    private String htmlContent;

    private boolean requiresSignature;
    private boolean requiresName;
    private boolean requiresBirthdate;

    public String getHtmlDocument() {
        return htmlDocument;
    }

    public void setHtmlDocument(String htmlDocument) {
        this.htmlDocument = htmlDocument;
    }

    public String getInvestigatorShortDescription() {
        return investigatorShortDescription;
    }

    public void setInvestigatorShortDescription(String investigatorShortDescription) {
        this.investigatorShortDescription = investigatorShortDescription;
    }

    public String getInvestigatorLongDescription() {
        return investigatorLongDescription;
    }

    public void setInvestigatorLongDescription(String investigatorLongDescription) {
        this.investigatorLongDescription = investigatorLongDescription;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public boolean requiresSignature() {
        return requiresSignature;
    }

    public void setRequiresSignature(boolean requiresSignature) {
        this.requiresSignature = requiresSignature;
    }

    public boolean requiresName() {
        return requiresName;
    }

    public void setRequiresName(boolean requiresName) {
        this.requiresName = requiresName;
    }

    public boolean requiresBirthdate() {
        return requiresBirthdate;
    }

    public void setRequiresBirthdate(boolean requiresBirthdate) {
        this.requiresBirthdate = requiresBirthdate;
    }
}
