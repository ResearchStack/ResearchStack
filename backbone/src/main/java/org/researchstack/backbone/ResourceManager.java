package org.researchstack.backbone;


import android.content.Context;
import android.util.Log;

import org.researchstack.backbone.onboarding.ResourceNameToStringConverter;
import org.researchstack.backbone.utils.LogExt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
     * @param taskFileName the filename for th task
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing a
     * individual task file
     */
    public abstract Resource getTask(String taskFileName);

    /**
     * Returns a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * inclusion criteria
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * inclusion criteria
     */
    public abstract Resource getInclusionCriteria();

    /**
     * Returns a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * Onboarding sections
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing the
     * Onboarding sections
     */
    public abstract Resource getOnboardingManager();

    /**
     * The NameJsonProvider is a useful class that utilizes the
     * ResourceManager to convert simple String names to Json Strings
     */
    public static class NameJsonProvider implements ResourceNameToStringConverter {

        private Context context;

        public NameJsonProvider(Context context) {
            this.context = context;
        }

        @Override
        public String getJsonStringForResourceName(String resourceName) {
            // Look at all methods of ResourceManager
            Method[] resourceMethods = ResourceManager.class.getDeclaredMethods();
            for (Method method : resourceMethods) {
                if (method.getReturnType().equals(ResourcePathManager.Resource.class)) {
                    String errorMessage = null;
                    try {
                        Object resourceObj = method.invoke(ResourceManager.getInstance());
                        if (resourceObj instanceof ResourcePathManager.Resource) {
                            ResourcePathManager.Resource resource = (ResourcePathManager.Resource)resourceObj;
                            if (resourceName.equals(resource.getName())) {
                                // Resource name match, return its contents as a JSON string
                                return ResourceManager.getResourceAsString(context, resource.getRelativePath());
                            }
                        }
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                    }
                    if (errorMessage != null) {
                        throw new IllegalStateException("You must define a method in ResourceManager that returns a Resource for the resourceName " + resourceName);
                    }
                }
            }
            // This should never happen unless you have an invalid resource name referenced in json
            LogExt.e(getClass(), "No resource with name " + resourceName + " found");
            return null;
        }

        @Override
        public String getHtmlStringForResourceName(String resourceName) {
            String htmlFilePath = ResourceManager.getInstance()
                    .generatePath(ResourceManager.Resource.TYPE_HTML, resourceName);
            return ResourceManager.getResourceAsString(context, htmlFilePath);
        }
    }
}
