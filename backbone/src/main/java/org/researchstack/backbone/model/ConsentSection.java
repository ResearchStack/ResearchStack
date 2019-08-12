package org.researchstack.backbone.model;
import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;

import org.researchstack.backbone.R;
import org.researchstack.backbone.utils.TextUtils;

import java.io.Serializable;

public class ConsentSection implements Serializable
{

    /**
     * The type of section. (read-only)
     * <p>
     * The value of this property indicates whether a predefined image, title, and animation are
     * present.
     */
    public static final String SECTION_TYPE_GSON = "sectionType";
    @SerializedName(SECTION_TYPE_GSON)
    private Type   type;

    /**
     * The title of the consent section in a localized string.
     * <p>
     * The title is displayed as a step title in the animated consent sequence and is also included
     * in the PDF file, but it can be overridden by setting <code>formalTitle</code>.
     */
    @SerializedName("sectionTitle")
    private String title;
    /**
     * The formal title of the section in a localized string, for use in the legal document.
     * <p>
     * If the value of this property is <code>null</code>, the value of <code>title</code> is used
     * in the legal document instead.
     */
    @SerializedName("sectionFormalTitle")
    private String formalTitle;
    /**
     * A short summary of the content in a localized string.
     * <p>
     * The summary is displayed as description text in the animated consent sequence. The summary
     * should be limited in length, so that the consent can be reliably displayed on smaller
     * screens.
     */
    @SerializedName("sectionSummary")
    private String summary;
    /**
     * The content of the section in a localized string.
     * <p>
     * In a consent review step or in PDF file generation, the string is printed as the section's
     * content. The string is also displayed as Learn More content in a visual consent step.
     * <p>
     * This property is never prepopulated based on the value of <code>type</code>. If both
     * <code>content</code> and <code>htmlContent</code> are non-nil, the value of the
     * <code>htmlContent</code> property is used.
     */
    @SerializedName("sectionContent")
    private String content;
    private String escapedContent;
    /**
     * The HTML content used to override the <code>content</code> property if additional formatting
     * is needed. The content should be localized.
     * <p>
     * In cases where plain text content is not sufficient to convey important details during the
     * consent process, you can provide HTML content in this property. When you do this, the
     * <code>htmlContent</code> property takes precedence over the <code>content</code> property.
     * <p>
     * In a consent review step or in PDF file generation, the value of this property is printed as
     * the section's content; in a visual consent step, the content is displayed as Learn More
     * content.
     */
    @SerializedName("sectionHtmlContent")
    private String htmlContent;
    /**
     * A custom illustration for the consent.
     * <p>
     * The custom image can override the image associated with any of the predefined section types
     * for an {@link org.researchstack.backbone.step.ConsentVisualStep} object. It is ignored for a
     * consent review step and for PDF generation.
     * <p>
     * The image is used in template rendering mode, and is tinted using the tint color.
     */
    @SerializedName("sectionImage")
    private String customImageName;
    /**
     * A custom Learn More button title in a localized string.
     * <p>
     * The predefined section types have localized descriptive Learn More button titles for a visual
     * consent step. When this property is not <code>null</code>, it overrides that default text.
     */
    @SerializedName("sectionMoreTitle")
    private String customLearnMoreButtonTitle;
    /**
     * A file URL that specifies a custom transition animation video.
     * <p>
     * Animations of the illustration between one screen and the next are provided by default for
     * transitions between consecutive section <code>type</code> codes. Custom sections and
     * out-of-order transitions may require custom animations.
     * <p>
     * The animation loaded from the file URL is played aspect fill in the illustration area for
     * forward transitions only. The video is rendered in template mode, with white treated as if it
     * were transparent.
     */
    @SerializedName("sectionAnimationUrl")
    private String customAnimationURL;

    /**
     * Used for storing custom type identifer when type is CUSTOM eum
     */
    transient String customTypeIdentifier;

    /* Default identifier for serilization/deserialization */
    ConsentSection() {
        super();
    }

