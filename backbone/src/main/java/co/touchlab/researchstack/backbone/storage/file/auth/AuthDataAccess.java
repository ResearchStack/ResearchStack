package co.touchlab.researchstack.backbone.storage.file.auth;
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

    void changePinCode(Context context, String oldPin, String newPin);
}
