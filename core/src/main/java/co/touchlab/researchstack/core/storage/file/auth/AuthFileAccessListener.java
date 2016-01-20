package co.touchlab.researchstack.core.storage.file.auth;
import co.touchlab.researchstack.core.storage.file.FileAccessListener;

public interface AuthFileAccessListener<T> extends FileAccessListener
{
    /**
     * Data access is not ready, needs auth.
     */
    void dataAuth(T config);
}
