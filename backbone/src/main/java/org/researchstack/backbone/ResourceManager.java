package org.researchstack.backbone;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for returning paths of resources defined in the assets folder. This
 * class has more structure and expects certain assets to be defined for use within the framework.
 */
public abstract class ResourceManager extends ResourcePathManager {

    /**
     * The resource map is a way to inject your own resources into the ResourceManager
     */
    private Map<String, Resource> resourceMap = new HashMap<>();

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
     * @param resourceName the name of the resource to find
     * @return a Resource this manager provides, null if none exists
     */
    public Resource getResource(String resourceName) {
        Resource resource = resourceMap.get(resourceName);
        // If we use the abstract methods (backwards compatibility)
        // the resource may exist in one of the methods
        if (resource == null) {
            List<Resource> resourceList = reflectToCreateResourceList();
            for (Resource resourceInList : resourceList) {
                if (resourceInList.getName().equals(resourceName)) {
                    return resourceInList;
                }
            }
        }
        return resource;
    }

    /**
     * Uses reflection to find all methods that return "Resource" types and builds it into a list
     * @return a list of Resource objects
     */
    private List<Resource> reflectToCreateResourceList() {
        List<Resource> resourceList = new ArrayList<>();
        // Look at all methods of ResourceManager to find ones that return type "Resource"
        Method[] resourceMethods = ResourceManager.class.getDeclaredMethods();
        for (Method method : resourceMethods) {
            if (method.getReturnType().equals(ResourcePathManager.Resource.class)) {
                String errorMessage = null;
                try {
                    Object resourceObj = method.invoke(ResourceManager.getInstance());
                    if (resourceObj instanceof ResourcePathManager.Resource) {
                        resourceList.add((ResourcePathManager.Resource)resourceObj);
                    }
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                }
                if (errorMessage != null) {
                    throw new IllegalStateException("You must define a method in ResourceManager " +
                            "that returns a Resource for the resourceName or add it to the" +
                            "ResourceManager with addResource()");
                }
            }
        }
        return resourceList;
    }

    /**
     * @param resourceName the name of the resource that will be
     * @param resource the full path, type, and name of the resource
     * @return an instance of this class to chain adding resources
     */
    public ResourceManager addResource(String resourceName, Resource resource) {
        resourceMap.put(resourceName, resource);
        return this;
    }

    /**
     * @return a duplicate copy of the ResourceManager's custom resource map
     */
    public Map<String, Resource> getResourceMap() {
        return new HashMap<>(resourceMap);
    }

    public void removeResource(String name) {
        resourceMap.remove(name);
    }
}
