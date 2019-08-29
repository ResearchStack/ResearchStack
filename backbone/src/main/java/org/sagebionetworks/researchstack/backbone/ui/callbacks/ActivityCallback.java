package org.sagebionetworks.researchstack.backbone.ui.callbacks;

public interface ActivityCallback {
    public void onRequestPermission(String id);

    @Deprecated
    public void startConsentTask();
}
