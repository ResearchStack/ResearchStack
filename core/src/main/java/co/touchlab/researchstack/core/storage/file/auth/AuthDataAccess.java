package co.touchlab.researchstack.core.storage.file.auth;
import android.content.Context;

/**
 * TODO Rename methods below
 */
public interface AuthDataAccess
{
    void logAccessTime();


    void authenticate(Context context, String pin);
}
