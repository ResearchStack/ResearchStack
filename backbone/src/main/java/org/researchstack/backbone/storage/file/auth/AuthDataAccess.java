package org.researchstack.backbone.storage.file.auth;
import android.content.Context;


public interface AuthDataAccess
{
    void logAccessTime();

    void authenticate(Context context, String pin);

    PinCodeConfig getPinCodeConfig();

    void setPinCode(Context context, String pin);

    void changePinCode(Context context, String oldPin, String newPin);
}
