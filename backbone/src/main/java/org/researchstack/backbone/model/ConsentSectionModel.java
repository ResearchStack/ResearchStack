package org.researchstack.backbone.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Deprecated // No longer needed with new OnboardingManager
public class ConsentSectionModel {

    @SerializedName("documentProperties")
    DocumentProperties properties;

    ConsentQuizModel quiz;

    @SerializedName("sections")
    List<ConsentSection> sections;

    public List<ConsentSection> getSections() {
        return sections;
    }

    public DocumentProperties getDocumentProperties() {
        return properties;
    }

    public ConsentQuizModel getQuiz() {
        return quiz;
    }
}
