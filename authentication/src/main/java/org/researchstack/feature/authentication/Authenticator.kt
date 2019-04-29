package org.researchstack.feature.authentication

import android.support.v4.os.CancellationSignal

open abstract class Authenticator<Result: Authenticator.AuthenticationResult, Callback: Authenticator.AuthenticationCallback<Result>> {

    open class AuthenticationResult {

    }

    open class AuthenticationCallback<Result: AuthenticationResult> {
        open fun onAuthenticationError(errorCode: Int, errString: String) {}

        open fun onAuthenticationFailed() {}

        open fun onAuthenticationHelp(helpCode: Int, helpString: String) {}

        open fun onAuthenticationSucceeded(result: Result) {}
    }

    abstract fun isRegistered(): Boolean
    abstract fun authenticate(cancel: CancellationSignal, callback: Callback)

}