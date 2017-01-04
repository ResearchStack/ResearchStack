package org.researchstack.backbone.onboarding;

/**
 * Created by TheMDP on 1/2/17.
 */

public interface ResourceNameJsonProvider {
    /**
     * @param resourceName name of a json resource
     * @return the json String from reading the resource with @param resourceName
     */
    String getJsonStringForResourceName(String resourceName);
}
