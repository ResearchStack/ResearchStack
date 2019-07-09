package org.researchstack.skin.model;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.model.DocumentProperties;
import org.researchstack.feature.consent.model.ConsentSection;

import java.util.List;

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
