package org.researchstack.feature.authentication.pincode

//This is for demonstration purposes only!
class SimplePasscodeStore: PasscodeAuthenticator.PasscodeStore {

    private var passcode: String? = null

    override fun hasPasscode(): Boolean {
        return this.passcode != null
    }

    override fun registerPasscode(passcode: String) {
        if (this.hasPasscode()) {
            throw PasscodeAuthenticator.PasscodeAuthenticatorException.PasscodeExistsException()
        }

        this.passcode = passcode
    }

    override fun checkPasscode(passcode: String) {
        if (this.passcode == null) {
            throw PasscodeAuthenticator.PasscodeAuthenticatorException.PasscodeDoesNotExistException()
        }

        if (this.passcode != passcode) {
            throw PasscodeAuthenticator.PasscodeAuthenticatorException.InvalidPasscodeException()
        }
    }

    override fun removePasscode() {
        this.passcode == null
    }

    override fun changePasscode(oldPasscode: String, newPasscode: String) {

        if (this.passcode == null) {
            throw PasscodeAuthenticator.PasscodeAuthenticatorException.PasscodeDoesNotExistException()
        }

        if (this.passcode != oldPasscode) {
            throw PasscodeAuthenticator.PasscodeAuthenticatorException.InvalidPasscodeException()
        }

        this.passcode = newPasscode
    }
}