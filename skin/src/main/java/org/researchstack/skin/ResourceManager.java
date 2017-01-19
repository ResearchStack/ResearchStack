package org.researchstack.skin;

import org.researchstack.backbone.ResourcePathManager;

/**
 * This class is responsible for returning paths of resources defined in the assets folder. This
 * class has more structure and expects certain assets to be defined for use within the framework.
 */
public abstract class ResourceManager extends ResourcePathManager {
    /**
     * Returns a singleton static instance of the ResourceManager class
     *
     * @return A singleton static instance of the ResourceManager class
     */
    public static ResourceManager getInstance() {
        return (ResourceManager) ResourcePathManager.getInstance();
    }

    /**
     * Returns a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * StudyOverview file.
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * StudyOverview file.
     */
    public abstract Resource getStudyOverview();

    /**
     * Returns a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing an
     * HTML version of the Consent file.
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing an
     * HTML version of the Consent file.
     */
    public abstract Resource getConsentHtml();

    /**
     * This is currently unused but will be when share-pdf feature is implemented
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing an PDF
     * version of the Consent file.
     */
    public abstract Resource getConsentPDF();

    /**
     * The consent section differs from ResearchKit™ as ResearchStack includes extra
     * documentProperties along with support for quiz steps
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing a
     * consent section file
     */
    public abstract Resource getConsentSections();

    /**
     * Returns a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing a
     * learn-sections file
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing a
     * learn-sections file
     */
    public abstract Resource getLearnSections();

    /**
     * Returns a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * privacy policy
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * privacy policy
     */
    public abstract Resource getPrivacyPolicy();

    /**
     * Returns a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * software notices
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * software notices
     */
    public abstract Resource getSoftwareNotices();

    /**
     * Returns a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * tasks and schedules file
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * tasks and schedules file
     */
    public abstract Resource getTasksAndSchedules();

    /**
     * Returns a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing a
     * individual task file
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing a
     * individual task file
     */
    public abstract Resource getTask(String taskFileName);

}
