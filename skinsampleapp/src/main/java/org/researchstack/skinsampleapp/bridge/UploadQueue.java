package org.researchstack.skinsampleapp.bridge;

import java.util.List;


public interface UploadQueue
{
    List<UploadRequest> loadUploadRequests();

    void saveUploadRequest(UploadRequest request);

    void deleteUploadRequest(UploadRequest request);
}