    /**
     * Returns an initialized consent section using the specified type.
     *
     * @param type The consent section type.
     */
    public ConsentSection(Type type)
    {
        this.type = type;
        this.summary = null;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getFormalTitle()
    {
        return formalTitle;
    }

    public Type getType()
    {
        return type;
    }
    void setType(Type type) {
        this.type = type;
    }

    public String getHtmlContent()
    {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent)
    {
        this.htmlContent = htmlContent;
    }

    public String getCustomImageName()
    {
        return customImageName;
    }

    void setCustomImageName(String imageName) {
        customImageName = imageName;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
        this.escapedContent = null;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getEscapedContent()
    {
        // If its null, return that. If not, escape/replace chars in var content
        if(TextUtils.isEmpty(content))
        {
            return content;
        }

        return escapedContent;
    }

    public String getCustomLearnMoreButtonTitle()
    {
        return customLearnMoreButtonTitle;
    }

    public void setCustomLearnMoreButtonTitle(String customLearnMoreButtonTitle)
    {
        this.customLearnMoreButtonTitle = customLearnMoreButtonTitle;
    }

    public String getTypeIdentifier() {
        if (type == Type.Custom) {
            return customTypeIdentifier;
        }
        return type.getIdentifier();
    }

    public static final int UNDEFINED_RES = -1;
    public enum Type implements Serializable
    {
        /**
         * Overview of the informed consent process.
         * <p>
         * This content can inform the user of what to expect during the process, and provide
         * general background information on the purpose of the study.
         */
        @SerializedName("overview")
        Overview(
                "overview",
                R.string.rsb_consent_section_welcome,
                R.string.rsb_consent_section_more_info_welcome,
                null),

        /**
         * A section informing the user that sensor data will be collected.
         * <p>
         * This content can identify which sensors will be used, for how long, and for what
         * purpose.
         */
        @SerializedName("dataGathering")
        DataGathering(
                "dataGathering",
                R.string.rsb_consent_section_data_gathering,
                R.string.rsb_consent_section_more_info_data_gathering,
                "rsb_consent_section_data_gathering"),

        /**
         * A section describing the privacy policies for the study.
         * <p>
         * This content can describe how data is protected, the processes used to sanitize the
         * collected data or make it anonymous, and address the risks
         * <p>
         * involved.
         */
        @SerializedName("privacy")
        Privacy(
                "privacy",
                R.string.rsb_consent_section_privacy,
                R.string.rsb_consent_section_more_info_privacy,
                "rsb_consent_section_privacy"),

        /**
         * A section describing how the collected data will be used.
         * <p>
         * This content can include details about those who will have access to the data, the types
         * of analysis that will be performed, and the degree of control the participant may have
         * over the data after it is collected.
         */
        @SerializedName("dataUse")
        DataUse(
                "dataUse",
                R.string.rsb_consent_section_data_use,
                R.string.rsb_consent_section_more_info_data_use,
                "rsb_consent_section_data_use"),

        /**
         * A section describing how much time is required for the study.
         * <p>
         * This content can help users understand what to expect as they participate in the study.
         */
        @SerializedName("timeCommitment")
        TimeCommitment(
                "timeCommitment",
                R.string.rsb_consent_section_time_commitment,
                R.string.rsb_consent_section_more_info_time_commitment,
                "rsb_consent_section_time_commitment"),

        /**
         * A section describing active task use in the study.
         * <p>
         * This content can describe what types of tasks need to be performed, how often, and for
         * what purpose. Any risks that are involved can also be communicated in this section.
         */
        @SerializedName("studyTasks")
        StudyTasks(
                "studyTasks",
                R.string.rsb_consent_section_study_tasks,
                R.string.rsb_consent_section_more_info_study_tasks,
                "rsb_consent_section_study_tasks"),

        /**
         * A section describing survey use in the study.
         * <p>
         * This content can explain how survey data will be collected, for what purpose, and make it
         * clear to what extent participation is optional.
         */
        @SerializedName("studySurvey")
        StudySurvey(
                "studySurvey",
                R.string.rsb_consent_section_study_survey,
                R.string.rsb_consent_section_more_info_study_survey,
                "rsb_consent_section_study_survey"),

        /**
         * A section describing how to withdraw from the study.
         * <p>
         * This section can describe the policies that govern the collected data if the user decides
         * to withdraw.
         */
        @SerializedName("withdrawing")
        Withdrawing(
                "withdrawing",
                R.string.rsb_consent_section_withdrawing,
                R.string.rsb_consent_section_more_info_withdrawing,
                "rsb_consent_section_withdrawing"),

        /**
         * A custom section.
         * <p>
         * Custom sections don't have a predefined title, summary, content, image, or animation. A
         * consent document may have as many or as few custom sections as needed.
         */
        @SerializedName("custom")
        Custom("custom", UNDEFINED_RES, R.string.rsb_consent_section_more_info, null),

        /**
         * Document-only sections.
         * <p>
         * Document-only sections are ignored for a visual consent step and are only displayed in a
         * consent review step (assuming no value is provided for the  <code>htmlReviewContent</code>
         * property).
         */
        @SerializedName("onlyInDocument")
        OnlyInDocument("onlyInDocument", UNDEFINED_RES, R.string.rsb_consent_section_more_info, null);

        Type(
            String identifier,
            @StringRes int titleRes,
            @StringRes int moreInfoRes,
            String imageName)
        {
            this.identifier = identifier;
            this.titleRes = titleRes;
            this.moreInfoRes = moreInfoRes;
            this.imageName = imageName;
        }

        String identifier;
        String imageName;
        @StringRes int titleRes;
        @StringRes int moreInfoRes;

        public int getTitleResId() {
            return titleRes;
        }
        public String getImageName() {
            return imageName;
        }
        public int getMoreInfoResId() {
            return moreInfoRes;
        }
        private String getIdentifier() {
            return identifier;
        }
    }

}
