package org.researchstack.feature.authentication.pincode

public class PinCodeConfigProvider() {
    companion object {
        private var _pinCodeConfig: PinCodeConfig? = null
        fun config(pinCodeConfig: PinCodeConfig) {
            this._pinCodeConfig = pinCodeConfig
        }

        val pinCodeConfig: PinCodeConfig
            get() = this._pinCodeConfig!!
    }
}