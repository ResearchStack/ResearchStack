package org.researchstack.sampleapp.bridge;

import java.util.List;

/**
 * Created by bradleymcdermott on 3/8/16.
 */
public interface UploadQueue
{
    List<UploadRequest> loadUploadRequests();

    void saveUploadRequest(UploadRequest request);

    void deleteUploadRequest(UploadRequest request);
}
