package org.researchstack.backbone.onboarding;

/**
 * Created by TheMDP on 1/2/17.
 */

public interface ResourceNameToStringConverter {
    /**
     * @param resourceName name of a json resource, for example, "onboarding"
     * @return the json String from reading the resource with @param resourceName
     */
    String getJsonStringForResourceName(String resourceName);

    /**
     * @param resourceName name of an html resource, for example, "consent7_data_use"
     * @return the html String from reading the resource with @param resourceName
     */
    String getHtmlStringForResourceName(String resourceName);
}
