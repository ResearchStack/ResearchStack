package co.touchlab.researchstack.core.storage.file.auth;
import android.content.Context;

/**
 * Created by bradleymcdermott on 2/2/16.
 */
public interface AuthDataAccess
{
    void logAccessTime();

    void authenticate(Context context, String pin);

    PinCodeConfig getPinCodeConfig();

    void setPinCode(Context context, String pin);
}
