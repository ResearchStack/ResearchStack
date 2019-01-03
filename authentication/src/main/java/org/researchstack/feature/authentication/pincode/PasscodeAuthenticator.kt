package org.researchstack.feature.authentication.pincode

import android.support.v4.os.CancellationSignal
import java.lang.ref.WeakReference
import org.researchstack.feature.authentication.Authenticator

//there are two things that need to be delegated
//how to store the passcode
//how to present the authentication
//also, we need to think about how one might register, but for now, let's assume that
//This class will need to define some type of storage backend
//this class will also need to figure out how to present the passcode authentication view

//in one case, this would be a fragment and we would pass it a cancelation signal and a callback
//however, it's not exactly clear how the fragment would be presented. The activity / app delegate would be re

//in another case,


open class PasscodeAuthenticator(val passcodeConfig: PinCodeConfig, val store: PasscodeStore): Authenticator<PasscodeAuthenticator.PasscodeAuthenticationResult, PasscodeAuthenticator.PasscodeAuthenticationCallback>() {

    open interface PasscodeStore {
        fun hasPasscode(): Boolean
        fun registerPasscode(passcode: String)
        fun checkPasscode(passcode: String)
        fun removePasscode()
        fun changePasscode(oldPasscode: String, newPasscode: String)
    }

    open interface PresentationDelegate {
        //this should present a way to get a passcode
        //note that in order to support combined passcode + biometric, this should probably only
        //return a yes / no response
        //therefore, we probably need to pass it some type of
        fun presentPasscodeAuthentication(authenticator: PasscodeAuthenticator, cancel: CancellationSignal, callback: PasscodeAuthenticator.PasscodeAuthenticationCallback)
    }

    companion object {

    }

    var presentationDelegate: WeakReference<PresentationDelegate>? = null
    fun setPresentationDelegate(presentationDelegate: PresentationDelegate) {
        this.presentationDelegate = WeakReference(presentationDelegate)
    }

    open class PasscodeAuthenticationResult(): Authenticator.AuthenticationResult() {

    }

    open class PasscodeAuthenticationCallback(): Authenticator.AuthenticationCallback<PasscodeAuthenticationResult>() {
        open fun onForgotPasscode() {}
    }

    sealed class PasscodeAuthenticatorException: RuntimeException {

        constructor() {}
        constructor(detailMessage: String) : super(detailMessage) {}
        constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}
        constructor(throwable: Throwable) : super(throwable) {}

        class InvalidPasscodeException : PasscodeAuthenticatorException {
            constructor() {}
            constructor(detailMessage: String) : super(detailMessage) {}
            constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}
            constructor(throwable: Throwable) : super(throwable) {}
        }

        class PasscodeExistsException : PasscodeAuthenticatorException {
            constructor() {}
            constructor(detailMessage: String) : super(detailMessage) {}
            constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}
            constructor(throwable: Throwable) : super(throwable) {}
        }

        class PasscodeDoesNotExistException : PasscodeAuthenticatorException {
            constructor() {}
            constructor(detailMessage: String) : super(detailMessage) {}
            constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}
            constructor(throwable: Throwable) : super(throwable) {}
        }

        class PresentationDelegateNotSetException : PasscodeAuthenticatorException {
            constructor() {}
            constructor(detailMessage: String) : super(detailMessage) {}
            constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}
            constructor(throwable: Throwable) : super(throwable) {}
        }

    }

    override fun isRegistered(): Boolean {
        return this.store.hasPasscode()
    }

    override fun authenticate(cancel: CancellationSignal, callback: PasscodeAuthenticationCallback) {
        val delegate = this.presentationDelegate?.get()
        if (delegate == null) {
            throw PasscodeAuthenticatorException.PresentationDelegateNotSetException();
        }
        else {
            delegate.presentPasscodeAuthentication(this, cancel, callback)
        }
    }

}