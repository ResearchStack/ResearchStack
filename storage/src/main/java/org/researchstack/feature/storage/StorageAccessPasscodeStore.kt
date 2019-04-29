package org.researchstack.feature.storage

import android.content.Context
import org.researchstack.feature.authentication.pincode.PasscodeAuthenticator

class StorageAccessPasscodeStore(val context: Context): PasscodeAuthenticator.PasscodeStore {

    override fun hasPasscode(): Boolean {
        return StorageAccess.getInstance().hasPinCode(context)
    }

    override fun registerPasscode(passcode: String) {
        StorageAccess.getInstance().createPinCode(context, passcode)
    }

    override fun checkPasscode(passcode: String) {
        StorageAccess.getInstance().authenticate(context, passcode)
    }

    override fun removePasscode() {
        StorageAccess.getInstance().removePinCode(context)
    }

    override fun changePasscode(oldPasscode: String, newPasscode: String) {
        StorageAccess.getInstance().changePinCode(context, oldPasscode, newPasscode)
    }

}