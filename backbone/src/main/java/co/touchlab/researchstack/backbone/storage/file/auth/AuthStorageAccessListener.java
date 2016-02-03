package co.touchlab.researchstack.backbone.storage.file.auth;
import co.touchlab.researchstack.backbone.storage.file.StorageAccessListener;

public interface AuthStorageAccessListener extends StorageAccessListener
{
    /**
     * Data access is not ready, needs auth.
     * TODO add bool hasPinCode as a param ?
     */
    void onDataAuth();
}
