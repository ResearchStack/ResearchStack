package co.touchlab.researchstack.core.storage.file.auth;
import co.touchlab.researchstack.core.storage.file.FileAccessListener;

public interface AuthFileAccessListener <T> extends FileAccessListener
{
    /**
     * Data access is not ready, needs auth.
     * TODO add bool hasPinCode as a param ?
     */
    void dataAuth(T config);
}
